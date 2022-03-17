# Homework 3
### Description: using a language for users of the set theory that you created in Homework 1 and Homework 2 add constructs for abstract classes and interfaces that supports limited inheritance and interface implementation.
### Grade: 10%

## Preliminaries
In the previous homework assignments you gained experience with creating and managing your Git repository and implementing your first *domain-specific language (DSL)* using Scala for writing and evaluating set operation expressions for users of the [set theory](https://en.wikipedia.org/wiki/Set_theory). Using your DSL users can describe and evaluate [binary operations on sets](https://images-na.ssl-images-amazon.com/images/G/31/img15/books/tiles/9352036042_chemistry.pdf) using variables and scopes where elements of the sets can be objects of any type. Also, you learned how to create [Scalatest](https://www.scalatest.org/) tests to test your implementation and to create build scripts using [SBT to build and run scripts](https://www.scala-sbt.org/) for your DSL project.

In this homework you will gain experience with inheritance and interface implementation by adding abstract classes and interfaces and their composition mechanism. Doing this homework is essential for successful completion of the rest of this course, since all other homeworks will share the same features of this homework: the underlying DSL and using git commands like branching, merging, committing, pushing your code into your Git repo, creating test cases and build scripts, reusing the DSL that you will design and implement and employing various tools like a debugger for diagnosing problems with your applications.

First things first, if you haven't done so, you must create your account at [Github](https://github.com), a Git repo management system. Then invite me, your course instructor as your collaborator – my github ID is 0x1DOCD00D and your TA whose github ID is **laxmena**. Since it is (still) a large class, please avoid direct emails from other accounts like funnybunny2000@gmail.com and use the corresponding channels on Teams instead. You will always receive a response within 12 hours at most and in reality the response time is within 30 minutes on average.

Next, if you haven't done so, you will install [IntelliJ](https://www.jetbrains.com/student/) with your academic license, the JDK, the Scala runtime and the IntelliJ Scala plugin and the [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html) and make sure that you can create, compile, and run Java and Scala programs. Please make sure that you can run [various Java tools from your chosen JDK between versions 8 and 17](https://docs.oracle.com/en/java/javase/index.html). It is highly recommended that you use Scala version 3.1.1.

Many students found the following book that I recommended very useful: [the fifth edition of the book on Programming in Scala by Martin Odersky and Lex Spoon et al](https://www.artima.com/shop/programming_in_scala_5ed). There are many other books and resources available on the Internet to learn Scala. Those who know more about functional programming can use the book on Functional Programming in Scala published on Sep 14, 2014 by Paul Chiusano and Runar Bjarnason and it is available using your academic subscription on [SafariBooksOnline](https://learning.oreilly.com/home/).

## Overview
In the first and the second homeworks, you created a DSL for binary set theory operations where you added expressions for storing results of some computations in variables and using them in different scopes and you implemented a feature called ***macros*** that expanded macro definitions in the expressions where the macro names are used and you introduced classes and their inheritance composition mechanism.

In this homework you will build upon your implementation of the DSL from the previous homeworks by adding abstract classes and interfaces with inheritance and interface implementation composition mechanisms. Your abstract classes will contain fields and methods with private, public and protected accesses to its members, similarly to your implementation of concrete classes in homework 2. In contrast to homework 2, abstact classes must contain at least one method declaration without its definition, i.e., no body of the method is implemented. If a concrete derived class inherits from an abstract class then all abstract methods of the parent classes must be implemented in the derived class.

In your implementation, as in the previous homework a class is a collection of methods that contain expressions for binary set theory operations that you implemented in your first homework. The return type of a method is defined by the return type of the last expression of the method that is always the top type Expression or whatever you named it in your previous implementation. Each method takes an arbitrary number of parameters and it can operate on the fields of the class that this method belongs to as well as any variable with an active binding in the parent scopes. Since a method defines a block of code the scoping rules apply to the method as you defined them for an arbitrary scope in the first homework. A method can be referenced and invoked using a specialized data type that you will introduce, e.g., InvokeMethod("methodName").

In this homework you will also introduce a concept of ***interface*** that you can model using its namesake in Java, C# or GoLang. You may introduce the notion of a default method similar to the JLS and a special datatype ***Implements*** to compose classes and interfaces with the semantics similar to the equivalent concept of interface implementation in OO languages that we studied in class lectures. You will implement the following abstract class and interface-related operations.

- Abstract class declaration in a given scope with the definitions of its fields and methods where at least one method is pure (abstract), i.e., no definition of its body is given.
- Interface declaration in a given scope with the declarations of its fields and methods where all methods are abstract.
- As before, classes can inherit from other classes using the datatype ***Extends***. If some class named A Extends a class named B then the class A inherits all methods and the fields of the class B in addition to the methods and the fields that are defined in the class A. If the class B is abstract then the class A can be concrete if it defines all abstract methods of B, otherwise the class A is either declared abstract or an error is issued.
- As before, each class will have one constructor that will be invoked when a class is instantiated. A constructor can be thought of as a method that is invoked when a data type ***NewObject*** is applied to the datatype ***ClassName***. Instantiating an abstract class or an interface should result in an error message.
- Multiple inheritance is not allowed; a class or an interface can extend only one class/interface. A class can extend a concrete or an abstract class; an interface can extend a single interface; an (abstract) class can ***Implements*** an interface.
- An interface cannot implement another interface - an error message should be issued in this case.
- Mutual dependencies among composed classes and interfaces should be resolved. That is, if an interface/class A extends an interface/class B and the interface/class B contains a field of the type A then this dependency should be resolved.
- Circular composition is not allowed, i.e., if there is a chain of inheritance from the class/interface A1 to the class/interface AN then once encountered in the chain of inheritance a class/interface AK cannot appear any more below itself in this inheritance chain.
- As before you will define data types for referencing class members and for enabling virtual dispatch. A virtual dispatch table will be used to determine the dynamic type of an object and to invoke a corresponding overridden method.
- Nested classes/interfaces are allowed similarly to your implementation of homework 2.

Consider the following example of using your language with respect to classes. Please note that it is an example and not a strict guide to your implementation. You are free to experiment to choose signatures of the data types that you like as long as you explain your rationale in your documentation.
```scala
//creating a class and populating it with methods. The data type AbstractClassDef declares an abstract class
//with a given name and the programmer can define the content of this class with at least one abstract method.
AbstractClassDef("someClassName", Field("f"), Constructor(Assign("f", Value(2))), Method("m1", Value(1)), Method("m2"))
//check if an object is in the set
NewObject("someClassName", Variable("z")) //an error should be issued for this operation
//in this example we use an infix method called Implements to allow a new interface named derivedClassName
//to implement all definitions from the previously defined interface someInterfaceName.
ClassDef("derivedClassName", Field("ff")) Implements InterfaceDecl("someInterfaceName") 
```
This homework script is written using a retroscripting technique, in which the homework outlines are generally and loosely drawn, and the individual students improvise to create the implementation that fits their refined objectives. In doing so, students are expected to stay within the basic core requirements of the homework (e.g., to implement classes with the relation inheritance) and they are free to experiments. Asking questions is important, so please ask away on the corresponding Teams channels!

## Functionality
In your language, objects can be created dynamically as instances of specific classes as part of the expressions in addition to being predefined in the environment. How you implement the object creation process is left to your imagination and your rationale. Internally, each object is represented by some record where relations between methods of the parent classes are described using some data structures like a hash map. Virtual tables should be implemented by keeping the track of all methods defined in parent classes and overridden by child classes.

The interesting part of the homework is how you will define the composition relation among concrete classes, abstract classes and interfaces. You should address the following questions, explain your rationale and design solutions around these questions based on your rationale.

- Can a class/interface inherit from itself?
- Can an interface inherit from an abstract class with all pure methods?
- Can an interface implement another interface?
- Can a class implement two or more different interfaces that declare methods with exactly the same signatures?
- Can an abstract class inherit from another abstract class and implement interfaces where all interfaces and the abstract class have methods with the same signatures?
- Can an abstract class implement interfaces?
- Can a class implement two or more interfaces that have methods whose signatures differ only in return types?
- Can an abstract class inherit from a concrete class?
- Can an abstract class/interface be instantiated as anonymous concrete classes?

Your homework can be divided roughly into five steps. First, you design the data types that represent abstract classes and interfaces. Second, you design the data types that represent abstract fields and methods. As in the previous homework you will add the logic for combining procedural and data abstractions into your class implementation with access modifiers. Next, you will define the data types for representing inheritance for abstract classes and interfaces as well as interface implementation and you will create an implementation of the abstract class/interface instantiation and method invocation. Fourth, you will create an algorithm for handling inheritance/implementation chains. Finally, you will create Scalatest tests to verify the correctness of your implementation. You will write a report to explain your implementation and the semantics of your language.

## Baseline
To be considered for grading, your project should include the constructs AbstractClassDef, InterfaceDecl, ClassDef, Field, Method, NewObject, InvokeMethod, Implements and Extends and all required operations and your project should be buildable using the SBT, and your documentation must specify how you create and evaluate expressions with class inheritance and interface implementation in your language.

## Teams collaboration
You can post questions and replies, statements, comments, discussion, etc. on Teams. For this homework, feel free to share your ideas, mistakes, code fragments, commands from scripts, and some of your technical solutions with the rest of the class, and you can ask and advise others using Teams on language design issues, resolving error messages and dependencies and configuration issues. When posting question and answers on Teams, please select the appropriate folder, i.e., **hw3** to ensure that all discussion threads can be easily located. Active participants and problem solvers will receive bonuses from the big brother :-) who is watching your exchanges on Teams (i.e., your class instructor). However, *you must not post the source code of your program or specific details on how your implemented your design ideas!*

## Git logistics
**This is an individual homework.** You can reuse your repo from the previous homework or you can create a separate private repository for each of your homeworks and for the course project. Inviting other students to join your repo for an individual homework will result in losing your grade. For grading, only the latest push timed before the deadline will be considered. **If you push after the deadline, your grade for the homework will be zero**. For more information about using the Git please use this [link as the starting point](https://confluence.atlassian.com/bitbucket/bitbucket-cloud-documentation-home-221448814.html). For those of you who struggle with the Git, I recommend a book by Ryan Hodson on Ry's Git Tutorial. The other book called Pro Git is written by Scott Chacon and Ben Straub and published by Apress and it is [freely available](https://git-scm.com/book/en/v2/). There are multiple videos on youtube that go into details of the Git organization and use.

I repeat, make sure that you will give the course instructor and your TA the read/write access to *your repository* so that we can leave the file feedback.txt with the explanation of the grade assigned to your homework.

## Discussions and submission
As it is mentioned above, you can post questions and replies, statements, comments, discussion, etc. on Teams. Remember that you cannot share your code and your solutions privately, but you can ask and advise others using Teams and StackOverflow or some other developer networks where resources and sample programs can be found on the Internet, how to resolve dependencies and configuration issues. Yet, your implementation should be your own and you cannot share it. Alternatively, you cannot copy and paste someone else's implementation and put your name on it. Your submissions will be checked for plagiarism. **Copying code from your classmates or from some sites on the Internet will result in severe academic penalties up to the termination of your enrollment in the University**. When posting question and answers on Teams, please select the appropriate folder, i.e., hw1 to ensure that all discussion threads can be easily located.


## Submission deadline and logistics
The submission deadline is Friday, March 18 at 6AM CST. You will turn in your submission using the corresponding Assignments entry in Teams where you submit the link to your Github repository. Your repo will include the code, your documentation with instructions and detailed explanations on how to assemble and deploy your program along with the tests and a document that explains the semantics of your language, and what the limitations of your implementation are. Again, do not forget, please make sure that you will give your instructor/TA the write access to your repository. Your name should be shown in your README.md file and other documents. Your code should compile and run from the command line using the commands **sbt clean compile test** and **sbt clean compile run**. Also, you project should be IntelliJ friendly, i.e., your graders should be able to import your code into IntelliJ and run from there. Use .gitignore to exlude files that should not be pushed into the repo.

## Evaluation criteria
- the maximum grade for this homework is 10%. Points are subtracted from this maximum grade: for example, saying that 2% is lost if some requirement is not completed means that the resulting grade will be 10%-2% => 8%; if the core homework functionality does not work, no bonus points will be given;
- only some basic expression language is implemented without scopes and assignments and macros and classes and interfaces and the composition mechanisms and nothing else is done: up to 10% lost;
- you should document how you implemented your solution to the questions in the section functionality and provide tests that result in error messages for prohibited uses of inheritance and interface implementation. Failure to do so results in losing up to 10% of your grade.
- for each use of **var** instead of **val** 0.2% will be substracted from the maximum grade unless the use is justified by using vars only in local scopes for optimization purposes;
- for each non-spelling-related problem reported by the IntelliJ code analysis and inspection tool 0.2% will be substracted from the maximum grade;
- having less than five unit and/or integration tests that show how your implemented features work: up to 5% lost;
- missing comments and explanations from the program: up to 5% lost;
- no instructions in your README.md on how to install and run your program: up to 5% lost;
- the program crashes without completing the core functionality or it is incorrect: up to 10% lost;
- the documentation exists but it is insufficient to understand how you assembled and deployed all language components: up to 8% lost;
- the minimum grade for this homework cannot be less than zero.

That's it, folks!