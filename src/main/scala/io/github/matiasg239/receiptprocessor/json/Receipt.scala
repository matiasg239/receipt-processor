package io.github.matiasg239.receiptprocessor.json

case class Receipt(retailer: Option[String],
                   purchaseDate: Option[String],
                   purchaseTime: Option[String],
                   items: List[Item],
                   total: Option[String])

