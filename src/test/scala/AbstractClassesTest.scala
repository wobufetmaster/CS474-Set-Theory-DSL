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

    assertThrows[RuntimeException] { //Not overriding abstract method in concrete class
      ClassDef("bonobo", Extends(Some("monkey")),Constructor()).eval() //Should fail
    }


  }
  
  test("Basic abstract classes Test") {

    AbstractClassDef("animal",Extends(None), Constructor(), Method("eat",Args())).eval()
    ClassDef("dog", Extends(Some("animal")),Constructor()).eval()

    Interface("animal",Extends(None)).eval()
    ClassDef("dog",Extends(Some("animal")),Constructor(),Method("eat",Args(),Insert(Value("food")))).eval()
    //ClassDef("daisy",Implements("dog"),Constructor(),Method("shed",Args())).eval()

    Assign("my_dog",NewObject("dog")).eval()
    //InvokeMethod("my_dog","eat").eval()


  }
  
}
