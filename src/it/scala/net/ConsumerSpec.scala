package net

import cats.implicits._
import cats.effect._
import fs2.Stream
import fs2.kafka.{AutoOffsetReset, ConsumerSettings, KafkaProducer, ProducerRecord, ProducerRecords, ProducerSettings, Serializer}
import munit.CatsEffectSuite
import java.util.UUID

class ConsumerSpec extends CatsEffectSuite {

  private val kafkaProducer: Stream[IO, KafkaProducer.Metrics[IO, String, Long]] =
    KafkaProducer.stream(ProducerSettings(Serializer.string[IO], Serializer.long[IO]).withBootstrapServers("localhost:9092"))

  private def produce(events: List[Long], topic: String): IO[Unit] = {
    kafkaProducer.evalMap { kp: KafkaProducer[IO, String, Long] =>
      kp.produce(ProducerRecords.apply(
        events.map { long: Long =>
          ProducerRecord(topic = topic, key = long.toString, value = long)
        })).flatten.as(())
    }
  }.compile.drain

  test("test with resource"){

    val consumerSettings =
      ConsumerSettings[IO, String, Long]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers("localhost:9092")
        .withGroupId("group")

    for {
      uuid <- IO(UUID.randomUUID())
      topic = uuid.toString.take(10).filter(_.isLetterOrDigit)
      events = List(1,2,3)
      _ <- IO(println(s"writing to topic: $topic"))
      _ <- produce(List(1,2,3), topic)
      testRepo <- TestRepository.make
      _ <- Consumer.consume(topic, testRepo, consumerSettings).take(events.size).compile.drain
      sum <- testRepo.currentSum
    } yield assertEquals(sum, events.sum.toLong)
  }

  // $kafka-console-consumer --topic 3be55b115 --bootstrap-server localhost:9092 --from-beginning  --value-deserializer "org.apache.kafka.common.serialization.LongDeserializer"

}
