package io.github.matiasg239.receiptprocessor.json

import io.github.matiasg239.receiptprocessor.json.Receipt.scoreReceipt
import org.scalatest.funsuite.AnyFunSuite

// weird intellij(?) issue -- tests failed when run from the sidebar
// with a NoClassDefFoundError, but rerunning them in the integration window (or via sbt test) works
// ¯\_(ツ)_/¯
// note to self: figured it out, needed to change the config and check the "use SBT" (and optionally "use SBT with UI") boxes
class ReceiptSpec extends AnyFunSuite {
  val emptyValidReceipt: Receipt = new Receipt(
    retailer = Option(""),
    purchaseDate = Option("2000-01-02"), // valid date that scores 0, can't use 0000-00-00
    purchaseTime = Option("00:00"),
    items = List(Item(shortDescription = Option(""), price = Option("0.00"))),
    total = Option("0.00")
  )

  val filledValidReceipt: Receipt = new Receipt(
    retailer = Option("Target"),
    purchaseDate = Option("2022-01-01"),
    purchaseTime = Option("13:01"),
    items = List(
      Item(shortDescription = Option("Mountain Dew 12PK"), price = Option("6.49")),
      Item(shortDescription = Option("Emils Cheese Pizza"), price = Option("12.25")),
      Item(shortDescription = Option("Knorr Creamy Chicken"), price = Option("1.26")),
      Item(shortDescription = Option("Doritos Nacho Cheese"), price = Option("3.35")),
      Item(shortDescription = Option("   Klarbrunn 12-PK 12 FL OZ  "), price = Option("12.00"))),
    total = Option("00.00")
  )
  // retailer tests
  test("retailer should score one point per alphanumeric character") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(retailer = Option("Wh3atsvill3"))
    assert(scoreReceipt(mostlyEmptyReceipt) == 11)
  }
  test("retailer should score one point per alphanumeric character and ignore spaces and nonalphanumerics") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(retailer = Option("Wheatsville #3"))
    assert(scoreReceipt(mostlyEmptyReceipt) == 12)
  }
  // malformed in this case just means empty
  test("malformed retailer should score a total of zero even if other data is valid") {
    val mostlyFilledReceipt = filledValidReceipt.copy(retailer = None)
    assert(scoreReceipt(mostlyFilledReceipt) == 0)
  }

  // purchaseDate tests
  test("odd day in purchase date should score 6") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(purchaseDate = Option("2025-01-01"))
    assert(scoreReceipt(mostlyEmptyReceipt) == 6)
  }
  test("even day in purchase date should score 0") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(purchaseDate = Option("   2025-01-02"))
    assert(scoreReceipt(mostlyEmptyReceipt) == 0)
  }
  test("malformed date should score 0") {
    val mostlyFilledReceipt = filledValidReceipt.copy(purchaseDate = Option("January 1st, 2025"))
    assert(scoreReceipt(mostlyFilledReceipt) == 0)
  }
  test("well-formed but impossible date should score 0") {
    val mostlyFilledReceipt = filledValidReceipt.copy(purchaseDate = Option("2025-51-51"))
    assert(scoreReceipt(mostlyFilledReceipt) == 0)
  }

  // purchaseTime tests
  test("purchase time between 14:00 and 16:00 should score 10") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(purchaseTime = Option("  14:01"))
    assert(scoreReceipt(mostlyEmptyReceipt) == 10)
  }
  // see assumption in Receipt.scoreTime
  test("purchase time of exactly 14:00 should score 10") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(purchaseTime = Option("14:00"))
    assert(scoreReceipt(mostlyEmptyReceipt) == 10)
  }
  test("purchase time of exactly 16:00 should score 0") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(purchaseTime = Option("16:00"))
    assert(scoreReceipt(mostlyEmptyReceipt) == 0)
  }
  test("malformed purchase time should score 0") {
    val mostlyFilledReceipt = filledValidReceipt.copy(purchaseTime = Option("4 o'clock"))
    assert(scoreReceipt(mostlyFilledReceipt) == 0)
  }
  test("well-formed but impossible time should score 0") {
    val mostlyFilledReceipt = filledValidReceipt.copy(purchaseTime = Option("25:00"))
    assert(scoreReceipt(mostlyFilledReceipt) == 0)
  }

  // item tests
  test("item list with 5 items and no description bonuses should score 10 points") {
    val itemList = List(
      Item(shortDescription = Option("A"), price = Option("6.49")),
      Item(shortDescription = Option("B"), price = Option("12.25")),
      Item(shortDescription = Option("C"), price = Option("1.26")),
      Item(shortDescription = Option("D"), price = Option("3.35")),
      Item(shortDescription = Option("E"), price = Option("12.00")))
    val mostlyEmptyReceipt = emptyValidReceipt.copy(items = itemList)
    assert(scoreReceipt(mostlyEmptyReceipt) == 10)
  }
  test("item list with 6 items and no description bonuses should score 15 points") {
    val itemList = List(
      Item(shortDescription = Option("A"), price = Option("6.49")),
      Item(shortDescription = Option("B"), price = Option("12.25")),
      Item(shortDescription = Option("C"), price = Option("1.26")),
      Item(shortDescription = Option("D"), price = Option("3.35")),
      Item(shortDescription = Option("E"), price = Option("12.00")),
      Item(shortDescription = Option("F"), price = Option("12.00")))

    val mostlyEmptyReceipt = emptyValidReceipt.copy(items = itemList)
    assert(scoreReceipt(mostlyEmptyReceipt) == 15)
  }
  test("item list with only one item that meets bonus criteria should score ceil(price * 0.2)") {
    val itemList = List(Item(shortDescription = Option("     123     \n\n"), price = Option("12.25")))

    val mostlyEmptyReceipt = emptyValidReceipt.copy(items = itemList)
    //ceil(12.25 * .2) = ceil(2.45) = 3
    assert(scoreReceipt(mostlyEmptyReceipt) == 3)
  }
  test("item list with multiple items that meet criteria should score the sum of the bonuses + the length score") {
    val itemList = List(
      Item(shortDescription = Option("A"), price = Option("6.49")),
      Item(shortDescription = Option("BBB"), price = Option("12.25")),
      Item(shortDescription = Option("C"), price = Option("1.26")),
      Item(shortDescription = Option("D"), price = Option("3.35")),
      Item(shortDescription = Option("    EEE   "), price = Option("12.00")))

    val mostlyEmptyReceipt = emptyValidReceipt.copy(items = itemList)
    //    10 points - 5 items (2 pairs @ 5 points each)
    //     3 Points - "Emils Cheese Pizza" is 18 characters (a multiple of 3)
    //                item price of 12.25 * 0.2 = 2.45, rounded up is 3 points
    //     3 Points - "Klarbrunn 12-PK 12 FL OZ" is 24 characters (a multiple of 3)
    //                item price of 12.00 * 0.2 = 2.4, rounded up is 3 points
    assert(scoreReceipt(mostlyEmptyReceipt) == 16)
  }

  test("item list with at least one malformed item scores 0") {
    val itemList = List(
      Item(shortDescription = Option("A"), price = Option("6.49")),
      Item(shortDescription = Option("BBB"), price = Option("12.25")),
      Item(shortDescription = Option("C"), price = Option("1.26")),
      Item(shortDescription = Option("D"), price = Option("3.35")),
      Item(shortDescription = Option("    EEE   "), price = Option("12.00")),
      Item(shortDescription = Option("Broken"), price = Option("10.23123"))
    )
    val mostlyFilledReceipt = filledValidReceipt.copy(items = itemList)

    assert(scoreReceipt(mostlyFilledReceipt) == 0)
  }

  // total tests
  test("total of a round dollar amount should score 75 (50 + 25 for being a multiple of 25)") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(total = Option("10.00"))

    assert(scoreReceipt(mostlyEmptyReceipt) == 75)
  }
  test("total of a non-round multiple of .25 amount should score 25") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(total = Option("10.75"))

    assert(scoreReceipt(mostlyEmptyReceipt) == 25)
  }

  test("malformed total should score 0") {
    val mostlyEmptyReceipt = emptyValidReceipt.copy(total = Option("10.7564636"))

    assert(scoreReceipt(mostlyEmptyReceipt) == 0)
  }
  
  // put it all together
  test("example test should score 28") {
    assert(scoreReceipt(filledValidReceipt) == 28)
  }
}