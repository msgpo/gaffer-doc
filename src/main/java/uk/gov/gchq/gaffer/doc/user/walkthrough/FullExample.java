/*
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.gchq.gaffer.doc.user.walkthrough;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.data.element.comparison.ElementPropertyComparator;
import uk.gov.gchq.gaffer.data.element.function.ElementFilter;
import uk.gov.gchq.gaffer.data.element.function.ElementTransformer;
import uk.gov.gchq.gaffer.data.elementdefinition.view.GlobalViewElementDefinition;
import uk.gov.gchq.gaffer.data.elementdefinition.view.View;
import uk.gov.gchq.gaffer.data.elementdefinition.view.ViewElementDefinition;
import uk.gov.gchq.gaffer.data.generator.CsvGenerator;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.graph.SeededGraphFilters;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.compare.Sort;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.output.ToCsv;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;
import uk.gov.gchq.gaffer.traffic.generator.RoadTrafficCsvElementGenerator;
import uk.gov.gchq.gaffer.types.function.FreqMapExtractor;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.predicate.IsMoreThan;
import uk.gov.gchq.koryphe.impl.predicate.range.InDateRangeDual;
import uk.gov.gchq.koryphe.predicate.PredicateMap;

import java.io.IOException;
import java.nio.charset.Charset;

public class FullExample extends UserWalkthrough {
    public FullExample() {
        super("Full Example", "FullExample", null);
    }

    @Override
    public Iterable<? extends String> run() throws OperationException, IOException {
        // [graph] Create a graph using our schema and store properties
        // ---------------------------------------------------------
        final Graph graph = new Graph.Builder()
                .config(getDefaultGraphConfig())
                .addSchemas(StreamUtil.openStreams(getClass(), schemaPath))
                .storeProperties(getDefaultStoreProperties())
                .build();
        // ---------------------------------------------------------


        // [user] Create a user
        // ---------------------------------------------------------
        final User user = new User("user01");
        // ---------------------------------------------------------


        // [add] Create a data generator and add the edges to the graph using an operation chain consisting of:
        // generateElements - generating edges from the data (note these are directed edges)
        // addElements - add the edges to the graph
        // ---------------------------------------------------------
        try (final CSVParser parser = CSVParser.parse(
                getClass().getResource("/FullExample/data.txt"),
                Charset.defaultCharset(),
                CSVFormat.DEFAULT.withFirstRecordAsHeader()
        )) {
            final OperationChain<Void> addOpChain = new OperationChain.Builder()
                    .first(new GenerateElements.Builder<CSVRecord>()
                            .generator(new RoadTrafficCsvElementGenerator())
                            .input(parser)
                            .build())
                    .then(new AddElements())
                    .build();

            graph.execute(addOpChain, user);
        }

        // ---------------------------------------------------------
        print("The elements have been added.");


        // [get] Get all road junctions in the South West that were heavily used by buses in the year 2000.
        // ---------------------------------------------------------
        final OperationChain<Iterable<? extends String>> opChain = new OperationChain.Builder()
                .first(new GetAdjacentIds.Builder()
                        .input(new EntitySeed("South West"))
                        .view(new View.Builder()
                                .edge("RegionContainsLocation")
                                .build())
                        .build())
                .then(new GetAdjacentIds.Builder()
                        .view(new View.Builder()
                                .edge("LocationContainsRoad")
                                .build())
                        .build())
                .then(new ToSet<>())
                .then(new GetAdjacentIds.Builder()
                        .view(new View.Builder()
                                .edge("RoadHasJunction")
                                .build())
                        .build())
                .then(new GetElements.Builder()
                        .view(new View.Builder()
                                .globalElements(new GlobalViewElementDefinition.Builder()
                                        .groupBy()
                                        .build())
                                .entity("JunctionUse", new ViewElementDefinition.Builder()
                                        .preAggregationFilter(new ElementFilter.Builder()
                                                .select("startDate", "endDate")
                                                .execute(new InDateRangeDual.Builder()
                                                        .start("2000/01/01")
                                                        .end("2001/01/01")
                                                        .build())
                                                .build())
                                        .postAggregationFilter(new ElementFilter.Builder()
                                                .select("countByVehicleType")
                                                .execute(new PredicateMap<>("BUS", new IsMoreThan(1000L)))
                                                .build())

                                        // Extract the bus count out of the frequency map and store in transient property "busCount"
                                        .transientProperty("busCount", Long.class)
                                        .transformer(new ElementTransformer.Builder()
                                                .select("countByVehicleType")
                                                .execute(new FreqMapExtractor("BUS"))
                                                .project("busCount")
                                                .build())
                                        .build())
                                .build())
                        .inOutType(SeededGraphFilters.IncludeIncomingOutgoingType.OUTGOING)
                        .build())
                .then(new Sort.Builder()
                        .comparators(new ElementPropertyComparator.Builder()
                                .groups("JunctionUse")
                                .property("busCount")
                                .reverse(true)
                                .build())
                        .resultLimit(2)
                        .deduplicate(true)
                        .build())
                // Convert the result entities to a simple CSV in format: Junction,busCount.
                .then(new ToCsv.Builder()
                        .generator(new CsvGenerator.Builder()
                                .vertex("Junction")
                                .property("busCount", "Bus Count")
                                .build())
                        .build())
                .build();
        // ---------------------------------------------------------

        printJsonAndPython("GET", opChain);

        final Iterable<? extends String> results = graph.execute(opChain, user);
        print("\nAll road junctions in the South West that were heavily used by buses in year 2000.");
        for (final String result : results) {
            print("RESULT", result);
        }

        return results;
    }

    public static void main(final String[] args) throws OperationException {
        System.out.println(new FullExample().walkthrough());
    }
}
