/*******************************************************************************
 * Copyright (c) Faktor Zehn GmbH - faktorzehn.org
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.views.modelstructure;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.faktorips.devtools.core.internal.model.pctype.PolicyCmptType;
import org.faktorips.devtools.core.internal.model.type.Association;
import org.faktorips.devtools.core.ui.IpsUIPlugin;

public final class ModelStructureLabelProvider extends LabelProvider implements IStyledLabelProvider {

    private static final String PRODUCT_SUBTYPE_IMAGE = "product_subtype.gif"; //$NON-NLS-1$
    private static final String POLICY_SUBTYPE_IMAGE = "policy_subtype.gif"; //$NON-NLS-1$
    private static final String POLICY_ASSOCIATION_IMAGE = "policy_AssociationType-Aggregation.gif"; //$NON-NLS-1$
    private static final String PRODUCT_ASSOCIATION_IMAGE = "product_AssociationType-Aggregation.gif"; //$NON-NLS-1$
    private static final String OVERLAY_INHERITED_ASSOCIATION_IMAGE = "OverrideIndicator_orange.gif"; //$NON-NLS-1$
    private static final String OVERLAY_LOOP_IMAGE = "ovr16/loop_ovr.gif"; //$NON-NLS-1$
    private static final String PRODUCT_CMPT_TYPE_IMAGE = "ProductCmptType_width30.gif"; //$NON-NLS-1$
    private static final String POLICY_CMPT_TYPE_IMAGE = "PolicyCmptType_width30.gif"; //$NON-NLS-1$
    private static final String OVERLAY_ABSTRACT_IMAGE = "AbstractIndicator.gif"; //$NON-NLS-1$

    private boolean showCardinalities = true;
    private boolean showRolenames = true;
    private boolean showProjects = true;

    private LocalResourceManager resourceManager;

    public ModelStructureLabelProvider() {
        super();
        resourceManager = new LocalResourceManager(JFaceResources.getResources());
    }

    @Override
    public Image getImage(Object element) {
        if (element instanceof ComponentNode) {
            ComponentNode node = (ComponentNode)element;
            boolean overlayed = false;
            String[] overlayImages = new String[4];
            String imageName;
            if (node.getValue() instanceof PolicyCmptType) {
                imageName = POLICY_CMPT_TYPE_IMAGE;
            } else {
                imageName = PRODUCT_CMPT_TYPE_IMAGE;
            }

            // define the overlay images
            if (node.isRepetition()) {
                overlayImages[IDecoration.BOTTOM_LEFT] = OVERLAY_LOOP_IMAGE;
                overlayed = true;
            }
            if (node.getValue().isAbstract()) {
                overlayImages[IDecoration.TOP_RIGHT] = OVERLAY_ABSTRACT_IMAGE;
                overlayed = true;
            }
            if ((element instanceof AssociationComponentNode && ((AssociationComponentNode)element).isInherited())
                    || node.isTargetOfInheritedAssociation()) {
                overlayImages[IDecoration.BOTTOM_RIGHT] = OVERLAY_INHERITED_ASSOCIATION_IMAGE;
                overlayed = true;
            }
            if (node instanceof AssociationComponentNode) {
                if (node.getValue() instanceof PolicyCmptType) {
                    imageName = POLICY_ASSOCIATION_IMAGE;
                } else {
                    imageName = PRODUCT_ASSOCIATION_IMAGE;
                }
            } else if (node instanceof SubtypeComponentNode) {
                if (node.getValue() instanceof PolicyCmptType) {
                    imageName = POLICY_SUBTYPE_IMAGE;
                } else {
                    imageName = PRODUCT_SUBTYPE_IMAGE;
                }
            }

            if (overlayed) {
                return resourceManager.createImage(IpsUIPlugin.getImageHandling().getSharedOverlayImage(imageName,
                        overlayImages));
            } else {
                Image image = IpsUIPlugin.getImageHandling().getSharedImage(imageName, true);
                return image;
            }
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        if (element instanceof ComponentNode) {
            ComponentNode node = (ComponentNode)element;
            return node.getValue().getName();
        } else if (element instanceof String) {
            return element.toString();
        }
        return ""; //$NON-NLS-1$
    }

    @Override
    public StyledString getStyledText(Object element) {
        String label = getText(element);

        StyledString styledLabel = new StyledString(label);

        if (element instanceof AssociationComponentNode) {
            AssociationComponentNode node = ((AssociationComponentNode)element);

            if (showRolenames) {
                String rolename = node.getTargetRoleSingular();
                if (node.isDerivedUnion()) {
                    // mark association as a derived union
                    rolename = "/" + rolename; //$NON-NLS-1$
                }
                styledLabel.append(new StyledString(" - " + rolename, StyledString.QUALIFIER_STYLER)); //$NON-NLS-1$
            }

            if (showCardinalities) {
                styledLabel
                        .append(new StyledString(
                                " ["    + getCardinalityText(node.getMinCardinality()) + ".." + getCardinalityText(node.getMaxCardinality()) + "]", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                StyledString.COUNTER_STYLER));
            }
        }
        if (showProjects && element instanceof ComponentNode) {
            styledLabel.append(new StyledString(
                    " [" + ((ComponentNode)element).getValue().getIpsProject().getName() + "]", //$NON-NLS-1$ //$NON-NLS-2$
                    StyledString.DECORATIONS_STYLER));
        }
        return styledLabel;
    }

    @Override
    public void dispose() {
        super.dispose();
        resourceManager.dispose();
    }

    private String getCardinalityText(int cardinality) {
        if (cardinality == Association.CARDINALITY_MANY) {
            return "*"; //$NON-NLS-1$
        }
        return "" + cardinality; //$NON-NLS-1$
    }

    /**
     * @see #setShowCardinalities(boolean)
     */
    public boolean getShowCardinalities() {
        return this.showCardinalities;
    }

    /**
     * Defines if cardinalities should be shown on AssociationComponentNode labels.
     */
    public void setShowCardinalities(boolean showCardinalities) {
        this.showCardinalities = showCardinalities;
    }

    /**
     * Toggles the show cardinalities state.
     * 
     * @see #setShowCardinalities(boolean)
     */
    public void toggleShowCardinalities() {
        this.showCardinalities = !this.showCardinalities;
    }

    /**
     * @see #setShowRolenames(boolean)
     */
    public boolean getShowRolenames() {
        return this.showRolenames;
    }

    /**
     * Defines if role names should be shown on AssociationComponentNode labels.
     */
    public void setShowRolenames(boolean showRolenames) {
        this.showRolenames = showRolenames;
    }

    /**
     * Toggles the show role names state.
     * 
     * @see #setShowRolenames(boolean)
     */
    public void toggleShowRolenames() {
        this.showRolenames = !this.showRolenames;
    }

    /**
     * @see #setShowProjects(boolean) .
     */
    public boolean getShowProjects() {
        return this.showProjects;
    }

    /**
     * Defines if the project names should be shown on ComponentNode labels.
     */
    public void setShowProjects(boolean showProjects) {
        this.showProjects = showProjects;
    }

    /**
     * Toggles the show projects state.
     * 
     * @see #setShowProjects(boolean)
     */
    public void toggleShowProjects() {
        this.showProjects = !this.showProjects;
    }

    public String getToolTipText(Object element) {
        String text = ""; //$NON-NLS-1$
        if (element instanceof ComponentNode) {
            if (element instanceof AssociationComponentNode && ((AssociationComponentNode)element).isInherited()) {
                AssociationComponentNode node = (AssociationComponentNode)element;
                text += Messages.ModelStructure_tooltipInheritedAssociations
                        + " " + node.getTargetingType().getQualifiedName(); //$NON-NLS-1$
            } else if (((ComponentNode)element).isTargetOfInheritedAssociation()) {
                text += Messages.ModelStructure_tooltipHasInheritedAssociation;
            }
            if (!text.isEmpty()) {
                return text;
            }
        }
        return null;
    }

}
