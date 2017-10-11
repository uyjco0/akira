	
**Akira**
-----

![Akira](images/akira.jpg)

----------


Goal
----

Sample application to learn the following technologies:

 - Play:
     - SBT configuration and plugins
     - Dependency Injection
     - Actions, Controllers, and Results
     - Assets management
     - Templates, Forms and Bootstrap
     - Session management, authentication and Silhouette
     - Concurrency:
        - Asynchronous vs. synchronous:
            - Blocking vs non-blocking
        - Execution contexts and Thread pools:
            - Work stealing (ForkJoin) 
            -  Asynchronous Actions and thread pools
     - Database access, Slick and PostgreSQL
     - Akka streams and WebSockets
 - Akka:
     - Isolated mutability, Messages and Actors
     - Actor System
     - Dispatcher

Prerequisites
-------------

Scala basic knowledge:

 - Classes:
     - Standard, abstract, case, trait
     - Companion object
     - Parameters, main and auxiliary constructor
     - Visibility
     - Type parametrization
 - Function and methods:
     - Function object (functor), Apply and FunctionN trait
     - Function literals
     - Methods and difference with Function Objects
     - Partial function, Partial application, Currying and Closures
     - Syntactic sugar for function/methods with one parameter:
        - Curly braces, white space
    - Repeated parameters, default values and implicits
 - Basic types
 - String interpolation
 - Pattern matching and case classes
 - For comprehension:
    - Map, filter, foreach, flatMap, foldLeft, foldRight
 - Options
 - Lazy instantiation
 - Concurrency with Futures and Promises

How to start
-------------

To start the application:

 - SBT:
     - Install SBT:
         - See [here](http://www.scala-sbt.org/release/docs/Setup.html)
 - Github:
     - Clone the repo:
         - Run 'git clone https://github.com/uyjco0/akira.git'
 - PostgreSQL database:
     - Create database and needed tables:
         - Run 'conf/db/postgresql/create.sql'
             - Explained at: conf/db/postgresql/README.md
 - Aplication configuration:
     - In 'conf/application.conf' change the string 'XXX' by your server domain/IP
     - In 'conf/application.conf' change the string '*** CHANGE IT ***' by your key
     - In 'conf/silhouette.conf' change the strings '*** CHANGE IT ***' by your keys
 - Client Javascript:
     - The application is using [IpInfo service](https://ipinfo.io) to get your client current location:
         - In 'app/assets/javascripts/client.js' change the string 'XXX' by your token
 - Run the application:
     - Run 'sbt', and once in the sbt console, run 'runProd' 
