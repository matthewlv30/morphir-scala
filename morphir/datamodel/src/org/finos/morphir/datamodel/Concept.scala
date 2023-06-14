package org.finos.morphir.datamodel
//TODO: Keep this non-GADT version as Concept and make a GADT version `Schema[A]`
sealed trait Concept

object Concept {
  sealed trait Basic[+A] extends Concept

  object Basic {
    type Boolean = Concept.Boolean.type
    val Boolean = Concept.Boolean
    type Byte = Concept.Byte.type
    val Byte = Concept.Byte
    type Decimal = Concept.Decimal.type
    val Decimal = Concept.Decimal
    type Integer = Concept.Integer.type
    val Integer = Concept.Integer
    type Int16 = Concept.Int16.type
    val Int16 = Concept.Int16
    type Int32 = Concept.Int32.type
    val Int32 = Concept.Int32
    type String = Concept.String.type
    val String = Concept.String
    type LocalDate = Concept.LocalDate.type
    val LocalDate = Concept.LocalDate
    type Month = Concept.Month.type
    val Month = Concept.Month
    type LocalTime = Concept.LocalTime.type
    val LocalTime = Concept.LocalTime
    type Char = Concept.Char.type
    val Char = Concept.Char
  }

  case object Boolean   extends Basic[scala.Boolean]
  case object Byte      extends Basic[Byte]
  case object Decimal   extends Basic[scala.BigDecimal]
  case object Integer   extends Basic[scala.BigInt]
  case object Int16     extends Basic[Short]
  case object Int32     extends Basic[Int]
  case object String    extends Basic[java.lang.String]
  case object LocalDate extends Basic[java.time.LocalDate]
  case object Month     extends Basic[Int]
  case object LocalTime extends Basic[java.time.LocalTime]
  case object Char      extends Basic[scala.Char]

  case class Record(fields: scala.List[(Label, Concept)]) extends Concept

  case class Alias(name: String, value: Concept) extends Concept

  case class List(elementType: Concept) extends Concept

  case class Map(keyType: Concept, valueType: Concept) extends Concept

  case class Tuple(values: scala.List[Concept]) extends Concept

  case class Optional(elementType: Concept) extends Concept

  /**
   * A discrimiated union type such as an ELM union (either with labels or not)
   *
   * Given an Elm Datatype that looks like this:
   * {{{
   * type MyUnion =
   *   = NoValue
   *   | IntValue x:Int
   *   | MultiValue x:Int y:String
   *   | MultiValueAnon Int String // no labels for the types
   * }}}
   *
   * Or a Scala 3 enum that looks like this:
   * {{{
   *   enum MyUnion:
   *     case NoValue
   *     case IntValue(x:Int)
   *     case MultiValue(x:Int, y:String)
   *     // case MultiValueAnon(Int, String) // cannot have un-labeled unions in Scala3
   * }}}
   *
   * The corresponding type-representation should look like this:
   * {{{
   * Enum(
   *   Case("NoValue", List()),
   *   Case("IntValue", List(Case.Field.Named("x", Schema.Int))),
   *   Case("MultiValue", List(Case.Field.Named("x", Schema.Int), Case.Field.Named("y", Schema.String)))
   *   Case("MultiValueAnon", List(Case.Field.Anon(Schema.Int), Case.Field.Anon(Schema.String)))
   * )
   * }}}
   *
   * On the value level this should look as follows
   * {{{
   *   // Given a type definition that looks like this (In Scala)
   *   val x: MyUnion = MyUnion.IntValue(123)
   *
   *   // It's data-level encoding should look like this
   *   Data.Case(
   *     value: Data.Int(123)
   *     case: Case("IntValue", List(Case.Field.Named("x", Schema.Int)))
   *     schema: Schema.Enum
   *   )
   * }}}
   */
  case class Enum(cases: scala.List[Enum.Case]) extends Concept

  object Enum {
    case class Case(label: Label, fields: scala.List[Case.Field])

    object Case {
      sealed trait Field

      object Field {
        case class Named(label: Label, value: Concept) extends Field

        case class Anon(value: Concept) extends Field
      }
    }
  }

  /**
   * A non-discrimiated union-type such as a Scala 3
   * {{{
   *   type MyUnion = Int | String
   * }}}
   * Would be defined as
   * {{{
   *   Union(Schema.Int, Schema.String)
   * }}}
   */
  case class Union(cases: scala.List[Concept]) extends Concept
}