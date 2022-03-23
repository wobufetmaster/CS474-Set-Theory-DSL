import MySetTheoryDSL.inheritanceExp.{Implements, Extends}
import MySetTheoryDSL.classExp.Constructor
import MySetTheoryDSL.setExp.*

import scala.collection.mutable


object MySetTheoryDSL:
  type BasicType = Any

  class abstractClass(Abstract: Boolean = false, Interface: Boolean = false): //Contains all of the information for a class
    val method_map: collection.mutable.Map[String, abstractMethod] = collection.mutable.Map() //Methods for this class
    val field_map: collection.mutable.Map[String, setExp] = collection.mutable.Map() //Fields for this class
    val inheritanceStack: mutable.Stack[String] = new mutable.Stack[String]()
    val isAbstract: Boolean = Abstract
    val isInterface: Boolean = Interface

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
    case Interface(name: String, parent: Extends, args: classBodyExp*)
    case Method(name: String, args: argExp.Args, body: setExp*)
    case ClassDef(name: String, parent: inheritanceExp, constructor: Constructor, args: classBodyExp*)
    case AbstractClassDef(name: String, parent: Extends, constructor: Constructor, args: classBodyExp*)

    def circularInheritanceCheck(s: mutable.Stack[String]): Unit =
      if (s.distinct.size != s.size) {
        throw new RuntimeException("Circular Inheritance")
      }

    def implementsAllMethodsCheck(c: abstractClass): Unit =
      if (c.method_map.values.count(_.isAbstract) != 0)
        throw new RuntimeException("Abstract method in concrete class")
      val base_map = c.method_map
      //print(c.inheritanceStack)
      for (s <- c.inheritanceStack)
        for (k <- vmt(s).method_map.keys)
          if(vmt(s).method_map(k).isAbstract && !base_map.contains(k))
            throw new RuntimeException("Abstract method not overriden in concrete class")



    def eval(): Unit =
      this match
        case ClassDef(name, parent, Constructor(cBody*), args*) =>
          val myClass = new abstractClass()
          myClass.inheritanceStack.push(name)
          parent match
            case Extends(Some (a)) => myClass.inheritanceStack.addAll(vmt(a).inheritanceStack)
            case Implements(p*) => p.map(e => myClass.inheritanceStack.addAll(vmt(e).inheritanceStack))
            case _ =>

          vmt.update(name, myClass)
          current_scope.push(name) //Enter the scope of the constructor
          args.map(a => a.eval())
          cBody.foldLeft(Set())((v1,v2) => v1 | v2.eval()) //Evaluate the constructor
          current_scope.pop()
          circularInheritanceCheck(myClass.inheritanceStack)
          implementsAllMethodsCheck(myClass)

        case AbstractClassDef(name, Extends(parent), Constructor(cBody*), args*) =>
          val myClass = new abstractClass(Abstract = true)
          myClass.inheritanceStack.push(name)
          vmt.update(name, myClass)
          current_scope.push(name) //Enter the scope of the constructor
          args.map(a => a.eval())
          cBody.foldLeft(Set())((v1,v2) => v1 | v2.eval()) //Evaluate Constructor
          current_scope.pop()
          circularInheritanceCheck(myClass.inheritanceStack)
          vmt(name).method_map.values.find(x => x.isAbstract).get

        case Interface(name, Extends(parent),args*) =>
          val myClass = new abstractClass(Interface = true)
          vmt.update(name, myClass)
          myClass.inheritanceStack.push(name)
          parent match
            case Some(str) => myClass.inheritanceStack.addAll(vmt(str).inheritanceStack)
            case None =>
          current_scope.push(name) //Enter the scope of the constructor
          args.map(a => a.eval())
          current_scope.pop()
          circularInheritanceCheck(myClass.inheritanceStack)
          if (vmt(name).method_map.values.count(_.isAbstract) != vmt(name).method_map.values.size)
            throw new RuntimeException("Concrete method in interface")

        case Field(name) => vmt(current_scope.head).field_map.update(name,Insert()) //Update the VMT with the field
        case Method(name,argExp.Args(args*)) => vmt(current_scope.head).method_map.update(name, new abstractMethod(args,Seq.empty,true))
        case Method(name,argExp.Args(args*),body*) => vmt(current_scope.head).method_map.update(name, new abstractMethod(args,body,false)) //update vmt with method


  enum fieldExp:
    case Object(name: String)
    case Set(set: setExp)


  enum inheritanceExp:
    case Implements(name: String*)
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
          if (vmt(oName).isAbstract || vmt(oName).isInterface)
            throw new RuntimeException("Attempt to instantiate non concrete class")
          object_binding.update((name,current_scope.headOption),vmt(oName))
          Set()
        case AssignField(Fields.This(), fName, rhs) => //Constructor updates the vmt
          vmt(current_scope.headOption.get).field_map(fName) = rhs
          Set()
        case AssignField(Fields.Object(obj), fName, rhs) => //Update the object binding, this is a local change
          object_binding(obj,current_scope.headOption).field_map(fName) = rhs
          Set()
        case GetField(obj, fName) => //Return the value of a field
          val cur_obj = object_binding(obj,current_scope.headOption)
          val class_name = cur_obj.inheritanceStack.find(x => vmt(x).field_map contains (fName)).get
          vmt(class_name).field_map(fName).eval()

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
          val class_name = cur_obj.inheritanceStack.find(x => vmt(x).method_map contains (mName)).get
          if (vmt(class_name).method_map(mName).isAbstract) {throw new RuntimeException("Abstract method called")}
          for (i <- vmt(class_name).method_map(mName).args.indices)
            Scope(obj,Assign(vmt(class_name).method_map(mName).args(i),assignRHS.Set(f_args(i)))).eval()
          Scope(obj,Insert(vmt(class_name).method_map(mName).body*)).eval()
  
  def Check(set_name: String, set_val: setExp, set_scope: Option[String] = None): Boolean =   //the Scope can be optionally supplied, or global scope will be used if omitted.
    set_val.eval().subsetOf(scope_map(set_name,set_scope))

  @main def runSetExp(): Unit =
    import setExp.*





