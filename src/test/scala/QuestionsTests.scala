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
    } //No. This is considered circular inheritance, so it is not allowed.
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
    Interface("monkey", Extends(None), Method("eat",Args())).eval()
    Interface("animal", Extends(None), Method("eat",Args())).eval()
    ClassDef("orangutang", Implements("monkey","animal"),Constructor(), Method("eat",Args(),Value("bannana"))).eval()
    succeed
    //Yes. As long as the method is overriden, this is fine. There aren't any default methods in interfaces, so we won't run into a diamond problem by doing this.
  }

  test("Question 5") {
    // - Can an abstract class inherit from another abstract class and implement interfaces where all interfaces and the abstract class have methods with the same signatures?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("Interface(\"primate\", Implements(\"monkey\"), Method(\"eat\", Args()))")
    //Yes to the Abstract class part, no to the interface part.
  }

  test("Question 6") {
    //- Can an abstract class implement interfaces?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("AbstractClassDef(\"primate\", Implements(\"monkey\"), Constructor(), Method(\"eat\", Args()))")
    //No. In order to implement an interface you must override all of it's methods, therefore it is a type error for an abstract class to implement anything.
  }

  test("Question 7") {
    //- Can a class implement two or more interfaces that have methods whose signatures differ only in return types?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("Interface(\"primate\", Implements(\"monkey\"), Method(\"eat\", Args()))")
    //Yes.
  }

  test("Question 8") {
    // - Can an abstract class inherit from a concrete class?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("Interface(\"primate\", Implements(\"monkey\"), Method(\"eat\", Args()))")
    //No. In order to implement an interface you must override all of it's methods, therefore it is a type error for an interface to implement anything.
  }

  test("Question 9") {
    // - Can an abstract class/interface be instantiated as anonymous concrete classes?
    Interface("monkey", Extends(None), Method("throw",Args())).eval()
    assertTypeError("Interface(\"primate\", Implements(\"monkey\"), Method(\"eat\", Args()))")
    //No. There is no support for anonymous classes in this language.
  }



  
}
