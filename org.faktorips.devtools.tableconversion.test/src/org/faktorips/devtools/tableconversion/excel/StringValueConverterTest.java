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

package org.faktorips.devtools.tableconversion.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.faktorips.datatype.Datatype;
import org.faktorips.util.message.MessageList;
import org.junit.Test;

/**
 * 
 * @author Thorsten Guenther
 */
public class StringValueConverterTest {

    @Test
    public void testGetIpsValue() {
        MessageList ml = new MessageList();
        StringValueConverter converter = new StringValueConverter();
        String value = converter.getIpsValue("1234", ml);
        assertTrue(Datatype.STRING.isParsable(value));
        assertTrue(ml.isEmpty());

        value = converter.getIpsValue(new Long(Long.MAX_VALUE), ml);
        assertTrue(Datatype.STRING.isParsable(value));
        assertTrue(ml.isEmpty());
    }

    @Test
    public void testGetExternalDataValue() {
        MessageList ml = new MessageList();
        StringValueConverter converter = new StringValueConverter();
        String value = (String)converter.getExternalDataValue("VALID", ml);
        assertEquals("VALID", value);
        assertTrue(ml.isEmpty());

        value = (String)converter.getExternalDataValue(null, ml);
        assertNull(value);
        assertTrue(ml.isEmpty());
    }

}
