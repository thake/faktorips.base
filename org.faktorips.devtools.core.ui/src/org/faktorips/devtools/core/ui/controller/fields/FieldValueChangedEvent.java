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

package org.faktorips.devtools.core.ui.controller.fields;

import org.faktorips.devtools.core.ui.controller.EditField;
import org.faktorips.util.ArgumentCheck;

/**
 * An event that signals that the value in the edit field has been changed.
 * 
 * @author Jan Ortmann
 */
public class FieldValueChangedEvent {

    /** the edit control that has changed */
    public EditField<?> field;

    public FieldValueChangedEvent(EditField<?> field) {
        ArgumentCheck.notNull(field);
        this.field = field;
    }
}
