import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.bExp.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.inheritanceExp.*
import MySetTheoryDSL.setExp.*
import org.scalatest.funsuite.AnyFunSuite


class PartialEvalTests extends AnyFunSuite {
  
  test("Basic Partial Eval Test") {
    assert(Variable("undefined").eval() == Variable("undefined"))

    val s = Insert(Variable("no"),Variable("nada"),Variable("nil"))

    assert(s == s.eval()) //No optimizations can be performed on this code





  }

  test("Complete eval") {
    Assign("total_eval",Set(Value(4))).eval() //This code can be evaluated fine.

    assert(Check("total_eval",Value(4)))

  }

  test("Optimization 1 test") {
    import scala.collection.immutable.Set

    val s = Insert(Union(Value("cat"),Value("dog")),Variable("Undefined"))
    assert(s.eval() == Insert(Literal(Set("cat","dog")), Variable("Undefined")))

    val r = Insert(
      Difference(
        Insert(Value(1),Value(2),Value(3)),
        Insert(Value(2),Value(3),Value(4))), Variable("Undefined"))


    assert(r.eval() == Insert(Literal(Set(1)),Variable("Undefined")))
  }
  test("Optimization 2 test") {
    import scala.collection.immutable.Set


    val r = Intersection(Variable("Undefined"), Variable("Undefined"))
    assert(r.eval() == Variable("Undefined"))



  }
  test("Optimization 3 test") {
    import scala.collection.immutable.Set

    val r = Difference(Variable("Undefined"), Variable("Undefined"))
    assert(r.eval() == Value(()))



  }
  test("All optimizations together") {
    import scala.collection.immutable.Set
    val r = Union(
      Intersection(
        Difference(
          Intersection(Variable("Undefined"), Variable("Undefined")),
          Intersection(Variable("Undefined"), Variable("Undefined"))),
        Difference(
          Intersection(Variable("Undefined"), Variable("Undefined")),
          Intersection(Variable("Undefined"), Variable("Undefined")))),
      Value(3))

    assert(r.eval() == Literal(Set((), 3)))
  }

  
}
