import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.setExp.*
import org.scalatest.funsuite.AnyFunSuite

class MethodArgumentTests extends AnyFunSuite {
  test("1 Argument Test") {
    ClassDef("dog",Extends(None),Constructor(),Method("eat",Args("food"),Insert(Value("I like to eat: "),Variable("food")))).eval()

    Assign("my_dog",NewObject("dog")).eval()
    Assign("my_food",Set(InvokeMethod("my_dog","eat",Value("peanut butter")))).eval()

    assert(Check("my_food",Insert(Value("I like to eat: "),Value("peanut butter"))))
  }

  test("Multiple argument test") {
    ClassDef("dog",Extends(None),Constructor(),Method("eat",Args("food1","food2","food3"),Insert(Value("My favorite foods are:  "),
      Variable("food1"),Variable("food2"),Variable("food3")))).eval()

    Assign("my_dog",NewObject("dog")).eval()
    Assign("my_food",Set(InvokeMethod("my_dog","eat",Value("bones"),Value("sausage"),Value("bacon")))).eval()

    assert(Check("my_food",Insert(Value("My favorite foods are:  "),Value("bones"),Value("sausage"),Value("bacon"))))
  }

  test("Wrong number of argument test") {
    ClassDef("dog",Extends(None),Constructor(),Method("eat",Args("food1","food2","food3"),Insert(Value("My favorite foods are:  "),
      Variable("food1"),Variable("food2"),Variable("food3")))).eval()

    Assign("my_dog",NewObject("dog")).eval()
    assertThrows[IndexOutOfBoundsException](Assign("my_food",Set(InvokeMethod("my_dog","eat",Value("bones")))).eval()) //Not enough arguments to the method

  }

  test("Inheritance override test") {
    ClassDef("dog", Extends(None), Constructor(), Method("eat", Args("food1", "food2", "food3"), Insert(Value("My favorite foods are:  "),
      Variable("food1"), Variable("food2"), Variable("food3")))).eval()
    ClassDef("beagle", Extends(Some("dog")), Constructor(), Method("eat", Args(), Insert(Value("beagle food")))).eval() //This eat function has no arguments

    Assign("my_dog", NewObject("beagle")).eval()

    Assign("my_food", Set(InvokeMethod("my_dog", "eat"))).eval() //No arguments, overwritten method takes none

    assert(Check("my_food", Insert(Value("beagle food"))))
  }



  test("Inheritance parent test") {
    ClassDef("dog",Extends(None),Constructor(),Method("eat",Args("food1","food2","food3"),Insert(Value("My favorite foods are:  "),
      Variable("food1"),Variable("food2"),Variable("food3")))).eval()

    ClassDef("beagle",Extends(Some("dog")),Constructor()).eval() //No eat method, use parents

    Assign("my_dog",NewObject("beagle")).eval()

    Assign("my_food",Set(InvokeMethod("my_dog","eat",Value("bones"),Value("sausage"),Value("bacon")))).eval()

    assert(Check("my_food",Insert(Value("My favorite foods are:  "),Value("bones"),Value("sausage"),Value("bacon"))))

  }


}
