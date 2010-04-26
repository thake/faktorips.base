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

package org.faktorips.devtools.core.ui.controller.fields;

import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Control;
import org.faktorips.devtools.core.ui.editors.productcmpt.CardinalityPanel;

/**
 * 
 * @author Thorsten Guenther
 */
public class CardinalityPaneEditField extends AbstractCardinalityField {

    private CardinalityPanel cp;
    private boolean min = false;

    /**
	 * 
	 */
    public CardinalityPaneEditField(CardinalityPanel cp, boolean min) {
        this.cp = cp;
        this.min = min;
    }

    public Control getControl() {
        if (min) {
            return cp.getMinCardinalityControl();
        } else {
            return cp.getMaxCardinalityControl();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    void setTextInternal(String newText) {
        if (min) {
            cp.setMinCardinality(newText);
        } else {
            cp.setMaxCardinality(newText);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void insertText(String text) {
        setTextInternal(text);
    }

    /**
     * {@inheritDoc}
     */
    public void selectAll() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void addListenerToControl() {
        if (min) {
            cp.addMinModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    notifyChangeListeners(new FieldValueChangedEvent(CardinalityPaneEditField.this));
                }
            });
        } else {
            cp.addMaxModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent e) {
                    notifyChangeListeners(new FieldValueChangedEvent(CardinalityPaneEditField.this));
                }
            });
        }
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        if (min) {
            return cp.getMinCardinality();
        } else {
            return cp.getMaxCardinality();
        }
    }
}
