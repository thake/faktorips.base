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

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.model.HierarchyVisitor;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;

/**
 * A visitor that makes it easy to implement a function based on all types in a supertype hierarchy.
 * The class provides navigation of the supertype hierarchy and stops if a cycle is detected in the
 * type hierarchy. It also provides the information if a cycle exists in the hierarchy.
 * 
 * @since 2.0
 * 
 * @author Jan Ortmann
 */
public abstract class TypeHierarchyVisitor<T extends IType> extends HierarchyVisitor<T> {

    /**
     * @param ipsProject The project which IPS object path is used to search.
     * 
     * @throws NullPointerException If <tt>ipsProject</tt> is <code>null</code>.
     */
    public TypeHierarchyVisitor(IIpsProject ipsProject) {
        super(ipsProject);
    }

    /**
     * Returns the types visited by the visitor in the order they were visited.
     */
    public IType[] getVisitedTypes() {
        return visitedTypes.toArray(new IType[visitedTypes.size()]);
    }

    @Override
    protected T findSupertype(T currentType, IIpsProject ipsProject) {
        try {
            @SuppressWarnings("unchecked")
            T supertype = (T)currentType.findSupertype(ipsProject);
            return supertype;
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

}
