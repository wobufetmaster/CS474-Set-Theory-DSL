import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.inheritanceExp.*
import MySetTheoryDSL.setExp.*
import MySetTheoryDSL.Condition
import MySetTheoryDSL.bExp.*

import org.scalatest.funsuite.AnyFunSuite

class IfTests extends AnyFunSuite {
  
  test("Basic IF Test") {

    Assign("my_var",Set(Value(7))).eval()
    Assign("result",Set(
      IF(CheckIf("my_var",Value(7)),
        Value("the value is 7"), //Should return this, without evaluating the second statement
        Variable("doesn't exist")))).eval() //If the second statement is evaluated, an error will be thrown*/
  }

  test("IF in function method") {


    ClassDef("myclass",Extends(None),Constructor(),
      Method("mymethod",Args(),
        Assign("result",Set(Value(4))),
        IF(CheckIf("result",Value(4)),
          Value("result is 4"),
          Value("result isn't 4")))).eval()
    Assign("myobject", NewObject("myclass")).eval()
    Assign("val", Set(InvokeMethod("myobject","mymethod"))).eval()
    assert(Check("val",Value("result is 4")))

  }

  
}
