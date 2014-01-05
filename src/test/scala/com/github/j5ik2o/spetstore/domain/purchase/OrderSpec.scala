package com.github.j5ik2o.spetstore.domain.purchase

import com.github.j5ik2o.spetstore.domain.account._
import com.github.j5ik2o.spetstore.domain.address.Contact
import com.github.j5ik2o.spetstore.domain.address.PostalAddress
import com.github.j5ik2o.spetstore.domain.address.Pref
import com.github.j5ik2o.spetstore.domain.address.ZipCode
import com.github.j5ik2o.spetstore.domain.item.Item
import com.github.j5ik2o.spetstore.domain.item.ItemId
import com.github.j5ik2o.spetstore.domain.item.ItemTypeId
import org.joda.time.DateTime
import org.specs2.mutable.Specification
import com.github.j5ik2o.spetstore.infrastructure.support.EntityIOContextOnMemory

class OrderSpec extends Specification {

  "order" should {
    val item = Item(
      id = ItemId(),
      itemTypeId = ItemTypeId(),
      name = "ぽち",
      description = None,
      price = BigDecimal(100),
      quantity = 1
    )
    "add orderItem" in {
      val order = Order(
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List.empty
      )
      val orderItem = OrderItem(item, 1)
      val newOrder = order.addOrderItem(orderItem)
      newOrder must_== order
      newOrder.orderItems.contains(orderItem) must beTrue
      newOrder.sizeOfOrderItems must_== 1
    }
    "remove orderItem" in {
      val orderItem = OrderItem(item, 1)
      val order = Order(
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List(orderItem)
      )
      val newOrder = order.removeOrderItem(orderItem)
      newOrder must_== order
      newOrder.orderItems.contains(orderItem) must beFalse
      newOrder.sizeOfOrderItems must_== 0
    }
    "remove orderItem by index" in {
      val orderItem = OrderItem(item, 1)
      val order = Order(
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List(orderItem)
      )
      val newOrder = order.removeOrderItemByIndex(0)
      newOrder must_== order
      newOrder.orderItems.contains(orderItem) must beFalse
      newOrder.sizeOfOrderItems must_== 0
    }
    "get totalPrice" in {
      val orderItem = OrderItem(item, 1)
      val order = Order(
        orderDate = DateTime.now,
        userName = "Junichi Kato",
        shippingAddress = PostalAddress(
          ZipCode("100", "1000"),
          Pref.東京都,
          "目黒区下目黒",
          "1-1-1"
        ),
        orderItems = List(orderItem)
      )
      order.totalPrice must_== BigDecimal(100)
    }
    "apply from cart" in {
      val account = Account(
        id = AccountId(),
        status = AccountStatus.Enabled,
        name = "Junichi Kato",
        profile = AccountProfile(
          postalAddress = PostalAddress(
            ZipCode("100", "1000"),
            Pref.東京都,
            "目黒区下目黒",
            "1-1-1"
          ),
          contact = Contact("hoge@hoge.com", "00-0000-0000")
        ),
        config = AccountConfig(
          password = "hogehoge",
          favoriteCategoryId = None
        )
      )
      val cart = Cart(
        id = CartId(),
        accountId = account.id,
        cartItems = List(
          CartItem(item, 1, false)
        )
      )
      implicit val ar = AccountRepository.ofMemory(Map(account.id -> account))
      implicit val ctx = EntityIOContextOnMemory
      val order = Order.fromCart(cart).get
      order.orderItems.exists(e => e.item == item && e.quantity == 1) must beTrue
    }

  }

}