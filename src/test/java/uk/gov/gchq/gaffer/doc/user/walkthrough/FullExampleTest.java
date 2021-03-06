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

import com.google.common.collect.Lists;
import org.junit.Test;

import uk.gov.gchq.gaffer.operation.OperationException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FullExampleTest {

    @Test
    public void shouldReturnExpectedEdges() throws OperationException, IOException {
        // Given
        final FullExample query = new FullExample();

        // When
        final Iterable<? extends String> results = query.run();

        // Then
        verifyResults(results);
    }

    private void verifyResults(final Iterable<? extends String> resultsItr) {
        final List<String> expectedResults = Arrays.asList(
                "Junction,Bus Count",
                "M4:LA Boundary,1958",
                "M32:2,1411"
        );

        assertEquals(expectedResults, Lists.newArrayList(resultsItr));
    }
}
