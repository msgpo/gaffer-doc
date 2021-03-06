${HEADER}

${CODE_LINK}

Finally this example introduces the full Road Traffic schema. This uses the sample data taken from the Department for Transport [GB Road Traffic Counts](http://data.dft.gov.uk/gb-traffic-matrix/Raw_count_data_major_roads.zip), which is licensed under the [Open Government Licence](http://www.nationalarchives.gov.uk/doc/open-government-licence/version/3/).

The data is now in a slightly different format. Each row now represents multiple vehicles of different types travelling between 2 junctions. We also have a bit of extra information in the data file. This has allow us to create some extra edges: RegionContainsLocation, LocationContainsRoad and JunctionLocatedAt.

As we now have multiple roads in our sample data, we will include the name of the road in the junction name, e.g. M5:23 represents junction 23 on the M5.

We have also add in a frequency map for the counts of each vehicle time. This will allow us to perform queries such as to find out which roads have a large number of buses. 
Here are the updated schema files:

### Elements schema
${ELEMENTS_JSON}


### Types schema
${TYPES_JSON}


## Example complex query
Now we have a the full schema we can load in our Road Traffic Sample ${ROAD_TRAFFIC_SAMPLE_DATA_LINK} and run a more complex query.

For this example, the question we want to ask is: "In the year 2000, which junctions in the South West were heavily used by buses".

There may be different and probably more efficient ways of doing this but we have tried to create an operation chain that demonstrates several features from the previous walkthroughs. 

The query is form a follows:

- We will start at the "South West" vertex, follow RegionContainsLocation edge, then LocationContainsRoad edge. 
- We may get duplicates at this point so we will add all the road vertices to a Set using ToSet (this is not recommended for a large number of results).
- Then we will continue on down RoadHasJunction edges.
- At this point we now have all the Junctions in the South West.
- We will then query for the JunctionUse entity to find out the number of buses.
- Next we will sort the entities based on the number of buses and we will just keep the top 2 results.
- Finally, just to demonstrate another operation, we will convert the results into a simple CSV of junction and bus count.

and here is the code in Java, JSON and Python:

${START_JAVA_CODE}
${GET_SNIPPET}
${JSON_CODE}
${GET_JSON}
${FULL_JSON_CODE}
${GET_FULL_JSON}
${PYTHON_CODE}
${GET_PYTHON}
${END_CODE}

We have a python shell for connecting to the Gaffer REST API. You can
get the python shell from [here](https://github.com/gchq/gaffer-tools/tree/master/python-shell).
To execute the python code above you will need to import the folowing:

```python
from gafferpy import gaffer as g
from gafferpy import gaffer_connector
```


When executed on the graph, the result is:

```
${RESULT}
```

The full road traffic example project can be found in ${ROAD_TRAFFIC_EXAMPLE_LINK}. 
At this point it might be a good idea to follow the documentation in that README to start up a Gaffer REST API and UI backed with the road traffic graph.
