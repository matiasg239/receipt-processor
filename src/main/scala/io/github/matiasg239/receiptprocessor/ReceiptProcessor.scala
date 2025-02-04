package io.github.matiasg239.receiptprocessor

import cats.effect.*
import io.circe.Json
import io.circe.generic.auto.*
import io.github.matiasg239.receiptprocessor.Database.Database
import io.github.matiasg239.receiptprocessor.json.{Item, Receipt}
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.io.*
import org.http4s.headers.Allow

import java.util.UUID


object ReceiptProcessor {
  implicit val itemJsonDecoder: EntityDecoder[IO, Item] = jsonOf[IO, Item]
  implicit val receiptJsonDecoder: EntityDecoder[IO, Receipt] = jsonOf[IO, Receipt]
  
  val receiptProcessor: HttpRoutes[IO] = HttpRoutes.of[IO] {
    // TODO: comment out, for testing
    case GET -> Root / "databaseDump" =>
      Ok(Database.getAll)
    case GET -> Root / "receipts" / "process" =>
      MethodNotAllowed(Allow(POST))
    case request@POST -> Root / "receipts" / "process" =>
      val id = UUID.randomUUID().toString
      //val receipt = request.as[Receipt]
      for {
        receipt <- request.as[Receipt]
        _ <- Database.store(id, receipt.toString)
        //response <- Ok(s"Your retailer is ${receipt.retailer.getOrElse("not listed")}")
        response <- Ok(Json.obj("id" -> Json.fromString(id)))
      } yield response
  }
}
