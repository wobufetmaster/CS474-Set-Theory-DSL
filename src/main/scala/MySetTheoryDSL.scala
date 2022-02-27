import MySetTheoryDSL.classExp.{Constructor, Extends}
import MySetTheoryDSL.setExp.*

import scala.collection.mutable


object MySetTheoryDSL:
  type BasicType = Any

  class abstractClass(p: Option[String]): //Contains all of the information for a class
    val method_map: collection.mutable.Map[String, abstractMethod] = collection.mutable.Map() //Methods for this class
    val field_map: collection.mutable.Map[String, setExp] = collection.mutable.Map() //Fields for this class
    val parent: Option[String] = p

  class abstractMethod(a: Seq[String], b: Seq[setExp]):
    val args: Seq[String] = a
    val body: Seq[setExp] = b

  private val macro_map: collection.mutable.Map[String, setExp] = collection.mutable.Map()
  private val scope_map: collection.mutable.Map[(String,Option[String]), Set[Any]] = collection.mutable.Map()
  private val current_scope: mutable.Stack[String] = new mutable.Stack[String]()


  private val vmt: collection.mutable.Map[String, abstractClass] = collection.mutable.Map() //Virtual method table
  private val object_binding: collection.mutable.Map[(String,Option[String]), abstractClass] = collection.mutable.Map() //Binds names to instances of objects


  def get_scope(name: String): Option[String] = //Walk up through the scope stack and find the first scope where our name is defined.
    current_scope.find(x => (scope_map get(name, Some(x))).isDefined)

  enum argExp:
    case Args(args: String*)
    case Arg(arg: setExp)

  enum classBodyExp:
    case Field(name: String)
    case Method(name: String,args: argExp.Args, body: setExp*)
    case ClassDef(name: String, parent: Extends, constructor: Constructor, args: classBodyExp*)

    def eval(): Unit = {
      this match {
        case ClassDef(name,Extends(parent),Constructor(cBody*),args*) => {
          val myClass = new abstractClass(parent)
          vmt.update(name, myClass)
          current_scope.push(name)
          for (arg <- args) {
            arg.eval()
          }

          cBody.foldLeft(Set())((v1,v2) => v1 | v2.eval()) //Evaluate the constructor
          current_scope.pop()
        }
        case Field(name) => vmt(current_scope.head).field_map.update(name,Insert())
        case Method(name,argExp.Args(args*),body*) =>
          val myMethod = new abstractMethod(args,body)
          vmt(current_scope.head).method_map.update(name, myMethod)
      }
    }


  enum fieldExp:
    case Object(name: String)
    case Set(set: setExp)

  enum classExp:
    case Constructor(body: setExp*) //Only one
    case Extends(name: Option[String]) //Only one


  enum assignRHS: //The value
    case Set(args: setExp)
    case NewObject(name: String)


  enum setExp:
    case AssignField(obj: String, fName: String, rhs: setExp)
    case Value(input: BasicType)
    case Variable(name: String)
    case Macro(name: String)
    case CreateMacro(name: String, op2: setExp)
    case Scope(name: String, op2:setExp)
    case Assign(name: String, op2: assignRHS)
    case Insert(op: setExp*)
    case NestedInsert(op: setExp*)
    case Delete(name: String)
    case Union(op1: setExp, op2: setExp)
    case Intersection(op1: setExp, op2: setExp)
    case Difference(op1: setExp, op2: setExp)
    case SymmetricDifference(op1: setExp, op2: setExp)
    case Product(op1: setExp, op2: setExp)
    case InvokeMethod(obj: String, mName: String, args: setExp*)
    case GetField(obj: String, fName: String)




    def eval(): Set[Any] = { //Walks through the AST and returns a set. Set[Any]
      this match {
        case Value(v) => Set(v)
        case Variable(name) => scope_map(name,get_scope(name)) //Lookup value
        case Macro(a) => macro_map(a).eval() //Lookup macro and execute
        case CreateMacro(a,b) =>
          macro_map.update(a,b)
          Set()
        case Scope(a,b) =>
          current_scope.push(a) //Push current scope onto stack
          val temp = b.eval() //Evaluate rhs
          current_scope.pop() //Current scope is over - go back to previous scope
          temp //Return the evaluated value
        case Assign(name, assignRHS.Set(set)) =>
          //println(current_scope)
          scope_map.update((name,current_scope.headOption),set.eval())
          Set()
        case Assign(name, assignRHS.NewObject(oName)) =>
          object_binding.update((name,current_scope.headOption),vmt(oName))
          Set()
        case AssignField(obj, fName, rhs) =>
          vmt(obj).field_map(fName) = rhs
          Set()
        case GetField(obj, fName) =>
          object_binding(obj,current_scope.headOption).field_map.get(fName) match {
            case Some(set) => return set.eval()
            case None =>
          }
          vmt(object_binding(obj,current_scope.headOption).parent.get).field_map(fName).eval()

        case Insert(to_insert*) => to_insert.foldLeft(Set())((v1,v2) => v1 | v2.eval())
        case NestedInsert(to_insert*) => to_insert.foldLeft(Set())((v1,v2) => v1 + v2.eval())
        case Delete(name) =>
          scope_map.remove(name,get_scope(name))
          Set()
        case Union(op1, op2) => op1.eval() | op2.eval()
        case Intersection(op1, op2) => op1.eval() & op2.eval()
        case Difference(op1, op2) => op1.eval() &~ op2.eval()
        case SymmetricDifference(op1, op2) =>
          val a = op1.eval()
          val b = op2.eval()
          (a &~ b).union(b &~ a)
        case Product(op1, op2) => //The two foldLeft()'s essentially act as a double for loop, so we can combine every element pairwise.
          op1.eval().foldLeft(Set())((left_op1, left_op2) => left_op1 | op2.eval().foldLeft(Set())((right_op1, right_op2) => right_op1 | Set(Set(left_op2) | Set(right_op2))))

        case InvokeMethod(obj,mName, f_args*) => {
          val cur_obj = object_binding(obj,current_scope.headOption)
          if (cur_obj.method_map.contains(mName))
            for (i <- cur_obj.method_map(mName).args.indices) {
              Scope(obj,Assign(cur_obj.method_map(mName).args(i),assignRHS.Set(f_args(i))))
            }
            return Scope(obj,Insert(cur_obj.method_map(mName).body*)).eval()
          InvokeMethod(cur_obj.parent.get,mName,f_args*).eval()
        }
      }
    }

  def Check(set_name: String, set_val: setExp, set_scope: Option[String] = None): Boolean = {  //the Scope can be optionally supplied, or global scope will be used if omitted.
    set_val.eval().subsetOf(scope_map(set_name,set_scope))
  }

  @main def runSetExp(): Unit =
    import setExp.*





