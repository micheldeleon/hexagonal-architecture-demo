#!/usr/bin/env bash
set -Eeuo pipefail

if [[ $# -ne 1 ]]; then
  echo "Usage: $0 <immutable-image-reference>" >&2
  exit 64
fi

readonly NEW_IMAGE="$1"
readonly DEPLOY_DIR="${DEPLOY_DIR:-/opt/tutorneo}"
readonly COMPOSE_FILE="${DEPLOY_DIR}/compose.prod.yml"
readonly HEALTH_URL="${HEALTH_URL:-http://127.0.0.1:18080/actuator/health}"
readonly HEALTH_ATTEMPTS="${HEALTH_ATTEMPTS:-30}"
readonly HEALTH_INTERVAL="${HEALTH_INTERVAL:-5}"

if [[ "$NEW_IMAGE" != ghcr.io/*@sha256:* ]]; then
  echo "The image must be an immutable GHCR digest (ghcr.io/...@sha256:...)." >&2
  exit 65
fi

if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "Missing ${COMPOSE_FILE}." >&2
  exit 66
fi

previous_image="$(docker inspect --format '{{.Config.Image}}' tutorneo-api 2>/dev/null || true)"

health_is_up() {
  curl --silent --show-error --fail --max-time 4 "$HEALTH_URL" \
    | grep --quiet '"status":"UP"'
}

wait_until_healthy() {
  local attempt
  for ((attempt = 1; attempt <= HEALTH_ATTEMPTS; attempt++)); do
    if health_is_up; then
      return 0
    fi
    sleep "$HEALTH_INTERVAL"
  done
  return 1
}

deploy_image() {
  export IMAGE_REF="$1"
  docker compose --file "$COMPOSE_FILE" pull api cloudflared
  docker compose --file "$COMPOSE_FILE" up --detach --remove-orphans
}

echo "Deploying ${NEW_IMAGE}"
deploy_image "$NEW_IMAGE"

if wait_until_healthy; then
  echo "Deployment healthy."
  exit 0
fi

echo "Health check failed." >&2
docker compose --file "$COMPOSE_FILE" logs --tail 150 api >&2 || true

if [[ -z "$previous_image" || "$previous_image" == "$NEW_IMAGE" ]]; then
  echo "No distinct previous image is available for rollback." >&2
  exit 1
fi

echo "Rolling back application container to ${previous_image}." >&2
deploy_image "$previous_image"

if wait_until_healthy; then
  echo "Rollback completed; the new deployment failed." >&2
  exit 1
fi

echo "Rollback also failed; manual intervention is required." >&2
exit 2
