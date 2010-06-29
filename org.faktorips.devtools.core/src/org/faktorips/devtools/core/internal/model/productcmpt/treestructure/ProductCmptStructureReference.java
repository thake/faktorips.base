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

package org.faktorips.devtools.core.internal.model.productcmpt.treestructure;

import java.util.ArrayList;

import org.eclipse.core.runtime.PlatformObject;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPartContainer;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.treestructure.CycleInProductStructureException;
import org.faktorips.devtools.core.model.productcmpt.treestructure.IProductCmptStructureReference;
import org.faktorips.devtools.core.model.productcmpt.treestructure.IProductCmptTreeStructure;

/**
 * Abstract reference for <code>ProductCmptStructure</code>.
 * 
 * @author Thorsten Guenther
 */
public abstract class ProductCmptStructureReference extends PlatformObject implements IProductCmptStructureReference {

    private IProductCmptTreeStructure structure;

    private ProductCmptStructureReference parent;

    private ProductCmptStructureReference[] children;

    public ProductCmptStructureReference(IProductCmptTreeStructure structure, ProductCmptStructureReference parent)
            throws CycleInProductStructureException {

        this.structure = structure;
        this.parent = parent;
        children = new ProductCmptStructureReference[0];
        detectCycle(new ArrayList<IIpsElement>());
    }

    @Override
    public IProductCmptTreeStructure getStructure() {
        return structure;
    }

    @Override
    public IProductCmptStructureReference getParent() {
        return parent;
    }

    ProductCmptStructureReference[] getChildren() {
        return children;
    }

    void setChildren(ProductCmptStructureReference[] children) {
        this.children = children;
    }

    private void detectCycle(ArrayList<IIpsElement> seenElements) throws CycleInProductStructureException {
        if (getWrappedIpsObject() instanceof IProductCmpt) {
            if (seenElements.contains(getWrappedIpsObject())) {
                throw new CycleInProductStructureException(seenElements.toArray(new IIpsElement[seenElements.size()]));
            } else {
                seenElements.add(getWrappedIpsObject());
            }
        }
        if (parent != null) {
            parent.detectCycle(seenElements);
        }
    }

    /**
     * @return The <code>IIpsObject</code> referenced by this object.
     */
    protected abstract IIpsObjectPartContainer getWrapped();

    @Override
    public IIpsSrcFile getWrappedIpsSrcFile() {
        return getWrappedIpsObject().getIpsSrcFile();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!this.getClass().equals(obj.getClass())) {
            return false;
        }
        ProductCmptStructureReference other = (ProductCmptStructureReference)obj;
        return getWrapped() == other.getWrapped() && getWrappedIpsObject() == other.getWrappedIpsObject()
                && getStructure() == other.getStructure();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        if (getWrapped() != null) {
            hash = 31 * hash + getWrapped().hashCode();
        }
        if (getWrappedIpsObject() != null) {
            hash = 31 * hash + getWrappedIpsObject().hashCode();
        }
        if (getStructure() != null) {
            hash = 31 * hash + getStructure().hashCode();
        }
        return hash;
    }
}
