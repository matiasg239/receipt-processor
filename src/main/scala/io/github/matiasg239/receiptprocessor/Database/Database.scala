package io.github.matiasg239.receiptprocessor.Database

import scala.collection.mutable
import cats.effect.IO


object Database {
  private val map = mutable.Map[String, String]()
  def retrieve(key: String): IO[String] = IO {
    map.getOrElse(key, "") // being a little lazy here by returning an empty value
  }
  def store(key: String, value: String): IO[Option[String]] = IO {
    map.put(key, value)
  }

  def getAll: String = map.mkString("{\n\t", ", \n\t", "\n}")

}