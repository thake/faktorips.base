/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3
 * and if and when this source code belongs to the faktorips-runtime or faktorips-valuetype
 * component under the terms of the LGPL Lesser General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.model.testcasetype;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestParameterTypeTest {

    @Test
    public void testIsTypeMatching() {
        assertTrue(TestParameterType.isTypeMatching(TestParameterType.INPUT, TestParameterType.COMBINED));
        assertTrue(TestParameterType.isTypeMatching(TestParameterType.EXPECTED_RESULT, TestParameterType.COMBINED));
        assertTrue(TestParameterType.isTypeMatching(TestParameterType.COMBINED, TestParameterType.COMBINED));

        assertTrue(TestParameterType.isTypeMatching(TestParameterType.COMBINED, TestParameterType.INPUT));
        assertTrue(TestParameterType.isTypeMatching(TestParameterType.COMBINED, TestParameterType.EXPECTED_RESULT));
    }

}
