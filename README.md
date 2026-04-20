# MetProg Combate Fantastico - Fase de pruebas

Repositorio de la fase de pruebas de la practica **Combate Fantastico** para la asignatura **Metodologia de la Programacion**.

Esta entrega parte del proyecto de codificacion ya implementado y anade la ultima fase solicitada por el enunciado: pruebas, evidencias y memoria de pruebas.

## Resumen de la entrega

La fase de pruebas se ha preparado sin modificar el proyecto original. El contenido de este repositorio incluye:

- Codigo completo de la aplicacion Java.
- Suite de pruebas offline en `src/test/java/es/urjc/metprog/tests`.
- Verificador funcional de regresion en `src/test/java/es/urjc/metprog/verification`.
- Scripts para compilar y ejecutar las pruebas sin descargar dependencias.
- Documento de pruebas en PDF.
- Matriz de trazabilidad y evidencia de ejecucion.

Resultado validado:

| Bloque | Resultado |
| --- | --- |
| Suite nueva `SuitePruebas` | 15/15 casos correctos |
| `VerificationRunner fresh` | Correcto |
| `VerificationRunner persistence-check` | Correcto |
| Comprobaciones positivas registradas | 53 |
| Fallos detectados | 0 |

## Requisitos

- Windows.
- Java JDK 21.
- IntelliJ IDEA, recomendado para abrir el proyecto.
- PowerShell para ejecutar el script de pruebas.

No se requiere descargar ningun plugin ni dependencia externa para ejecutar las pruebas offline.

## Como ejecutar las pruebas

Desde la raiz del proyecto:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\run-pruebas.ps1
```

Tambien se puede ejecutar con el lanzador de Windows:

```bat
scripts\run-pruebas.cmd
```

El script realiza automaticamente estos pasos:

1. Localiza un JDK 21.
2. Compila `src/main/java` y `src/test/java`.
3. Ejecuta `es.urjc.metprog.tests.SuitePruebas`.
4. Ejecuta `es.urjc.metprog.verification.VerificationRunner fresh`.
5. Ejecuta `es.urjc.metprog.verification.VerificationRunner persistence-check`.

## Ubicacion de las pruebas

Suite principal:

```text
src/test/java/es/urjc/metprog/tests/SuitePruebas.java
```

Casos de prueba:

```text
src/test/java/es/urjc/metprog/tests/ValidacionesTest.java
src/test/java/es/urjc/metprog/tests/PersonajeEquipoEsbirrosTest.java
src/test/java/es/urjc/metprog/tests/DesafioEstadoTest.java
src/test/java/es/urjc/metprog/tests/RankingTest.java
src/test/java/es/urjc/metprog/tests/CombateTest.java
src/test/java/es/urjc/metprog/tests/SistemaFacadeFlujosTest.java
```

Soporte de pruebas:

```text
src/test/java/es/urjc/metprog/tests/TestSupport.java
src/test/java/es/urjc/metprog/tests/TestFixtures.java
```

Verificador funcional de regresion:

```text
src/test/java/es/urjc/metprog/verification/VerificationRunner.java
```

## Documentacion de la fase de pruebas

Documento principal:

```text
docs/Memoria_Pruebas_Combate_Fantastico_G1.pdf
```

Version editable:

```text
docs/Memoria_Pruebas_Combate_Fantastico_G1.md
```

Matriz de trazabilidad:

```text
docs/Matriz_Trazabilidad_Pruebas.csv
```

Evidencia de ejecucion:

```text
docs/evidencias/resultado-pruebas-offline.txt
```

## Estrategia de pruebas

La estrategia combina:

- Pruebas unitarias de dominio.
- Pruebas de caja negra con entradas validas e invalidas.
- Pruebas de caja blanca sobre transiciones de estado, dano, penalizaciones y persistencia.
- Pruebas de integracion a traves de `SistemaFacade`.
- Pruebas de regresion con `VerificationRunner`.

La suite nueva no usa JUnit ni Maven para ejecutarse, de forma intencionada, para que la defensa pueda hacerse sin conexion ni descargas de dependencias.

## Casos cubiertos

La bateria cubre, entre otros puntos:

- Validacion de campos obligatorios y rangos.
- Password de usuarios y formato de registro.
- Oro no negativo.
- Reglas de armas de una mano y dos manos.
- Decoradores de equipo.
- Esbirros y absorcion de dano.
- Restriccion de vampiros con esbirros humanos.
- Modificadores de fortalezas y debilidades.
- Estados de desafio.
- Penalizacion por rechazo.
- Ranking global.
- Combate por rondas.
- Registro, login y control de roles.
- Regla de las 24 horas.
- Persistencia basica.

## Estructura del proyecto

```text
src/main/java
  Codigo de la aplicacion.

src/test/java/es/urjc/metprog/tests
  Suite nueva de pruebas offline.

src/test/java/es/urjc/metprog/verification
  Verificador funcional de regresion.

scripts
  Scripts para ejecutar la fase de pruebas.

docs
  Memoria, matriz de trazabilidad y evidencias.
```

## Ejecucion de la aplicacion

La aplicacion principal se conserva para poder probar manualmente el sistema:

```text
src/main/java/es/urjc/metprog/app/Main.java
```

La interfaz grafica Swing se conserva en:

```text
src/main/java/es/urjc/metprog/gui/GuiMain.java
```

## Notas sobre persistencia

La aplicacion genera datos en la carpeta `data`.

Estos ficheros son de ejecucion local y no forman parte de la entrega versionada:

```text
data/metprog-combate.dat
data/metprog-combate.txt
```

Por eso estan excluidos mediante `.gitignore`.

## Entrega

Este repositorio representa la fase de pruebas completa:

- Codigo probado.
- Tests implementados.
- Scripts de ejecucion.
- Evidencia de resultado.
- Memoria final de pruebas en PDF.

Repositorio objetivo:

```text
https://github.com/ahmed234fgtv/metprog-combate-fantastico-pruebas
```
