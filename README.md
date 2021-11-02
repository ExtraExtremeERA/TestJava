<div align="center">
  <h1>Denery's Frameworks (Libraries and Java Packages) use examples.</h1>
  <h3>Libraries shown in this repository are more performance oriented.</h3>
  <h4>The whole project contains a couple of JCStress/JUnit test and JMH microbenchmarks.</h4>
</div>

List of examples:
------
* Java.
  * VarHandles.
* ASM Bytecode Manipulation framework.
  * Core API.
    * Java Instrumentation API.
  * Tree API
    * Java Attach API. (With Instrumentation API)
* Reactor Netty.
  * Project Reactor.
* FastUtil. (unimi.dsi)
* AsyncUtil. (ibm)
* Threadly. (ONLY JAVA 8)

List of concepts:
------
* network-bytecode-manipulation.
  * ASM Tree API.
  * Reactor Netty.
  * Attach API.
  * FastUtil. (unimi.dsi)
  * AsyncUtil. (ibm)

Project structure:
------
Project structure is simple - all packages in
the TestJava directory is gradle subprojects with 
library usage examples except: "regular-java" - 
pure java interesting packages examples and 
"proof-of-concepts" - weird concepts which use
stuff shown in "test" projects.

For more info about each example see [this](/settings.gradle) file.

Using and improving examples:
------
The whole repo uses Java 16, excepts some examples described in:
[settings.gradle](/settings.gradle)
Every project has JUnit, JMH, JCStress and JOL dependencies,
to run JMH in subproject (if subproject has JMH tests)
run command: gradle jmhPROJECT-NAME-CAPITALIZED where
PROJECT-NAME-CAPITALIZED is (obvious) subproject's name
CAPITALIZED, for example: gradle jmhREGULAR-JAVA.
Also with JCStress but jcstressPROJECT-NAME-CAPITALIZED.

If you look at a [build.gradle](/build.gradle) file then you'll see
hellish gradle buildscript, but it looks like this because adding each
subproject was really boring, so to add your example you'll need:
1. Register subproject in [settings.gradle](/settings.gradle).
2. Add new String matching the name of your subproject
to the "tests" ArrayList variable in [build.gradle](/build.gradle) (line 81).
3. Add dependency to the switch statement in dependencies block in [build.gradle](/build.gradle)
4. (Optional) if you're adding something with a custom JAR manifest
then add acceptable project name and manifest in manifestsInit() 
function in [build.gradle](/build.gradle) (line 137)

Some formalization rules:
1. Subproject's names with more than one word should divided by '-'.
2. Subproject dir should match subproject name except these
unoriginal names like: fastutil or asyncutil, in this case you should
also before names add companies.
3. You should make changes in a subproject in the first creator's package.
for example if you see: "io.denery" or other package you shouldn't add your
"com.myname" package, you should edit "io.denery"
4. Be as simple as you can, don't add unnecessary dependencies.

License:
------
The project is licensed under [ISC License](/ISC)