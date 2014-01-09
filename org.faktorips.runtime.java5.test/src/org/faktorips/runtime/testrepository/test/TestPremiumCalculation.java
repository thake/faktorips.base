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

package org.faktorips.runtime.testrepository.test;

import org.faktorips.runtime.internal.XmlUtil;
import org.faktorips.runtime.test.IpsTestCase2;
import org.faktorips.runtime.test.IpsTestResult;
import org.w3c.dom.Element;

/**
 * 
 * @author Jan Ortmann
 */
public class TestPremiumCalculation extends IpsTestCase2 {

    private String inputSumInsured;
    private String expResultPremium;

    public TestPremiumCalculation(String qName) {
        super(qName);
    }

    @Override
    protected void initInputFromXml(Element inputEl) {
        Element el = XmlUtil.getFirstElement(inputEl);
        inputSumInsured = el.getAttribute("value");
    }

    @Override
    protected void initExpectedResultFromXml(Element resultEl) {
        Element el = XmlUtil.getFirstElement(resultEl);
        expResultPremium = el.getAttribute("value");
    }

    public String getExpResultPremium() {
        return expResultPremium;
    }

    public String getInputSumInsured() {
        return inputSumInsured;
    }

    @Override
    public void executeBusinessLogic() throws Exception {
        // do nothing
    }

    @Override
    public void executeAsserts(IpsTestResult result) throws Exception {
        // do nothing
    }

    public void testDummy() {
        // do nothing
    }

}
