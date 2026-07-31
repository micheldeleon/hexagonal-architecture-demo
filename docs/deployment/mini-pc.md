# Despliegue en mini PC

La API y `cloudflared` se ejecutan con Docker Compose. La API no publica ningún
puerto en la red local: el puerto de diagnóstico queda ligado exclusivamente a
`127.0.0.1:18080`.

## Resultado esperado

Después de completar esta guía:

- cada push o merge a `main` ejecuta tests y construye una imagen;
- GitHub publica la imagen en GHCR identificada por digest;
- el job `production` entra por Cloudflare Access y un SSH restringido;
- la mini PC descarga la imagen, ejecuta Flyway y verifica el health check;
- ante un fallo, la aplicación vuelve automáticamente a la imagen anterior;
- ni PostgreSQL, ni SSH, ni el puerto 8080 quedan abiertos en el router.

Los pushes a otras ramas sólo ejecutan CI. El despliegue permanece desactivado
hasta definir `PRODUCTION_ENABLED=true`.

## 1. Requisitos previos

- Mini PC x86-64 o ARM64 con una distribución Linux mantenida.
- Dominio administrado por Cloudflare.
- Repositorio GitHub con Actions habilitado.
- Acceso administrativo inicial por consola o red local.
- Docker Engine, plugin Docker Compose, OpenSSH Server y `curl`.
- Credenciales nuevas para PostgreSQL, Supabase Storage y Mailjet.

Para Ubuntu o Debian, instalar primero los paquetes generales:

```bash
sudo apt-get update
sudo apt-get install -y ca-certificates curl openssh-server
```

Instalar Docker Engine y Compose desde el repositorio oficial de Docker para la
versión concreta del sistema. Verificar:

```bash
docker --version
docker compose version
sudo systemctl enable --now docker ssh
```

No continuar hasta que ambos comandos de Docker funcionen.

## 2. Preparar usuarios y directorios

Crear un usuario de despliegue sin contraseña:

```bash
sudo useradd --create-home --shell /bin/bash deploy
sudo usermod --append --groups docker deploy
sudo install -d -o root -g deploy -m 0750 /opt/tutorneo
sudo install -d -o root -g deploy -m 0750 /opt/tutorneo/secrets
```

Cerrar y volver a abrir la sesión del usuario después de modificar sus grupos.
Ser miembro del grupo `docker` equivale prácticamente a privilegios root; por
eso la clave de CI se restringe posteriormente a un solo comando.

## 3. Provisionar los artefactos

Desde una copia confiable del repositorio en la mini PC, ejecutar:

Provisionar los artefactos como archivos propiedad de `root`, para que la clave
de CI no pueda modificarlos:

```bash
sudo install -o root -g deploy -m 0640 deploy/compose.prod.yml \
  /opt/tutorneo/compose.prod.yml
sudo install -o root -g deploy -m 0750 deploy/deploy.sh \
  /opt/tutorneo/deploy.sh
sudo install -o root -g deploy -m 0750 deploy/ssh-deploy-gate.sh \
  /opt/tutorneo/ssh-deploy-gate.sh
```

Verificar:

```bash
sudo ls -la /opt/tutorneo
sudo -u deploy bash -n /opt/tutorneo/deploy.sh
sudo -u deploy bash -n /opt/tutorneo/ssh-deploy-gate.sh
```

## 4. Crear los secretos de aplicación

Crear los secretos directamente en la mini PC:

```bash
sudo install -o root -g deploy -m 0640 deploy/app.env.example \
  /opt/tutorneo/secrets/app.env
sudo install -o root -g deploy -m 0640 deploy/cloudflared.env.example \
  /opt/tutorneo/secrets/cloudflared.env
```

Editar `app.env` y completar todas las variables necesarias. Generar
`JWT_SECRET` localmente:

```bash
openssl rand -hex 48
```

En `cloudflared.env`, el formato será:

```text
TUNNEL_TOKEN=token_del_tunel
```

No colocar comillas innecesarias, no copiar estos archivos al repositorio y no
enviar sus valores por chat o logs.

## 5. Crear la clave SSH de CI

En una estación administrativa, no dentro del repositorio, generar una clave
dedicada:

```bash
ssh-keygen -t ed25519 -a 100 -f github-actions-production \
  -C github-actions-production
```

La clave privada se guardará posteriormente en GitHub. Copiar la clave pública
a la mini PC y agregarla a `/home/deploy/.ssh/authorized_keys` con estas
restricciones en una única línea:

```text
command="/opt/tutorneo/ssh-deploy-gate.sh",restrict ssh-ed25519 AAAA... github-actions-production
```

El `forced command` sólo acepta `deploy ghcr.io/micheldeleon/hexagonal-architecture-demo@sha256:...`.
La clave no permite shell, forwarding, `scp` ni modificar la configuración del
servidor. Usar una clave administrativa distinta para mantenimiento manual.

Aplicar permisos:

```bash
sudo install -d -o deploy -g deploy -m 0700 /home/deploy/.ssh
sudo chown deploy:deploy /home/deploy/.ssh/authorized_keys
sudo chmod 0600 /home/deploy/.ssh/authorized_keys
```

Si la imagen GHCR es privada, iniciar sesión una sola vez como `deploy` usando
un token con permiso mínimo `read:packages`:

```bash
docker login ghcr.io
```

La mini PC también necesita `curl`, utilizado por el health check con rollback.

## 6. Crear el túnel Cloudflare

En Cloudflare Zero Trust:

1. Ir a **Networks → Tunnels**.
2. Crear un túnel administrado remotamente llamado `tutorneo-mini-pc`.
3. Elegir Docker como entorno.
4. Copiar solamente el token `eyJ...`.
5. Guardarlo en:

```text
/opt/tutorneo/secrets/cloudflared.env
```

Crear dos rutas publicadas en el mismo túnel:

| Host público | Servicio de origen |
| --- | --- |
| `api.example.com` | `http://api:8080` |
| `ssh.example.com` | `ssh://host.docker.internal:22` |

No publicar `18080` ni `8080` en el router.

## 7. Proteger SSH con Cloudflare Access

1. Ir a **Access → Applications**.
2. Crear una aplicación self-hosted para `ssh.example.com`.
3. Crear un Service Token llamado `github-actions-production`.
4. Guardar su Client ID y Client Secret una sola vez.
5. Crear una política con acción **Service Auth** que permita exclusivamente
   ese Service Token.
6. Crear aparte una política de acceso humano con identidad y MFA para tareas
   administrativas.

El token del túnel y el Service Token de Access son credenciales diferentes.
No reutilizarlos.

Antes de configurar GitHub, comprobar manualmente el túnel con `cloudflared`
desde una estación administrativa:

```bash
cloudflared access ssh --hostname ssh.example.com
```

En la mini PC, obtener la clave pública del host:

```bash
sudo cat /etc/ssh/ssh_host_ed25519_key.pub
```

Construir `DEPLOY_SSH_HOST_KEY` anteponiendo el hostname:

```text
ssh.example.com ssh-ed25519 AAAA...
```

Transferir y verificar esta línea por un canal confiable. No usar `ssh-keyscan`
a través de una red no confiable ni aceptar una huella automáticamente.

## 8. Configurar GitHub

Crear el environment `production`. Se recomienda exigir aprobación durante los
primeros despliegues.

Variables del environment:

| Variable | Ejemplo |
| --- | --- |
| `PRODUCTION_ENABLED` | `true` |
| `DEPLOY_HOST` | `ssh.example.com` |
| `DEPLOY_USER` | `deploy` |

Secrets del environment:

| Secret | Contenido |
| --- | --- |
| `DEPLOY_SSH_PRIVATE_KEY` | Clave privada dedicada al despliegue |
| `DEPLOY_SSH_HOST_KEY` | Línea completa de `ssh-keyscan` verificada |
| `CF_ACCESS_CLIENT_ID` | Client ID del Service Token |
| `CF_ACCESS_CLIENT_SECRET` | Client Secret del Service Token |

El host key debe obtenerse y verificarse por un canal confiable. No usar
`StrictHostKeyChecking=no`.

Configurar también:

- **Deployment branches:** solamente `main`.
- **Required reviewers:** al menos un revisor durante la puesta en marcha.
- **Prevent self-review:** habilitado si hay más de un colaborador.

Mantener inicialmente `PRODUCTION_ENABLED=false`.

## 9. Preflight en la mini PC

Validar archivos y acceso a secretos sin imprimirlos:

```bash
sudo -u deploy test -r /opt/tutorneo/secrets/app.env
sudo -u deploy test -r /opt/tutorneo/secrets/cloudflared.env
sudo -u deploy docker info
sudo -u deploy curl --version
```

Comprobar que el gate rechaza comandos arbitrarios:

```bash
sudo -u deploy env SSH_ORIGINAL_COMMAND=shell \
  /opt/tutorneo/ssh-deploy-gate.sh
```

Debe terminar con código `64` y rechazar la operación.

## 10. Primera publicación

Los cambios deben llegar a `main` mediante Pull Request. En esta preparación se
trabaja desde la rama `Miche`; no se debe activar producción antes de revisar y
fusionar esos cambios.

Después de provisionar los artefactos de `deploy/` y completar los secretos,
hacer:

1. Push de la rama de trabajo.
2. Abrir Pull Request hacia `main`.
3. Esperar que CI quede verde.
4. Revisar y fusionar.
5. Confirmar que GHCR recibió la primera imagen.
6. Cambiar `PRODUCTION_ENABLED` a `true`.
7. Ejecutar un nuevo push controlado a `main` o relanzar el workflow.
8. Aprobar el environment `production`.

El workflow publicará por digest y solicitará el despliegue por el canal SSH
restringido.

## 11. Validación posterior

En la mini PC:

```bash
docker compose -f /opt/tutorneo/compose.prod.yml ps
curl --fail http://127.0.0.1:18080/actuator/health
docker logs --tail 100 tutorneo-cloudflared
```

Desde Internet:

```bash
curl --fail https://api.example.com/actuator/health
```

La respuesta esperada es:

```json
{"status":"UP"}
```

Comprobar además que el router no tenga reglas de port forwarding hacia
`22`, `8080` o `18080`.

## 12. Rollback y mantenimiento

Los cambios futuros en `compose.prod.yml`, `deploy.sh` o
`ssh-deploy-gate.sh` requieren reprovisionarlos manualmente con `sudo install`.
Esto es deliberado: un push no debe poder ampliar sus propios privilegios.

Las migraciones Flyway deben ser compatibles hacia atrás: el rollback restaura
la imagen anterior, pero no revierte automáticamente la base de datos.

Si el health check de una versión nueva falla, `deploy.sh` vuelve a la imagen
anterior y el workflow queda rojo. Revisar:

```bash
docker logs --tail 200 tutorneo-api
docker inspect tutorneo-api --format '{{.Config.Image}}'
```

Operaciones periódicas:

- renovar el Service Token antes de que expire;
- rotar claves SSH y credenciales externas;
- actualizar la versión fijada de `cloudflared` con checksum verificado;
- actualizar Docker y el sistema operativo;
- verificar backups y recuperación de PostgreSQL;
- revisar logs de Access y GitHub Environments;
- mantener migraciones Flyway compatibles con la versión anterior.

## Checklist final

- [ ] Docker y Compose instalados.
- [ ] Usuario `deploy` creado y limitado por forced command.
- [ ] Artefactos en `/opt/tutorneo` propiedad de root.
- [ ] Secretos rotados y guardados sólo en la mini PC.
- [ ] Login de sólo lectura en GHCR configurado.
- [ ] Túnel Cloudflare conectado.
- [ ] Rutas HTTP y SSH configuradas.
- [ ] Access Service Auth y MFA administrativo configurados.
- [ ] Host key SSH verificada.
- [ ] Environment `production` configurado.
- [ ] CI verde en Pull Request.
- [ ] Primera imagen publicada por digest.
- [ ] Health local y público en estado `UP`.
- [ ] Rollback ensayado.
