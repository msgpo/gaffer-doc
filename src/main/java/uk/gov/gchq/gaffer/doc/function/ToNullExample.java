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

package uk.gov.gchq.gaffer.doc.function;

import uk.gov.gchq.koryphe.impl.function.ToNull;

import java.util.Arrays;

public class ToNullExample extends FunctionExample {
    public static void main(final String[] args) {
        new ToNullExample().runAndPrint();
    }

    public ToNullExample() {
        super(ToNull.class, "Returns null on any input object.");
    }

    @Override
    public void runExamples() {
        toNull();
    }

    public void toNull() {
        // ---------------------------------------------------------
        final ToNull function = new ToNull();
        // ---------------------------------------------------------

        runExample(function,
                null,
                4, 5L, "aString", Arrays.asList(6, 3), null
        );
    }
}
