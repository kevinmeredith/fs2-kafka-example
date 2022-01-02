package net

import cats.effect.{ExitCode, IO, IOApp}
import fs2.Stream
import fs2.kafka._

object Main extends IOApp {

  private def processRecord(record: ConsumerRecord[String, String]): IO[(String, String)] = {
    val keyValue: (String, String) = record.key -> record.value
    IO(println(s">> key: ${record.key} with value: ${record.value} at offset: ${record.offset}"))
      .as(keyValue)
  }

  private val consumerSettings =
    ConsumerSettings[IO, String, String]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group")

  private val stream: Stream[IO, Unit] =
    KafkaConsumer.stream(consumerSettings)
      .subscribeTo("topic1")
      .records
      .evalMap {
        cr: CommittableConsumerRecord[IO, String, String] =>
          processRecord(cr.record) *> cr.offset.commit
      }

  override def run(args: List[String]): IO[ExitCode] =
    IO(println("running")) *> stream.compile.drain.as(ExitCode.Success)

}