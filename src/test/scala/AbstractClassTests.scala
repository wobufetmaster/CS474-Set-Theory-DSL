import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.setExp.*
import MySetTheoryDSL.inheritanceExp.*
import org.scalatest.funsuite.AnyFunSuite

class AbstractClassTests extends AnyFunSuite {
  
  test("Instantiate abstract class fails") {
    AbstractClassDef("dog",Extends(None),Constructor(),Method("eat",Args())).eval()

    assertThrows[RuntimeException] { //Attempting to instantiate abstract class
      Assign("my_dog",NewObject("dog")).eval()
    }
    assertThrows[RuntimeException] { //Abstract class with no abstract methods
      AbstractClassDef("monkey", Extends(None), Constructor(), Method("eat", Args(), Insert(Value("Banana")))).eval()
    }

  }
  
  test("Basic interfaces Test") {

    AbstractClassDef("animal",Extends(None),Constructor(),Method("eat",Args())).eval()
    ClassDef("dog", Extends(Some("animal")),Constructor()).eval()

    Interface("animal",Extends(None)).eval()
    Interface("dog",Extends(Some("animal"))).eval()
    ClassDef("daisy",Implements("dog"),Constructor()).eval()


  }
  
}
