/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.controller.fields;

import org.faktorips.devtools.core.ui.controls.TextAndSecondControlComposite;

public class TextButtonField extends AbstractTextButtonField<String> {

    public TextButtonField(TextAndSecondControlComposite control) {
        super(control);
    }

    @Override
    public String parseContent() {
        return StringValueEditField.prepareObjectForGet(getControl().getTextControl().getText(),
                supportsNullStringRepresentation());
    }

    @Override
    public void setValue(String newValue) {
        setText(prepareObjectForSet(newValue));
    }

    private String prepareObjectForSet(String newValue) {
        return StringValueEditField.prepareObjectForSet(newValue, supportsNullStringRepresentation());
    }

}
