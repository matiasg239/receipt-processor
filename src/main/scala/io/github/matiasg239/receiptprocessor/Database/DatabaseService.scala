package io.github.matiasg239.receiptprocessor.Database

import cats.effect.IO

import scala.collection.mutable


// known issue: this class isn't tested
// rationale: while in an actual production environment this would have integration tests, i don't
// believe in "testing the JVM"; if mutable.Map is broken, then there's bigger issues.
object DatabaseService {
  private val receiptMap = mutable.Map[String, (String, Int)]()

  def retrieveReceipt(key: String): IO[String] = IO {
    receiptMap.getOrElse(key, ("", 0))._1 // being a little lazy here by returning an empty string
  }

  def retrieveScore(key: String): IO[Option[Int]] = IO {
    receiptMap.get(key).map(_._2)
  }

  def store(key: String, receipt: String, receiptScore: Int): IO[Option[(String, Int)]] = IO {
    receiptMap.put(key, (receipt, receiptScore))
  }

  def getAll: String = receiptMap.mkString("{\n\t", ", \n\t", "\n}")

}