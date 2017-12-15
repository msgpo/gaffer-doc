/*
 * Copyright 2017 Crown Copyright
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
package uk.gov.gchq.gaffer.doc.function;

import com.google.common.collect.Lists;

import uk.gov.gchq.koryphe.impl.function.LastItem;

public class LastItemExample extends FunctionExample {
    public static void main(final String[] args) {
        new LastItemExample().runAndPrint();
    }

    public LastItemExample() {
        super(LastItem.class, "For a given Iterable, a LastItem will extract the last item.");
    }

    @Override
    protected void runExamples() {
        extractLastItem();

    }

    public void extractLastItem() {
        // ---------------------------------------------------------
        final LastItem<Integer> function = new LastItem<>();
        // ---------------------------------------------------------

        runExample(function,
                null,
                Lists.newArrayList(1, 2, 3),
                Lists.newArrayList(5, 8, 13),
                Lists.newArrayList(21, 34, 55),
                Lists.newArrayList(1, null, 3),
                Lists.newArrayList(1, 2, null),
                null);
    }
}
