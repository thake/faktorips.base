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

package org.faktorips.devtools.core.ui.controlfactories;

import junit.framework.TestCase;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * @author Jan Ortmann
 */
public class BooleanComboFieldTest extends TestCase {

    private Shell shell;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        this.shell = new Shell(Display.getCurrent());
    }

    public void tearDownExtension() {
        shell.dispose();
    }

    public void testSetText() {
        Combo c = new Combo(shell, SWT.READ_ONLY);
        c.setItems(new String[] { "true", "false" });
        BooleanComboField field = new BooleanComboField(c, "true", "false");

        field.setText("false");
        assertEquals("false", field.getValue()); // in this case get value returns a string
        assertTrue(field.isTextContentParsable());

        field.setText("true");
        assertEquals("true", field.getValue());
        assertTrue(field.isTextContentParsable());

        field.setText("unkown");
        assertEquals("false", field.getValue());
        assertTrue(field.isTextContentParsable());

        c = new Combo(shell, SWT.READ_ONLY);
        c.setItems(new String[] { "<null>", "Yes", "No" });
        field = new BooleanComboField(c, "Yes", "No");

        field.setText("Yes");
        assertEquals("true", field.getValue());

        field.setText("No");
        assertEquals("false", field.getValue());

        field.setText("unknown");
        assertEquals("false", field.getValue());

        field.setText("<null>");
        assertEquals(null, field.getValue());
    }

    public void testSetValue() {
        Combo c = new Combo(shell, SWT.READ_ONLY);
        c.setItems(new String[] { "true", "false" });
        BooleanComboField field = new BooleanComboField(c, "true", "false");

        field.setValue("false");
        assertEquals("false", field.getValue()); // in this case get value returns a string
        assertTrue(field.isTextContentParsable());

        field.setValue("true");
        assertEquals("true", field.getValue());
        assertTrue(field.isTextContentParsable());

        field.setValue("unkown");
        assertEquals("false", field.getValue());
        assertTrue(field.isTextContentParsable());
    }

}
