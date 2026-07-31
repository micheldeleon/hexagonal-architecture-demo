#!/usr/bin/env bash
set -Eeuo pipefail

readonly ALLOWED_IMAGE_PREFIX="${ALLOWED_IMAGE_PREFIX:-ghcr.io/micheldeleon/hexagonal-architecture-demo@sha256:}"
readonly DEPLOY_SCRIPT="${DEPLOY_SCRIPT:-/opt/tutorneo/deploy.sh}"

read -r -a command_parts <<< "${SSH_ORIGINAL_COMMAND:-}"

if [[ ${#command_parts[@]} -ne 2 || "${command_parts[0]}" != "deploy" ]]; then
  echo "Only 'deploy <immutable-image-reference>' is allowed." >&2
  exit 64
fi

image_ref="${command_parts[1]}"
if [[ "$image_ref" != "${ALLOWED_IMAGE_PREFIX}"* ]]; then
  echo "Image reference is outside the allowed repository." >&2
  exit 65
fi

exec "$DEPLOY_SCRIPT" "$image_ref"
