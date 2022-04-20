import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.setExp.*
import org.scalatest.funsuite.AnyFunSuite
import MySetTheoryDSL.inheritanceExp.*

class InheritanceTests extends AnyFunSuite {
  test("Overwritten Method Test") {
    ClassDef("dog",Extends(None),Constructor(),Method("eat",Args(),Insert(Value("dog food")))).eval()
    ClassDef("beagle",Extends(Some("dog")),Constructor(),Method("eat",Args(),Insert(Value("beagle food")))).eval()

    Assign("my_dog",NewObject("beagle")).strict_eval()
    Assign("my_food",Set(InvokeMethod("my_dog","eat"))).strict_eval()

    assert(Check("my_food",Insert(Value("beagle food"))))
  }

  test("Parent method test") {
    ClassDef("dog",Extends(None),Constructor(),Method("eat",Args(),Insert(Value("dog food")))).eval()
    ClassDef("beagle",Extends(Some("dog")),Constructor()).eval() //Note that there is no eat function anymore

    Assign("my_dog",NewObject("beagle")).strict_eval()
    Assign("my_food",Set(InvokeMethod("my_dog","eat"))).strict_eval()

    assert(Check("my_food",Insert(Value("dog food"))))
  }

  test("Overwritten Field Test") {
    ClassDef("dog",Extends(None), Constructor(
      AssignField(This(),"name",Insert(Value("doggy")))),Field("name")).eval()
    ClassDef("beagle",Extends(Some("dog")), Constructor(
      AssignField(This(),"name",Insert(Value("Daisy")))),Field("name")).eval()

    Assign("my_dog",NewObject("beagle")).strict_eval()
    Assign("dog_name",Set(GetField("my_dog","name"))).strict_eval()

    assert(Check("dog_name",Value("Daisy")))
  }
  test("Parent Field Test") {
    ClassDef("dog",Extends(None), Constructor(
      AssignField(This(),"name",Insert(Value("doggy")))),Field("name")).eval()
    ClassDef("beagle",Extends(Some("dog")),Constructor()).eval() //No constructor or field, both are inherited from parent

    Assign("my_dog",NewObject("beagle")).strict_eval()
    Assign("dog_name",Set(GetField("my_dog","name"))).strict_eval()

    assert(Check("dog_name",Value("doggy"))) //Should have the default value given by the constructor of the parent class
  }


}
