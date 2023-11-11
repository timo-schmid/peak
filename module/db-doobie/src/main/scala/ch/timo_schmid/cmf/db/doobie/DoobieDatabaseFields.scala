package ch.timo_schmid.cmf.db.doobie

import cats.implicits.*
import doobie.*
import doobie.implicits.*
import doobie.syntax.SqlInterpolator.SingleFragment.fromWrite
import doobie.util.fragment.Fragment
import shapeless3.deriving.K0
import shapeless3.deriving.Labelling

trait DoobieDatabaseFields[Data[_[_]]]:

  def fields: Fragment

  def fieldNames: List[String]

  def values(data: Data[cats.Id]): Fragment

  def setValues(data: Data[cats.Id]): Fragment

object DoobieDatabaseFields:

  inline def apply[Data[_[_]]](using
      dbFields: DoobieDatabaseFields[Data]
  ): DoobieDatabaseFields[Data] =
    dbFields

  given dbFieldsGen[Data[_[_]]](using
      inst: K0.ProductInstances[Write, Data[cats.Id]],
      labelling: Labelling[Data[cats.Id]]
  ): DoobieDatabaseFields[Data] with

    def fields: Fragment =
      fieldNames
        .map(Fragment.const0(_))
        .intercalate(fr0", ")

    override def fieldNames: List[String] =
      labelling.elemLabels.toList

    def values(data: Data[cats.Id]): Fragment =
      inst
        .foldLeft[Seq[Fragment]](data)(Seq.empty[Fragment])(
          [t] => (acc: Seq[Fragment], write: Write[t], x: t) => acc ++ Seq(fromWrite(x)(write).fr)
        )
        .toList
        .intercalate(fr0", ")

    def setValues(data: Data[cats.Id]): Fragment =
      labelling.elemLabels.zipWithIndex.toList
        .map { (label, i) =>
          Fragment.const0(label) ++
            fr0" = " ++
            inst.project[Fragment](data)(i)(
              [t] => (write: Write[t], x: t) => fromWrite(x)(write).fr
            )
        }
        .intercalate(fr0", ")

  inline def derived[Data[_[_]]](using
      labelling: Labelling[Data[cats.Id]],
      inst: K0.ProductInstances[Write, Data[cats.Id]]
  ): DoobieDatabaseFields[Data] =
    dbFieldsGen[Data]
