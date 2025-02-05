package io.github.matiasg239.receiptprocessor

import cats.effect.*
import io.circe.Json
import io.circe.generic.auto.*
import io.github.matiasg239.receiptprocessor.Database.DatabaseService
import io.github.matiasg239.receiptprocessor.json.{Item, Receipt}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.headers.Allow

import java.util.UUID


object ReceiptProcessor {
  implicit val itemJsonDecoder: EntityDecoder[IO, Item] = jsonOf[IO, Item]
  implicit val receiptJsonDecoder: EntityDecoder[IO, Receipt] = jsonOf[IO, Receipt]

  def receiptProcessor: HttpRoutes[IO] = HttpRoutes.of[IO] {
//    // TODO: comment out, for testing
//    case GET -> Root / "databaseDump" =>
//      Ok(DatabaseService.getAll)
    case GET -> Root / "receipts" / "process" =>
      MethodNotAllowed(Allow(POST))
    case GET -> Root / "receipts" / id / "points" =>
      for {
        score <- DatabaseService.retrieveScore(id)
        response <- if (score.nonEmpty) Ok(Json.obj("points" -> Json.fromInt(score.get))) else NotFound("No receipt found for that ID.")
      } yield response
    case request@POST -> Root / "receipts" / "process" =>
      val id = UUID.randomUUID().toString
      for {
        receipt <- request.as[Receipt]
        score <- IO{Receipt.scoreReceipt(receipt)}
        // we could combine these two lines since to use the same if check, but with for-comprehension syntax i think it's more readable as is
        _ <- if (score > 0) DatabaseService.store(id, receipt.toString, score) else IO.unit
        response <- if(score > 0) Ok(Json.obj("id" -> Json.fromString(id))) else BadRequest("The receipt is invalid.")
      } yield response
  }
}
