/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.controls.chooser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.internal.model.productcmpt.MultiValueHolder;
import org.faktorips.devtools.core.internal.model.productcmpt.SingleValueHolder;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.util.message.MessageList;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

public class MultiValueSubsetChooserModelTest {

    private MultiValueHolder multiValueHolder;
    private MultiValueSubsetChooserModel model;
    private SingleValueHolder holderA;
    private SingleValueHolder holderC;
    private SingleValueHolder holderB;
    private SingleValueHolder holder1;
    private SingleValueHolder holder2;

    @Test
    public void testGetValueIndices() {
        List<ListChooserValue> stringValues = new ArrayList<ListChooserValue>();
        stringValues.add(new ListChooserValue("A"));
        stringValues.add(new ListChooserValue("C"));
        stringValues.add(new ListChooserValue("2"));

        int[] indices = model.getValueIndices(stringValues);
        assertEquals(3, indices.length);
        assertEquals(0, indices[0]);
        assertEquals(2, indices[1]);
        assertEquals(4, indices[2]);
    }

    @Test
    public void moveValuesUp() {
        model = spy(model);

        List<ListChooserValue> valuesToMove = new ArrayList<ListChooserValue>();
        valuesToMove.add(new ListChooserValue("B"));
        valuesToMove.add(new ListChooserValue("1"));
        model.move(valuesToMove, true);
        verify(model).updateMultiValueHolder();

        List<ListChooserValue> newValues = model.getResultingValues();
        assertEquals(5, newValues.size());
        assertEquals("B", newValues.get(0).getValue());
        assertEquals("A", newValues.get(1).getValue());
        assertEquals("1", newValues.get(2).getValue());
        assertEquals("C", newValues.get(3).getValue());
        assertEquals("2", newValues.get(4).getValue());
    }

    @Test
    public void moveValuesDown() {
        model = spy(model);

        List<ListChooserValue> valuesToMove = new ArrayList<ListChooserValue>();
        valuesToMove.add(new ListChooserValue("B"));
        valuesToMove.add(new ListChooserValue("1"));
        model.move(valuesToMove, false);
        verify(model).updateMultiValueHolder();

        List<ListChooserValue> newValues = model.getResultingValues();
        assertEquals(5, newValues.size());
        assertEquals("A", newValues.get(0).getValue());
        assertEquals("C", newValues.get(1).getValue());
        assertEquals("B", newValues.get(2).getValue());
        assertEquals("2", newValues.get(3).getValue());
        assertEquals("1", newValues.get(4).getValue());
    }

    @Test
    public void removeFromResultingValues() {
        model = spy(model);

        List<ListChooserValue> valueList = new ArrayList<ListChooserValue>();
        valueList.add(new ListChooserValue("B"));
        model.removeFromResultingValues(valueList);
        verify(model).updateMultiValueHolder();
        List<ListChooserValue> newValues = model.getResultingValues();
        assertEquals(4, newValues.size());
        assertEquals("A", newValues.get(0).getValue());
        assertEquals("C", newValues.get(1).getValue());
        assertEquals("1", newValues.get(2).getValue());
        assertEquals("2", newValues.get(3).getValue());
    }

    @Test
    public void addToResultingValues() {
        model = spy(model);

        List<ListChooserValue> valueList = new ArrayList<ListChooserValue>();
        valueList.add(new ListChooserValue("X"));
        model.addToResultingValues(valueList);
        verify(model).updateMultiValueHolder();
        List<ListChooserValue> newValues = model.getResultingValues();
        assertEquals(6, newValues.size());
        assertEquals("A", newValues.get(0).getValue());
        assertEquals("B", newValues.get(1).getValue());
        assertEquals("C", newValues.get(2).getValue());
        assertEquals("1", newValues.get(3).getValue());
        assertEquals("2", newValues.get(4).getValue());
        assertEquals("X", newValues.get(5).getValue());
    }

    // Mockito does not support type-safety when capturing lists
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void updateMultiValueHolder() {
        model = spy(model);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        List<ListChooserValue> valueList = new ArrayList<ListChooserValue>();
        valueList.add(new ListChooserValue("X"));
        model.addToResultingValues(valueList);

        verify(multiValueHolder).setValue(captor.capture());

        List<SingleValueHolder> holderList = captor.getValue();
        assertEquals(6, holderList.size());
        assertEquals("A", holderList.get(0).getStringValue());
        assertEquals("B", holderList.get(1).getStringValue());
        assertEquals("C", holderList.get(2).getStringValue());
        assertEquals("1", holderList.get(3).getStringValue());
        assertEquals("2", holderList.get(4).getStringValue());
        assertEquals("X", holderList.get(5).getStringValue());
    }

    @Before
    public void setUp() {
        setUpMultiValueHolder();

        model = new MultiValueSubsetChooserModel(new ArrayList<String>(), multiValueHolder, null);
        List<ListChooserValue> values = model.getResultingValues();
        assertEquals(5, values.size());
        assertEquals("A", values.get(0).getValue());
        assertEquals("B", values.get(1).getValue());
        assertEquals("C", values.get(2).getValue());
        assertEquals("1", values.get(3).getValue());
        assertEquals("2", values.get(4).getValue());
    }

    @Test
    public void findSingleValueHolderFor() {
        setUpMultiValueHolder();

        model = new MultiValueSubsetChooserModel(new ArrayList<String>(), multiValueHolder, null);
        SingleValueHolder holder = model.findSingleValueHolderFor(new ListChooserValue("C"));
        assertSame(multiValueHolder.getValue().get(2), holder);
    }

    @Test
    public void findSingleValueHolderForNull() {
        setUpMultiValueHolder();
        multiValueHolder.getValue().add(new SingleValueHolder(null, null));

        model = new MultiValueSubsetChooserModel(new ArrayList<String>(), multiValueHolder, null);
        SingleValueHolder holder = model.findSingleValueHolderFor(new ListChooserValue(null));
        assertSame(multiValueHolder.getValue().get(5), holder);
    }

    @Test
    public void findSingleValueHolderForNull2() {
        setUpMultiValueHolder();
        multiValueHolder.getValue().add(new SingleValueHolder(null, "X"));

        model = new MultiValueSubsetChooserModel(new ArrayList<String>(), multiValueHolder, null);
        SingleValueHolder holder = model.findSingleValueHolderFor(new ListChooserValue(null));
        assertNull(holder);
    }

    @Test
    public void findSingleValueHolderForNull3() {
        setUpMultiValueHolder();
        multiValueHolder.getValue().add(new SingleValueHolder(null, null));

        model = new MultiValueSubsetChooserModel(new ArrayList<String>(), multiValueHolder, null);
        SingleValueHolder holder = model.findSingleValueHolderFor(new ListChooserValue("X"));
        assertNull(holder);
    }

    protected void setUpMultiValueHolder() {
        List<SingleValueHolder> holderList = new ArrayList<SingleValueHolder>();
        multiValueHolder = mock(MultiValueHolder.class);
        when(multiValueHolder.getValue()).thenReturn(holderList);
        holderA = mock(SingleValueHolder.class);
        when(holderA.getStringValue()).thenReturn("A");
        when(holderA.getValue()).thenReturn("A");
        holderList.add(holderA);
        holderB = mock(SingleValueHolder.class);
        when(holderB.getStringValue()).thenReturn("B");
        when(holderB.getValue()).thenReturn("B");
        holderList.add(holderB);
        holderC = mock(SingleValueHolder.class);
        when(holderC.getStringValue()).thenReturn("C");
        when(holderC.getValue()).thenReturn("C");
        holderList.add(holderC);
        holder1 = mock(SingleValueHolder.class);
        when(holder1.getStringValue()).thenReturn("1");
        when(holder1.getValue()).thenReturn("1");
        holderList.add(holder1);
        holder2 = mock(SingleValueHolder.class);
        when(holder2.getStringValue()).thenReturn("2");
        when(holder2.getValue()).thenReturn("2");
        holderList.add(holder2);
    }

    @Test
    public void testValidateValue_NoError() throws CoreException {
        setUpMultiValueHolder();
        MessageList messageList = mock(MessageList.class);
        doReturn(messageList).when(multiValueHolder).validate(any(IIpsProject.class));

        setUpMultiValueHolder();

        model = spy(new MultiValueSubsetChooserModel(new ArrayList<String>(), multiValueHolder, null));
        doReturn(holderC).when(model).findSingleValueHolderFor(any(ListChooserValue.class));
        doReturn(messageList).when(multiValueHolder).validate(any(IIpsProject.class));

        model.validateValue(null);
        verify(messageList, never()).getMessagesFor(holderA);
        verify(messageList, never()).getMessagesFor(holderB);
        verify(messageList).getMessagesFor(holderC);
        verify(messageList, never()).getMessagesFor(holder1);
        verify(messageList, never()).getMessagesFor(holder2);
    }
}