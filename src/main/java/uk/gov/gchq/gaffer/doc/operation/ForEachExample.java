/*
 * Copyright 2018-2019 Crown Copyright
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

package uk.gov.gchq.gaffer.doc.operation;

import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.ForEach;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.output.ToSingletonList;
import uk.gov.gchq.gaffer.operation.impl.output.ToVertices;

public class ForEachExample extends OperationExample {
    public ForEachExample() {
        super(ForEach.class);
    }

    public static void main(final String[] args) throws OperationException {
        new ForEachExample().runAndPrint();
    }

    @Override
    protected void runExamples() {
        forEachInChainExample();
    }

    public Iterable<?> forEachInChainExample() {
        // ---------------------------------------------------------
        final OperationChain<Iterable<?>> opChain = new OperationChain.Builder()
                .first(new GetAdjacentIds.Builder()
                        .input(new EntitySeed(1))
                        .build())
                .then(new ForEach.Builder<>()
                        .operation(new OperationChain.Builder()
                                .first(new ToSingletonList<EntitySeed>())
                                .then(new GetAdjacentIds())
                                .then(new ToVertices())
                                .build())
                        .build())
                .build();
        // ---------------------------------------------------------

        return runExample(opChain, null);
    }
}
