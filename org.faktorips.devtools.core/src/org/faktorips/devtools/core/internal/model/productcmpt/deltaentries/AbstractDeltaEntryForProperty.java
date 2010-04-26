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

package org.faktorips.devtools.core.internal.model.productcmpt.deltaentries;

import org.faktorips.devtools.core.internal.model.productcmpt.GenerationToTypeDelta;
import org.faktorips.devtools.core.model.productcmpt.IDeltaEntryForProperty;

/**
 * 
 * @author Jan Ortmann
 */
public abstract class AbstractDeltaEntryForProperty extends AbstractDeltaEntry implements IDeltaEntryForProperty {

    public AbstractDeltaEntryForProperty(GenerationToTypeDelta delta) {
        super(delta);
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return getPropertyName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getDeltaType() + ": " + getPropertyName() + "(" + getPropertyType().getName() + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

}
