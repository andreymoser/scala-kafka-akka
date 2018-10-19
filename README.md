# Kafka consumer boilerplate with Scala and Akka

## Dependencies

- Docker (docker compose version 2)
- JDK 8
- Scala 2.12.7

## Startup

Start kafka:

```docker-compose up -d``` 

Run consumer (Java 8):

```java com.bidicode.Main```

Find kafka `container id`:

```docker ps```

Replace `<id>` into the command by the `container id` found and send messages to the topic:

```
docker exec -it <id> /opt/kafka/bin/kafka-console-producer.sh --broker-list localhost:9092 --topic topicTest
message 1
message 2
```

