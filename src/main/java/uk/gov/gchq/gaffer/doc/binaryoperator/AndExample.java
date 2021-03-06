/*
 * Copyright 2019 Crown Copyright
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
package uk.gov.gchq.gaffer.doc.binaryoperator;

import uk.gov.gchq.gaffer.commonutil.pair.Pair;
import uk.gov.gchq.koryphe.impl.binaryoperator.And;

public class AndExample extends BinaryOperatorExample {

    public AndExample() {
        super(And.class, "Applies the logical AND operation to 2 booleans");
    }

    public static void main(final String[] args) {
        new AndExample().runAndPrint();
    }

    @Override
    protected void runExamples() {
        andWithBooleans();
        andWithNulls();
        andWithNonBooleanValues();
    }

    private void andWithBooleans() {
        // ---------------------------------------------------------
        final And and = new And();
        // ---------------------------------------------------------

        runExample(and, null,
                new Pair<>(true, true),
                new Pair<>(true, false),
                new Pair<>(false, false)
        );
    }

    private void andWithNulls() {
        // ---------------------------------------------------------
        final And and = new And();
        // ---------------------------------------------------------

        runExample(and, null,
                new Pair<>(false, null),
                new Pair<>(true, null),
                new Pair<>(null, null));

    }

    private void andWithNonBooleanValues() {
        // ---------------------------------------------------------
        final And and = new And();
        // ---------------------------------------------------------

        runExample(and, null,
                new Pair<>("test", 3),
                new Pair<>(0, 0),
                new Pair<>(1, 0));
    }
}
