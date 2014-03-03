
Notes on the Pick-and-Ship coding exercise from OpenTempo.


Main Points:

- algorithm
- application design
- testing (unit tests)
- documenting (javadoc style in methods and classes)
- deploying (Maven?)

## Algorithm

The wikipedia page for bin packing (http://en.wikipedia.org/wiki/Bin_packing_problem) has this to say:

- Exact algorithm: "Martello and Toth[11] developed an exact algorithm for the 1-D bin-packing problem, called MTP."
- Free open-source Java constraint solver with bin packing examples: optaplanner.org.
- "the first fit algorithm provides a fast but often non-optimal solution, involving placing each item into the first bin in which it will fit. It requires Θ(n log n) time, where n is the number of elements to be packed. The algorithm can be made much more effective by first sorting the list of elements into decreasing order (sometimes known as the first-fit decreasing algorithm), although this still does not guarantee an optimal solution, and for longer lists may increase the running time of the algorithm. It is known, however, that there always exists at least one ordering of items that allows first-fit to produce an optimal solution."

One potential advantage of first-fit (descending) is that one can explain it such that another person can imagine how it would be done and how sensible it seems.  And efficient to implement for humans.

Bin-packing example application, using Optaplanner: http://docs.jboss.org/drools/release/6.0.1.Final/optaplanner-docs/html_single/index.html#statusOfOptaPlanner.

## Application Design

### Input Data

### Basic Classes

Objects, implementation services.

### Class Interaction

Dependencies, Workflows, Joins
