import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.setExp.*
import MySetTheoryDSL.inheritanceExp.*
import org.scalatest.funsuite.AnyFunSuite

import scala.util.Success

class QuestionsTests extends AnyFunSuite {
  
  test("Question 1") {
    assertThrows[RuntimeException] { //Can a class/interface inherit from itself?
      ClassDef("chimp", Extends(Some("chimp")), Constructor(), Method("eat", Args(), Insert(Value("Banana")))).eval()
    } 
    //No. This is considered circular composition, so it is not allowed.
  }
  
  test("Question 2") {
    //- Can an interface inherit from an abstract class with all pure methods?

    AbstractClassDef("monkey", Extends(None), Constructor(), Method("throw",Args())).eval()
    Interface("primate", Extends(Some("monkey")), Method("eat", Args())).eval()
    succeed
    //Yes. As long as all of the methods in an interface are abstract, this is OK.
  }

  test("Question 3") {
    // - Can an interface implement another interface?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("Interface(\"primate\", Implements(\"monkey\"), Method(\"eat\", Args()))" )
    //No. In order to implement an interface you must override all of it's methods, therefore it is a type error for an interface to implement anything.
  }

  test("Question 4") {
    // Can a class implement two or more different interfaces that declare methods with exactly the same signatures?
    Interface("monkey", Extends(None), Method("eat",Args(),Value("banana"))).eval()
    Interface("animal", Extends(None), Method("eat",Args(), Value("animal food"))).eval()
    ClassDef("orangutan", Implements("monkey","animal"),Constructor()).eval()

    Assign("pet_monkey",NewObject("orangutan")).eval()
    Assign("monkey_food",Set(InvokeMethod("pet_monkey","eat"))).eval()

    assert(Check("monkey_food",Value("banana")))

    //Yes. If the method is overriden, this is fine. If it isn't, and we're using default methods, the priority on whic to use goes from left to right in the order the interfaces are implemented.
    //"monkey" appears before animal, so that method is called instead of the animal one.
  }

  test("Question 5") {
    // - Can an abstract class inherit from another abstract class and implement interfaces where all interfaces and the abstract class have methods with the same signatures?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("Interface(\"primate\", Implements(\"monkey\"), Method(\"eat\", Args()))")
    //Abstract classes cannot implement interfaces, but they can inherit from other abstract classes.
  }

  test("Question 6") {
    //- Can an abstract class implement interfaces?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("AbstractClassDef(\"primate\", Implements(\"monkey\"), Constructor(), Method(\"eat\", Args()))")
    //No. I think it would make more sense to simply implement whichever interfaces you want, rather than extending an abstract class that implements them.
  }

  test("Question 7") {
    //- Can a class implement two or more interfaces that have methods whose signatures differ only in return types?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    Interface("animal", Extends(None), Method("throw",Args())).eval()
    ClassDef("chimpanzee", Implements("monkey", "animal"), Constructor(), Method("throw", Args(), Value("bugs"))).eval()
    //Yes. Once again which default method is used depends on which name comes first in Implements(). 
  }

  test("Question 8") {
    // - Can an abstract class inherit from a concrete class?
    ClassDef("bonobo", Extends(None), Constructor(), Method("eat", Args(), Value("banana"))).eval()
    AbstractClassDef("pet_monkey", Extends(Some("bonobo")), Constructor(), Method("eat", Args())).eval()
    succeed
    //Yes. This is allowed.
  }

  test("Question 9") {
    // - Can an abstract class/interface be instantiated as anonymous concrete classes?
    //No. abstract classes / interfaces can never be instantiated, and there is no support for anonymous classes in this language.
    succeed
  }



  
}
