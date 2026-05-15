# Firebase para StyleMatchAI

La app ya esta preparada para que existan dos tipos de usuario:

- `admin`: tiene `isAdmin: true` en Realtime Database y entra directamente al CRUD.
- `usuario normal`: no tiene `isAdmin` o lo tiene en `false`, y entra al catalogo normal.
- Las imagenes elegidas desde la galeria se suben gratis a ImgBB y la app guarda la URL en Realtime Database.

## 1. Activa Authentication

En Firebase Console:

1. Entra en `Authentication`.
2. Pulsa `Comenzar` si todavia no esta activo.
3. Entra en `Sign-in method`.
4. Activa `Correo electronico/Contrasena`.
5. Activa `Google`.

El archivo de configuracion debe estar aqui:

```text
app/google-services.json
```

## 2. Crea los administradores

Hazlo en este orden, que es el mas facil:

1. En Firebase Console entra en `Authentication > Users`.
2. Pulsa `Add user`.
3. Crea el correo y contrasena del primer admin.
4. Copia el `User UID` que Firebase genera.
5. Repite si quieres un segundo admin.

Ejemplo:

```text
admin1@gmail.com -> UID: abc123
admin2@gmail.com -> UID: def456
```

## 3. Anade datos en Realtime Database

En la pantalla que me has ensenado estas en `Realtime Database > Datos`.

Tienes dos formas:

### Opcion rapida: importar JSON

1. Abre el menu de tres puntos de Realtime Database.
2. Pulsa `Importar JSON`.
3. Selecciona este archivo del proyecto:

```text
firebase_seed_stylematchia.json
```

Despues entra en `usuarios` y anade tus admins con su UID real.

### Opcion manual: crear nodos

Pulsa el `+` en la raiz de la base de datos y crea estos nodos:

```json
{
  "productos": {
    "hoodie_tech_fleece": {
      "id": "hoodie_tech_fleece",
      "nombre": "Hoodie Tech Fleece",
      "marca": "Nike",
      "descripcion": "Sudadera hoodie negra con capucha Nike Tech Fleece para un look comodo, moderno, casual y de invierno.",
      "precio": 89.99,
      "imagenUrl": "https://static.nike.com/a/images/t_PDP_1728_v1/f_auto,q_auto:eco/492a7364-6ea6-4a2d-b9f7-cd6d7747b5b0/M+NK+TF+HD+FZ+WR.png",
      "enlaceTienda": "https://www.nike.com/es/"
    }
  },
  "usuarios": {
    "PEGA_AQUI_EL_UID_DEL_ADMIN": {
      "uid": "PEGA_AQUI_EL_UID_DEL_ADMIN",
      "nombre": "Admin StyleMatch",
      "email": "admin@gmail.com",
      "isAdmin": true
    }
  },
  "favoritos": {},
  "recomendaciones": {}
}
```

Importante: cambia `PEGA_AQUI_EL_UID_DEL_ADMIN` por el UID real de `Authentication > Users`.

## 4. Campos de cada producto

Cada producto solo usa estos campos:

```json
{
  "id": "camiseta_basic",
  "nombre": "Camiseta Basic",
  "marca": "Pull&Bear",
  "descripcion": "Camiseta basica blanca barata, fresca y facil de combinar para verano.",
  "precio": 12.99,
  "imagenUrl": "https://...",
  "enlaceTienda": "https://..."
}
```

La IA y los filtros leen sobre todo `descripcion`. Por eso conviene escribir descripciones con palabras utiles como `camiseta`, `pantalon`, `zapatillas`, `negra`, `blanca`, `verano`, `elegante`, `casual`, `sport`, `barata`, etc.

## 5. Reglas recomendadas

Cuando ya tengas al menos un admin creado en `usuarios`, entra en `Realtime Database > Reglas` y pega:

```json
{
  "rules": {
    ".read": "auth != null",
    "productos": {
      ".read": "auth != null",
      ".write": "auth != null && root.child('usuarios').child(auth.uid).child('isAdmin').val() === true"
    },
    "usuarios": {
      "$uid": {
        ".read": "auth != null && auth.uid === $uid",
        "uid": {
          ".write": "auth != null && auth.uid === $uid"
        },
        "nombre": {
          ".write": "auth != null && auth.uid === $uid"
        },
        "email": {
          ".write": "auth != null && auth.uid === $uid"
        },
        "isAdmin": {
          ".write": "auth != null && root.child('usuarios').child(auth.uid).child('isAdmin').val() === true"
        }
      }
    },
    "favoritos": {
      "$uid": {
        ".read": "auth != null && auth.uid === $uid",
        ".write": "auth != null && auth.uid === $uid"
      }
    },
    "recomendaciones": {
      ".read": "auth != null",
      "$recomendacionId": {
        ".write": "auth != null && ((!data.exists() && newData.child('userId').val() === auth.uid) || root.child('usuarios').child(auth.uid).child('isAdmin').val() === true)"
      }
    }
  }
}
```

Si todavia no tienes admin, deja reglas de prueba solo mientras creas el primer admin. Despues pega estas reglas.

## 6. Imagenes con ImgBB

No hace falta activar Firebase Storage. La app usa ImgBB para subir imagenes desde la galeria y guardar la URL publica en `productos/{id}/imagenUrl`.

La API key esta en:

```text
InsertEditProductoActivity.java
```

Campo:

```java
IMGBB_API_KEY
```

## 7. Que pasa al iniciar sesion

- Si `usuarios/TU_UID/isAdmin` es `true`, la app abre `CRUD Productos`.
- Si no existe `isAdmin` o es `false`, la app abre `Inicio`.
- Cualquier usuario que se registre desde la app se guarda como usuario normal.
- Los favoritos se guardan en `favoritos/TU_UID`.
- Las sugerencias nuevas se guardan automaticamente en `recomendaciones`.
