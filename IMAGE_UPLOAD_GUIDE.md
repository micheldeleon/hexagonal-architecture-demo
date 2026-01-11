# Guía de Configuración de Imágenes con Supabase Storage

## ✅ Implementación Completada

Se ha implementado la funcionalidad completa para permitir que usuarios y torneos tengan imágenes usando Supabase Storage.

## 📋 Cambios Realizados

### 1. Modelos de Dominio
- ✅ `User.java`: Agregado campo `profileImageUrl`
- ✅ `Tournament.java`: Agregado campo `imageUrl`

### 2. Servicio de Carga
- ✅ Creado `ImageUploadService.java` con métodos:
  - `uploadUserImage(MultipartFile)` - Sube imágenes de perfil
  - `uploadTournamentImage(MultipartFile)` - Sube imágenes de torneos
  - `deleteImage(String)` - Elimina imágenes

### 3. Entidades JPA y Mappers
- ✅ `UserEntity.java`: Campo `profileImageUrl`
- ✅ `TournamentJpaEntity.java`: Campo `imageUrl`
- ✅ Mappers actualizados para incluir URLs

### 4. DTOs
- ✅ `UserFullDto`: Campo `profileImageUrl`
- ✅ `CreateTournamentRequest`: Campo `imageUrl`
- ✅ `TournamentResponse`: Campo `imageUrl`

### 5. Endpoints REST

#### Usuario - Subir Imagen de Perfil
```
POST /api/users/{id}/profile-image
Content-Type: multipart/form-data

Parámetros:
- file: archivo de imagen (máx 5MB)

Respuesta:
{
  "message": "Imagen subida exitosamente",
  "imageUrl": "https://proyecto.supabase.co/storage/v1/object/public/profile-images/..."
}
```

#### Torneo - Subir Imagen
```
POST /api/tournaments/{id}/image
Content-Type: multipart/form-data

Parámetros:
- file: archivo de imagen (máx 5MB)

Respuesta:
{
  "message": "Imagen subida exitosamente",
  "imageUrl": "https://proyecto.supabase.co/storage/v1/object/public/tournament-images/..."
}
```

### 6. Configuración
- ✅ Agregada dependencia `spring-boot-starter-webflux` al `pom.xml`
- ✅ Configuración en `application.properties`
- ✅ Script SQL de migración `add_image_columns.sql`

## 🔧 Configuración Requerida

### 1. Variables de Entorno
Agregar al archivo `.env`:
```properties
SUPABASE_URL=https://tu-proyecto.supabase.co
SUPABASE_STORAGE_KEY=tu-service-role-key
```

### 2. Crear Buckets en Supabase

Ir a Supabase Dashboard > Storage y crear:

#### Bucket: `profile-images`
- **Visibilidad**: Público
- **Tamaño máximo**: 5MB
- **Tipos permitidos**: image/*

#### Bucket: `tournament-images`
- **Visibilidad**: Público
- **Tamaño máximo**: 5MB
- **Tipos permitidos**: image/*

**Políticas de Storage (RLS):**
```sql
-- Permitir INSERT público
CREATE POLICY "Allow public upload" 
ON storage.objects FOR INSERT 
TO public 
WITH CHECK (bucket_id = 'profile-images');

CREATE POLICY "Allow public upload tournaments" 
ON storage.objects FOR INSERT 
TO public 
WITH CHECK (bucket_id = 'tournament-images');

-- Permitir lectura pública
CREATE POLICY "Allow public read" 
ON storage.objects FOR SELECT 
TO public 
USING (bucket_id = 'profile-images');

CREATE POLICY "Allow public read tournaments" 
ON storage.objects FOR SELECT 
TO public 
USING (bucket_id = 'tournament-images');
```

### 3. Ejecutar Migración SQL
```bash
# Conectar a tu base de datos y ejecutar:
psql -h db.proyecto.supabase.co -U postgres -d postgres -f add_image_columns.sql
```

O ejecutar directamente en Supabase SQL Editor:
```sql
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_image_url TEXT;
ALTER TABLE tournaments ADD COLUMN IF NOT EXISTS image_url TEXT;
```

### 4. Instalar Dependencias Maven
```bash
./mvnw clean install
```

## 🧪 Pruebas

### Desde cURL:
```bash
# Subir imagen de perfil
curl -X POST http://localhost:8080/api/users/1/profile-image \
  -H "Authorization: Bearer <token>" \
  -F "file=@/ruta/imagen.jpg"

# Subir imagen de torneo
curl -X POST http://localhost:8080/api/tournaments/1/image \
  -H "Authorization: Bearer <token>" \
  -F "file=@/ruta/imagen.jpg"
```

### Desde JavaScript (Frontend):
```javascript
// Subir imagen de perfil
const uploadProfileImage = async (userId, file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`http://localhost:8080/api/users/${userId}/profile-image`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  
  const result = await response.json();
  console.log('Image URL:', result.imageUrl);
  return result.imageUrl;
};

// Subir imagen de torneo
const uploadTournamentImage = async (tournamentId, file) => {
  const formData = new FormData();
  formData.append('file', file);
  
  const response = await fetch(`http://localhost:8080/api/tournaments/${tournamentId}/image`, {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`
    },
    body: formData
  });
  
  const result = await response.json();
  return result.imageUrl;
};
```

### Ejemplo con Input de Archivo:
```html
<!-- HTML -->
<input type="file" id="profileImage" accept="image/*">
<button onclick="subirImagen()">Subir Imagen</button>

<script>
async function subirImagen() {
  const input = document.getElementById('profileImage');
  const file = input.files[0];
  
  if (!file) {
    alert('Selecciona una imagen');
    return;
  }
  
  const imageUrl = await uploadProfileImage(userId, file);
  console.log('Imagen subida:', imageUrl);
}
</script>
```

## 📝 Validaciones Implementadas

- ✅ Archivo no vacío
- ✅ Tipo de archivo debe ser imagen (image/*)
- ✅ Tamaño máximo: 5MB
- ✅ Nombres únicos con UUID
- ✅ Manejo de errores completo

## 🔒 Seguridad

- Las URLs generadas son públicas pero impredecibles (UUID)
- Para mayor seguridad, considera:
  - Implementar autenticación en endpoints
  - Validar permisos (usuario solo puede cambiar su propia imagen)
  - Rate limiting para prevenir abuso

## 📦 Estructura Final

```
demo/
├── src/main/java/.../
│   ├── core/
│   │   ├── application/service/
│   │   │   └── ImageUploadService.java ✨ NUEVO
│   │   └── domain/models/
│   │       ├── User.java (+ profileImageUrl)
│   │       └── Tournament.java (+ imageUrl)
│   ├── adapters/
│   │   ├── in/api/
│   │   │   ├── controllers/
│   │   │   │   ├── UserController.java (+ endpoint)
│   │   │   │   └── TournamentController.java (+ endpoint)
│   │   │   └── dto/
│   │   │       ├── UserFullDto.java (+ campo)
│   │   │       ├── CreateTournamentRequest.java (+ campo)
│   │   │       └── TournamentResponse.java (+ campo)
│   │   └── out/persistence/jpa/
│   │       ├── entities/
│   │       │   ├── UserEntity.java (+ campo)
│   │       │   └── TournamentJpaEntity.java (+ campo)
│   │       └── mappers/
│   │           ├── UserMapper.java (actualizado)
│   │           └── TournamentMapper.java (actualizado)
├── add_image_columns.sql ✨ NUEVO
└── pom.xml (+ webflux dependency)
```

## ✅ Próximos Pasos

1. Configurar las variables de entorno en `.env`
2. Crear los buckets en Supabase Dashboard
3. Ejecutar el script SQL de migración
4. Reiniciar la aplicación
5. Probar los endpoints desde Postman/Frontend

## 🐛 Troubleshooting

### Error: "supabase.url not found"
- Verifica que las variables estén en `.env`
- Reinicia la aplicación

### Error: "Bucket not found"
- Crea los buckets en Supabase Dashboard
- Verifica los nombres exactos

### Error: "403 Forbidden"
- Configura las políticas RLS en Supabase
- Verifica el `service-role-key`

### Error: "File too large"
- Máximo 5MB por archivo
- Redimensiona la imagen antes de subir
