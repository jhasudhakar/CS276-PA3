# CS276-PA3

In this programming assignment we implemented:

1. Three ranking algorithms
    1. Cosine Similarity
    2. BM25F
    3. Cosine Similarity with Smallest Window Boost
2. Two tuning strategies
    1. Random
    2. Local Hill Climbing

For details please read `report.pdf`.

## Java 8

Java 8 features such as [lambda expressions](http://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html) and [streams](http://docs.oracle.com/javase/tutorial/collections/streams/index.html) are used extensively in this project. We found with Java 8 it's much more enjoyable to write less cumbersome Java code.

We also use reflection to load parameter configuration from config files.
