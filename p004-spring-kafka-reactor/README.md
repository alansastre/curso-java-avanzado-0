
## REACTOR KAFKA

Hemos agregado la librería reactor-kafka que se usa con:

* KafkaSender
* KafkaReceiver

o con los envoltorios template de Spring:

* ReactiveKafkaProducerTemplate
* ReactiveKafkaConsumerTemplate

Requisito crear los @Bean para ambos template desde una clase de @Configuration.

