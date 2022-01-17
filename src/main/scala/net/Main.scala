package net

import cats.effect.{Ref, ExitCode, IO, IOApp}
import fs2.Stream
import fs2.kafka._

object Main extends IOApp {

  private def processRecord(record: ConsumerRecord[String, String], ref: Ref[IO, List[String]]): IO[Unit] = {
    for {
      _ <- IO(println(s">> key: ${record.key} with value: ${record.value} at offset: ${record.offset}"))
      _ <- ref.update { acc => record.value :: acc }
    } yield ()
  }

  private val consumerSettings =
    ConsumerSettings[IO, String, String]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group")

  private def stream(ref: Ref[IO, List[String]], topic: String): Stream[IO, Unit] =
    KafkaConsumer.stream(consumerSettings)
      .subscribeTo(topic)
      .records
      .evalMap {
        cr: CommittableConsumerRecord[IO, String, String] =>
          processRecord(cr.record, ref) *> cr.offset.commit
      }

  def streamList(ref: Ref[IO, List[String]], topic: String): IO[Unit] =
    stream(ref, topic).compile.drain

  override def run(args: List[String]): IO[ExitCode] =
    IO(println("running")) *> {
      Ref.of[IO, List[String]](Nil).flatMap { streamList(_, "topic1") }
    }.as(ExitCode.Success)

}