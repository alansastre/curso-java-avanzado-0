
# Apache Kafka

Instalación con Docker Compose: https://github.com/confluentinc/cp-all-in-one

Ver docs:

https://kafka.apache.org/documentation/#gettingStarted


./kafka-up.sh para levantar kafka

./kafka-down.sh para parar kafka

Entrar en http://localhost:9021

IntelliJ IDEA Ultimate: instalar el plugin Big Data Tools - Kafka


## USAR KAFKA DESDE JAVA:

Desde Java sin Spring:

* https://mvnrepository.com/artifact/org.apache.kafka/kafka-clients
* https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams

Con Spring:

* https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka
* https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka-test
* https://mvnrepository.com/artifact/org.apache.kafka/kafka-streams

Con Spring Cloud:

* https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-stream
* https://mvnrepository.com/artifact/org.springframework.kafka/spring-kafka
* https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-stream-binder-kafka
* https://mvnrepository.com/artifact/org.springframework.cloud/spring-cloud-stream-binder-kafka-streams

Con Reactor:

* https://mvnrepository.com/artifact/io.projectreactor.kafka/reactor-kafka