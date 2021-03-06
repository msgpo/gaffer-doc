${HEADER}

${CODE_LINK}

We'll walk through it in some detail.

First, let’s do the most basic thing; take some data from a csv file, load it into a Gaffer graph and then run a very simple query to return some graph edges. 
We'll also look at specific examples of an ${ELEMENT_GENERATOR_JAVADOC2} and ${SCHEMA_JAVADOC2}.

We are going to base the following walkthroughs on Road Traffic data, a simplified version of the ${ROAD_TRAFFIC_EXAMPLE_LINK}. 
Throughout these walkthroughs we will gradually build up the graph, so as we learn about new features we will add them to our Graph schema. 

This is the data we will use, a simple csv in the format road,junctionA,junctionB. Each row represents a vehicle travelling on the road between junctionA and junctionB.
${DATA}

The first thing to do when creating a Gaffer Graph is to model your data and write your Gaffer Schema. 
When modelling data for Gaffer you really need to work out what questions you want to ask on the data. 
Gaffer queries are seeded by the vertices, essentially the vertices are indexed. 
For this example we want to be able to ask questions like 'How many vehicles have travelled from junction 10 to junction 11?', so junction needs to be a vertex in the Graph.

The Graph will look something like this (sorry it is not particularly exciting at this point). The number on the edge is the edge count property:

```
    --3->
10         11
    <-1--
 

23  --2->  24
    
    
27  <-1--  28
```

The Schema file can be broken down into small parts, we encourage at least 2 files:

- ${ELEMENTS_SCHEMA_LINK}
- ${TYPES_SCHEMA_LINK}

Splitting the schema up into these 2 files helps to illustrate the different roles that the schemas fulfil.

## The Elements Schema

The Elements Schema is a JSON document that describes the Elements (Edges and Entities) in the Graph. We will start by using this very basic schema:

${ELEMENTS_JSON}

We have one Edge Group, `"RoadUse"`. The Group simply labels a particular type of Edge defined by its vertex types, directed flag and set of properties.

This edge is a directed edge representing vehicles moving from junction A to junction B.

You can see the `“RoadUse”` Edge has a source and a destination vertex of type `"junction"` and a single property called `"count"` of type `"count.long"`. 
These types are defined in the DataType file.

## The Types Schema

The Types Schema is a JSON document that describes the types of objects used by Elements. Here's the one we'll start with:

${TYPES_JSON}

First we'll look at `"junction"`, the type we specified for our source and destination vertices on the `"RoadUse"` edge. It's defined here as a java.lang.String.

The property `"count"` on the `"RoadUse"` Edges was specified as type `"count.long"`. The definition here says that any object of type `"count.long"` is a java.lang.Long, and we have added a validator that mandates that the count object's value must be greater than or equal to 0.
If we have a `"RoadUse"` Edge with a count that's not a Long or is a Long but has a value less than 0 it will fail validation and won't be added to the Graph.
Gaffer validation is done using [Java Predicates](https://docs.oracle.com/javase/8/docs/api/java/util/function/Predicate.html)

We also supply a ${SUM_JAVADOC2} [BinaryOperator](https://docs.oracle.com/javase/8/docs/api/java/util/function/BinaryOperator.html) to aggregate the count.long type.
Gaffer allows Edges of the same Group to be aggregated together. This means that when different vehicles travel from junction 10 to junction 11 the edges will be aggregated together and the count property will represent the total number of vehicles that have travelled between the 2 junctions. 


## Generating Graph Elements

So now we have modelled our data we need to write an Element Generator, an implementation of a [Java Function](https://docs.oracle.com/javase/8/docs/api/java/util/function/Function.html) to convert each line of our csv file to a RoadUse edge.

Here is our simple Element Generator.

${ELEMENT_GENERATOR_JAVA}

The `_apply` method takes a line from the data file as a String and returns a Gaffer ${EDGE_JAVADOC2}.

First we take a line from the file as a String and split on `","` to get 3 Strings: (Road,JunctionA,JunctionB).
Then we create a new Edge object with a group `"RoadUse"`. 
We set the source vertex of the Edge to be the first junction and the destination vertex to be the second junction.
Next we set the Edge's directed flag to true, indicating that in this case our Edges are directed (JunctionA->JunctionB != JunctionB->JunctionA).
Finally we add a property called 'count' to our Edge. This is a Long and is going to represent how many vehicles travel between junctionA and junctionB. We're initialising this to be 1, as a single line in our csv data represents 1 vehicle.

In a later example you will see there is a quick way of adding data to Gaffer directly from CSVs but for now we will do it in 2 steps. First we convert the csv to Edges.

${START_JAVA_CODE}
${GENERATE_SNIPPET}
${END_CODE}

This produces these edges:

```
${GENERATED_EDGES}
```

## Creating a Graph

The next thing we do is create an instance of a Gaffer ${GRAPH_JAVADOC2}, this is basically just a proxy for your chosen Gaffer Store.
To do this we need to provide 3 things; the schema files we introduced in the previous section, a Graph Configuration and a Store Properties file.

### The Graph Configuration

The graph configuration identifies the graph you are connecting to.
In it's simplest form, this is just a JSON document containing a single String to identify your graph.
For more information please see [Graph Configuration](../../components/core/graph.md#graph-configuration).

${SIMPLE_GRAPH_CONFIGURATION}

### The Store Properties

Here is the Store Properties file required to connect to the Mock Accumulo store:

${SIMPLE_STORE_PROPERTIES}

This contains information specific to the actual instance of the Store you are using. Refer to the documentation for your chosen store for the configurable properties, e.g ${ACCUMULO_USER_GUIDE}.
The important property is 'gaffer.store.class' this tells Gaffer the type of store you wish to use to store your data.

### The Graph Object

Now we've got everything we need to create the Graph:

${START_JAVA_CODE}
${GRAPH_FROM_FILES_SNIPPET}
${END_CODE}

All of the graph configuration files can also be written directly in Java:

${START_JAVA_CODE}
${GRAPH_SNIPPET}
${END_CODE}

## Loading and Querying Data

Now we've generated some Graph Edges and created a Graph, let's put the Edges in the Graph.

Before we can do anything with our graph, we need to create a user. In production systems, it will be used to hold the security authorisations of the user, but for now, we just need a String to identify the user interacting with the Graph:

${START_JAVA_CODE}
${USER_SNIPPET}
${END_CODE}

That user can then add our Edges to the Graph. To interact with a Gaffer Graph we use an ${OPERATION_JAVADOC2}. In this case our Operation is ${ADD_ELEMENTS_JAVADOC2}.

${START_JAVA_CODE}
${ADD_SNIPPET}
${END_CODE}

Finally, we run a query to return all Edges in our Graph that contain the vertex "10". To do this we use a ${GET_ELEMENTS_JAVADOC2} Operation.

${START_JAVA_CODE}
${GET_SNIPPET}
${END_CODE}

## Summary

In this example we've taken some simple pairs of integers in a file and, using an ElementGenerator, converted them into Gaffer Graph Edges with a `”count”` property.

Then we loaded the Edges into a Gaffer Graph backed by a MockAccumuloStore and returned only the Edges containing the Vertex `”10”`. In our Schema we specified that we should sum the "count" property on Edges of the same Group between the same pair of Vertices. We get the following Edges returned, with their "counts" summed:

```
${GET_ELEMENTS_RESULT}
```
