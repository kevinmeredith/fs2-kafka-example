package net

import weaver.IOSuite
import cats.implicits._
import scala.concurrent.duration._
import cats.effect._
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName


object   E2eSuite extends IOSuite {

  override type Res = KafkaContainer

  override def sharedResource: Resource[IO, Res] =
      Resource.make(
        acquire = IO(new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:6.2.1"))) <* IO.sleep(30.seconds)
      )(
        release = _ => IO.unit
      )

  test("test with resource"){ kc: KafkaContainer =>


    IO(println(">> kc.isCreated: " + kc.isCreated + " | kc.getBootstrapServers" + kc.getBootstrapServers)).as(expect(true))
  }

}
