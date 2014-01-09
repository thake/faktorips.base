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

package org.faktorips.devtools.core.ui.inputformat;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.internal.model.productcmpt.ConfigElement;
import org.faktorips.devtools.core.internal.model.valueset.EnumValueSet;
import org.faktorips.devtools.core.internal.model.valueset.UnrestrictedValueSet;
import org.faktorips.devtools.core.model.IIpsModel;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.valueset.IEnumValueSet;
import org.faktorips.devtools.core.model.valueset.IRangeValueSet;
import org.faktorips.devtools.core.model.valueset.IValueSet;
import org.faktorips.devtools.core.model.valueset.Messages;
import org.faktorips.devtools.core.model.valueset.ValueSetType;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ValueSetFormatTest {

    @Mock
    private IIpsObject ipsObject;

    @Mock
    private IpsUIPlugin uiPlugin;

    @Mock
    private IIpsProject ipsProject;

    @Mock
    private ConfigElement configElement;

    @Mock
    private IIpsModel ipsModel;

    @Mock
    private ValueDatatype datatype;

    @Mock
    private IInputFormat<String> inputFormat;

    private String INPUT_VALUE = "test.input";

    private String OUTPUT_VALUE = "test.output";

    private EnumValueSet enumValueSet;

    private ValueSetFormat format;

    @Before
    public void setUp() throws Exception {
        enumValueSet = new EnumValueSet(configElement, "ID");
        when(configElement.getValueSet()).thenReturn(enumValueSet);
        when(configElement.getIpsProject()).thenReturn(ipsProject);
        format = new ValueSetFormat(configElement, uiPlugin);
        when(configElement.getAllowedValueSetTypes(ipsProject)).thenReturn(Arrays.asList(ValueSetType.ENUM));
        when(configElement.getIpsModel()).thenReturn(ipsModel);
        when(configElement.getIpsObject()).thenReturn(ipsObject);
        format = new ValueSetFormat(configElement, uiPlugin);
    }

    @Test
    public void testParseInternalEmptyUnrestrictedValueSet() throws Exception {
        when(configElement.getAllowedValueSetTypes(ipsProject)).thenReturn(
                Arrays.asList(ValueSetType.ENUM, ValueSetType.UNRESTRICTED));

        IValueSet parseInternal = format.parseInternal("");

        assertNotNull(parseInternal);
        assertTrue(parseInternal instanceof UnrestrictedValueSet);
        assertEquals(configElement, parseInternal.getParent());
    }

    @Test
    public void testParseInternalEmptyUnrestrictedValueSet_alreadyUnrestricted() throws Exception {
        IValueSet unrestrictedValueSet = new UnrestrictedValueSet(configElement, "");
        when(configElement.getValueSet()).thenReturn(unrestrictedValueSet);
        when(configElement.getAllowedValueSetTypes(ipsProject)).thenReturn(
                Arrays.asList(ValueSetType.ENUM, ValueSetType.UNRESTRICTED));

        IValueSet parseInternal = format.parseInternal("");

        assertSame(unrestrictedValueSet, parseInternal);
    }

    @Test
    public void testParseInternalEmptyEnumValueSet_alreadyEnumValueSet() {
        IValueSet parseInternal = format.parseInternal("");

        assertSame(enumValueSet, parseInternal);
    }

    @Test
    public void testParseInternalEmptyEnumValueSet() {
        IValueSet anyValueSet = new UnrestrictedValueSet(configElement, "");
        when(configElement.getValueSet()).thenReturn(anyValueSet);

        IValueSet parseInternal = format.parseInternal("");

        assertTrue(parseInternal instanceof IEnumValueSet);
        assertTrue(((IEnumValueSet)parseInternal).getValuesAsList().isEmpty());
    }

    @Test
    public void testParseInternalUnrestrictedValueSet() throws Exception {
        when(configElement.getAllowedValueSetTypes(ipsProject)).thenReturn(
                Arrays.asList(ValueSetType.ENUM, ValueSetType.UNRESTRICTED));

        IValueSet parseInternal = format.parseInternal(Messages.ValueSetFormat_unrestricted);

        assertNotNull(parseInternal);
        assertTrue(parseInternal instanceof UnrestrictedValueSet);
        assertEquals(configElement, parseInternal.getParent());
    }

    @Test
    public void testParseInternalNewEnumValueSet() {
        IValueSet parseInternal = format.parseInternal("test | test2");
        enumValueSet.addValue("test | test1");

        assertNotNull(parseInternal);
        assertTrue(parseInternal instanceof EnumValueSet);
        EnumValueSet enumVS = (EnumValueSet)parseInternal;
        assertEquals(configElement, enumVS.getParent());
        assertEquals(2, enumVS.getValuesAsList().size());
        assertEquals("test", enumVS.getValue(0));
        assertEquals("test2", enumVS.getValue(1));
    }

    @Test
    public void testParseWithFormatter_InputFormatNotNull() throws CoreException {
        when(configElement.findValueDatatype(ipsProject)).thenReturn(datatype);
        when(uiPlugin.getInputFormat(datatype)).thenReturn(inputFormat);
        when(inputFormat.parse(INPUT_VALUE)).thenReturn(OUTPUT_VALUE);
        String result = format.parseWithFormater(INPUT_VALUE);
        assertEquals(OUTPUT_VALUE, result);
    }

    @Test
    public void testParseWithFormatter_InputFormatNull() {
        String result = format.parseWithFormater(INPUT_VALUE);
        assertEquals(INPUT_VALUE, result);
    }

    @Test
    public void testParseInternalOldEnumValueSet() {
        enumValueSet.addValue("test");
        enumValueSet.addValue("test1");
        IValueSet parseInternal = format.parseInternal("test | test1");

        assertNotNull(parseInternal);
        assertTrue(parseInternal instanceof EnumValueSet);
        EnumValueSet enumVS = (EnumValueSet)parseInternal;
        assertEquals(configElement, enumVS.getParent());
        assertTrue(parseInternal.getId().equals("ID"));
        assertEquals(2, enumVS.getValuesAsList().size());
        assertEquals("test", enumVS.getValue(0));
        assertEquals("test1", enumVS.getValue(1));
    }

    @Test
    public void testParseInternalRange() throws CoreException {
        when(configElement.getAllowedValueSetTypes(ipsProject)).thenReturn(
                Arrays.asList(ValueSetType.RANGE, ValueSetType.ENUM));
        IRangeValueSet range = mock(IRangeValueSet.class);
        when(configElement.getValueSet()).thenReturn(range);
        IValueSet parseInternal = format.parseInternal("[10..100/2]");

        assertNotNull(parseInternal);
        assertEquals(range, parseInternal);
    }

}
