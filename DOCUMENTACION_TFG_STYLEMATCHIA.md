# Documentacion TFG - StyleMatchIA

## 1. Definicion del proyecto

### 1.1 Idea de la aplicacion
StyleMatchIA es una aplicacion movil Android orientada al sector de la moda que permite consultar un catalogo de prendas, guardar favoritos, recibir recomendaciones personalizadas y enviar sugerencias de mejora. El proyecto incorpora un asistente de recomendacion basado en inteligencia artificial para ayudar al usuario a encontrar prendas acordes a sus gustos, estilo y presupuesto.

### 1.2 Problema real que resuelve
Uno de los problemas habituales en el comercio de moda digital es la sobrecarga de opciones. El usuario encuentra una gran cantidad de productos, pero no siempre tiene claro cuales se ajustan mejor a su estilo, a una ocasion concreta o a un limite de precio. Ademas, muchas aplicaciones muestran catalogos extensos sin una capa de personalizacion real.

StyleMatchIA resuelve este problema mediante:

- filtrado por categorias y busqueda textual;
- sistema de favoritos para guardar prendas de interes;
- recomendacion inteligente en funcion de la descripcion escrita por el usuario;
- canal de recomendaciones del usuario hacia el administrador para mejorar el catalogo.

### 1.3 Publico objetivo
La aplicacion esta orientada principalmente a usuarios jovenes y adultos familiarizados con las compras online y con interes en la moda urbana, casual y deportiva.

Perfil objetivo:

- edad aproximada entre 16 y 35 anos;
- usuarios que desean encontrar ropa de forma rapida y personalizada;
- personas acostumbradas al uso de aplicaciones moviles;
- usuarios que valoran la comodidad, la inspiracion de estilo y la organizacion de favoritos.

### 1.4 Escenario de uso
Un usuario accede a la aplicacion, se registra o inicia sesion y entra al catalogo principal. Desde ahi puede buscar prendas, filtrarlas por categoria, marcar productos como favoritos y consultar su perfil. Si no sabe exactamente que buscar, puede describir lo que necesita en lenguaje natural, por ejemplo: "quiero una sudadera negra casual por menos de 50 euros". El sistema analiza la peticion y devuelve los productos mas adecuados del catalogo. Ademas, el usuario puede enviar sugerencias de nuevas marcas o estilos para mejorar futuras incorporaciones.

### 1.5 Propuesta de valor
La propuesta de valor de StyleMatchIA consiste en unir catalogo de moda, personalizacion e inteligencia artificial en una sola aplicacion. No se limita a mostrar productos, sino que facilita el descubrimiento de prendas mediante recomendaciones interpretadas desde la intencion del usuario.

Los elementos diferenciales del proyecto son:

- recomendacion de productos a partir de texto libre;
- combinacion de IA externa con un sistema local de respaldo;
- gestion diferenciada entre usuario normal y administrador;
- sincronizacion con Firebase y almacenamiento local para mejorar disponibilidad.

### 1.6 Justificacion del proyecto
El proyecto esta justificado por la creciente importancia de la personalizacion en aplicaciones de comercio digital. Tambien responde a una necesidad academica y tecnica: desarrollar una solucion movil completa que integre autenticacion, persistencia local, base de datos remota, logica de negocio y consumo de servicios externos de IA.

Desde el punto de vista del TFG, StyleMatchIA permite demostrar competencias en:

- desarrollo de aplicaciones Android;
- diseno de interfaces moviles;
- modelado de datos;
- uso de Firebase Authentication y Realtime Database;
- persistencia local con Room;
- integracion de APIs externas;
- definicion de roles, permisos y flujos de usuario.

## 2. Analisis de mercado y competencia

### 2.1 Contexto del mercado
El mercado de aplicaciones de moda esta ampliamente consolidado. Existen plataformas de comercio electronico, aplicaciones de marcas concretas y soluciones con recomendaciones personalizadas. Sin embargo, muchas de ellas se centran en la venta masiva de productos y no en una experiencia guiada segun la intencion del usuario escrita en lenguaje natural.

### 2.2 Aplicaciones similares analizadas

#### Zalando
- Funcionalidades principales: catalogo, filtros, favoritos, compra online, recomendaciones basicas.
- Modelo de negocio: venta de productos y colaboracion con marcas.
- Puntos fuertes: gran variedad de catalogo, buena experiencia de usuario, marca consolidada.
- Puntos debiles: personalizacion limitada si el usuario no sabe usar bien los filtros.

#### SHEIN
- Funcionalidades principales: catalogo, favoritos, ofertas, recomendaciones comerciales.
- Modelo de negocio: venta directa de moda low cost.
- Puntos fuertes: variedad, precios competitivos, alto volumen de producto.
- Puntos debiles: exceso de informacion, saturacion visual, menor enfoque en calidad de recomendacion personalizada.

#### ASOS
- Funcionalidades principales: busqueda de moda, filtros, carrito, favoritos, sugerencias.
- Modelo de negocio: ecommerce multimarca.
- Puntos fuertes: publico joven, buena segmentacion visual, fuerte imagen de marca.
- Puntos debiles: recomendacion apoyada sobre todo en navegacion y categorias, no tanto en lenguaje natural.

### 2.3 Tabla comparativa

| Aplicacion | Catalogo | Favoritos | Recomendacion IA | Roles admin | Sugerencias usuario |
|---|---|---|---|---|---|
| Zalando | Si | Si | Parcial | No visible al usuario | No |
| SHEIN | Si | Si | Parcial | No visible al usuario | No |
| ASOS | Si | Si | Parcial | No visible al usuario | No |
| StyleMatchIA | Si | Si | Si | Si | Si |

### 2.4 Diferenciacion del proyecto
StyleMatchIA se diferencia porque no se plantea solo como una tienda o escaparate digital, sino como una aplicacion experimental de recomendacion de moda con una estructura tecnica completa. El valor diferencial esta en interpretar la necesidad del usuario mediante texto libre y en ofrecer una arquitectura donde el administrador puede gestionar el catalogo y las sugerencias recibidas.

### 2.5 Alcance del proyecto

Incluye:

- registro e inicio de sesion por correo y Google;
- deteccion de rol administrador o usuario normal;
- catalogo de productos;
- detalle de producto;
- favoritos por usuario;
- asistente de recomendacion con IA;
- fallback local cuando la IA externa no esta disponible;
- envio de recomendaciones o sugerencias por parte del usuario;
- gestion administrativa de productos y sugerencias.

No incluye:

- pasarela de pago;
- carrito de compra;
- gestion de stock real;
- notificaciones push;
- panel web administrativo;
- analitica avanzada de comportamiento.

## 3. Requisitos del sistema

### 3.1 Requisitos funcionales

1. El sistema debe permitir al usuario registrarse con correo y contrasena.
2. El sistema debe permitir iniciar sesion con correo y con Google.
3. El sistema debe almacenar el perfil basico del usuario en Firebase.
4. El sistema debe distinguir entre usuarios administradores y usuarios normales.
5. El usuario normal debe poder consultar el catalogo de productos.
6. El usuario debe poder buscar productos por texto.
7. El usuario debe poder filtrar productos por categorias.
8. El usuario debe poder ver el detalle de cada producto.
9. El usuario debe poder marcar y desmarcar productos favoritos.
10. El sistema debe guardar los favoritos por usuario autenticado.
11. El usuario debe poder solicitar recomendaciones escribiendo una descripcion libre.
12. El sistema debe intentar obtener recomendaciones mediante OpenRouter.
13. Si OpenRouter no esta disponible, el sistema debe generar recomendaciones mediante logica local.
14. El usuario debe poder enviar sugerencias indicando marca, estilo y mensaje.
15. El administrador debe poder insertar, editar y eliminar productos.
16. El administrador debe poder gestionar el estado de las recomendaciones recibidas.
17. El usuario debe poder consultar y actualizar su foto de perfil.

### 3.2 Requisitos no funcionales

- Usabilidad: la interfaz debe ser clara, visual y apta para dispositivos moviles.
- Rendimiento: la carga del catalogo debe ser fluida incluso con acceso remoto a Firebase.
- Disponibilidad: la app debe seguir mostrando datos locales aunque falle temporalmente la sincronizacion remota.
- Seguridad: solo usuarios autenticados pueden acceder a los datos; solo administradores pueden modificar productos y gestionar recomendaciones.
- Mantenibilidad: el codigo se organiza por capas de datos, logica, adaptadores e interfaces.
- Escalabilidad: la estructura JSON de Firebase evita anidamiento excesivo y facilita futuras ampliaciones.
- Compatibilidad: la aplicacion se ejecuta desde Android 7.0 en adelante, al utilizar minSdk 24.

### 3.3 Requisitos de entorno

#### Hardware minimo
- dispositivo Android o emulador compatible;
- conexion a Internet para autenticacion, sincronizacion y recomendaciones externas.

#### Software necesario
- Android Studio;
- SDK de Android;
- Firebase Authentication;
- Firebase Realtime Database;
- cuenta de Google para el inicio de sesion social;
- clave de OpenRouter para la recomendacion externa;
- clave de ImgBB para subida de imagenes.

#### Restricciones tecnicas
- el proyecto esta desarrollado para Android nativo;
- la persistencia local se limita a productos mediante Room;
- la recomendacion IA depende de disponibilidad de red y de una API externa;
- el control de roles depende del campo `isAdmin` almacenado en Firebase.

## 4. Arquitectura y tecnologias

### 4.1 Plataforma
La aplicacion se desarrolla para la plataforma Android, orientada a smartphone. Se trata de una solucion movil nativa, lo que permite aprovechar componentes propios del sistema, integracion con autenticacion de Google y una experiencia de usuario optimizada para dispositivos Android.

### 4.2 Lenguajes y herramientas

- Java como lenguaje principal del proyecto.
- Kotlin DSL en archivos Gradle.
- XML para los layouts de interfaz.
- Android Studio como entorno de desarrollo.
- Git como sistema de control de versiones.

### 4.3 Arquitectura aplicada
La arquitectura del proyecto sigue una separacion por capas cercana a un patron MVC/MVVM simplificado:

- capa de presentacion: Activities, adapters y layouts XML;
- capa de datos: repositorios Firebase, repositorio Room y repositorio unificado de productos;
- capa de logica: clases como `ProductMatcher` y `AiRecommender`;
- capa de modelo: clases `Producto` y `Recomendacion`.

No se aplica MVVM puro con ViewModel y LiveData, pero si existe una organizacion modular que separa interfaz, acceso a datos y logica del dominio.

### 4.4 Firebase
Firebase es una parte central del proyecto:

- Firebase Authentication para registro e inicio de sesion con correo o Google.
- Firebase Realtime Database para usuarios, productos, favoritos y recomendaciones.

La eleccion de Firebase se justifica por:

- facilidad de integracion con Android;
- sincronizacion en tiempo real;
- reduccion del trabajo de backend;
- rapidez para construir un prototipo funcional y escalable.

### 4.5 Base de datos local
Se utiliza Room como base de datos local para almacenar productos. Esta decision mejora la resiliencia del sistema, ya que el usuario puede seguir consultando informacion cargada previamente aunque exista un fallo puntual de conexion o del servicio remoto.

El repositorio de productos realiza:

- lectura local inicial;
- escucha remota en Firebase;
- actualizacion de Room cuando llegan cambios;
- uso de Room como respaldo si falla la sincronizacion remota.

### 4.6 Servicio de recomendacion IA
La aplicacion incorpora dos niveles de recomendacion:

- recomendacion externa con OpenRouter;
- recomendacion local mediante logica basada en palabras clave.

Esto aporta robustez al sistema. Si la API de IA no esta configurada o falla la conexion, el usuario sigue recibiendo una respuesta funcional. Esta doble estrategia constituye una de las decisiones tecnicas mas importantes del proyecto.

### 4.7 Librerias utilizadas

- Firebase Auth;
- Firebase Realtime Database;
- Google Play Services Auth;
- Room;
- Glide;
- OkHttp.

### 4.8 Justificacion tecnica de decisiones

- Android nativo: adecuado para un TFG centrado en desarrollo movil real.
- Java: lenguaje ampliamente conocido y valido para demostrar bases solidas.
- Firebase: reduce complejidad de backend y permite un desarrollo iterativo rapido.
- Room: aporta persistencia local estructurada y mejora experiencia de uso.
- OpenRouter: permite incorporar IA generativa con una API flexible.
- Glide: simplifica la carga eficiente de imagenes remotas.
- OkHttp: ofrece una forma robusta de consumir APIs externas.

## 5. Estudio de viabilidad y presupuesto

### 5.1 Viabilidad tecnica
El proyecto es tecnicamente viable porque utiliza tecnologias maduras, bien documentadas y compatibles entre si. Android Studio, Firebase, Room y OkHttp forman un ecosistema estable para construir una aplicacion movil con autenticacion, base de datos remota y consumo de APIs externas.

### 5.2 Viabilidad economica
El coste inicial del proyecto es bajo, ya que gran parte de las herramientas usadas disponen de planes gratuitos o de uso academico.

Estimacion orientativa:

- Android Studio: 0 euros.
- Firebase Spark Plan: 0 euros en fase academica o prototipo.
- Git y GitHub: 0 euros en uso basico.
- OpenRouter: coste variable segun modelo y volumen de peticiones.
- ImgBB: coste 0 o bajo para uso limitado.

### 5.3 Recursos necesarios

- 1 desarrollador.
- 1 equipo de desarrollo con Android Studio.
- 1 cuenta Firebase.
- 1 cuenta OpenRouter.
- 1 cuenta ImgBB.
- tiempo para analisis, desarrollo, pruebas y documentacion.

### 5.4 Tiempo estimado
Una planificacion razonable del proyecto puede distribuirse asi:

- analisis y definicion: 1 a 2 semanas;
- diseno tecnico y mockups: 1 semana;
- desarrollo base de autenticacion y catalogo: 2 semanas;
- desarrollo de favoritos, perfil y CRUD: 2 semanas;
- integracion IA y recomendaciones: 1 a 2 semanas;
- pruebas, correcciones y documentacion final: 1 a 2 semanas.

### 5.5 Analisis de riesgos

- dependencia de servicios externos;
- errores de configuracion en Firebase;
- indisponibilidad de la API de OpenRouter;
- limitaciones de tiempo propias del TFG;
- riesgo de crecimiento desordenado del catalogo si no se mantiene una estructura consistente en los datos.

### 5.6 Presupuesto detallado
Para una primera fase academica el presupuesto es bajo. En un escenario profesional, el coste principal no estaria en infraestructura, sino en horas de desarrollo, pruebas y mantenimiento.

Ejemplo orientativo:

- analisis y diseno: 20 horas;
- desarrollo: 80 horas;
- pruebas y validacion: 20 horas;
- documentacion: 20 horas.

Si se estimara un coste de 20 euros por hora:

- coste total estimado: 140 horas x 20 euros = 2800 euros.

Infraestructura estimada:

- Firebase: 0 euros en prototipo; escalable segun uso real.
- OpenRouter: variable segun numero de consultas y modelo elegido.
- ImgBB: coste bajo o gratuito en fase de pruebas.

### 5.7 Plan de monetizacion
Aunque el proyecto nace con fin academico, podria monetizarse a futuro mediante:

- comision por redireccionamiento a tiendas asociadas;
- publicidad segmentada;
- version premium con recomendaciones mas avanzadas;
- acuerdos con marcas para destacar productos.

## 6. Planificacion del proyecto

### 6.1 Fases del desarrollo

1. Analisis del problema y definicion de objetivos.
2. Estudio de mercado y analisis de aplicaciones competidoras.
3. Definicion de requisitos funcionales y no funcionales.
4. Diseno de arquitectura, base de datos y mockups.
5. Implementacion del sistema de autenticacion.
6. Implementacion del catalogo y filtros.
7. Desarrollo del sistema de favoritos y perfil.
8. Desarrollo del panel de administracion.
9. Integracion del asistente IA y recomendacion local.
10. Pruebas, correccion de errores y documentacion final.

### 6.2 Hitos principales

- Hito 1: aplicacion base creada y ejecutable.
- Hito 2: autenticacion funcional.
- Hito 3: catalogo sincronizado con Firebase.
- Hito 4: gestion de favoritos y perfil.
- Hito 5: CRUD administrativo operativo.
- Hito 6: recomendacion con IA integrada.
- Hito 7: documentacion final y defensa.

### 6.3 Tareas

- configuracion del proyecto Android;
- conexion con Firebase;
- modelado de datos;
- desarrollo de interfaces;
- implementacion de roles;
- integracion de API IA;
- pruebas funcionales;
- elaboracion de memoria y presentacion.

### 6.4 Cronograma orientativo para Canva o Gantt

| Semana | Trabajo principal |
|---|---|
| 1 | Definicion del proyecto |
| 2 | Analisis de mercado y requisitos |
| 3 | Arquitectura, base de datos y diseno |
| 4 | Autenticacion y usuarios |
| 5 | Catalogo y detalle de producto |
| 6 | Favoritos y perfil |
| 7 | CRUD administrativo |
| 8 | Recomendaciones e IA |
| 9 | Pruebas y mejoras |
| 10 | Documentacion y defensa |

## 7. Analisis y diseno del sistema

### 7.1 Diagrama de casos de uso

#### Actores del sistema
- Usuario no autenticado.
- Usuario autenticado.
- Administrador.
- Servicio externo OpenRouter.
- Firebase.

#### Casos de uso principales del usuario
- registrarse;
- iniciar sesion;
- iniciar sesion con Google;
- consultar catalogo;
- buscar productos;
- filtrar por categoria;
- ver detalle de producto;
- guardar favoritos;
- eliminar favoritos;
- solicitar recomendacion IA;
- enviar sugerencia;
- consultar perfil;
- cambiar foto de perfil;
- cerrar sesion.

#### Casos de uso del administrador
- acceder al panel de administracion;
- insertar producto;
- editar producto;
- eliminar producto;
- consultar recomendaciones recibidas;
- marcar recomendacion como hecha o pendiente;
- eliminar recomendacion.

### 7.2 Diagrama de clases

#### Clases principales

`Producto`
- id
- nombre
- marca
- descripcion
- precio
- imagenUrl
- enlaceTienda

`Recomendacion`
- id
- userId
- userName
- userEmail
- marca
- estilo
- mensaje
- estado
- fecha

`ProductoRepository`
- observeProducts()
- saveProduct()
- deleteProduct()
- seedDemoProducts()

`FirebaseProductoRepository`
- addProductsListener()
- saveProduct()
- deleteProduct()

`FirebaseUserRepository`
- saveUser()
- loadUserProfile()
- updateUserProfile()
- checkIsAdmin()

`FirebaseFavoritosRepository`
- observeFavoritos()
- toggleFavorito()

`FirebaseRecomendacionRepository`
- observeRecommendations()
- saveRecommendation()
- updateStatus()
- deleteRecommendation()

`ProductMatcher`
- matchesCategory()
- matchesSearch()
- matchesRequestedAttributes()
- normalize()

`AiRecommender`
- recommend()

`OpenRouterRecommendationService`
- isConfigured()
- recommend()

### 7.3 Diseno de base de datos Firebase Realtime Database

La estructura de datos se ha disenado evitando un anidamiento excesivo. Cada nodo principal responde a una entidad concreta del sistema y se relaciona con otras a traves de identificadores.

#### Nodos principales

- `usuarios`
- `productos`
- `favoritos`
- `recomendaciones`

#### Estructura JSON orientativa

```json
{
  "usuarios": {
    "uid_usuario": {
      "uid": "uid_usuario",
      "nombre": "Vera",
      "email": "vera@gmail.com",
      "photoUrl": "https://...",
      "isAdmin": false
    }
  },
  "productos": {
    "hoodie_tech_fleece": {
      "id": "hoodie_tech_fleece",
      "nombre": "Hoodie Tech Fleece",
      "marca": "Nike",
      "descripcion": "Sudadera negra con capucha para un look casual y moderno",
      "precio": 89.99,
      "imagenUrl": "https://...",
      "enlaceTienda": "https://..."
    }
  },
  "favoritos": {
    "uid_usuario": {
      "hoodie_tech_fleece": true
    }
  },
  "recomendaciones": {
    "rec_001": {
      "id": "rec_001",
      "userId": "uid_usuario",
      "userName": "Vera",
      "userEmail": "vera@gmail.com",
      "marca": "Nike",
      "estilo": "casual",
      "mensaje": "Me gustaria ver mas sudaderas negras.",
      "estado": "pendiente",
      "fecha": 1740000000000
    }
  }
}
```

#### Justificacion del diseno

- `usuarios`: guarda identidad, nombre, correo, foto y rol.
- `productos`: almacena el catalogo central.
- `favoritos`: se organiza por UID para separar la informacion de cada usuario.
- `recomendaciones`: almacena sugerencias con trazabilidad del autor y estado de gestion.

La estructura es escalable y facilita reglas de seguridad basadas en usuario autenticado y rol administrador.

### 7.4 Base de datos local Room

Room se utiliza para persistir productos en el dispositivo. La tabla principal es `productos`, equivalente al modelo `Producto`. Esto permite:

- cargar datos rapidamente;
- mantener informacion disponible sin depender totalmente de red;
- sincronizar el contenido remoto en segundo plano.

### 7.5 Diseno de interfaz de usuario

Las pantallas principales del sistema son:

- pantalla de autenticacion: login, registro y acceso con Google;
- pantalla principal: catalogo, buscador y filtros por categoria;
- detalle de producto;
- pantalla de favoritos;
- asistente IA;
- pantalla de recomendaciones del usuario;
- perfil de usuario;
- panel de administracion;
- CRUD de productos;
- gestion administrativa de recomendaciones.

#### Flujo de navegacion

1. El usuario inicia sesion.
2. Si es administrador, entra al panel admin.
3. Si es usuario normal, entra al inicio.
4. Desde inicio puede navegar a IA, favoritos, recomendaciones y perfil.
5. Desde admin puede gestionar productos y recomendaciones.

#### Criterios de usabilidad

- navegacion simple mediante barra inferior;
- botones visibles para acciones principales;
- filtros directos por categorias frecuentes;
- mensajes de estado cuando no hay resultados o falta informacion.

## 8. Explicacion tecnica del funcionamiento real de StyleMatchIA

### 8.1 Flujo de autenticacion
El usuario puede iniciar sesion por correo o con Google. Tras autenticarse, la aplicacion guarda o actualiza sus datos en Firebase bajo el nodo `usuarios`. Despues consulta el campo `isAdmin` para decidir el destino:

- si `isAdmin` es `true`, abre el panel de administracion;
- si `isAdmin` es `false`, abre la pantalla principal del catalogo.

### 8.2 Flujo del catalogo
El catalogo se obtiene mediante un repositorio de productos que combina Room y Firebase. Primero carga datos locales y despues escucha cambios remotos en Firebase. Cuando recibe nuevos productos, sustituye la tabla local y refresca la interfaz.

### 8.3 Flujo de favoritos
Los favoritos se almacenan en Firebase bajo `favoritos/{uid}/{productId}`. Cada usuario ve solo sus favoritos. La pantalla de favoritos cruza la lista de ids favoritos con la lista global de productos para mostrar las prendas completas.

### 8.4 Flujo del asistente IA
El usuario escribe una peticion libre. Si OpenRouter esta configurado, la app envia el prompt junto al catalogo disponible y espera una respuesta en formato JSON con los ids recomendados. Si la respuesta falla o no existe configuracion, entra en accion un recomendador local basado en coincidencia de categorias, colores, palabras clave y limite de precio.

### 8.5 Flujo de recomendaciones del usuario
El usuario puede enviar sugerencias de nuevas marcas o estilos. Estas se guardan en Firebase con identificador, autor, mensaje, fecha y estado inicial `pendiente`. El administrador puede revisarlas, marcarlas como `hecha` o `pendiente`, o eliminarlas.

### 8.6 Flujo del perfil
La pantalla de perfil muestra nombre, correo y foto. La foto puede actualizarse eligiendo una imagen del dispositivo, que se sube a ImgBB. La URL resultante se guarda en el perfil de Firebase y tambien se refleja en el usuario autenticado.

## 9. Apartado listo para defensa oral

### 9.1 Resumen corto del proyecto
StyleMatchIA es una aplicacion Android de recomendacion de moda que combina catalogo digital, favoritos, gestion administrativa y un asistente inteligente capaz de interpretar las necesidades del usuario mediante lenguaje natural. El proyecto utiliza Firebase para autenticacion y sincronizacion de datos, Room para persistencia local y OpenRouter como capa externa de inteligencia artificial.

### 9.2 Que aporta el proyecto
El valor principal del proyecto es que no se limita a mostrar productos, sino que mejora la experiencia de descubrimiento de prendas. El usuario puede expresar lo que busca de forma natural y el sistema traduce esa necesidad en recomendaciones concretas. Ademas, la aplicacion contempla roles, seguridad basica, persistencia local y gestion de sugerencias, lo que la convierte en una solucion mas completa y realista para un TFG.

### 9.3 Fortalezas tecnicas que puedes destacar

- integracion real de servicios externos;
- arquitectura por capas facil de entender;
- uso combinado de persistencia local y remota;
- diferenciacion de usuarios y administradores;
- enfoque practico y aplicable a un contexto comercial real.

## 10. Guion para Canva

Si lo vas a presentar en Canva, puedes dividirlo en estas diapositivas:

1. Titulo del proyecto y nombre.
2. Problema que resuelve.
3. Objetivos del proyecto.
4. Publico objetivo.
5. Analisis de mercado y competencia.
6. Funcionalidades principales.
7. Requisitos funcionales y no funcionales.
8. Arquitectura y tecnologias.
9. Base de datos Firebase.
10. Mockups o capturas de pantallas.
11. Funcionamiento del asistente IA.
12. Panel administrador.
13. Viabilidad y presupuesto.
14. Cronograma del proyecto.
15. Conclusiones y mejoras futuras.

## 11. Posibles mejoras futuras

- incorporacion de carrito de compra;
- pasarela de pago;
- sistema de recomendaciones mas avanzado con historico de comportamiento;
- notificaciones push;
- panel web administrativo;
- analitica de productos mas vistos o favoritos;
- integracion con mas tiendas o marcas.
