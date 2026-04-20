# Interfaz grafica Swing

La consola original sigue disponible ejecutando:

`es.urjc.metprog.app.Main`

La nueva interfaz grafica se ejecuta en IntelliJ con:

`es.urjc.metprog.gui.GuiMain`

No requiere dependencias externas: usa Swing, incluido en el JDK. La GUI reutiliza `SistemaFacade`, por lo que las mismas reglas de dominio, validaciones y persistencia siguen centralizadas en el codigo existente.

## Flujo cubierto

- Registro e inicio de sesion de jugadores y operadores.
- Creacion completa de personajes: tipo, habilidad, equipo, modificadores y esbirros.
- Configuracion de equipo activo.
- Lanzamiento, validacion, aceptacion y rechazo de desafios.
- Visualizacion del resultado de combate por rondas.
- Gestion de jugadores por operador: reemplazar personaje, anadir equipo, modificadores, esbirros, bloquear y desbloquear.
- Ranking global, historial, movimientos de oro y notificaciones.

## Recomendacion para entregar

En IntelliJ, crea una configuracion de ejecucion nueva de tipo `Application`, usa el SDK Java 21 y selecciona como clase principal `es.urjc.metprog.gui.GuiMain`.
