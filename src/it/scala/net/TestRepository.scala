package net

import cats.effect.{IO, Ref}

final class TestRepository private (ref: Ref[IO, Long]) extends Repository {
  override def add(a: Long): IO[Unit] = ref.update(_ + a)
  override def currentSum: IO[Long] = ref.get
}
object TestRepository {
  def make: IO[TestRepository] =
    Ref.of[IO, Long](0).map(new TestRepository(_))
}
