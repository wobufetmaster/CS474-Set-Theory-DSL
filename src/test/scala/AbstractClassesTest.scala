import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.inheritanceExp.*
import MySetTheoryDSL.setExp.*
import org.scalatest.funsuite.AnyFunSuite

class AbstractClassesTest extends AnyFunSuite {
  
  test("Incorrect abstract classes") {

    assertThrows[RuntimeException] { //Abstract class with no abstract methods
      AbstractClassDef("chimp", Extends(Some("chimp")), Constructor(), Method("eat", Args(), Insert(Value("Banana")))).eval()
    }

    AbstractClassDef("monkey", Extends(None), Constructor(), Method("eat", Args())).eval()

    assertThrows[RuntimeException] { //Attemp to instantiate abstract class
      Assign("pet_monkey", NewObject("monkey")).eval()
    }

  }
  
  test("Basic abstract classes Test") {

    AbstractClassDef("monkey",Extends(None), Constructor() , Method("eat",Args()), Method("yell", Args(), Value("monkey noises"))).eval()
    assertThrows[RuntimeException] { //The concrete class doesn't override the abstract method eat
      ClassDef("chimp", Extends(Some("monkey")),Constructor()).eval()
    }

    ClassDef("gorilla", Extends(Some("monkey")),Constructor(), Method("eat", Args(), Value("meat"))).eval() //Overrides the abstract method.
    Assign("pet_gorilla", NewObject("gorilla")).eval()
    Assign("noise", Set(InvokeMethod("pet_gorilla","yell"))).eval() //Use concrete method from abstract class
    Assign("food", Set(InvokeMethod("pet_gorilla","eat"))).eval() //Use overwritten method in gorilla class

    assert(Check("noise",Value("monkey noises")))
    assert(Check("food", Value("meat")))

  }
  
}
