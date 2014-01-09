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

package org.faktorips.devtools.core.internal.model.enums;

import static org.junit.Assert.assertEquals;

import javax.xml.parsers.ParserConfigurationException;

import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.enums.IEnumAttributeReference;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public class EnumAttributeReferenceTest extends AbstractIpsEnumPluginTest {

    private IEnumAttributeReference genderIdReference;
    private IEnumAttributeReference genderNameReference;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        genderIdReference = genderEnumContent.getEnumAttributeReferences().get(0);
        genderNameReference = genderEnumContent.getEnumAttributeReferences().get(1);
    }

    @Test
    public void testGetSetName() {
        assertEquals(GENDER_ENUM_ATTRIBUTE_ID_NAME, genderIdReference.getName());
        genderIdReference.setName("foo");
        assertEquals("foo", genderIdReference.getName());
    }

    @Test
    public void testXml() throws ParserConfigurationException {
        Element xmlElement = genderIdReference.toXml(createXmlDocument(IEnumAttributeReference.XML_TAG));
        NamedNodeMap attributes = xmlElement.getAttributes();
        assertEquals(GENDER_ENUM_ATTRIBUTE_ID_NAME, attributes.getNamedItem(IIpsElement.PROPERTY_NAME).getTextContent());

        genderNameReference.initFromXml(xmlElement);
        assertEquals(genderIdReference.getName(), genderNameReference.getName());
    }

}
