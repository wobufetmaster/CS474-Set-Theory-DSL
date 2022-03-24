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

    assertThrows[RuntimeException] { //Attempt to instantiate abstract class should fail
      Assign("pet_monkey", NewObject("monkey")).eval()
    }

    assertThrows[RuntimeException] { //Concrete class with an abstract method.
      ClassDef("panda", Extends(None), Constructor(), Method("eat", Args())).eval()
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
  test("Abstract classes with fields and constructors Test") {
    

    AbstractClassDef("monkey",Extends(None), Constructor( //Constructors and fields in abstract classes
      AssignField(This(),"brain_size",Insert(Value("small")))),
      Field("brain_size"),
      Method("eat", Args())).eval()
    ClassDef("orangutan",Extends(Some("monkey")), Constructor(), Method("eat", Args(), Value("banana"))).eval() 

    Assign("my_orangutan",NewObject("orangutan")).eval()
    Assign("orangutan_brain",Set(GetField("my_orangutan","brain_size"))).eval()

    assert(Check("orangutan_brain",Value("small")))
    

  }

  test("Nested abstract classes Test") {

    ClassDef("outer",Extends(None),Constructor(),
      AbstractClassDef("nested_abstract",Extends(None), Constructor(),
        Method("abstract",Args()), //An abstract class needs at least one abstract method
        Method("hello",Args(),Value("hello from the inner abstract class!"))),
      ClassDef("inner_class", Extends(Some("nested_abstract")), Constructor(),
      Method("abstract",Args(),Value("abstract"))), //And that method must be overwritten
      Method("say_hello",Args(),Assign("inner",NewObject("inner_class")),InvokeMethod("inner","hello"))).eval()

    Assign("my_outer",NewObject("outer")).eval()
    Assign("my_inner",Set(InvokeMethod("my_outer","say_hello"))).eval()

    assert(Check("my_inner",Value("hello from the inner abstract class!")))

  }
  
}
