import MySetTheoryDSL.inheritanceExp.{Implements, Extends}
import MySetTheoryDSL.classExp.Constructor
import MySetTheoryDSL.setExp.*

import scala.collection.mutable


object MySetTheoryDSL:
  type BasicType = Any

  class abstractClass(p: Option[String], a: Boolean): //Contains all of the information for a class
    val method_map: collection.mutable.Map[String, abstractMethod] = collection.mutable.Map() //Methods for this class
    val field_map: collection.mutable.Map[String, setExp] = collection.mutable.Map() //Fields for this class
    val parent: Option[String] = p //None if it has no parent, otherwise Some(parent)
    val isAbstract: Boolean = a;

  class abstractMethod(a: Seq[String], b: Seq[setExp], c: Boolean): //Contains all of the information for a method
    val args: Seq[String] = a //The list of argument names for this function
    val body: Seq[setExp] = b //The body of the method
    val isAbstract: Boolean = c

  private val macro_map: collection.mutable.Map[String, setExp] = collection.mutable.Map()
  private val scope_map: collection.mutable.Map[(String,Option[String]), Set[Any]] = collection.mutable.Map()
  private val current_scope: mutable.Stack[String] = new mutable.Stack[String]()


  private val vmt: collection.mutable.Map[String, abstractClass] = collection.mutable.Map() //Virtual method table
  private val object_binding: collection.mutable.Map[(String,Option[String]), abstractClass] = collection.mutable.Map() //Binds names to instances of objects


  def get_scope(name: String): Option[String] = //Walk up through the scope stack and find the first scope where our name is defined.
    current_scope.find(x => (scope_map get(name, Some(x))).isDefined)

  enum argExp: //Arguments for Methods, not to be used in method calls!
    case Args(args: String*)

  enum classBodyExp: //The body of a class declaration can be any combination of fields, methods, and nested classes
    case Field(name: String)
    case Interface(name: String, parent: inheritanceExp, args: classBodyExp*)
    case Method(name: String, args: argExp.Args, body: setExp*)
    case ClassDef(name: String, parent: inheritanceExp, constructor: Constructor, args: classBodyExp*)
    case AbstractClassDef(name: String, parent: inheritanceExp, constructor: Constructor, args: classBodyExp*)

    def eval(): Unit =
      this match
        case ClassDef(name,Extends(parent),Constructor(cBody*),args*) =>
          val myClass = new abstractClass(parent,false)
          vmt.update(name, myClass)
          current_scope.push(name) //Enter the scope of the constructor
          for (arg <- args)
            arg.eval()
          cBody.foldLeft(Set())((v1,v2) => v1 | v2.eval()) //Evaluate the constructor
          current_scope.pop()

        case AbstractClassDef(name,Extends(parent),Constructor(cBody*),args*) =>
          val myClass = new abstractClass(parent,true)
          vmt.update(name, myClass)
          current_scope.push(name) //Enter the scope of the constructor
          for (arg <- args)
            arg.eval()
          cBody.foldLeft(Set())((v1,v2) => v1 | v2.eval()) //Evaluate the constructor
          current_scope.pop()
          for (method <- vmt(name).method_map.values)
            if (method.isAbstract)
              return
          throw new RuntimeException

        case Interface(name, Implements(parent),args*) =>
        case Field(name) => vmt(current_scope.head).field_map.update(name,Insert()) //Update the VMT with the field
        case Method(name,argExp.Args(args*)) => vmt(current_scope.head).method_map.update(name, new abstractMethod(args,Seq.empty,true))
        case Method(name,argExp.Args(args*),body*) => vmt(current_scope.head).method_map.update(name, new abstractMethod(args,body,false)) //update vmt with method




  enum fieldExp:
    case Object(name: String)
    case Set(set: setExp)


  enum inheritanceExp:
    case Implements(name: String)
    case Extends(name: Option[String]) //Only one

  enum classExp:
    case Constructor(body: setExp*) //Only one



  enum assignRHS: //The value
    case Set(args: setExp)
    case NewObject(name: String)

  enum Fields:
    case This()
    case Object(name: String)


  enum setExp:
    case AssignField(obj: Fields, fName: String, rhs: setExp)
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




    def eval(): Set[Any] =  //Walks through the AST and returns a set. Set[Any]
      this match 
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
          scope_map.update((name,current_scope.headOption),set.eval()) //Assign a variable to a set
          Set()
        case Assign(name, assignRHS.NewObject(oName)) => //Assign a variable to a new instance of an object
          if (vmt(oName).isAbstract)
            throw new RuntimeException
          object_binding.update((name,current_scope.headOption),vmt(oName))
          Set()
        case AssignField(Fields.This(), fName, rhs) => //Constructor updates the vmt
          vmt(current_scope.headOption.get).field_map(fName) = rhs
          Set()
        case AssignField(Fields.Object(obj), fName, rhs) => //Update the object binding, this is a local change
          object_binding(obj,current_scope.headOption).field_map(fName) = rhs
          Set()
        case GetField(obj, fName) => //Return the value of a field
          object_binding(obj,current_scope.headOption).field_map.get(fName) match { //The field is defined in the base class
            case Some(set) => return set.eval()
            case None => //Not defined in base class, look in parent
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

        case InvokeMethod(obj,mName, f_args*) =>
          val cur_obj = object_binding(obj,current_scope.headOption)
          if (cur_obj.method_map.contains(mName)) //Method is defined in base class
            for (i <- cur_obj.method_map(mName).args.indices)
              Scope(obj,Assign(cur_obj.method_map(mName).args(i),assignRHS.Set(f_args(i)))).eval() //Setting up arguments, assigning them in the funciton scope
            return Scope(obj,Insert(cur_obj.method_map(mName).body*)).eval() //Evaluate the function body
          val p_name = cur_obj.parent.get //Parent name
          val parent_class = vmt(p_name)
          for (i <- parent_class.method_map(mName).args.indices)
            Scope(p_name,Assign(parent_class.method_map(mName).args(i),assignRHS.Set(f_args(i)))).eval() //Set up the arguments in the parent method
          Scope(p_name,Insert(parent_class.method_map(mName).body*)).eval() //Evaluate the parent function body
  
  def Check(set_name: String, set_val: setExp, set_scope: Option[String] = None): Boolean =   //the Scope can be optionally supplied, or global scope will be used if omitted.
    set_val.eval().subsetOf(scope_map(set_name,set_scope))

  @main def runSetExp(): Unit =
    import setExp.*





