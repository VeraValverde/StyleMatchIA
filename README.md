# StyleMatchIA

StyleMatchIA es una aplicacion movil Android nativa orientada al sector de la moda. Permite a los usuarios consultar un catalogo de prendas, guardar favoritos, recibir recomendaciones basadas en inteligencia artificial y enviar sugerencias de mejora. Ademas, incorpora un flujo de administracion para gestionar productos y revisar recomendaciones enviadas por los usuarios.

## Descripcion del proyecto

La idea principal del proyecto es mejorar la experiencia de descubrimiento de ropa dentro de un catalogo digital. En lugar de depender solo de filtros tradicionales, la app permite al usuario describir en lenguaje natural lo que busca, por ejemplo:

- `quiero una sudadera negra casual`
- `busco zapatillas deportivas por menos de 100 euros`

A partir de esa peticion, la app intenta obtener una recomendacion con IA mediante OpenRouter. Si la IA no esta disponible, utiliza una logica local de respaldo para seguir ofreciendo resultados.

## Objetivo

El objetivo de StyleMatchIA es combinar:

- catalogo de moda
- busqueda y filtrado
- favoritos por usuario
- recomendaciones con IA
- gestion de usuarios con roles
- administracion de productos y sugerencias

Todo ello dentro de una app Android nativa con persistencia local y sincronizacion remota.

## Funcionalidades principales

- Registro de usuarios con correo y contrasena
- Inicio de sesion con correo y con Google
- Deteccion de rol `usuario` o `admin`
- Visualizacion del catalogo de productos
- Filtrado por categorias
- Busqueda textual
- Vista de detalle de producto
- Sistema de favoritos por usuario
- Asistente de recomendacion con IA
- Sistema de respaldo local cuando falla la IA externa
- Envio de recomendaciones o sugerencias por parte del usuario
- Gestion administrativa de productos
- Gestion administrativa de recomendaciones recibidas
- Foto de perfil del usuario

## Tecnologias utilizadas

- `Java`: lenguaje principal de la aplicacion
- `XML`: diseno de interfaces
- `Android Studio`: entorno de desarrollo
- `Firebase Authentication`: autenticacion de usuarios
- `Firebase Realtime Database`: almacenamiento remoto de usuarios, productos, favoritos y recomendaciones
- `Room`: persistencia local del catalogo
- `OpenRouter`: recomendacion externa con IA
- `ImgBB`: alojamiento de imagenes y devolucion de URL publica
- `Glide`: carga y visualizacion de imagenes
- `OkHttp`: peticiones HTTP para OpenRouter e ImgBB
- `Gradle Kotlin DSL`: configuracion del proyecto

## Arquitectura del proyecto

El proyecto esta organizado por capas para separar responsabilidades y hacer el codigo mas mantenible:

- `ui/`: pantallas (`Activities`) y comportamiento visual
- `adapter/`: adaptadores de `RecyclerView`
- `model/`: clases de datos del dominio
- `data/local/`: acceso a Room
- `data/remote/`: acceso a Firebase y OpenRouter
- `data/repository/`: coordinacion entre datos locales y remotos
- `logic/`: reglas de filtrado y recomendacion local
- `res/`: layouts, estilos, iconos y recursos visuales

No sigue MVVM puro, pero si una separacion clara entre presentacion, acceso a datos y logica.

## Estructura del proyecto

```text
StyleMatchIA/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/example/stylematchia/
│       │   │   ├── MainActivity.java
│       │   │   ├── StyleMatchApplication.java
│       │   │   ├── adapter/
│       │   │   ├── data/
│       │   │   ├── logic/
│       │   │   ├── model/
│       │   │   └── ui/
│       │   └── res/
│       │       ├── drawable/
│       │       ├── layout/
│       │       ├── mipmap-*/
│       │       ├── values/
│       │       └── xml/
│       ├── androidTest/
│       └── test/
├── gradle/
├── build.gradle.kts
├── settings.gradle.kts
└── firebase_seed_stylematchia.json
```

## Flujo general de la app

### 1. Autenticacion

La app comienza en `MainActivity`, donde el usuario puede:

- iniciar sesion con correo
- registrarse
- iniciar sesion con Google

Despues del login, la app guarda o actualiza el usuario en Firebase y comprueba si el campo `isAdmin` es `true`.

- Si es `true`, entra en el panel de administrador
- Si es `false`, entra en la pantalla principal del catalogo

### 2. Catalogo

La pantalla principal muestra los productos disponibles. El usuario puede:

- buscar por texto
- filtrar por categorias
- abrir el detalle del producto
- marcar o desmarcar favoritos

### 3. Recomendacion con IA

En el asistente IA, el usuario escribe una peticion en lenguaje natural. La app:

1. recoge el texto del usuario
2. envia la peticion y parte del catalogo a OpenRouter
3. recibe los identificadores de los productos recomendados
4. muestra las coincidencias en pantalla

Si OpenRouter no esta configurado o falla, la app usa `AiRecommender`, una logica local basada en palabras clave y precio.

### 4. Favoritos

Cada usuario guarda sus favoritos en Firebase bajo su propio `uid`. Esto permite mantener el estado personalizado por cuenta.

### 5. Recomendaciones del usuario

El usuario puede enviar sugerencias de marca, estilo o mejora. Estas se almacenan en Firebase con estado inicial `pendiente`.

### 6. Panel de administracion

El administrador tiene acceso a:

- CRUD de productos
- gestion de recomendaciones recibidas

## Base de datos y persistencia

### Firebase Realtime Database

Se usa para almacenar:

- `usuarios`
- `productos`
- `favoritos`
- `recomendaciones`

### Room

Room se utiliza para almacenar localmente el catalogo de productos. Su funcion principal es servir como respaldo local y mejorar la disponibilidad de datos cuando hay problemas de red o sincronizacion.

### Repositorio de productos

`ProductoRepository` coordina ambas fuentes:

- primero puede cargar datos locales
- despues escucha los cambios remotos en Firebase
- actualiza Room
- devuelve a la interfaz la lista actualizada

## Funcionamiento de la IA

La app tiene dos niveles de recomendacion:

### OpenRouter

Es la opcion principal. La app envia una peticion HTTP con:

- el texto introducido por el usuario
- una lista de productos del catalogo
- instrucciones para devolver una respuesta en formato JSON

La IA responde con:

- un resumen textual
- una lista de `product_ids`

### Recomendacion local

Si OpenRouter no responde o no esta configurado, se usa `AiRecommender`, que:

- detecta si la peticion tiene intencion relacionada con moda
- extrae palabras clave
- detecta precio maximo si el usuario lo indica
- compara esas palabras con la informacion de los productos

## Funcionamiento de ImgBB

Las imagenes no se guardan como archivo dentro de Firebase Realtime Database.

El flujo real es:

1. el usuario selecciona una imagen
2. la app la convierte y la sube a ImgBB mediante su API
3. ImgBB devuelve una URL publica
4. la app guarda esa URL en Firebase
5. posteriormente la app lee esa URL y muestra la imagen con Glide

En resumen:

- `ImgBB` almacena la imagen
- `Firebase` guarda el enlace de la imagen

## Pantallas principales

- `MainActivity`: login, registro y acceso con Google
- `HomeActivity`: catalogo principal
- `ProductDetailActivity`: detalle de producto
- `FavoritesActivity`: listado de favoritos
- `AiAssistantActivity`: recomendacion con IA
- `ProfileActivity`: perfil y foto del usuario
- `RecomendacionesActivity`: sugerencias del usuario
- `AdminHomeActivity`: menu principal de administracion
- `CrudProductosActivity`: gestion de productos
- `InsertEditProductoActivity`: insertar o editar producto
- `GestionRecomendacionesActivity`: gestion de sugerencias recibidas

## Configuracion del proyecto

### Requisitos previos

- Android Studio
- SDK de Android instalado
- Cuenta de Firebase
- Archivo `google-services.json`
- Clave de OpenRouter
- Conexion a Internet

### Firebase

Es necesario configurar:

- `Authentication`
- `Realtime Database`
- usuarios con rol `admin` mediante el campo `isAdmin`

El archivo de configuracion debe estar en:

```text
app/google-services.json
```

### OpenRouter

La clave de OpenRouter se obtiene desde `local.properties` mediante:

```properties
openrouter.api.key=TU_CLAVE
openrouter.model=TU_MODELO
```

Esa informacion se inyecta en `BuildConfig` desde `app/build.gradle.kts`.

### ImgBB

La app utiliza la API de ImgBB para subir imagenes y obtener una URL publica.

## Como ejecutar el proyecto

1. Clonar el repositorio
2. Abrir el proyecto en Android Studio
3. Añadir `app/google-services.json`
4. Configurar `local.properties` con la clave de OpenRouter si se quiere usar la IA externa
5. Sincronizar Gradle
6. Ejecutar la app en emulador o dispositivo fisico

## Pruebas

El proyecto incluye:

- una prueba unitaria de ejemplo
- una prueba instrumentada de ejemplo

Actualmente sirven como base de testing, pero no cubren toda la logica de negocio.

## Limitaciones actuales

- La clasificacion por categorias se basa en palabras clave, no en un campo de categoria explicito
- La recomendacion local es simple comparada con la IA externa
- No existe carrito de compra ni pasarela de pago
- No hay panel web administrativo
- La cobertura de tests es reducida

## Posibles mejoras futuras

- Añadir categoria explicita a cada producto
- Incorporar Firebase Storage en lugar de ImgBB
- Mejorar cobertura de pruebas
- Añadir filtros mas avanzados
- Implementar carrito de compra
- Añadir notificaciones push
- Evolucionar hacia una arquitectura MVVM con ViewModel

## Repositorio y control de versiones

Este proyecto se gestiona con Git y esta preparado para alojarse en GitHub como repositorio de la aplicacion.

Para una entrega academica, se recomienda:

- mantener un `README` completo
- usar ramas de funcionalidad cuando haya nuevas mejoras
- realizar commits frecuentes y con mensajes descriptivos

## Autor

Proyecto desarrollado como aplicacion Android de moda con recomendacion asistida por IA.
