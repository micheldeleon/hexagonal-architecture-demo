# Sistema de Blog y Avisos - Tutorneo

## 📋 Descripción General

Sistema completo de blog con soporte para:
- **Noticias** sobre torneos y eventos
- **Chats generales** para discusiones de la comunidad
- **Avisos clasificados**:
  - Busco equipo
  - Equipo busca jugador
  - Falta jugador para partido urgente

## 🗄️ Base de Datos

### Tablas Creadas

#### 1. **posts**
Publicaciones principales del blog/avisos.

Campos:
- `id` (UUID): Identificador único
- `titulo` (VARCHAR 200): Título de la publicación
- `contenido` (TEXT): Contenido/descripción
- `autor_id` (BIGINT): Usuario que creó el post
- `tipo_post` (VARCHAR 50): CHAT_GENERAL, NOTICIA, BUSCO_EQUIPO, EQUIPO_BUSCA_JUGADOR, PARTIDO_URGENTE
- `estado` (VARCHAR 20): ACTIVO, CERRADO, ARCHIVADO
- `deporte` (VARCHAR 50): Opcional, tipo de deporte
- `ubicacion` (VARCHAR 200): Opcional, ubicación del evento
- `fecha_creacion`, `fecha_actualizacion`: Timestamps

#### 2. **comentarios**
Comentarios y respuestas en publicaciones.

Campos:
- `id` (UUID): Identificador único
- `post_id` (UUID): Post al que pertenece
- `autor_id` (BIGINT): Usuario que comentó
- `contenido` (TEXT): Texto del comentario
- `comentario_padre_id` (UUID): Opcional, para respuestas anidadas
- `fecha_creacion`: Timestamp

#### 3. **contactos_revelados**
Registro de contactos realizados en avisos.

Campos:
- `id` (UUID): Identificador único
- `post_id` (UUID): Aviso contactado
- `usuario_interesado_id` (BIGINT): Usuario que contactó
- `autor_post_id` (BIGINT): Autor del aviso
- `telefono_revelado` (VARCHAR 20): Teléfono compartido
- `fecha_contacto`: Timestamp

### Triggers Automáticos

1. **notify_comment_on_post**: Notifica al autor cuando comentan su post
2. **notify_reply_to_comment**: Notifica cuando responden tu comentario
3. **notify_contacto_aviso**: Notifica al autor cuando contactan su aviso

## 🚀 API Endpoints

### Posts

**Crear post**
```
POST /api/posts
Body: {
  "titulo": "Busco equipo de fútbol 5",
  "contenido": "Jugador disponible para equipo amateur...",
  "autorId": 1,
  "tipoPost": "BUSCO_EQUIPO",
  "deporte": "Fútbol",
  "ubicacion": "Montevideo"
}
```

**Listar todos los posts**
```
GET /api/posts
```

**Obtener post por ID**
```
GET /api/posts/{id}
```

**Filtrar por tipo**
```
GET /api/posts/tipo/BUSCO_EQUIPO
GET /api/posts/tipo/EQUIPO_BUSCA_JUGADOR
GET /api/posts/tipo/PARTIDO_URGENTE
GET /api/posts/tipo/NOTICIA
GET /api/posts/tipo/CHAT_GENERAL
```

**Posts de un usuario**
```
GET /api/posts/autor/{userId}
```

**Cerrar un post** (solo el autor)
```
PUT /api/posts/{id}/cerrar?userId={userId}
```

### Comentarios

**Crear comentario**
```
POST /api/comentarios
Body: {
  "postId": "uuid-del-post",
  "autorId": 1,
  "contenido": "Interesante propuesta...",
  "comentarioPadreId": null  // opcional, para respuestas
}
```

**Listar comentarios de un post**
```
GET /api/comentarios/post/{postId}
```

### Contactos (Avisos)

**Contactar un aviso** (revela teléfono automáticamente)
```
POST /api/contactos/aviso/{postId}?usuarioId={usuarioId}

Respuesta: {
  "id": "uuid",
  "postId": "uuid-del-post",
  "usuarioInteresadoId": 2,
  "autorPostId": 1,
  "telefonoRevelado": "+598 91 234 567",
  "fechaContacto": "2026-01-22T15:30:00"
}
```

**Ver quién contactó mis avisos**
```
GET /api/contactos/recibidos/{autorId}
```

**Ver avisos que contacté**
```
GET /api/contactos/realizados/{usuarioId}
```

## 🔔 Notificaciones

El sistema genera notificaciones automáticas para:

1. **NUEVO_COMENTARIO_POST**: Cuando alguien comenta tu publicación
2. **RESPUESTA_COMENTARIO**: Cuando responden tu comentario
3. **CONTACTO_AVISO**: Cuando alguien contacta tu aviso

Las notificaciones se entregan vía SSE (Server-Sent Events) en tiempo real.

## 💡 Flujos de Usuario

### Flujo 1: Busco Equipo

1. Usuario crea post tipo `BUSCO_EQUIPO`
2. Equipos interesados hacen clic en "Contactar"
3. Se revela automáticamente el teléfono del jugador
4. El jugador recibe notificación
5. El equipo ve el contacto en "Contactos realizados"

### Flujo 2: Equipo Busca Jugador

1. Equipo crea post tipo `EQUIPO_BUSCA_JUGADOR`
2. Jugadores interesados hacen clic en "Contactar"
3. Se revela automáticamente el teléfono del equipo
4. El equipo recibe notificación
5. El jugador ve el contacto en "Contactos realizados"

### Flujo 3: Partido Urgente

1. Usuario crea post tipo `PARTIDO_URGENTE`
2. Incluye detalles: deporte, ubicación, hora
3. Interesados contactan (mismo flujo que avisos)

### Flujo 4: Noticias y Chats

1. Usuario crea post tipo `NOTICIA` o `CHAT_GENERAL`
2. Otros usuarios comentan
3. Se pueden crear hilos de conversación (comentarios anidados)
4. Notificaciones automáticas en cada respuesta

## 🔐 Seguridad

- RLS (Row Level Security) habilitado
- Políticas permisivas (Spring Boot maneja autenticación)
- Validaciones en backend:
  - Solo el autor puede cerrar su post
  - No puedes contactar tu propio aviso
  - No puedes contactar dos veces el mismo aviso

## 📱 Integración Frontend

### Ejemplo de uso en React/Angular/Vue

```javascript
// Crear un aviso
const crearAviso = async () => {
  const response = await fetch('/api/posts', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      titulo: 'Falta 1 para fútbol 5 HOY',
      contenido: 'Jugamos a las 20hs en Complejo X',
      autorId: getCurrentUserId(),
      tipoPost: 'PARTIDO_URGENTE',
      deporte: 'Fútbol 5',
      ubicacion: 'Montevideo - Complejo Deportivo'
    })
  });
  return response.json();
};

// Contactar aviso
const contactarAviso = async (postId) => {
  const response = await fetch(
    `/api/contactos/aviso/${postId}?usuarioId=${getCurrentUserId()}`,
    { method: 'POST' }
  );
  const contacto = await response.json();
  // contacto.telefonoRevelado contiene el teléfono
  mostrarTelefono(contacto.telefonoRevelado);
};

// Comentar
const comentar = async (postId, texto) => {
  await fetch('/api/comentarios', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({
      postId,
      autorId: getCurrentUserId(),
      contenido: texto
    })
  });
};
```

## 🎨 Sugerencias de UI

### Vista de Posts
- Filtros por tipo (pestañas: Todos, Noticias, Avisos, Chat)
- Badges de colores según tipo
- Indicador de estado (ACTIVO/CERRADO)
- Botón "Contactar" solo en avisos y si no eres el autor

### Vista de Aviso
- Mostrar deporte y ubicación prominentemente
- Botón grande "Contactar" con modal de confirmación
- Después de contactar, mostrar teléfono en modal
- Opción de copiar teléfono al portapapeles

### Comentarios
- Diseño anidado para respuestas
- Notificación visual al recibir respuesta
- Mención de usuarios con @

## 🧪 Testing

Los endpoints están listos para probar con:
- Postman
- cURL
- Swagger (si lo tienes configurado)

Ejemplo cURL:
```bash
# Crear post
curl -X POST http://localhost:8080/api/posts \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Busco equipo",
    "contenido": "Jugador disponible",
    "autorId": 1,
    "tipoPost": "BUSCO_EQUIPO"
  }'

# Listar posts
curl http://localhost:8080/api/posts

# Contactar aviso
curl -X POST "http://localhost:8080/api/contactos/aviso/{UUID}?usuarioId=2"
```

## 📈 Próximas Mejoras (Opcionales)

- [ ] Sistema de likes/reacciones en posts
- [ ] Imágenes en posts (similar a tournaments)
- [ ] Búsqueda por texto completo
- [ ] Moderación de contenido
- [ ] Reportar posts inapropiados
- [ ] Chat privado después de contactar
- [ ] Sistema de valoraciones entre usuarios
- [ ] Estadísticas de avisos (vistas, contactos)

---

✅ **Sistema completamente implementado y listo para usar**
