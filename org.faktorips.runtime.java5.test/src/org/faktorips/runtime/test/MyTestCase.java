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

package org.faktorips.runtime.test;

import org.w3c.dom.Element;

class MyTestCase extends IpsTestCase2 {

    private Object expectedValue;
    private Object actualValue;

    public MyTestCase(String qName) {
        super(qName);
    }

    public MyTestCase(String qName, Object expectedValue, Object actualValue) {
        super(qName);
        this.expectedValue = expectedValue;
        this.actualValue = actualValue;
    }

    @Override
    protected void initInputFromXml(Element inputEl) {
        // do nothing
    }

    @Override
    protected void initExpectedResultFromXml(Element resultEl) {
        // do nothing
    }

    @Override
    public void executeBusinessLogic() throws Exception {
        // do nothing
    }

    @Override
    public void executeAsserts(IpsTestResult result) throws Exception {
        assertEquals(expectedValue, actualValue, result, "TestObject", "TestedAttribute");
    }

}
