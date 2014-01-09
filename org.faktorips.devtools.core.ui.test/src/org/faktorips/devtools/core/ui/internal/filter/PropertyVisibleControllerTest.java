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

package org.faktorips.devtools.core.ui.internal.filter;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.faktorips.devtools.core.model.type.IProductCmptProperty;
import org.faktorips.devtools.core.ui.filter.IProductCmptPropertyFilter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PropertyVisibleControllerTest {

    @Mock
    private Control outerControl;

    private PropertyVisibleController controller;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new PropertyVisibleController();
    }

    @Test
    public void testUpdateUI() {
        IProductCmptProperty p1 = mock(IProductCmptProperty.class);
        IProductCmptProperty p2 = mock(IProductCmptProperty.class);
        Composite parent = mock(Composite.class);
        GridData p1C1LayoutData = new GridData();
        GridData p1C2LayoutData = new GridData();
        GridData p2C1LayoutData = new GridData();
        GridData p2C2LayoutData = new GridData();
        Control p1C1 = mockControl(parent, p1C1LayoutData);
        Control p1C2 = mockControl(parent, p1C2LayoutData);
        Control p2C1 = mockControl(parent, p2C1LayoutData);
        Control p2C2 = mockControl(parent, p2C2LayoutData);
        IProductCmptPropertyFilter filter1 = mock(IProductCmptPropertyFilter.class);
        IProductCmptPropertyFilter filter2 = mock(IProductCmptPropertyFilter.class);

        when(filter1.isFiltered(p1)).thenReturn(false);
        when(filter2.isFiltered(p1)).thenReturn(true);
        when(filter1.isFiltered(p2)).thenReturn(false);
        when(filter2.isFiltered(p2)).thenReturn(false);

        controller.addFilter(filter1);
        controller.addFilter(filter2);

        controller.addPropertyControlMapping(outerControl, p1, p1C1, p1C2);
        controller.addPropertyControlMapping(outerControl, p2, p2C1, p2C2);

        // Check visibility
        verify(p1C1).setVisible(false);
        verify(p1C2).setVisible(false);
        verify(p2C1).setVisible(true);
        verify(p2C2).setVisible(true);

        // Check grid data exclusion
        assertTrue(p1C1LayoutData.exclude);
        assertTrue(p1C2LayoutData.exclude);
        assertFalse(p2C1LayoutData.exclude);
        assertFalse(p2C2LayoutData.exclude);
    }

    @Test
    public void testIsFiltered() {
        IProductCmptProperty p1 = mock(IProductCmptProperty.class);
        IProductCmptProperty p2 = mock(IProductCmptProperty.class);
        IProductCmptPropertyFilter filter1 = mock(IProductCmptPropertyFilter.class);
        IProductCmptPropertyFilter filter2 = mock(IProductCmptPropertyFilter.class);

        when(filter1.isFiltered(p1)).thenReturn(false);
        when(filter2.isFiltered(p1)).thenReturn(true);
        when(filter1.isFiltered(p2)).thenReturn(false);
        when(filter2.isFiltered(p2)).thenReturn(false);

        controller.addFilter(filter1);
        controller.addFilter(filter2);

        assertTrue(controller.isFiltered(p1));
        assertFalse(controller.isFiltered(p2));
    }

    @Test
    public void testUpdateUI_RelayoutParents() {
        IProductCmptProperty p1 = mock(IProductCmptProperty.class);
        IProductCmptProperty p2 = mock(IProductCmptProperty.class);

        Composite parentP1C1 = mock(Composite.class);
        Composite parentP1C2 = mock(Composite.class);
        Composite parentP2 = mock(Composite.class);
        Control p1C1 = mockControl(parentP1C1, new GridData());
        Control p1C2 = mockControl(parentP1C2, new GridData());
        Control p2C1 = mockControl(parentP2, new GridData());
        Control p2C2 = mockControl(parentP2, new GridData());
        when(p1C1.getParent()).thenReturn(parentP1C1);
        when(p1C2.getParent()).thenReturn(parentP1C2);
        when(p2C1.getParent()).thenReturn(parentP2);
        when(p2C2.getParent()).thenReturn(parentP2);

        IProductCmptPropertyFilter filter = mock(IProductCmptPropertyFilter.class);
        when(filter.isFiltered(any(IProductCmptProperty.class))).thenReturn(true);

        controller.addPropertyControlMapping(outerControl, p1, p1C1, p1C2);
        controller.addPropertyControlMapping(outerControl, p2, p2C1, p2C2);
        controller.addFilter(filter);

        controller.updateUI();

        verify(parentP1C1).layout();
        verify(parentP1C2).layout();
        verify(parentP2).layout();
    }

    @Test
    public void testAddPropertyControlMapping() {
        IProductCmptProperty property = mock(IProductCmptProperty.class);
        Control control1 = mockControl(null, new GridData());
        Control control2 = mockControl(null, new GridData());

        assertTrue(controller.addPropertyControlMapping(outerControl, property, control1));
        assertFalse(controller.addPropertyControlMapping(outerControl, property, control1));
        assertTrue(controller.addPropertyControlMapping(outerControl, property, control2));
        assertTrue(controller.addPropertyControlMapping(outerControl, property, control1, control2));
    }

    @Test
    public void testAddPropertyControlMapping_settingVisibleState() {
        IProductCmptProperty property = mock(IProductCmptProperty.class);
        Control control1 = mockControl(null, new GridData());
        Control control2 = mockControl(null, new GridData());

        IProductCmptPropertyFilter filter = mock(IProductCmptPropertyFilter.class);
        when(filter.isFiltered(property)).thenReturn(true);
        controller.addFilter(filter);

        controller.addPropertyControlMapping(outerControl, property, control1);
        verify(control1).setVisible(false);

        control1 = mockControl(null, new GridData());

        controller.addPropertyControlMapping(outerControl, property, control2);
        verifyZeroInteractions(control1);
        verify(control2).setVisible(false);

        control2 = mockControl(null, new GridData());

        controller.addPropertyControlMapping(outerControl, property, control1, control2);
        verify(control1).setVisible(false);
        verify(control2).setVisible(false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddPropertyControlMapping_NoControlProvided() {
        IProductCmptProperty property = mock(IProductCmptProperty.class);
        controller.addPropertyControlMapping(outerControl, property, new Control[0]);
    }

    /**
     * <strong>Scenario:</strong><br>
     * A property control mapping is added, but one of the provided controls does not provide
     * {@link GridData} as layout data.
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * An {@link IllegalArgumentException} should be thrown as only controls using {@link GridData}
     * can be fully excluded from the UI.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAddPropertyControlMapping_ControlWithNoGridDataProvided() {
        IProductCmptProperty property = mock(IProductCmptProperty.class);
        Control c1 = mockControl(null, new GridData());
        Control c2 = mockControl(null, new RowData());

        controller.addPropertyControlMapping(outerControl, property, c1, c2);
    }

    @Test
    public void testRemovePropertyControlMapping() {
        IProductCmptProperty property = mock(IProductCmptProperty.class);
        Control control = mockControl(null, new GridData());

        controller.addPropertyControlMapping(outerControl, property, control);
        assertTrue(controller.removePropertyControlMapping(outerControl));
        assertFalse(controller.removePropertyControlMapping(outerControl));
    }

    @Test
    public void testAddFilter() {
        IProductCmptPropertyFilter filter = mock(IProductCmptPropertyFilter.class);
        assertTrue(controller.addFilter(filter));
        assertFalse(controller.addFilter(filter));
    }

    @Test
    public void testRemoveFilter() {
        IProductCmptPropertyFilter filter = mock(IProductCmptPropertyFilter.class);
        controller.addFilter(filter);
        assertTrue(controller.removeFilter(filter));
        assertFalse(controller.removeFilter(filter));
    }

    private Control mockControl(Composite parent, Object layoutData) {
        Control control = mock(Control.class);
        if (parent == null) {
            parent = mock(Composite.class);
        }

        when(control.getParent()).thenReturn(parent);
        when(control.getLayoutData()).thenReturn(layoutData);

        return control;
    }

}
