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

package org.faktorips.devtools.core.model.type;

import org.faktorips.devtools.core.model.ipsobject.IDescribedElement;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsobject.ILabeledElement;
import org.faktorips.devtools.core.model.ipsobject.Modifier;
import org.faktorips.devtools.core.model.ipsobject.QualifiedNameType;

/**
 * Represents a part that is common to all {@link IType}s.
 * 
 * @author Alexander Weickmann
 */
public interface ITypePart extends IIpsObjectPart, IDescribedElement, ILabeledElement {

    public static final String PROPERTY_MODIFIER = "modifier"; //$NON-NLS-1$

    public static final String PROPERTY_CATEGORY = "category"; //$NON-NLS-1$

    /**
     * Returns the {@link IType} this part belongs to.
     */
    public IType getType();

    /**
     * Returns whether this {@link ITypePart} belongs to the {@link IType} identified by the
     * provided {@link QualifiedNameType}.
     * 
     * @param qualifiedNameType the {@link QualifiedNameType} identifying the {@link IType} in
     *            question
     */
    public boolean isOfType(QualifiedNameType qualifiedNameType);

    /**
     * Returns the part's {@link Modifier}.
     */
    public Modifier getModifier();

    /**
     * Sets the part's {@link Modifier}.
     * 
     * @throws NullPointerException If the parameter is null
     */
    public void setModifier(Modifier modifier);

}
