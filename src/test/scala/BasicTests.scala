import MySetTheoryDSL.*
import MySetTheoryDSL.setExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import org.scalatest.funsuite.AnyFunSuite

class BasicTests extends AnyFunSuite {
  test("Basic Classes Test") {
    // Assign(a,NewObject("mycoolclass"))
    ClassDef("dog",Extends(None),Constructor(),Method("eat",Args(),Insert(Value("dog food")))).eval()

    Assign("my_dog",NewObject("dog")).eval()
    Assign("my_food",Set(InvokeMethod("my_dog","eat"))).eval()
    assert(Check("my_food",Insert(Value("dog food"))))



  }


}
