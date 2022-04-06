import MySetTheoryDSL.*
import MySetTheoryDSL.Fields.*
import MySetTheoryDSL.argExp.*
import MySetTheoryDSL.assignRHS.*
import MySetTheoryDSL.bExp.*
import MySetTheoryDSL.classBodyExp.*
import MySetTheoryDSL.classExp.*
import MySetTheoryDSL.inheritanceExp.*
import MySetTheoryDSL.setExp.*
import org.scalatest.funsuite.AnyFunSuite

class ExceptionTests extends AnyFunSuite {
  
  test("Basic Exception Test") {

    ExceptionClassDef("myExceptionClass", Extends(None), Constructor(), Field("reason")).eval()
    assertThrows[templateException] {
      ThrowException(NewObject("myExceptionClass")).eval()
    }

    Assign("exception",Set(Value("no exception"))).eval()

    Scope("myScope", CatchException("myExceptionClass",
      Assign("mySet",Set(Value("bad"))),
      IF(CheckIf("mySet",Value("bad")),
        ThrowException(NewObject("myExceptionClass")),
        Value(4)),
      ThrowException(NewObject("non existent class")), //This line shouldn't be executed.
      Catch(Variable("e"), Assign("exception",Set(Value("exception occurred"))))
    )).eval()

    assert(Check("exception", Value("exception occurred"),Some("myScope")))


  }

  test("Exceptions in function method") {

    ExceptionClassDef("myExceptionClass", Extends(None), 
      Constructor(
        AssignField(This(),"reason",Value("no error"))), 
      Field("reason")).eval()

    ClassDef("IHateElephants", Extends(None) , Constructor(),
      Method("no_elephants", Args("arg1"), //Throws an exception if given an elephant
        IF(CheckIf("arg1",Value("elephant")), 
          ThrowException(NewObject("myExceptionClass")), 
          Value("no elephants here")))
      ).eval()

    Assign("myObj",NewObject("IHateElephants")).eval()

    Assign("giraffe",Set(InvokeMethod("myObj","no_elephants",Value("giraffe")))).eval() //A giraffe is not an elephant
    assert(Check("giraffe", Value("no elephants here")))

    Scope("myScope", CatchException("myExceptionClass",
      InvokeMethod("myObj","no_elephants",Value("elephant")),
      Catch(Variable("e"), AssignField(Object("e"),"Message",Value("there was an elephant")))
    )).eval()
    Assign("mySet", Set(Value("there was an elephant"))).eval()
    



  }

  
}
