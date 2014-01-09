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

package org.faktorips.devtools.core.ui.table;

import org.eclipse.swt.widgets.Text;

/**
 * A cell editor using the {@link Text} control to enter values in a given language. In contrast to
 * {@link InternationalStringCellEditor} this cell editor can only modify a text for one locale. The
 * {@link InternationalStringCellEditor} has additionally a button to edit the values in other
 * languages.
 * 
 * @see InternationalStringCellEditor
 */
public class LocalizedStringCellEditor extends AbstractLocalizedStringCellEditor {

    public LocalizedStringCellEditor(Text control) {
        super(control);
    }

    @Override
    public Text getControl() {
        return (Text)super.getControl();
    }

    @Override
    protected Text getTextControl() {
        return getControl();
    }

}
