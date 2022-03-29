# Homework 4
### Description: using a language for users of the set theory that you created in the previous homeworks add constructs for control structures and exception handling using lazy evaluation and call-by-name.
### Grade: 10%

## Preliminaries
In the previous homework assignments you gained experience with creating and managing your Git repository and implementing your first *domain-specific language (DSL)* using Scala for writing and evaluating set operation expressions for users of the [set theory](https://en.wikipedia.org/wiki/Set_theory). Using your DSL users can describe and evaluate [binary operations on sets](https://images-na.ssl-images-amazon.com/images/G/31/img15/books/tiles/9352036042_chemistry.pdf) using variables and scopes where elements of the sets can be objects of any type. However, it is not a usable DSL since there are no branching constructs, e.g., IF-THEN-ELSE and exception handling functionality. Yet, you learned how to create [Scalatest](https://www.scalatest.org/) tests to test your implementation and to create build scripts using [SBT to build and run scripts](https://www.scala-sbt.org/) for your DSL project.

In this homework you will gain experience with using lazy evaluation and call-by-name to implement branching constructs and exception handling. First things first, if you haven't done so, you must create your account at [Github](https://github.com), a Git repo management system. Then invite me, your course instructor as your collaborator – my github ID is 0x1DOCD00D and your TA whose github ID is **laxmena**. Since it is (still) a large class, please avoid direct emails from other accounts like funnybunny2000@gmail.com and use the corresponding channels on Teams instead. You will always receive a response within 12 hours at most and in reality the response time is within 30 minutes on average.

Next, if you haven't done so, you will install [IntelliJ](https://www.jetbrains.com/student/) with your academic license, the JDK, the Scala runtime and the IntelliJ Scala plugin and the [Simple Build Toolkit (SBT)](https://www.scala-sbt.org/1.x/docs/index.html) and make sure that you can create, compile, and run Java and Scala programs. Please make sure that you can run [various Java tools from your chosen JDK between versions 8 and 17](https://docs.oracle.com/en/java/javase/index.html). It is highly recommended that you use Scala version 3.1.1.

Many students found the following book that I recommended very useful: [the fifth edition of the book on Programming in Scala by Martin Odersky and Lex Spoon et al](https://www.artima.com/shop/programming_in_scala_5ed). There are many other books and resources available on the Internet to learn Scala. Those who know more about functional programming can use the book on Functional Programming in Scala published on Sep 14, 2014 by Paul Chiusano and Runar Bjarnason and it is available using your academic subscription on [SafariBooksOnline](https://learning.oreilly.com/home/).

## Overview
In the previous homeworks, you created a DSL for binary set theory operations where you added expressions for storing results of some computations in variables and using them in different scopes and you implemented a feature called ***macros*** that expanded macro definitions in the expressions where the macro names are used and you introduced classes and their inheritance composition mechanism. Also, you add abstract classes and interfaces with inheritance and interface implementation composition mechanisms. Your abstract classes contain fields and methods with private, public and protected accesses to its members, similarly to your implementation of concrete classes.

The goal of his homework is to learn how to use lazy evaluation and call-by-name to implement the branching construct **IF** and exception handling in your DSL. The branching construct **IF** can be viewed as a function that takes three parameters and it has the following signature.
```scala
def IF(condition: => Boolean, thenClause: => Set[Any], elseClause: => Set[Any]): Set[Any] = 
  if condition then thenClause else elseClause
end IF
```
Essentially, all parameters are passed lazily to the construct **IF** and they are not evaluated strictly, i.e., they are evaluated only if used inside the body of **IF**. That is, if the condition is evaluated to ***true*** then only ***thenClause*** is evaluated and ***elseClause*** is not. You may also try to implement ***while-do*** loops even though it is not required for his homework.

Next, you will implement exception handling where you will introduce a construct for declaring exception classes, a construct for throwing exceptions and a construct for catching exceptions similar to ***try...catch(...)...*** constructs in many OO programming languages. Consider the following example of using your language with respect to the branching expression and exception handling. Please note that it is an example and not a strict guide to your implementation. You are free to experiment to choose signatures of the data types that you like as long as you explain your rationale in your documentation.
```scala
//declare some exception class
ExceptionClassDef("someExceptonClassName", Field("Reason"))
//this example shows how users use branching and exception constructs
Scope("scopename", CatchException("someExceptonClassName", //this parameter specifies what exceptions to catch in this block
          //this parameter is the try code block
          IF(Check("someSetName", Value(1)), 
          Insert(Variable("var"), Value(1)),
          ThrowException(ClassDef("someExceptonClassName"), Assign(Field("Reason"), "Check failed"))),
          Insert(Variable("var"), Value(3)),
          //and this parameter is the catch code block
          //the variable "storageOfException" is bound to the exception class someExceptonClassName
          //and the value of its field, Reason is retrieved and stored in a set bound to the variable var.
          Catch(Variable("storageOfException"), Insert(Variable("var"), Field("Reason")))))
```

This homework script is written using a retroscripting technique, in which the homework outlines are generally and loosely drawn, and the individual students improvise to create the implementation that fits their refined objectives. In doing so, students are expected to stay within the basic core requirements of the homework (e.g., to implement classes with the relation inheritance) and they are free to experiments. Asking questions is important, so please ask away on the corresponding Teams channels!

## Functionality
In the expanded DSL in this homework, code blocks are defined not only in ***Scopes*** but also inside the branching construct ***IF*** and exception handlers that can be thought of as variants of your previous ***Scope*** and ***ClassDef*** implementations. The interesting part of the homework is how you will define the semantics of exception throwing and handling that interrupts the linear flow of the code block evaluation. A straightforward semantics is to evaluate all expressions until the expression ***ThrowException*** is encountered. Then all other expressions between the expression ***ThrowException*** and the corresponding expression ***Catch*** are bypassed and the control is transfered to the code block defined in the second parameter of the corresponding ***Catch*** construct that can be located in one of the outer scopes.

Your homework can be divided roughly into five steps. First, you design the data types that represent exception classes and the branching (and looping) constructs. Second, you design the data types that represent exception throwing and catching in your DSL. As in the previous homework you will add the logic for combining procedural and data abstractions into your class implementation with methods. Next, you will implement the branching expression. Fourth, you will create an algorithm for exception handling. Finally, you will create Scalatest tests to verify the correctness of your implementation. You will write a report to explain your implementation and the semantics of your language.

## Baseline
To be considered for grading, your project should include the constructs IF, ExceptionClassDef, CatchException, and ThrowException and all required operations and your project should be buildable using the SBT, and your documentation must specify how you create and evaluate expressions with branching and exception handling implementation in your language.

## Teams collaboration
You can post questions and replies, statements, comments, discussion, etc. on Teams. For this homework, feel free to share your ideas, mistakes, code fragments, commands from scripts, and some of your technical solutions with the rest of the class, and you can ask and advise others using Teams on language design issues, resolving error messages and dependencies and configuration issues. When posting question and answers on Teams, please select the appropriate folder, i.e., **hw4** to ensure that all discussion threads can be easily located. Active participants and problem solvers will receive bonuses from the big brother :-) who is watching your exchanges on Teams (i.e., your class instructor). However, *you must not post the source code of your program or specific details on how your implemented your design ideas!*

## Git logistics
**This is an individual homework.** You can reuse your repo from the previous homework or you can create a separate private repository for each of your homeworks and for the course project. Inviting other students to join your repo for an individual homework will result in losing your grade. For grading, only the latest push timed before the deadline will be considered. **If you push after the deadline, your grade for the homework will be zero**. For more information about using the Git please use this [link as the starting point](https://confluence.atlassian.com/bitbucket/bitbucket-cloud-documentation-home-221448814.html). For those of you who struggle with the Git, I recommend a book by Ryan Hodson on Ry's Git Tutorial. The other book called Pro Git is written by Scott Chacon and Ben Straub and published by Apress and it is [freely available](https://git-scm.com/book/en/v2/). There are multiple videos on youtube that go into details of the Git organization and use.

I repeat, make sure that you will give the course instructor and your TA the read/write access to *your repository* so that we can leave the file feedback.txt with the explanation of the grade assigned to your homework.

## Discussions and submission
As it is mentioned above, you can post questions and replies, statements, comments, discussion, etc. on Teams. Remember that you cannot share your code and your solutions privately, but you can ask and advise others using Teams and StackOverflow or some other developer networks where resources and sample programs can be found on the Internet, how to resolve dependencies and configuration issues. Yet, your implementation should be your own and you cannot share it. Alternatively, you cannot copy and paste someone else's implementation and put your name on it. Your submissions will be checked for plagiarism. **Copying code from your classmates or from some sites on the Internet will result in severe academic penalties up to the termination of your enrollment in the University**. When posting question and answers on Teams, please select the appropriate folder to ensure that all discussion threads can be easily located.


## Submission deadline and logistics
The submission deadline is Monday, April 11 at 6AM CST. **THERE WILL BE NO SUBMISSION DEADLINE EXTENSION!** You will turn in your submission using the corresponding Assignments entry in Teams where you submit the link to your Github repository. Your repo will include the code, your documentation with instructions and detailed explanations on how to assemble and deploy your program along with the tests and a document that explains the semantics of your language, and what the limitations of your implementation are. Again, do not forget, please make sure that you will give your instructor/TA the write access to your repository. Your name should be shown in your README.md file and other documents. Your code should compile and run from the command line using the commands **sbt clean compile test** and **sbt clean compile run**. Also, you project should be IntelliJ friendly, i.e., your graders should be able to import your code into IntelliJ and run from there. Use .gitignore to exlude files that should not be pushed into the repo.

## Evaluation criteria
- the maximum grade for this homework is 10%. Points are subtracted from this maximum grade: for example, saying that 2% is lost if some requirement is not completed means that the resulting grade will be 10%-2% => 8%; if the core homework functionality does not work, no bonus points will be given;
- only some basic expression language is implemented without branching and exception handling: up to 10% lost;
- you should document how you implemented your solution to the questions in the section functionality and provide tests that result in error messages for prohibited uses of the implementation of the branching expression and exception handling. Failure to do so results in losing up to 10% of your grade.
- for each use of **var** instead of **val** 0.2% will be substracted from the maximum grade unless the use is justified by using vars only in local scopes for optimization purposes;
- for each non-spelling-related problem reported by the IntelliJ code analysis and inspection tool 0.2% will be substracted from the maximum grade;
- having less than five unit and/or integration tests that show how your implemented features work: up to 5% lost;
- missing comments and explanations from the program: up to 5% lost;
- no instructions in your README.md on how to install and run your program: up to 5% lost;
- the program crashes without completing the core functionality or it is incorrect: up to 10% lost;
- the documentation exists but it is insufficient to understand how you assembled and deployed all language components: up to 8% lost;
- the minimum grade for this homework cannot be less than zero.

That's it, folks!
