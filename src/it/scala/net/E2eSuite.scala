package net

import cats.implicits._
import cats.effect._
import fs2.Stream
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords, ProducerSettings, Serializer}
import munit.CatsEffectSuite


class E2eSuite extends CatsEffectSuite {

  private val kafkaProducer: Stream[IO, KafkaProducer.Metrics[IO, String, String]] =
    KafkaProducer.stream(ProducerSettings(Serializer.string[IO], Serializer.string[IO]).withBootstrapServers("localhost:9092"))

  private final case class Message(key: String, value: String)

  private def produce(messages: List[Message], topic: String): IO[Unit] = {
    kafkaProducer.evalMap { kp: KafkaProducer[IO, String, String] =>
      kp.produce(ProducerRecords.apply(
        messages.map { case Message(key, value) =>
          ProducerRecord(topic = topic, key = key, value = value)
        })).flatten.as(())
    }
  }.compile.drain


  test("test with resource"){
    for {
      _ <- produce(List(Message("hi", "world"), Message("goodnight", "moon")), Main.topic)
      ref <- Ref.of[IO, List[String]](Nil)
      _ <- Main.streamList(ref, Main.topic)
      values <- ref.get
    } yield (assertEquals(values, List("moon", "world")))
  }

}
