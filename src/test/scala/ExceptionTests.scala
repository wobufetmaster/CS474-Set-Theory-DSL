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

    ExceptionClassDef("myExceptionClass", Extends(None), Constructor()).eval()
    assertThrows[templateException] {
      ThrowException(NewObject("myExceptionClass")).eval()
    }
    
    
  }

  test("Exceptions in function method") {




  }

  
}
