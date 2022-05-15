package net

import cats.effect.{Ref, ExitCode, IO, IOApp}
import fs2.Stream
import fs2.kafka._

object Main extends IOApp {

  private val consumerSettings =
    ConsumerSettings[IO, String, Long]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers("localhost:9092")
      .withGroupId("group")

  private val repo: Repository = ???

  override def run(args: List[String]): IO[ExitCode] =
    IO(println("running")) *> {
      Consumer.consume("topic", repo, consumerSettings).compile.drain
    }.as(ExitCode.Success)

}