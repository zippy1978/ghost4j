Contributing
============

Anyone can contribute to the project, any new pull request is welcomed: bug fixes, new features or any other contribution of any kind.

However, if you consider contributing, try to follow the rules described below.

### Code conventions

As a Java project, Ghost4J must follow the [standard Java code convention](http://www.oracle.com/technetwork/java/javase/documentation/codeconvtoc-136057.html) , other rules are defined below.

#### File headers

Every source file of the project must include the following header:

    /*
     * Ghost4J: a Java wrapper for Ghostscript API.
     * 
     * Distributable under LGPL license.
     * See terms of license at http://www.gnu.org/licenses/lgpl.html. 
     */

#### Author

The initial author of each source file must appear in the class documentation comment. First name, last name and e-mail address of the author must be provided with the @author tag as described below:

    @author Gilles Grousset (gi.grousset@gmail.com)

#### Comments

Javadoc comments must be provided for every method in every class.

Code comments should be used to detail code logic when necessary (long or complicated method).

#### Unit tests

Every 'testable' class added to the project must be tested by a unit test class.

What is meant by the 'testable class' term is classes where methods logic may lead to different results. In this case, it is important to test at least that the method is working as expected under normal circumstances. Error case tests are not mandatory but are recommended. The choice of error cases to test is left to the developer.

Methods are not tested when:

* There is no logic in it (example: getters and setters).
* The test cannot be run as a 'black box'. That is when the test cannot run without involving another - uncontrolled - software component.

### External libraries 

As a library Ghost4J must be as light as possible. To do that, dependencies to other libraries must be kept as low as possible.

Every new library addition must be discussed by developers.


