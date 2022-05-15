package net

import cats.effect.{Ref, ExitCode, IO}
import fs2.Stream
import fs2.kafka._

object Consumer {

  def consume(topic: String, repo: Repository, consumerSettings: ConsumerSettings[IO, String, Long]): Stream[IO, Unit] =
    KafkaConsumer
      .stream(consumerSettings)
      .subscribeTo(topic)
      .records
      .evalMap {
        cr: CommittableConsumerRecord[IO, String, Long] =>
          repo.add(cr.record.value) *> cr.offset.commit
      }
}