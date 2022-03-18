import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.setExp.*
import MySetTheoryDSL.inheritanceExp.*
import org.scalatest.funsuite.AnyFunSuite

class AbstractBasics extends AnyFunSuite {
  
  test("Instantiate Abstract class") {
    AbstractClassDef("dog",Extends(None),Constructor(),Method("eat",Args())).eval()

    assertThrows[RuntimeException] { //Attempting to instantiate abstract class
      Assign("my_dog",NewObject("dog")).eval()
    }
    assertThrows[RuntimeException] { //Abstract class with no abstract methods
      AbstractClassDef("monkey", Extends(None), Constructor(), Method("eat", Args(), Insert(Value("Banana")))).eval()
    }
  }
  
  test("Basic Fields and Constructors Test") {
    ClassDef("dog",Extends(None),Constructor(AssignField(This(),"tail_size",Insert(Value(4)))),Field("tail_size")).eval()

    Assign("my_dog",NewObject("dog")).eval()
    Assign("my_tail",Set(GetField("my_dog","tail_size"))).eval()
    assert(Check("my_tail",Insert(Value(4)))) //Default value for constructor

    AssignField(Object("my_dog"),"tail_size",Value(8)).eval() //Tail extension
    Assign("my_tail",Set(GetField("my_dog","tail_size"))).eval()
    assert(Check("my_tail",Insert(Value(8)))) //Should have new value

  }
  
}
