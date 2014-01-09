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

package org.faktorips.devtools.core.internal.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.PlatformObject;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.IIpsModel;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;

public abstract class IpsElement extends PlatformObject implements IIpsElement {

    protected String name; // FIXME make private
    protected IIpsElement parent; // FIXME make private

    final static IIpsElement[] NO_CHILDREN = new IIpsElement[0];

    public IpsElement(IIpsElement parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    /**
     * Constructor for testing purposes.
     */
    public IpsElement() {
        // Constructor for testing purposes.
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public final IIpsElement getParent() {
        return parent;
    }

    @Override
    public boolean exists() {
        if (getParent() == null || !getParent().exists()) {
            return false;
        }
        if (getCorrespondingResource() == null) {
            /*
             * If no corresponding resource exists, the EnclosingResource.exists() is handled by
             * calling getParent().exists() above. So if we have arrived here, we have to return
             * true (the parent exists) to avoid a NullPointerException in the rest of the code.
             */
            return true;
        }
        return getCorrespondingResource().exists();
    }

    @Override
    public IResource getEnclosingResource() {
        IResource resource = getCorrespondingResource();
        if (resource != null) {
            return resource;
        }
        return getParent().getEnclosingResource();
    }

    @Override
    public IIpsModel getIpsModel() {
        return IpsPlugin.getDefault().getIpsModel();
    }

    @Override
    public IIpsProject getIpsProject() {
        if (getParent() == null) {
            return null;
        }
        return getParent().getIpsProject();
    }

    @Override
    public IIpsElement[] getChildren() throws CoreException {
        return NO_CHILDREN;
    }

    @Override
    public boolean hasChildren() throws CoreException {
        return getChildren().length > 0;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IIpsElement)) {
            return false;
        }
        IIpsElement other = (IIpsElement)o;
        return other.getName().equals(getName())
                && ((parent == null && other.getParent() == null) || (parent != null && parent
                        .equals(other.getParent())));
    }

    @Override
    public String toString() {
        if (getParent() == null) {
            return getName();
        }
        return getParent().toString() + "/" + getName(); //$NON-NLS-1$
    }

    @Override
    public boolean isContainedInArchive() {
        return getParent().isContainedInArchive();
    }

}
