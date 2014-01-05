package com.github.j5ik2o.spetstore.domain.customer

import com.github.j5ik2o.spetstore.infrastructure.support.{EntityIOContext, RepositoryOnMemory}
import scala.util.{Success, Try}

/**
 * [[com.github.j5ik2o.spetstore.domain.customer.CustomerRepository]]のためのオンメモリリポジトリ。
 *
 * @param entities エンティティの集合
 */
private[customer]
class CustomerRepositoryOnMemory(entities: Map[CustomerId, Customer])
extends RepositoryOnMemory[CustomerId, Customer](entities) with CustomerRepository {

  protected def createInstance(entities: Map[CustomerId, Customer]): This =
    new CustomerRepositoryOnMemory(entities)

  def resolveByName(name: String)(implicit ctx: EntityIOContext): Try[Customer] = Success {
    entities.map(_._2).toList.filter(_.name == name).head
  }

}
