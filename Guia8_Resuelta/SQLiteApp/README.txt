GUÍA 09 - PRÁCTICA Y DISCUSIÓN DE RESULTADOS

1. BUSCAR
Ingrese un código y pulse Buscar. Se cargan descripción, precio, cantidad y
categoría. Un código inexistente muestra un aviso y limpia los datos anteriores.

2. ACTUALIZAR Y ELIMINAR
Ambas operaciones comprueban el número de filas afectadas: solo anuncian éxito
si existe el producto. Se validan códigos enteros positivos, precio positivo y
cantidad entera no negativa. Eliminar solo requiere el código.
La primera categoría ya no se repite en el selector.

3. LOGIN Y REGISTRO
LoginActivity es la actividad MAIN/LAUNCHER del manifiesto. Crear una cuenta
abre RegistroActivity. Se solicitan Nick, contraseña y confirmación. Al registrar
se vuelve al login y las credenciales correctas permiten entrar a productos.
La tabla usuario de tienda.sqlite contiene idusuario, nick único y contrasena.
Las consultas usan parámetros. El modelo Usuario y los cursores se cierran.
La contraseña se guarda directamente para este ejercicio académico.

ACTUALIZACIÓN DE LA BASE
HelperDB usa versión 4 para admitir las versiones 1, 2 y 3 anteriores.
onUpgrade crea usuario si no existe, sin borrar productos ni categorías.

PRUEBA EN ANDROID
1. Crear una cuenta y comprobar rechazo de nick repetido.
2. Intentar entrar con contraseña incorrecta y después con la correcta.
3. Agregar un producto y anotar el código que muestra.
4. Buscarlo, cambiar precio/cantidad, actualizar y buscar de nuevo.
5. Eliminarlo y comprobar que Buscar y Eliminar informan que ya no existe.
6. Cerrar y abrir la app para comprobar que la cuenta permanece guardada.

VERIFICACIÓN
No se afirma ejecución en emulador: el entorno de Codex ha denegado acceso al
SDK/ADB. Las pruebas de SQLite de escritorio no sustituyen las pruebas Android.
