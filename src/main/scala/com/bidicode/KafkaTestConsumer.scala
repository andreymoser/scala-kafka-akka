package com.bidicode

import akka.Done
import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, Sink}
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}

import scala.concurrent.Future

class KafkaTestConsumer {

  implicit val system: ActorSystem = ActorSystem()
  implicit val context = system.dispatcher
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  def setup(): Unit = {
    val config = system.settings.config.getConfig("akka.kafka.consumer")
    val consumerSettings =
      ConsumerSettings(config, new StringDeserializer, new ByteArrayDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId("groupTest")
        .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")

    Consumer
      .committableSource(consumerSettings, Subscriptions.topics("testTopic"))
      .mapAsync(1) {
        msg => handler(msg.record.key, msg.record.value, msg).map(_ => msg.committableOffset)
      }
      .mapAsync(1){
        offset => offset.commitScaladsl()
      }
      .toMat(Sink.ignore)(Keep.both)
      .run()
  }

  private def handler(key: String, value: Array[Byte], msg: Any): Future[Done] = {
    println(s"received message: ${key} | value: ${value} | msg: ${msg}")
    Future.successful(Done)
  }

}
