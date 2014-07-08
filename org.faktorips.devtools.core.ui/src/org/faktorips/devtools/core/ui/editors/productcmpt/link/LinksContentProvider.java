/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.editors.productcmpt.link;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.internal.model.productcmpt.IProductCmptLinkContainer;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptLink;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.core.ui.util.LinkCreatorUtil;

/**
 * Provides the content for a generation-based association-tree. The association names are requested
 * from the given generation and all supertypes the type containing this generation is based on.
 * 
 * @author Thorsten Guenther
 */
public class LinksContentProvider implements ITreeContentProvider {

    @Override
    public Object[] getElements(Object inputElement) {
        if (!(inputElement instanceof IProductCmptGeneration)) {
            throw new RuntimeException("Unknown input element type " + inputElement.getClass()); //$NON-NLS-1$
        }
        IProductCmptGeneration generation = (IProductCmptGeneration)inputElement;
        try {
            IProductCmpt pc = generation.getProductCmpt();

            IProductCmptType pcType = pc.findProductCmptType(generation.getIpsProject());
            if (pcType == null) {
                /*
                 * Type can't be found in case product component is loaded from a VCS repository.
                 * Extract the association names from the links in the generation and product
                 * component.
                 */
                return getDetachedAssociationViewItems(generation);
            } else {
                // find association using the product cmpt's project
                return getAssociationItems(pcType, pc.getIpsProject(), generation);
            }
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    protected DetachedAssociationViewItem[] getDetachedAssociationViewItems(IProductCmptGeneration gen) {
        List<DetachedAssociationViewItem> items = new ArrayList<DetachedAssociationViewItem>();
        items.addAll(getAssociationItemsForLinkContainer(gen.getProductCmpt()));
        items.addAll(getAssociationItemsForLinkContainer(gen));
        return items.toArray(new DetachedAssociationViewItem[items.size()]);
    }

    protected List<DetachedAssociationViewItem> getAssociationItemsForLinkContainer(IProductCmptLinkContainer linkContainer) {
        List<DetachedAssociationViewItem> items = new ArrayList<DetachedAssociationViewItem>();
        Set<String> associations = new LinkedHashSet<String>();
        List<IProductCmptLink> links = linkContainer.getLinksAsList();
        for (IProductCmptLink link : links) {
            if (associations.add(link.getAssociation())) {
                items.add(new DetachedAssociationViewItem(linkContainer, link.getAssociation()));
            }
        }
        return items;
    }

    protected AssociationViewItem[] getAssociationItems(IProductCmptType type,
            IIpsProject ipsProject,
            IProductCmptGeneration generation) {
        List<AssociationViewItem> items = new ArrayList<AssociationViewItem>();
        List<IProductCmptTypeAssociation> associations = type.findAllNotDerivedAssociations(ipsProject);
        for (IProductCmptTypeAssociation association : associations) {
            if (association.isRelevant()) {
                IProductCmptLinkContainer container = LinkCreatorUtil.getLinkContainerFor(generation, association);
                AssociationViewItem associationViewItem = createAssociationViewItem(container, association);
                items.add(associationViewItem);
            }
        }
        return items.toArray(new AssociationViewItem[items.size()]);
    }

    private AssociationViewItem createAssociationViewItem(IProductCmptLinkContainer container,
            IProductCmptTypeAssociation association) {
        return new AssociationViewItem(container, association);
    }

    @Override
    public void dispose() {
        // Nothing to do
    }

    @Override
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof AbstractAssociationViewItem) {
            List<ILinkSectionViewItem> children = ((AbstractAssociationViewItem)parentElement).getChildren();
            return children.toArray(new ILinkSectionViewItem[children.size()]);
        }
        return new Object[0];
    }

    @Override
    public Object getParent(Object element) {
        if (element instanceof LinkViewItem) {
            LinkViewItem linkViewItem = (LinkViewItem)element;
            IProductCmptLink link = linkViewItem.getLink();
            try {
                IProductCmptTypeAssociation association = link.findAssociation(link.getIpsProject());
                return createAssociationViewItem(link.getProductCmptLinkContainer(), association);
            } catch (CoreException e) {
                throw new CoreRuntimeException(e);
            }
        }
        return null;
    }

    @Override
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        /*
         * No need to implement. Input is given in #getElements() and needs to be recalculated every
         * time anyways.
         */
    }

}
