import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.setExp.*
import org.scalatest.funsuite.AnyFunSuite

class AdvancedTests extends AnyFunSuite {


  test("Nested Class Test") {
    ClassDef("outer",Extends(None),Constructor(),
      ClassDef("nested_class",Extends(None),Constructor(),Method("hello",Args(),Value("hello from the inner class!"))),
      Method("say_hello",Args(),Assign("inner",NewObject("nested_class")),InvokeMethod("inner","hello"))).eval()

    Assign("my_outer",NewObject("outer")).eval()
    Assign("my_inner",Set(InvokeMethod("my_outer","say_hello"))).eval()

    assert(Check("my_inner",Value("hello from the inner class!"))) //Should have new value

  }
  test("Advanced method") {
    ClassDef("myClass",Extends(None),Constructor(),Method("intersection",Args("arg1","arg2"),
      Intersection(Variable("arg1"),Variable("arg2")))).eval()

    Assign("obj",NewObject("myClass")).eval()
    Assign("val",Set(InvokeMethod("obj","intersection",Insert(Value(1),Value(3)),Insert(Value(3),Value(5))))).eval()

    assert(Check("val",Insert(Value(3))))
  }


}
