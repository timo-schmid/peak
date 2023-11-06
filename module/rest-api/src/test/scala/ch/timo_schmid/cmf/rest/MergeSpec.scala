package ch.timo_schmid.cmf.rest

import cats.Id
import ch.timo_schmid.cmf.core.entity.Merge
import org.specs2.mutable.Specification

case class Foo[H[_]](bar: H[String], baz: H[Int]) derives Merge

class DbFieldsSpec extends Specification {

  "Merge" should {

    "merge a partial with a full Foo" in {
      Merge[Foo].apply(
        Foo[Option](Some("baz"), None),
        Foo[Id]("bar", 123)
      ) ==== Foo[Id]("baz", 123)
    }

  }

}