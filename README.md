
# Programming Exercise: Pick-and-Ship

Notes on the Pick-and-Ship coding exercise from OpenTempo.


## Overview

The Pick-and-Ship programming exercise is an opportunity to demonstrate:

- parsing text files
- serializing objects to a text format
- implementing an approximate algorithm (with some relevance to OpenTempo's automated scheduling product)
- documenting code
- class structure design
- unit testing
- automated deployment

## Usage

This project is written in Java 7 and built with Apache Maven 3.2.1.  After installing Java and Maven, the project can be built by going to the repository root and running:

    mvn clean install

To run the code (also from the repo root), run:

```
java -classpath target/pickship-1.0-SNAPSHOT.jar pickship.PickShipApp external/pick-ship/inventory.txt external/pick-ship/order3.txt
```


## Problem Description

### Input Data

The file inventory.txt contains a serialized Inventory, which is a list of Items.

```
INVENTORY START
ITEM START 
CODE: <String> 
NAME: <String> 
WEIGHT: <double> 
ITEM END 
....
ITEM START 
CODE: <String> 
NAME: <String> 
WEIGHT: <double> 
ITEM END 
INVENTORY END
```

The files order1.txt, etc., contain a serialized Order, which in turn contains a list of (item, quantity) tuples.

```
ORDER START
ORDER NUMBER: <Integer> 
CUSTOMER CODE: <String> 
ITEM: <code>, <qty> 
ITEM: <code>, <qty> 
ITEM: <code>, <qty>
...
ITEM: <code>, <qty> 
ORDER END
```

As you can see, the serialization format is line-based and easily parsed.  There are some unspecified aspects, such as:

- Are item codes unique in the inventory?  I assume that they are.
- Will an order ever contain an item code not in the inventory?  I assume not.
- Can there be whitespace or comment line in the files?  I assume not (mostly).
- Is `<qty>` an integer? I assume it is.


### Output Data

Here is the file format for the Pick/Ship list:

```
PICK SHIP START
ORDER NUMBER: <Integer>
TOTAL SHIP WEIGHT: <double>
BOXSTART:<Integer> /*indicatestheboxnumberformultiplebox shipments */
SHIP WEIGHT: <double>
ITEM: <Code>, <Qty>
ITEM: <Code>, <Qty>
...
ITEM: <Code>, <Qty>
BOX END
...
[ /* Optionally include more box records if required */ ]
...
PICK SHIP END
```

I thought it was quirky that there is no space separating `BOXSTART:` from `<Integer>`.  There is also little guidance on whether the box number is optional when there is only a single box and whether the box numbers should be consecutive, starting from 1.  I assumed that box numbers starts from 1, increase consecutively, and are present even for a single box.

As described in the Programming exercise documentation:

> The main program entry point should accept as command­line arguments: the name of the inventory file, and the name of the order file. For example:
> 
>     java MyProgram <inventory­file> <order­file>
> 
> After parsing the file, and processing the order, the pick/ship list should be generated. You can either print to the screen, or output to a file. This is up to you.

I chose to output the pick/ship list to stdout.


## The Bin Packing Algorithm

The programming exercise requires the program to be "very good" at filling boxes.  Note that it does not specify "optimal".  As I recall, this type of problem is called bin packing.  Some googling turns up the wikipedia page for bin packing (http://en.wikipedia.org/wiki/Bin_packing_problem).  One-dimensional bin packing attempts to minimize the number of boxes (of a fixed size) used to store a variable number of items which can vary in size.  Regarding solutions, the wikipedia page has this to say:

- "Martello and Toth[11] developed an exact algorithm for the 1-D bin-packing problem, called MTP."
- There is a free open-source Java constraint solver with bin packing examples.  See http://optaplanner.org.
- "the first fit algorithm provides a fast but often non-optimal solution, involving placing each item into the first bin in which it will fit. It requires Θ(n log n) time, where n is the number of elements to be packed. The algorithm can be made much more effective by first sorting the list of elements into decreasing order (sometimes known as the first-fit decreasing algorithm), although this still does not guarantee an optimal solution, and for longer lists may increase the running time of the algorithm. It is known, however, that there always exists at least one ordering of items that allows first-fit to produce an optimal solution."

One potential advantage of first-fit (descending) is that one can explain it such that another person can imagine how it would be done and how sensible it seems.  Also, I imagine that it could be efficiently implemented by humans.

Here is a bin-packing example application, using Optaplanner: http://docs.jboss.org/drools/release/6.0.1.Final/optaplanner-docs/html_single/index.html#cloudBalancingTutorial

For the purposes of this exercise, I think implementing a "first fit descending" will pack well enough and demonstrate my ability to code an algorithm.  The program design should be modular enough that an external library, such as Optaplanner, or a more accurate algorithm from http://www.or.deis.unibo.it/kp/Chapter8.pdf could be used.

Note that for small orders, a brute-force algorithm could be fast enough.  I imagine in a production system, one might have a system that produces optimal fits for small orders and approximate ones for larger orders.  


## Class Design

From the project description and input files, I've extracted several basic classes:

- Inventory.  Has items.
- Item. Has code, name, weight.
- Order.  Has number, customer code, and line items.
- LineItem.  Has item code and quantity.
- PickShip.  Has order number, total ship weight, and boxes.
- Box.  Has integer (index), ship weight, and line items.

To tie these classes together and implement the application, I've created:

- PickShipApp.  Implements the command line functionality and ties the various pieces together
- InventorySerializer and OrderSerializer.  Parse input files and serializing Inventory and Order objects (for debugging).
- Packer1D.  An interface that matches the description of a one-dimensional bin packing problem.
- NextFitPacker.  A very simple implementation of bin packing.
- FirstFitDescendingPacker.  A slightly more complex implementation of Packer1D.

One design decision was to put Item into LineItem, instead of using the Item code.  This requires Inventory to exist before reading Order, and makes parsing an order file dependent on Inventory.  It also disallows line items with item codes not in Inventory.  Though all these constraints seemed reasonable for this exercise, the increased coupling makes testing order file parsing more complex.


## Parsing and Serialization

Parsing is implemented using a line-based state machine.  The file is read line by line, and then an action is taken and a state transition occurs,  depending on the line and current state.

Serialization is an even more straightforward walk of the objects, writing the various START lines, value lines and END lines, as the objects are traversed.

All parsing and serialization is done by hand, which is likely brittle and error-prone.  


## Testing and Error handling

Error handling is done primarily by throwing an Exception with a simple error message or by not catching Exceptions thrown by other parts of the system.  For example, specifying a file that does not exist on the command line would result in an unhandled NoSuchFileException rather than a attractive and informative error message.

Some conditions not yet checked or tested:

- Negative weights.
- Duplicate item codes in inventory.
- Weights larger than box size.

Throwing Exception.  Should throw exceptions that are more specialized and more informative (e.g. At what line did parsing fail?)

For the sake of demonstration, one unit test was implemented.  Production code should have much better test coverage as well as specialized exception classes with informative error messages.  For example, a parsing error could result in a ParsingException specifying what line the error occurred at and what specifically was wrong, like missing an Item Code.


## Documenting

There are some JavaDoc comments in the code, as well as a sprinkling of inline comments explaining some of the implementation details.


## Extra Credit

> Describe your thought process around your implementation of this problem; why did you choose the algorithm you implemented. What problems did you encounter? How did you fix them?

When solving this problem, the first thing I did was read the Programming Exercise description.  My immediate impression was that this problem allows candidates to demonstrate a nice balance of software engineering methods and algorithmics.  Recognizing the problem as related to bin packing, I did some research to see what algorithms and implementations were available.  I decided to implement my own algorithm, to avoid the complexity of OptaPlanner and demonstrate my own coding abilities.  The First Fit Descending algorithm looked simple to implement and hopefully is close enough to optimal to suffice for this exercise.

After researching bin packing, I then reread the problem description and broke the problem into some main tasks I wanted to expand upon as I wrote a solution:

- Hello World
- Examine Inputs and Outputs
- Class Design
- Parsing and Serializing
- Testing
- Documenting

Since it has been a while since I've written Java, so I started by installing Java, downloading an IDE or two, and creating a HelloWorld console application, to make sure my Java installation was working.  Then I got down to business.

After studying the input and output file descriptions and examples, I wrote some basic classes (Order, Item, Inventory, LineItem, Box, ...).  I then wrote parsers for Inventory and Orders.  That was quickly followed by serializers for those classes.  While not necessary for the problem, they allowed me to check that I could make a parse-the-serialize round trip without losing any information.  After writing a PickShip serializer, I extracted the serializers out of the main app class (PickShipApp) into their own classes, to keep the classes tidy and small.

Next came algorithmics.  I decided on a simple API for the bin packing algorithm itself, which left the application with the task of transforming an order into the algorithm input and the output into a PickShip.  That was a bit tricky.  Finally, I implemented a very simple packing algorithm, which I tested on the 3 example orders.  Then I implemented the First Fit Descending algorithm.  This was complicated by the API constraint I had set myself.  Thankfully, StackOverflow has an answer for everything, including get a sorted list of indices of an array.  This is something that I'd encountered in R before, which allows one to "sort" an array without losing information about the original position of the elements.  Time complexity of the First Fit Descending algorithm is O(nlogn).  However, my implementation is O(n^2), since I am simply scanning the list of bins (for each item) to find where to put an item and the size of that list is O(n), where n is the number of items.

Now that the program was working, I tackled the hard parts: deployment, documentation, and testing.  Being new to Maven and JUnit, I trudged up the learning curve enough to get some token functionality.

The vast majority of the problems I encountered were of the type: How do I do X?  I'll google it.  How do I install Maven?  How do I tell the compiler I'm using Java 7?  How do I parse a file line-by-line?  How do I split a string?  How do I sum a list of numbers?   What are Generics?  

I encountered a more interesting software design problem when I tried to test parsing the inventory file.  My parser took a filename as input.  While this seemed reasonable before I tried to test it, it complicated testing.  Was I to write a temporary file during the unit, in order to test the parser?  Instead, I changed the parser to take a list of strings.  While not ideal for large files, that would suffice for this exercise. 

