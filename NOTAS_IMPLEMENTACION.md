# Notas de Implementacion

## Patrones reflejados en codigo

- `Factory Method`: `DirectorPersonaje.java` + factorias concretas `VampiroFactory`, `LicantropoFactory`, `CazadorFactory`.
- `Builder`: `PersonajeBuilder` y sus variantes, usados por `DirectorPersonaje`.
- `Singleton`: `BaseDeDatos`.
- `Composite`: `UnidadControlable`, `GrupoEsbirros`, `EsbirroDemonio` y el resto de esbirros.
- `Decorator`: `EquipoDecorador`, `ArmaConDefensa`, `ArmaduraConAtaque`.
- `Facade`: `SistemaFacade` y `FachadaCombate`.
- `Observer`: `ObservadorNotificacion`, `PublicadorNotificaciones`, `Usuario`.
- `Strategy`: `CalculadorPotencial` y sus implementaciones en `Combate.java`.
- `State`: `EstadoDesafio` y `EstadoPersonaje`.
- `Command`: `ComandoCombate`, `CalcularPotencialesCommand`, `ResolverTiradasCommand`, `AplicarDanioCommand`.

## Decisiones tomadas

- La logica de negocio se ha separado de la consola para cumplir el requisito de independencia de interfaz.
- La persistencia se resuelve con serializacion Java estandar para no depender de librerias externas.
- Tras un combate, la salud y recursos temporales de combate se restauran para que la derrota no sea permanente.
- La sangre del vampiro si se mantiene entre combates, porque es el unico recurso que el enunciado describe como acumulado.
- La penalizacion por rechazar un desafio se transfiere al desafiante.
- El ranking se ordena por oro actual, y en caso de empate por victorias y nick.
- Para evitar inconsistencias, un usuario no puede iniciar ni recibir nuevos desafios si ya participa en uno no resuelto.
- Al validar un desafio, el operador introduce manualmente los modificadores presentes.

## Ejecucion

- Abrir la carpeta del proyecto en IntelliJ.
- Configurar `Java 21`.
- Ejecutar `es.urjc.metprog.app.Main`.
