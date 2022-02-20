# Homework 2
### Description: using a language for users of the set theory that you created in Homework 1 add the class construct that supports limited inheritance.
### Grade: 10%

## Preliminaries
In the previous homework assignment you gained experience with creating and managing your Git repository and implementing your first *domain-specific language (DSL)* using Scala for writing and evaluating set operation expressions for users of the [set theory](https://en.wikipedia.org/wiki/Set_theory). Using your DSL users can describe and evaluate [binary operations on sets](https://images-na.ssl-images-amazon.com/images/G/31/img15/books/tiles/9352036042_chemistry.pdf) using variables and scopes where elements of the sets can be objects of any type. Also, you learned how to create [Scalatest](https://www.scalatest.org/) tests to test your implementation and to create build scripts using [SBT to build and run scripts](https://www.scala-sbt.org/) for your DSL project.

In this homework you will gain experience with inheritance by adding classes and their instantiation mechanism. Doing this homework is essential for successful completion of the rest of this course, since all other homeworks will share the same features of this homework: the underlying DSL and using git commands like branching, merging, committing, pushing your code into your Git repo, creating test cases and build scripts, reusing the DSL that you will design and implement and employing various tools like a debugger for diagnosing problems with your applications.

First things first, if you haven't done so, you must create your account at [Github](https://github.com), a Git repo management system. Then invite me, your course instructor as your collaborator – my github ID is 0x1DOCD00D and your TA whose github ID is **laxmena**. Since it is a large class, please avoid direct emails from other accounts like funnybunny2000@gmail.com and use the corresponding channels on Teams instead. You will always receive a response within 12 hours at most.

Next, if you haven't done so, you will install [IntelliJ](https://www.jetbrains.com/student/) with your academic license, the JDK, the Scala runtime and the IntelliJ Scala plugin and the [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html) and make sure that you can create, compile, and run Java and Scala programs. Please make sure that you can run [various Java tools from your chosen JDK between versions 8 and 17](https://docs.oracle.com/en/java/javase/index.html). It is highly recommended that you use Scala version 3.1.1.

Many students found the following book that I recommended very useful: [the fifth edition of the book on Programming in Scala by Martin Odersky and Lex Spoon et al](https://www.artima.com/shop/programming_in_scala_5ed). There are many other books and resources available on the Internet to learn Scala. Those who know more about functional programming can use the book on Functional Programming in Scala published on Sep 14, 2014 by Paul Chiusano and Runar Bjarnason and it is available using your academic subscription on [SafariBooksOnline](https://learning.oreilly.com/home/).

## Overview
In the first homework, you created a DSL for binary set theory operations where you added expressions for storing results of some computations in variables and using them in different scopes and you implemented a feature called ***macros*** that expanded macro definitions in the expressions where the macro names are used.

In this homework you will build upon your implementation of the DSL from the previous homework(s) by adding classes with inheritance. Your classes will contain fields and methods with private, public and protected accesses to its members. In your implementation, a class is a collection of methods that contain expressions for binary set theory operations that you implemented in your first homework. The return type of a method is defined by the return type of the last expression of the method that is always the top type Expression or whatever you named it in your previous implementation. Each method takes an arbitrary number of parameters and it can operate on the fields of the class that this method belongs to as well as any variable with an active binding in the parent scopes. Since a method defines a block of code the scoping rules apply to the method as you defined them for an arbitrary scope in the first homework. A method can be referenced and invoked using a specialized data type that you will introduce, e.g., InvokeMethod("methodName").

You will implement the following class-related operations.
- Class declaration in a given scope with the definitions of its fields and methods.
- Composing classes to enable inheritance using the datatype ***Extends***. If some class named A Extends a class named B then the class A inherits all methods and the fields of the class B in addition to the methods and the fields that are defined in the class A.
- Each class will have one constructor that will be invoked when a class is instantiated. A constructor can be thought of as a method that is invoked when a data type ***NewObject*** is applied to the datatype ***ClassName***.
- Multiple inheritance is not allowed.
- You will define data types for referencing class members and for enabling virtual dispatch. A virtual dispatch table will be used to determine the dynamic type of an object and to invoke a corresponding overridden method.
- Nested classes are allowed similarly to your previous nested scopes implementation.

Consider the following example of using your language with respect to classes. Please note that it is an example and not a strict guide to your implementation. You are free to experiment to choose signatures of the data types that you like as long as you explain your rationale in your documentation.
```scala
//creating a class and populating it with methods. The data type ClassDef declares a class
//with a gine name and the programmer can define the content of this class. Please note that
//methods may have more than one expression or none (what return type would it be in this case?)
ClassDef("someClassName", Field("f"), Constructor(Assign("f", Value(2))), Method("m1", Value(1)), Method("m2", Union(Variable("setX"), Value(1))))
//check if an object is in the set
NewObject("someClassName", Variable("z")) //the variable z is bound to the new instance
//in this example we use an infix method called Extends to allow a new class named derivedClassName
//to inherit all definitions from the previously defined class someClassName.
ClassDef("derivedClassName", Field("ff")) Extends ClassDef("someClassName") 
```

This homework script is written using a retroscripting technique, in which the homework outlines are generally and loosely drawn, and the individual students improvise to create the implementation that fits their refined objectives. In doing so, students are expected to stay within the basic core requirements of the homework (e.g., to implement classes with the relation inheritance) and they are free to experiments. Asking questions is important, so please ask away on the corresponding Teams channels!

## Functionality
In your language, objects can be created dynamically as instances of specific classes as part of the expressions in addition to being predefined in the environment. How you implement the object creation process is left to your imagination and your rationale. Internally, each object is represented by some record where relations between methods of the parent classes are described using some data structures like a hash map. Virtual tables should be implemented by keeping the track of all methods defined in parent classes and overridden by child classes.

Your homework can be divided roughly into five steps. First, you design the data types that represent classes. Second, you design the data types that represent class fields and methods. You will add the logic for combining procedural and data abstractions into your class implementation with access modifiers, which will later be used in the extensions of this language in your next homeworks. Next, you will define the data types for representing inheritance and you will create an implementation of class instantiation and method invocation. Fourth, you will create an algorithm for handling parameter lists in methods and class constructors. Finally, you will create Scalatest tests to verify the correctness of your implementation. You will write a report to explain your implementation and the semantics of your language.

## Baseline
To be considered for grading, your project should include the constructs ClassDef, Field, Method, NewObject, InvokeMethod and Extends and all required operations and your project should be buildable using the SBT, and your documentation must specify how you create and evaluate expressions with class inheritance in your language.

## Teams collaboration
You can post questions and replies, statements, comments, discussion, etc. on Teams. For this homework, feel free to share your ideas, mistakes, code fragments, commands from scripts, and some of your technical solutions with the rest of the class, and you can ask and advise others using Teams on language design issues, resolving error messages and dependencies and configuration issues. When posting question and answers on Teams, please select the appropriate folder, i.e., **hw2** to ensure that all discussion threads can be easily located. Active participants and problem solvers will receive bonuses from the big brother :-) who is watching your exchanges on Teams (i.e., your class instructor). However, *you must not post the source code of your program or specific details on how your implemented your design ideas!*

## Git logistics
**This is an individual homework.** Separate private repositories will be created for each of your homeworks and for the course project. Inviting other students to join your repo for an individual homework will result in losing your grade. For grading, only the latest push timed before the deadline will be considered. **If you push after the deadline, your grade for the homework will be zero**. For more information about using the Git please use this [link as the starting point](https://confluence.atlassian.com/bitbucket/bitbucket-cloud-documentation-home-221448814.html). For those of you who struggle with the Git, I recommend a book by Ryan Hodson on Ry's Git Tutorial. The other book called Pro Git is written by Scott Chacon and Ben Straub and published by Apress and it is [freely available](https://git-scm.com/book/en/v2/). There are multiple videos on youtube that go into details of the Git organization and use.

I repeat, make sure that you will give the course instructor and your TA the read/write access to *your repository* so that we can leave the file feedback.txt with the explanation of the grade assigned to your homework.

## Discussions and submission
As it is mentioned above, you can post questions and replies, statements, comments, discussion, etc. on Teams. Remember that you cannot share your code and your solutions privately, but you can ask and advise others using Teams and StackOverflow or some other developer networks where resources and sample programs can be found on the Internet, how to resolve dependencies and configuration issues. Yet, your implementation should be your own and you cannot share it. Alternatively, you cannot copy and paste someone else's implementation and put your name on it. Your submissions will be checked for plagiarism. **Copying code from your classmates or from some sites on the Internet will result in severe academic penalties up to the termination of your enrollment in the University**. When posting question and answers on Teams, please select the appropriate folder, i.e., hw1 to ensure that all discussion threads can be easily located.


## Submission deadline and logistics
Monday, February 28 at 6AM CST by turning in your submission using the corresponding Assignments entry in Teams where you submit the link to your Github repository. Your repo will include the code, your documentation with instructions and detailed explanations on how to assemble and deploy your program along with the tests and a document that explains the semantics of your language, and what the limitations of your implementation are. Again, do not forget, please make sure that you will give your instructor/TA the write access to your repository. Your name should be shown in your README.md file and other documents. Your code should compile and run from the command line using the commands **sbt clean compile test** and **sbt clean compile run**. Also, you project should be IntelliJ friendly, i.e., your graders should be able to import your code into IntelliJ and run from there. Use .gitignore to exlude files that should not be pushed into the repo.

## Evaluation criteria
- the maximum grade for this homework is 10%. Points are subtracted from this maximum grade: for example, saying that 2% is lost if some requirement is not completed means that the resulting grade will be 10%-2% => 8%; if the core homework functionality does not work, no bonus points will be given;
- only some basic expression language is implemented without scopes and assignments and macros and nothing else is done: up to 10% lost;
- for each use of **var** instead of **val** 0.2% will be substracted from the maximum grade unless the use is justified by using vars only in local scopes for optimization purposes;
- for each non-spelling-related problem reported by the IntelliJ code analysis and inspection tool 0.2% will be substracted from the maximum grade;
- having less than five unit and/or integration tests that show how your implemented features work: up to 5% lost;
- missing comments and explanations from the program: up to 5% lost;
- no instructions in your README.md on how to install and run your program: up to 5% lost;
- the program crashes without completing the core functionality or it is incorrect: up to 10% lost;
- the documentation exists but it is insufficient to understand how you assembled and deployed all language components: up to 8% lost;
- the minimum grade for this homework cannot be less than zero.

That's it, folks!
