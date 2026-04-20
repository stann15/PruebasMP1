# Memoria de pruebas

**Combate Fantástico**  
Metodología de la Programación  
Grupo 1 | Fase de pruebas | 19 de abril de 2026

## Resumen ejecutivo

La fase de pruebas se ha realizado sobre una copia independiente del proyecto original para mantener intacta la entrega de codificación. El objetivo ha sido comprobar, con una batería reproducible, que las reglas principales del sistema funcionan correctamente: usuarios, personajes, equipo, esbirros, desafíos, combate, oro, ranking, notificaciones y persistencia.

La ejecución final ha sido satisfactoria:

| Indicador | Resultado |
| --- | --- |
| Casos de prueba nuevos | 15/15 correctos |
| Regresión funcional `fresh` | Correcta |
| Regresión de persistencia | Correcta |
| Comprobaciones positivas registradas | 53 |
| Fallos detectados | 0 |
| Descargas o plugins externos | Ninguno |

## Equipo de trabajo

| Integrante | Rol | Responsabilidad en la fase de pruebas |
| --- | --- | --- |
| Stanislaw Cherkhavskyy Pater | Analista funcional | Trazabilidad con requisitos y revisión de reglas de negocio. |
| Fernán Rama Hombreiro | Analista programador | Revisión técnica de unidades, estructura de pruebas y coherencia de la suite. |
| Raúl García Piedra | Ingeniero de desarrollo | Soporte de compilación, ejecución y revisión de incidencias. |
| Ahmed Ehab Ahmed | QA | Diseño de casos, ejecución, evidencias y preparación de la defensa. |

## 1. Objetivo

El enunciado de la práctica solicita desarrollar las pruebas unitarias necesarias para verificar el correcto funcionamiento del sistema y entregar un documento de pruebas en PDF. Esta memoria documenta la estrategia seguida, los casos implementados, la trazabilidad con los requisitos y los resultados obtenidos.

El alcance de la fase incluye:

- Validaciones de campos obligatorios, rangos y saldo de oro.
- Registro de usuarios, autenticación, roles y permisos.
- Construcción de personajes y recursos propios de vampiros, licántropos y cazadores.
- Reglas de equipo activo, armas de una mano y armas de dos manos.
- Esbirros, absorción de daño, restauración y restricción de humanos para vampiros.
- Modificadores de fortalezas y debilidades.
- Ciclo de vida de desafíos y validación por operador.
- Rechazo de desafíos, penalización económica y notificaciones.
- Regla de las 24 horas y bloqueo de usuarios.
- Ejecución de combate, rondas, eventos y resultado económico.
- Ranking global, movimientos de oro y persistencia.

## 2. Entorno de ejecución

| Elemento | Valor utilizado |
| --- | --- |
| Sistema operativo | Windows 11 |
| Lenguaje | Java |
| Versión de referencia | JDK 21 |
| JDK usado en la ejecución | JetBrains Runtime OpenJDK 21.0.10 |
| Carpeta del proyecto probado | `metprog-combate-fantastico-pruebas` |
| Script de ejecución | `scripts/run-pruebas.ps1` |
| Evidencia generada | `docs/evidencias/resultado-pruebas-offline.txt` |
| Dependencias descargadas | Ninguna |

## 3. Decisión técnica: pruebas offline

Durante esta fase se ha decidido no descargar plugins ni dependencias externas. Por ello, la suite nueva está implementada en Java puro, dentro de `src/test/java`, con clases terminadas en `Test` y un ejecutor propio llamado `SuitePruebas`.

Esta decisión tiene tres ventajas para la entrega:

- La ejecución es reproducible sin conexión a Internet.
- No depende de que Maven descargue artefactos desde repositorios externos.
- Se puede defender en clase con un JDK 21 y los scripts incluidos en el propio proyecto.

El proyecto conserva su estructura Maven, pero la evidencia final se ha generado con `javac` y `java`, sin añadir librerías externas.

## 4. Estrategia de pruebas

La estrategia se ha planteado siguiendo los conceptos vistos en el tema de pruebas software:

| Técnica | Aplicación en la práctica |
| --- | --- |
| Prueba unitaria | Validación de métodos y clases concretas del dominio. |
| Caja negra | Entradas válidas e inválidas con comparación contra resultados esperados. |
| Caja blanca | Ejercicio de ramas internas relevantes: estados, daño, penalización, restricciones y persistencia. |
| Prueba de integración | Flujos completos a través de `SistemaFacade`. |
| Regresión | Ejecución del `VerificationRunner` ya existente para asegurar que los flujos funcionales previos siguen pasando. |
| Aislamiento | Reseteo de persistencia antes de flujos de fachada para evitar contaminación entre casos. |

## 5. Estructura añadida al proyecto

| Archivo | Propósito |
| --- | --- |
| `src/test/java/es/urjc/metprog/tests/TestSupport.java` | Aserciones propias, control de excepciones esperadas y reseteo de persistencia. |
| `src/test/java/es/urjc/metprog/tests/TestFixtures.java` | Creación reutilizable de personajes y configuraciones de prueba. |
| `src/test/java/es/urjc/metprog/tests/ValidacionesTest.java` | Pruebas de campos obligatorios, rangos, password y oro. |
| `src/test/java/es/urjc/metprog/tests/PersonajeEquipoEsbirrosTest.java` | Pruebas de equipo, esbirros, daño, restauración y modificadores. |
| `src/test/java/es/urjc/metprog/tests/DesafioEstadoTest.java` | Pruebas del ciclo de vida de desafíos. |
| `src/test/java/es/urjc/metprog/tests/RankingTest.java` | Pruebas de ordenación y estadísticas del ranking. |
| `src/test/java/es/urjc/metprog/tests/CombateTest.java` | Pruebas del motor de combate. |
| `src/test/java/es/urjc/metprog/tests/SistemaFacadeFlujosTest.java` | Pruebas de flujos de aplicación y reglas transversales. |
| `src/test/java/es/urjc/metprog/tests/SuitePruebas.java` | Ejecutor de todos los casos sin dependencias externas. |
| `scripts/run-pruebas.ps1` | Compila y ejecuta la suite offline y las pruebas de regresión. |
| `scripts/run-pruebas.cmd` | Lanzador para Windows. |

## 6. Matriz de casos de prueba

| ID | Caso de prueba | Tipo | Objetivo | Resultado |
| --- | --- | --- | --- | --- |
| CP-01 | `ValidacionesTest.validaCamposObligatoriosYRangos` | Unitaria | Rechazar nombres vacíos y valores fuera de rango. | Correcto |
| CP-02 | `ValidacionesTest.jugadorValidaPasswordYRegistro` | Unitaria | Validar longitud de password y número de registro. | Correcto |
| CP-03 | `ValidacionesTest.oroNuncaPuedeSerNegativo` | Unitaria | Impedir que el oro quede por debajo de cero. | Correcto |
| CP-04 | `PersonajeEquipoEsbirrosTest.equipaDosArmasDeUnaManoYRechazaDosManosCombinada` | Unitaria | Verificar reglas de armas activas. | Correcto |
| CP-05 | `PersonajeEquipoEsbirrosTest.decoradoresDeEquipoSumanAtaqueYDefensa` | Unitaria | Comprobar decoradores de armas y armaduras. | Correcto |
| CP-06 | `PersonajeEquipoEsbirrosTest.esbirrosAbsorbenDanioAntesQueElPersonajeYSeRestauran` | Unitaria | Confirmar absorción de daño y restauración. | Correcto |
| CP-07 | `PersonajeEquipoEsbirrosTest.vampiroRechazaHumanosYCalculaModificadoresSinDistinguirMayusculas` | Unitaria | Probar restricción de vampiros y modificadores presentes. | Correcto |
| CP-08 | `DesafioEstadoTest.controlaTransicionesLegalesEInvalidas` | Caja blanca | Verificar transiciones válidas e inválidas del desafío. | Correcto |
| CP-09 | `DesafioEstadoTest.calculaPenalizacionYRechazoAdministrativo` | Unitaria | Calcular penalización y rechazo administrativo. | Correcto |
| CP-10 | `RankingTest.ordenaPorOroVictoriasYNickYCalculaDerrotas` | Unitaria | Ordenar ranking y calcular victorias/derrotas. | Correcto |
| CP-11 | `CombateTest.combateGeneraRondasEventosYResultadoCoherente` | Integración | Comprobar rondas, eventos y resultado económico. | Correcto |
| CP-12 | `SistemaFacadeFlujosTest.registraAutenticaYControlaRoles` | Integración | Probar registro, login y permisos por rol. | Correcto |
| CP-13 | `SistemaFacadeFlujosTest.desafioValidadoBloqueaAccionesYRechazoPenaliza` | Integración | Verificar desafío pendiente, rechazo, oro y notificaciones. | Correcto |
| CP-14 | `SistemaFacadeFlujosTest.validacionAplicaReglaDe24HorasYBloqueaDesafiante` | Integración | Aplicar regla de 24 horas y bloqueo. | Correcto |
| CP-15 | `SistemaFacadeFlujosTest.validaApuestaEquipoYPersistenciaBasica` | Integración | Validar apuestas, desafíos simultáneos y persistencia. | Correcto |
| REG-01 | `VerificationRunner fresh` | Regresión | Ejecutar flujo funcional completo desde cero. | Correcto |
| REG-02 | `VerificationRunner persistence-check` | Regresión | Comprobar recuperación de datos persistidos. | Correcto |

## 7. Procedimiento de ejecución

Desde la raíz de la copia de pruebas:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-pruebas.ps1
```

También puede ejecutarse con el lanzador de Windows:

```bat
scripts\run-pruebas.cmd
```

El script realiza automáticamente estos pasos:

1. Localiza un JDK 21.
2. Compila `src/main/java` y `src/test/java` en `target/manual-classes`.
3. Ejecuta `es.urjc.metprog.tests.SuitePruebas`.
4. Ejecuta `VerificationRunner fresh`.
5. Ejecuta `VerificationRunner persistence-check`.
6. Deja una evidencia textual de la ejecución en `docs/evidencias`.

## 8. Resultado final

La ejecución final se ha completado correctamente. El resultado resumido es:

| Bloque ejecutado | Resultado obtenido |
| --- | --- |
| Suite nueva `SuitePruebas` | 15 de 15 casos correctos |
| Verificador funcional `fresh` | Correcto |
| Verificador funcional `persistence-check` | Correcto |
| Total de comprobaciones positivas en evidencia | 53 |
| Fallos detectados | 0 |

La evidencia completa queda guardada en:

```text
docs/evidencias/resultado-pruebas-offline.txt
```

## 9. Incidencias y decisiones

No se han detectado defectos funcionales durante la ejecución documentada. La única limitación técnica relevante ha sido evitar descargas externas. Por ese motivo, se ha utilizado una suite autocontenida en lugar de JUnit.

Si en una futura entrega se permitiera añadir dependencias, la migración a JUnit 5 sería directa: bastaría con sustituir las aserciones de `TestSupport` por `org.junit.jupiter.api.Assertions` y anotar cada caso con `@Test`.

## 10. Conclusión

La fase de pruebas queda implementada, ejecutada y documentada. La batería cubre reglas unitarias del dominio, integración de la fachada, flujos funcionales completos, persistencia y regresión. Todas las pruebas han finalizado correctamente y la entrega queda preparada para defensa con evidencia reproducible, sin modificar el proyecto original y sin descargar ningún plugin.
