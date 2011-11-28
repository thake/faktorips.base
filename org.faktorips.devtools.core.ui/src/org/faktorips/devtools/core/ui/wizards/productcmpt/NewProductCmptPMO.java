/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.wizards.productcmpt;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.internal.model.type.TypeHierarchy;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsobject.QualifiedNameType;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.type.IType;
import org.faktorips.devtools.core.ui.binding.PresentationModelObject;

public class NewProductCmptPMO extends PresentationModelObject {

    public static final String PROPERTY_IPSPROJECT = "ipsProject"; //$NON-NLS-1$

    public static final String PROPERTY_SELECTED_BASE_TYPE = "selectedBaseType"; //$NON-NLS-1$

    public static final String PROPERTY_CAN_EDIT_RUNTIME_ID = "canEditRuntimeId"; //$NON-NLS-1$

    public static final String PROPERTY_SELECTED_TYPE = "selectedType"; //$NON-NLS-1$

    public static final String PROPERTY_NAME = "name"; //$NON-NLS-1$

    public static final String PROPERTY_VERSION_ID = "versionId"; //$NON-NLS-1$

    public static final String PROPERTY_RUNTIME_ID = "runtimeId"; //$NON-NLS-1$

    private String ipsProject = StringUtils.EMPTY;

    private IProductCmptType selectedBaseType;

    private IProductCmptType selectedType;

    private List<IProductCmptType> baseTypes = new ArrayList<IProductCmptType>();

    private String name = StringUtils.EMPTY;

    private String versionId = StringUtils.EMPTY;

    private String runtimeId = StringUtils.EMPTY;

    /**
     * @param ipsProject The ipsProject to set.
     */
    public void setIpsProject(String ipsProject) {
        String oldValue = this.ipsProject;
        this.ipsProject = ipsProject;
        updateBaseTypeList();
        selectedBaseType = null;
        notifyListeners(new PropertyChangeEvent(this, PROPERTY_IPSPROJECT, oldValue, ipsProject));
    }

    private IIpsProject findIpsProject() {
        return IpsPlugin.getDefault().getIpsModel().getIpsProject(ipsProject);
    }

    /**
     * Searches all {@link IProductCmptType} in selected project and adding fills the list of base
     * types.
     * <p>
     * Every type that has either no super type or which super type is an layer supertype is added
     * as an base type.
     */
    private void updateBaseTypeList() {
        if (ipsProject == null) {
            return;
        }
        baseTypes = new ArrayList<IProductCmptType>();
        IIpsProject ipsProject = findIpsProject();
        if (ipsProject == null) {
            return;
        }
        try {
            IIpsSrcFile[] findIpsSrcFiles = ipsProject.findIpsSrcFiles(IpsObjectType.PRODUCT_CMPT_TYPE);
            for (IIpsSrcFile ipsSrcFile : findIpsSrcFiles) {
                boolean layerSupertype = Boolean.valueOf(ipsSrcFile
                        .getPropertyValue(IProductCmptType.PROPERTY_LAYER_SUPERTYPE));
                if (!layerSupertype) {
                    String superType = ipsSrcFile.getPropertyValue(IType.PROPERTY_SUPERTYPE);
                    if (StringUtils.isNotEmpty(superType)) {
                        IIpsSrcFile superTypeIpsSrcFile = ipsProject.findIpsSrcFile(new QualifiedNameType(superType,
                                IpsObjectType.PRODUCT_CMPT_TYPE));
                        if (Boolean.valueOf(superTypeIpsSrcFile
                                .getPropertyValue(IProductCmptType.PROPERTY_LAYER_SUPERTYPE))) {
                            baseTypes.add((IProductCmptType)ipsSrcFile.getIpsObject());
                        }
                    } else {
                        baseTypes.add((IProductCmptType)ipsSrcFile.getIpsObject());
                    }
                }
            }
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    /**
     * @return Returns the ipsProject.
     */
    public String getIpsProject() {
        return ipsProject;
    }

    /**
     * @return Returns the baseTypes.
     */
    public List<IProductCmptType> getBaseTypes() {
        List<IProductCmptType> sortedBaseTypes = new ArrayList<IProductCmptType>(baseTypes);
        // TODO Sort the list by any criteria
        // Collections.sort(sortedBaseTypes, new StrutcureComparator(baseTypes, findIpsProject()));
        return sortedBaseTypes;
    }

    /**
     * @param selectedBaseType The selectedBaseType to set.
     */
    public void setSelectedBaseType(IProductCmptType selectedBaseType) {
        IProductCmptType oldSelection = this.selectedBaseType;
        this.selectedBaseType = selectedBaseType;
        setSelectedType(null);
        notifyListeners(new PropertyChangeEvent(this, PROPERTY_SELECTED_BASE_TYPE, oldSelection, selectedBaseType));

    }

    /**
     * @param selectedType The selectedType to set.
     */
    public void setSelectedType(IProductCmptType selectedType) {
        IProductCmptType oldSelection = this.selectedType;
        this.selectedType = selectedType;
        notifyListeners(new PropertyChangeEvent(this, PROPERTY_SELECTED_TYPE, oldSelection, selectedType));
    }

    /**
     * @return Returns the selectedType.
     */
    public IProductCmptType getSelectedType() {
        return selectedType;
    }

    /**
     * @return Returns the selectedBaseType.
     */
    public IProductCmptType getSelectedBaseType() {
        return selectedBaseType;
    }

    public boolean isCanEditRuntimeId() {
        return IpsPlugin.getDefault().getIpsPreferences().canModifyRuntimeId();
    }

    public List<IProductCmptType> getSubtypes() {
        ArrayList<IProductCmptType> result = new ArrayList<IProductCmptType>();
        if (selectedBaseType == null) {
            return result;
        }
        try {
            TypeHierarchy subtypeHierarchy = TypeHierarchy.getSubtypeHierarchy(selectedBaseType, findIpsProject());
            List<IType> subtypes = subtypeHierarchy.getAllSubtypes(selectedBaseType);
            for (IType type : subtypes) {
                result.add((IProductCmptType)type);
            }
            return result;
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        String oldName = this.name;
        this.name = name;
        updateRuntimeId();
        notifyListeners(new PropertyChangeEvent(this, PROPERTY_NAME, oldName, name));
    }

    private void updateRuntimeId() {
        try {
            setRuntimeId(findIpsProject().getProperties().getProductCmptNamingStrategy()
                    .getUniqueRuntimeId(findIpsProject(), name));
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    public String getFullName() {
        return findIpsProject().getProperties().getProductCmptNamingStrategy().getProductCmptName(name, versionId);
    }

    /**
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param versionId The versionId to set.
     */
    public void setVersionId(String versionId) {
        String oldId = this.versionId;
        this.versionId = versionId;
        notifyListeners(new PropertyChangeEvent(this, PROPERTY_VERSION_ID, oldId, versionId));
    }

    /**
     * @return Returns the versionId.
     */
    public String getVersionId() {
        return versionId;
    }

    /**
     * @param runtimeId The runtimeId to set.
     */
    public void setRuntimeId(String runtimeId) {
        String oldRuntimeId = this.runtimeId;
        this.runtimeId = runtimeId;
        notifyListeners(new PropertyChangeEvent(this, PROPERTY_RUNTIME_ID, oldRuntimeId, runtimeId));
    }

    /**
     * @return Returns the runtimeId.
     */
    public String getRuntimeId() {
        return runtimeId;
    }
}
