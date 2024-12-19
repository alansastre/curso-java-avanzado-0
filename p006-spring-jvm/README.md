
## HERRAMIENTAS MONITORIZACIÓN ESPECÍFICAS PARA JAVA JVM

* jconsole: C:\Users\XX\.jdks\temurin-23\bin\jconsole (Viene incluido en el JDK)
  * Básica, ligero

* "IntelliJ Profiler" de IntelliJ IDEA Ultimate
  * Básica intermedia, profiling de memoria, hilos, CPU en desarrollo, integrado en el IDE
  * Te indica memoria y tiempo en cada clase y método con avisos y gráficos en tiempo real

* VisualVM: https://visualvm.github.io/ (Se tiene que descargar)
  * Antes JVisualVM
  * Más completo

* JMC (Se tiene que descargar)
  * Usa para capturar datos JFR y JMC para interpretarlos analizarlos y visualizarlos
  * JFR: https://dev.java/learn/jvm/jfr/getting-started/
  * https://www.oracle.com/java/technologies/jdk-mission-control.html
  * MBeanServer se puede ver la monitorización de la aplicación en tiempo real
  * Flight Recorder se graba una sesión y luego se analiza, proporcionando gran nivel de detalle de todas las métricas. Ideal para grabar sesiones en producción y luego analizarlas.
  * Es el más completo de todos
  * Interfaz UI basada en Eclipse IDE

* Eclipse MAT
  * Específico para analizar la memoria y hacer head dump y encontrar memory leaks
  * https://github.com/eclipse-mat/mat
  * https://eclipse.dev/mat/download/
  * Interfaz UI basada en Eclipse IDE


* Apache JMeter (Se tiene que descargar)
  * https://jmeter.apache.org/
  * Herramienta de load y performance testing
  * Está creada en Java pero es para testing de cualquier aplicación web, base de datos, etc
  * Requiere JDK para ejecutarla: JAVA_HOME
  * Crear un Thread Group y lanzar peticiones concurrente al API Spring Boot y ver el performance
  * Comparar un test con hilos normales, y otro test con hilos virtuales (Loom JDK 21)
  * Se ejecuta desde terminal:
    * C:\Users\XX\Downloads\apache-jmeter-5.6.3\bin
  * Abrir el plan.jmx y ejecutar el test


* Spring Actuator + Prometheus + Grafana (normalmente se monta con docker compose)
* Logstash + Elasticsearch + Kibana (para logs, normalmente se monta con docker compose)

* Soluciones comerciales: NewRelic

## JVM OPTIONS

* Heap size: tamaño total de la memoria utilizada para almacenar objetos y datos en tiempo de ejecución.
  * -Xms tamaño inicial
  * -Xmx tamaño máximo
  * Ejemplo: -Xms1g -Xmx1g 
  * Si superamos el Heap size veremos: java.lang.OutOfMemoryError: Java heap space

* Used Heap
  * memoria del heap que está en uso

* Metaspace:
  * Memoria separada del Heap (a partir Java 8) para metadatos de clases, clases cargadas, métodos, etc
  * -XX:MaxMetaspaceSize
  * Uso: -XX:MaxMetaspaceSize=512m

Garbage Collector:

Java 23: Usa G1GC


* G1GC (activado por defecto)
  * grandes cantidades de memoria
  * pausas cortas 200ms, ideal para aplicaciones con muchos usuarios concurrentes
  * Equilibrio rendimiento y baja latencia
  * Modificar la latencia: -XX:MaxGCPauseMillis:100

* ZGC:
  * pausas muy cortas < 10ms y heaps muy grandes de hasta terabytes
  * Tiene consumo CPU más alto que G1GC
  * Respuestas en tiempo real, tipo juegos, sistemas financieros
  * -XX:+UseZGC

Ejemplo con todo:

java -Xms8g -Xmx8g -XX:MaxGCPauseMillis:100 -jar app.jar

./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Xms8g -Xmx8g -XX:MaxGCPauseMillis:100"

./gradlew bootRun --jvm-args="-Xms8g -Xmx8g -XX:MaxGCPauseMillis:100"

## APACHE JMETER

Prueba con hilos virtuales y sin hilos virtuales.

## HILOS VIRTUALES (PROJECT LOOM jdk 21)

Buscar video codemotion 24 comparativa.

Se ve que con virtual thread enabled no genera overhead y responde bien ~1s versus los ~5s de Spring web normal sin virtual threads.

* Hilos normales: hilos de sistema operativo (OS Threads) son gestionados por el sistema operativo, coste elevado a nivel de memoria y recursos porque tiene preasignar un stack de memoria.
* Hilos virtuales (Project Loom): hilos gestionados por la JVM en lugar del sistema operativo, permiten concurrencia más eficiente, especialmente muchas tareas IO.


En aplicaciones legacy, spring boot 2 puede haber problemas.

Recomendación: Spring Boot 3 con JDK 21 en adelante.

Repositorios de concurrencia, hilos:

* Concurrencia básica: hilos
  * https://github.com/certidevs/java-se-jakarta-ee/tree/main/11.%20Concurrencia/java-concurrency

* Concurrencia avanzada: Executors, Future, CompletableFuture
  * https://github.com/certidevs/java-se-jakarta-ee/blob/main/21.%20Concurrencia%20avanzada/java-concurrency-advanced

* Framework Collections de Java tiene colecciones concurrentes
  * https://github.com/certidevs/java-se-jakarta-ee/tree/main/12.%20Colecciones%20concurrentes/java-concurrency-collections

* Paralelización: https://github.com/certidevs/java-se-jakarta-ee/tree/main/22.%20Paralelizaci%C3%B3n/java-parallelism

En Java:

* Thread
* Runnable (Interfaz funcional)
* Callable (Interfaz funcional)
* Executors y  ExecutorService
* Future y ScheduledFuture (JDK 5)
* CompletableFuture (JDK 8)
* Flow (JDK 9)
* RxJava (2012)
* Reactor (2015)

En Spring Web, sin webflux, lo habitual es trabajar con lo siguiente:

* @Configuration @Bean para crear un ExecutorService
* @Async
* CompletableFuture

En Spring WebFlux, lo habitual es trabajar con Reactor:
* Mono
* Flux
* .subscribeOn



## GRAALVM 

Spring Native

https://github.com/certidevs/spring/tree/main/097.%20Ejecuta%20tu%20aplicaci%C3%B3n%20Spring%20Boot%20con%20im%C3%A1genes%20nativas%20de%20GraalVM/spring-native


En JDK 23, al seleccionar el starter: GRAALVM NATIVE SUPPORT nos añade el plugin al pom:

```xml
<plugin>
  <groupId>org.graalvm.buildtools</groupId>
  <artifactId>native-maven-plugin</artifactId>
</plugin>
```

Tarda más en compilar y preparar el empaquetado, pero luego el arranque lo hace en milisegundos.

https://github.com/graalvm/graalvm-ce-builds/releases

Alternativa a Spring Boot: https://code.quarkus.io/