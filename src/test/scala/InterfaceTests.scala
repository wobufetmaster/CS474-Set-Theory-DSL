import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.inheritanceExp.*
import MySetTheoryDSL.setExp.*
import org.scalatest.funsuite.AnyFunSuite

class InterfaceTests extends AnyFunSuite {
  
  test("Incorrect interface usage") {
    Interface("monkey",Extends(None), Method("eat",Args())).eval()

    assertThrows[RuntimeException] { //Attempting to instantiate an interface should fail
      Assign("my_monkey",NewObject("monkey")).eval()
    }

    assertThrows[RuntimeException] { //Not implementing the abstract method eat
      ClassDef("lemur", Implements("monkey"), Constructor()).eval()
    }

    Interface("animal", Extends(None), Method("breathe",Args())).eval()

    assertThrows[RuntimeException] { //Must implement all abstract methods, breathe is not implemented, so should fail
      ClassDef("bonobo", Implements("monkey", "animal"), Constructor(), Method("eat", Args(), Value("banana"))).eval()
    }

  }
  
  test("Basic interfaces Test") {
    Interface("monkey",Extends(None),
      Method("eat",Args()),
      Method("throw", Args(), Value("rock"))).eval()

    Interface("animal",Extends(None),
      Method("speak",Args()),
      Method("drink", Args(), Value("water"))).eval()


    ClassDef("lemur", Implements("monkey" ,"animal"), Constructor(),
      Method("speak", Args(), Value("bark")), //Overriding abstract methods
      Method("eat", Args(), Value("bugs"))).eval()

    Assign("pet_lemur", NewObject("lemur")).eval()
    Assign("throw", Set(InvokeMethod("pet_lemur","throw"))).eval()
    Assign("drink", Set(InvokeMethod("pet_lemur","drink"))).eval()

    assert(Check("throw",Value("rock")))
    assert(Check("drink", Value("water")))

    ClassDef("dog", Implements("animal"), Constructor(),
      Method("speak", Args(), Value("bark")), //Overriding the default method drink in the animal interface
      Method("drink", Args(), Value("dog water"))).eval()

    Assign("pet_dog", NewObject("dog")).eval()
    Assign("drink", Set(InvokeMethod("pet_dog","drink"))).eval()
    assert(Check("drink", Value("dog water")))
  }

  test("Advanced interfaces") {

    ClassDef("outer",Extends(None),Constructor(),
      Interface("nested_interface",Extends(None),
        Method("hello",Args(),Value("hello from the inner interface!"))),
      ClassDef("inner_class", Implements("nested_interface"), Constructor()),
      Method("say_hello",Args(),Assign("inner",NewObject("inner_class")),InvokeMethod("inner","hello"))).eval()

    Assign("my_outer",NewObject("outer")).eval()
    Assign("my_inner",Set(InvokeMethod("my_outer","say_hello"))).eval()

    assert(Check("my_inner",Value("hello from the inner interface!")))


  }

  test("Long inheritance chains") {
    Interface("animal",Extends(None), Method("eat", Args(), Value("food"))).eval()
    Interface("monkey",Extends(Some("animal"))).eval()
    Interface("gorilla",Extends(Some("animal"))).eval()
    Interface("silverback",Extends(Some("gorilla"))).eval()


    ClassDef("alpha", Implements("silverback"), Constructor()).eval()
    Assign("my_alpha",NewObject("alpha")).eval()
    Assign("eat",Set(InvokeMethod("my_alpha","eat"))).eval()
    assert(Check("eat",Value("food")))

  }

  
  test("Circular composition") {


    assertThrows[RuntimeException] { //Circular composition
      Interface("chicken",Extends(Some("egg"))).eval()
      Interface("egg",Extends(Some("chicken"))).eval()
    }

    Interface("A",Extends(None)).eval() //None of the interfaces have methods for simplicity's sake.
    Interface("B",Extends(Some("A"))).eval()
    Interface("C",Extends(Some("B"))).eval()
    assertThrows[RuntimeException] { //Circular composition
      ClassDef("myClass",Implements("B","C"), Constructor()).eval()
    }



  }
  
}
