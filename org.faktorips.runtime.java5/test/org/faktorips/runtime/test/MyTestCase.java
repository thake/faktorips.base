/*******************************************************************************
 * Copyright (c) 2005-2009 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initInputFromXml(Element inputEl) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initExpectedResultFromXml(Element resultEl) {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeBusinessLogic() throws Exception {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void executeAsserts(IpsTestResult result) throws Exception {
        assertEquals(expectedValue, actualValue, result, "TestObject", "TestedAttribute");
    }
}
