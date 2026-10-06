# Guía 11 - RetrofitCrudApp

CRUD completo de alumnos y profesores. Abrir en Android Studio, sincronizar y ejecutar app. Pulsar + para crear; tocar una tarjeta para modificar o eliminar. Las eliminaciones piden confirmación.

Se conserva la API de la guía. Sus registros profesor contienen edades de texto: la app muestra todos esos registros con "Edad no disponible" y permite introducir una edad numérica al editarlos. No inventa edades ni cambia automáticamente el servidor.

Ambos listados muestran carga, estado vacío, errores y botón Reintentar. Los formularios validan los datos antes de guardar. Las escrituras envían edades numéricas; las lecturas aceptan números y cadenas numéricas. Un ID inválido se rechaza para evitar operar sobre otro registro.

Pruebas: gradlew.bat assembleDebug testDebugUnitTest lintDebug. ApiContractTest valida CRUD, cuerpos JSON, errores HTTP, edades incompatibles y rechazo de IDs inválidos.

El avatar es un equivalente vectorial a la imagen del aula virtual. La errata PUT al eliminar se corrigió a DELETE.
