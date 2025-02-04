package io.github.matiasg239.receiptprocessor

import cats.effect.{ExitCode, IO, IOApp}
import com.comcast.ip4s.{ipv4, port}
import org.http4s.ember.server.EmberServerBuilder

object Main extends IOApp {
  private val receiptProcessorService = ReceiptProcessor.receiptProcessor.orNotFound

  override def run(args: List[String]): IO[ExitCode] =
    EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(receiptProcessorService)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)
}
