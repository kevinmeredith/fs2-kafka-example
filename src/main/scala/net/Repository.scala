package net

import cats.effect.IO

trait Repository {
  def add(a: Long): IO[Unit]
  def currentSum: IO[Long]
}
