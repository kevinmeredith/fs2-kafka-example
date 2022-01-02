package net

import fs2.Stream
import fs2.kafka.CommittableConsumerRecord

trait Consumer[F[_], K, V] {
  def consume(stream: Stream[F, CommittableConsumerRecord[F, K, V]]): F[Unit]
}
//object Consumer {
//  def printsTupleKeyValue[F[_], K, V] = new Consumer[F, K, V] {
//    override def consume(stream: Stream[F, CommittableConsumerRecord[F, K, V]]): F[Unit] = ???
//  }
//}
