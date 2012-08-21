/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.stdbuilder.xpand.model;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.type.IAssociation;
import org.faktorips.devtools.core.model.type.IType;
import org.faktorips.devtools.core.model.type.TypeHierarchyVisitor;

/**
 * This is the generator model node for a derived union association. It is very important to
 * remember, that the corresponding (derived union) association is not in the IType you currently
 * generate code for. Thats why many methods need to know the xClass in which context you currentyl
 * generate the code.
 * 
 * @author dirmeier
 */
public class XDerivedUnionAssociation extends XAssociation {

    /**
     * The default constructor, called by model service
     * 
     * @param association The derived union association
     * @param context The generator model context
     * @param modelService the model service used to instantiate new generator model nodes
     */
    public XDerivedUnionAssociation(IAssociation association, GeneratorModelContext context, ModelService modelService) {
        super(association, context, modelService);
    }

    /**
     * For derived union the getNumOf... method is generated as internal method. e.g.
     * <code>getNumOfCoveragesInternal</code>
     * 
     * @return The name of the getNumOf...Internal method
     */
    public String getMethodNameGetNumOfInternal() {
        return getMethodNameGetNumOf() + "Internal";
    }

    /**
     * Returns a list of associations that are a subset of this derived union. This includes derived
     * union associations that are at the same time subsets of this derived union. The associations
     * are part of the given {@link XClass}.
     * 
     * @param xClass The type in which context you generate code
     * @return the list of associations that subsets this derived union
     */
    public Set<XAssociation> getSubsetAssociations(XClass xClass) {
        Set<XAssociation> result = new LinkedHashSet<XAssociation>();
        Set<? extends XAssociation> associations = xClass.getMasterToDetailAssociations();
        for (XAssociation xAssociation : associations) {
            if (xAssociation.isSubsetOf(this)) {
                result.add(xAssociation);
            }
        }
        return result;
    }

    /**
     * Checks whether this derived union is already implemented in any superclass. This is the case
     * if there is any superclass that has already a subset of this derived union.
     * 
     * @param xClass The type in which context you generate the code
     * @return True if there is already an implementation in any super class (super call is needed),
     *         false otherwise
     */
    public boolean isImplementedInSuperclass(XClass xClass) {
        if (getAssociationType().equals(xClass.getType())) {
            return false;
        }
        try {
            IType supertype = xClass.getType().findSupertype(getIpsProject());
            FindSubsetOfDerivedUnionVisitor findSubsetOfDerivedUnionVisitor = new FindSubsetOfDerivedUnionVisitor(
                    getAssociation(), getIpsProject());
            findSubsetOfDerivedUnionVisitor.start(supertype);
            return findSubsetOfDerivedUnionVisitor.isSubsetFound();
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    /**
     * Searches the given type (and the super type hierarchy) for subsets of the derived union
     * specified when creating the visitor.
     * 
     */
    class FindSubsetOfDerivedUnionVisitor extends TypeHierarchyVisitor<IType> {

        private final IAssociation derivedUnion;

        boolean foundSubset = false;

        public FindSubsetOfDerivedUnionVisitor(IAssociation derivedUnion, IIpsProject ipsProject) {
            super(ipsProject);
            this.derivedUnion = derivedUnion;
        }

        @Override
        protected boolean visit(IType currentType) throws CoreException {
            List<IAssociation> associations = currentType.getAssociations();
            for (IAssociation aAssociation : associations) {
                if (aAssociation.getSubsettedDerivedUnion().equals(derivedUnion.getName())) {
                    foundSubset = true;
                    return false;
                }
            }
            if (currentType.equals(derivedUnion.getType())) {
                return false;
            }
            return true;
        }

        public boolean isSubsetFound() {
            return foundSubset;
        }
    }

    /**
     * Returns <code>true</code> if this derived union association is defined in the given class.
     * <code>false</code> else.
     */
    public boolean isDefinedIn(XClass xClass) {
        System.out.println(NLS.bind("isDefinedIn() auf {0} für {1} aufgerufen.", this, xClass));
        return getAssociation().getType().equals(xClass.getType());
    }

    public String getMarker() {
        String teset = "";
        return teset;
    }

    /**
     * Returns <code>true</code> when the method getNumOfXXInternal() should call super. This is the
     * case if this derived union is not defined in the given class and at the same time is
     * subsetted by an association of the given class. IOW returns <code>true</code> if there is a
     * method in the super class that can be called. <code>false</code> otherwise.
     * 
     * @param xClass the class in which a super call could be generated or not.
     */
    public boolean generateGetNumOfInternalSuperCall(XClass xClass) {
        return !isDefinedIn(xClass) && isImplementedInSuperclass(xClass);
    }

}
