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

package org.faktorips.devtools.core.internal.model.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.faktorips.datatype.EnumDatatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.internal.model.ipsobject.IpsObjectPartCollection;
import org.faktorips.devtools.core.model.enums.EnumTypeValidations;
import org.faktorips.devtools.core.model.enums.EnumsUtil;
import org.faktorips.devtools.core.model.enums.IEnumAttribute;
import org.faktorips.devtools.core.model.enums.IEnumAttributeValue;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.enums.IEnumValue;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.util.ArgumentCheck;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.faktorips.util.message.ObjectProperty;
import org.w3c.dom.Element;

/**
 * Implementation of <code>IEnumType</code>, see the corresponding interface for more details.
 * 
 * @see org.faktorips.devtools.core.model.enums.IEnumType
 * 
 * @author Alexander Weickmann
 * 
 * @since 2.3
 */
public class EnumType extends EnumValueContainer implements IEnumType {

    /** Qualified name of the super enum type if any. */
    private String superEnumType;

    /** Flag indicating whether the values for this enum type are defined in the model. */
    private boolean containingValues;

    /** Qualified name of the package fragment a referencing enum content must be stored in. */
    private String enumContentPackageFragment;

    /** Collection containing all enum attributes for this enum type. */
    private IpsObjectPartCollection enumAttributes;

    /**
     * Flag indicating whether this enum type is abstract in means of the object oriented abstract
     * concept.
     */
    private boolean isAbstract;

    /**
     * Creates a new <code>EnumType</code>.
     * 
     * @param file The ips source file in which this enum type will be stored in.
     */
    public EnumType(IIpsSrcFile file) {
        super(file);

        this.superEnumType = "";
        this.containingValues = false;
        this.isAbstract = false;
        this.enumContentPackageFragment = "";
        this.enumAttributes = new IpsObjectPartCollection(this, EnumAttribute.class, IEnumAttribute.class,
                IEnumAttribute.XML_TAG);
    }

    /**
     * {@inheritDoc}
     */
    public IpsObjectType getIpsObjectType() {
        return IpsObjectType.ENUM_TYPE;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isAbstract() {
        return isAbstract;
    }

    /**
     * {@inheritDoc}
     */
    public void setAbstract(boolean isAbstract) {
        boolean oldIsAbstract = this.isAbstract;
        this.isAbstract = isAbstract;
        valueChanged(oldIsAbstract, isAbstract);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isContainingValues() {
        return containingValues;
    }

    /**
     * {@inheritDoc}
     */
    public void setContainingValues(boolean containingValues) {
        boolean oldContainingValues = this.containingValues;
        this.containingValues = containingValues;
        valueChanged(oldContainingValues, containingValues);
    }

    /**
     * {@inheritDoc}
     */
    public String getSuperEnumType() {
        return superEnumType;
    }

    /**
     * {@inheritDoc}
     */
    public void setSuperEnumType(String superEnumTypeQualifiedName) {
        ArgumentCheck.notNull(superEnumTypeQualifiedName);

        String oldSupertype = superEnumType;
        superEnumType = superEnumTypeQualifiedName;
        valueChanged(oldSupertype, superEnumType);
    }

    /**
     * {@inheritDoc}
     */
    public List<IEnumAttribute> getEnumAttributes() {
        return getEnumAttributes(false);
    }

    /**
     * {@inheritDoc}
     */
    public List<IEnumAttribute> getEnumAttributesIncludeSupertypeCopies() {
        return getEnumAttributes(true);
    }

    /**
     * Returns a list containing all enum attributes that belong to this enum type. It can be
     * specified whether to include copied inherited attributes or not.
     */
    private List<IEnumAttribute> getEnumAttributes(boolean includeInheritedCopies) {
        List<IEnumAttribute> attributesList = new ArrayList<IEnumAttribute>();
        IIpsObjectPart[] parts = enumAttributes.getParts();
        for (IIpsObjectPart currentIpsObjectPart : parts) {
            IEnumAttribute currentEnumAttribute = (IEnumAttribute)currentIpsObjectPart;
            if (!(currentEnumAttribute.isInherited()) || includeInheritedCopies) {
                attributesList.add(currentEnumAttribute);
            }
        }

        return attributesList;
    }

    /**
     * {@inheritDoc}
     */
    public List<IEnumAttribute> findAllEnumAttributesIncludeSupertypeOriginals() throws CoreException {
        // TODO aw: do we need this again and again if we want to search super enum types?
        EnumTypeHierachyVisitor collector = new EnumTypeHierachyVisitor(getIpsProject()) {
            protected boolean visit(IEnumType currentType) throws CoreException {
                return true;
            }
        };

        List<IEnumAttribute> attributesList = new ArrayList<IEnumAttribute>();
        IEnumType currentEnumType = this;
        while (currentEnumType != null) {
            attributesList.addAll(currentEnumType.getEnumAttributes());
            currentEnumType = collector.findSupertype(currentEnumType, getIpsProject());
        }

        return attributesList;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute newEnumAttribute() throws CoreException {
        /*
         * The creation of a new enum attribute consists of multiple operations that need to be
         * batched.
         */
        NewEnumAttributeRunnable workspaceRunnable = new NewEnumAttributeRunnable();
        getIpsModel().runAndQueueChangeEvents(workspaceRunnable, null);

        return workspaceRunnable.newEnumAttribute;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumType findEnumType() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public int getEnumAttributesCount(boolean includeInherited) {
        if (includeInherited) {
            return getEnumAttributesIncludeSupertypeCopies().size();
        } else {
            return getEnumAttributes().size();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void initFromXml(Element element, Integer id) {
        isAbstract = Boolean.parseBoolean(element.getAttribute(PROPERTY_ABSTRACT));
        containingValues = Boolean.parseBoolean(element.getAttribute(PROPERTY_CONTAINING_VALUES));
        superEnumType = element.getAttribute(PROPERTY_SUPERTYPE);
        enumContentPackageFragment = element.getAttribute(PROPERTY_ENUM_CONTENT_PACKAGE_FRAGMENT);

        super.initFromXml(element, id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void propertiesToXml(Element element) {
        super.propertiesToXml(element);

        element.setAttribute(PROPERTY_SUPERTYPE, superEnumType);
        element.setAttribute(PROPERTY_ABSTRACT, String.valueOf(isAbstract));
        element.setAttribute(PROPERTY_CONTAINING_VALUES, String.valueOf(containingValues));
        element.setAttribute(PROPERTY_ENUM_CONTENT_PACKAGE_FRAGMENT, enumContentPackageFragment);
    }

    /**
     * {@inheritDoc}
     */
    public int moveEnumAttribute(IEnumAttribute enumAttribute, boolean up) throws CoreException {
        ArgumentCheck.notNull(enumAttribute);
        if (enumAttribute.getEnumType() != this) {
            throw new NoSuchElementException();
        }

        if (up) {
            // Can't move further up any more
            if (enumAttribute == enumAttributes.getPart(0)) {
                return getIndexOfEnumAttribute(enumAttribute);
            }
        } else {
            // Can't move further down any more
            if (enumAttribute == enumAttributes.getPart(enumAttributes.size() - 1)) {
                return getIndexOfEnumAttribute(enumAttribute);
            }
        }

        int indexToMove = getIndexOfEnumAttribute(enumAttribute);

        // Moving an enum attribute consists of multiple operations that need to be batched.
        MoveEnumAttributeRunnable workspaceRunnable = new MoveEnumAttributeRunnable(indexToMove, up);
        getIpsModel().runAndQueueChangeEvents(workspaceRunnable, null);

        return workspaceRunnable.newIndex;
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public int getIndexOfEnumAttribute(IEnumAttribute enumAttribute) {
        ArgumentCheck.notNull(enumAttribute);

        List<IEnumAttribute> enumAttributesList = enumAttributes.getBackingList();
        for (int i = 0; i < enumAttributesList.size(); i++) {
            IEnumAttribute currentEnumAttribute = enumAttributesList.get(i);
            if (currentEnumAttribute == enumAttribute) {
                return i;
            }
        }

        throw new NoSuchElementException();
    }

    /**
     * Moves the enum attribute value corresponding to the given enum attribute identified by its
     * index in each given enum value up or down in the containing list by 1.
     */
    private void moveEnumAttributeValues(int enumAttributeIndex, List<IEnumValue> enumValues, boolean up)
            throws CoreException {

        for (IEnumValue currentEnumValue : enumValues) {
            currentEnumValue.moveEnumAttributeValue(currentEnumValue.getEnumAttributeValues().get(enumAttributeIndex),
                    up);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean enumAttributeExists(String name) {
        ArgumentCheck.notNull(name);

        for (IEnumAttribute currentEnumAttribute : getEnumAttributes()) {
            if (currentEnumAttribute.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute getEnumAttribute(String name) {
        ArgumentCheck.notNull(name);

        return getEnumAttribute(name, false);
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute getEnumAttributeIncludeSupertypeCopies(String name) {
        ArgumentCheck.notNull(name);

        return getEnumAttribute(name, true);
    }

    /**
     * Searches and returns the enum attribute with the given name or <code>null</code> if none
     * exists. It can be specified whether to include inherited enum attributes.
     */
    private IEnumAttribute getEnumAttribute(String name, boolean includeInherited) {
        List<IEnumAttribute> enumAttributesToSearch;
        if (includeInherited) {
            enumAttributesToSearch = getEnumAttributesIncludeSupertypeCopies();
        } else {
            enumAttributesToSearch = getEnumAttributes();
        }

        for (IEnumAttribute currentEnumAttribute : enumAttributesToSearch) {
            if (currentEnumAttribute.getName().equals(name)) {
                return currentEnumAttribute;
            }
        }

        // No enum attribute with the given name found
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute findEnumAttributeIncludeSupertypeOriginals(String name) throws CoreException {
        ArgumentCheck.notNull(name);

        // TODO aw: do we need this again and again if we want to search super enum types?
        EnumTypeHierachyVisitor collector = new EnumTypeHierachyVisitor(getIpsProject()) {
            protected boolean visit(IEnumType currentType) throws CoreException {
                return true;
            }
        };

        IEnumType currentEnumType = this;
        while (currentEnumType != null) {
            IEnumAttribute enumAttribute = currentEnumType.getEnumAttribute(name);
            if (enumAttribute != null) {
                return enumAttribute;
            }

            currentEnumType = collector.findSupertype(currentEnumType, getIpsProject());
        }

        // No enum attribute with the given name found
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void validateThis(MessageList list, IIpsProject ipsProject) throws CoreException {
        super.validateThis(list, ipsProject);

        Message validationMessage = null;

        // Validate super enum type
        if (hasSuperEnumType()) {
            EnumTypeValidations.validateSuperEnumType(list, this, superEnumType, ipsProject);
        }

        EnumTypeValidations.validateSuperTypeHierarchy(list, this, ipsProject);

        // Validate inherited attributes
        if (hasSuperEnumType()) {
            if (validationMessage == null) {
                validateInheritedAttributes(list, this);
            }
        }

        // Validate identifier attribute
        validateLiteralNameAttribute(list, this);
    }

    /**
     * Validates whether the given enum type inherits all enum attributes defined in its supertype
     * hierarchy.
     * <p>
     * Adds validation messages to the given message list.
     */
    private void validateInheritedAttributes(MessageList validationMessageList, IEnumType enumType)
            throws CoreException {

        ArgumentCheck.notNull(new Object[] { validationMessageList, enumType });

        // All attributes from supertype hierarchy inherited?
        List<IEnumAttribute> notInheritedAttributes = getNotInheritedAttributes(enumType);
        int notInheritedAttributesCount = notInheritedAttributes.size();
        if (notInheritedAttributesCount > 0) {
            IEnumAttribute firstNotInheritedAttribute = notInheritedAttributes.get(0);
            String showFirst = firstNotInheritedAttribute.getName() + " (" + firstNotInheritedAttribute.getDatatype() //$NON-NLS-1$
                    + ')';
            String text = (notInheritedAttributesCount > 1) ? NLS.bind(
                    Messages.EnumType_NotInheritedAttributesInSupertypeHierarchyPlural, notInheritedAttributesCount,
                    showFirst) : NLS.bind(Messages.EnumType_NotInheritedAttributesInSupertypeHierarchySingular,
                    showFirst);
            Message message = new Message(IEnumType.MSGCODE_ENUM_TYPE_NOT_INHERITED_ATTRIBUTES_IN_SUPERTYPE_HIERARCHY,
                    text, Message.ERROR, new ObjectProperty[] { new ObjectProperty(enumType,
                            IEnumType.PROPERTY_SUPERTYPE) });
            validationMessageList.add(message);
        }
    }

    /**
     * Validates whether the given enum type has at least one attribute being marked as literal
     * name.
     * <p>
     * If the given enum type is abstract the validation will succeed even if there is no literal
     * name attribute.
     */
    private void validateLiteralNameAttribute(MessageList validationMessageList, IEnumType enumType) {
        ArgumentCheck.notNull(new Object[] { validationMessageList, enumType });

        // Pass validation if given enum type is abstract
        if (enumType.isAbstract()) {
            return;
        }

        boolean literalNameAttributeFound = false;
        for (IEnumAttribute currentEnumAttribute : enumType.getEnumAttributesIncludeSupertypeCopies()) {
            if (currentEnumAttribute.isLiteralName()) {
                literalNameAttributeFound = true;
                break;
            }
        }

        if (!(literalNameAttributeFound)) {
            String text = Messages.EnumType_NoLiteralNameAttribute;
            Message message = new Message(IEnumType.MSGCODE_ENUM_TYPE_NO_LITERAL_NAME_ATTRIBUTE, text, Message.ERROR,
                    new ObjectProperty[] { new ObjectProperty(enumType, null) });
            validationMessageList.add(message);
        }
    }

    /**
     * Returns all attributes from the supertype hierarchy of the given enum type that are not
     * inherited in the given enum type.
     */
    private List<IEnumAttribute> getNotInheritedAttributes(IEnumType enumType) throws CoreException {
        List<IEnumAttribute> inheritedAttributes = new ArrayList<IEnumAttribute>();
        for (IEnumAttribute currentEnumAttribute : enumType.getEnumAttributesIncludeSupertypeCopies()) {
            if (currentEnumAttribute.isInherited()) {
                inheritedAttributes.add(currentEnumAttribute);
            }
        }
        List<IEnumAttribute> supertypeHierarchyAttributes = findAllAttributesInSupertypeHierarchy(enumType);
        List<IEnumAttribute> notInheritedAttributes = new ArrayList<IEnumAttribute>();

        for (IEnumAttribute currentSupertypeHierarchyAttribute : supertypeHierarchyAttributes) {
            if (!(EnumsUtil.containsEqualEnumAttribute(inheritedAttributes, currentSupertypeHierarchyAttribute))) {
                notInheritedAttributes.add(currentSupertypeHierarchyAttribute);
            }
        }

        return notInheritedAttributes;
    }

    /** Returns all attributes that are defined in the supertype hierarchy of the given enum type. */
    private List<IEnumAttribute> findAllAttributesInSupertypeHierarchy(IEnumType enumType) throws CoreException {
        List<IEnumAttribute> returnAttributesList = new ArrayList<IEnumAttribute>();

        /* Go over all enum attributes of every enum type of the supertype hierarchy */
        for (IEnumType currentSuperEnumType : enumType.findAllSuperEnumTypes()) {
            for (IEnumAttribute currentEnumAttribute : currentSuperEnumType.getEnumAttributes()) {

                /*
                 * Add to the return list if the list does not yet contain an attribute with the
                 * name, datatype and identifier of the current inspected enum attribute from the
                 * supertype hierarchy.
                 */
                String currentName = currentEnumAttribute.getName();
                String currentDatatype = currentEnumAttribute.getDatatype();
                boolean currentIsIdentifier = currentEnumAttribute.isLiteralName();

                boolean attributeInList = false;
                for (IEnumAttribute currentAttributeInReturnList : returnAttributesList) {
                    if (currentAttributeInReturnList.getName().equals(currentName)
                            && currentAttributeInReturnList.getDatatype().equals(currentDatatype)
                            && currentAttributeInReturnList.isLiteralName() == currentIsIdentifier) {
                        attributeInList = true;
                        break;
                    }
                }

                if (!(attributeInList)) {
                    returnAttributesList.add(currentEnumAttribute);
                }
            }
        }

        return returnAttributesList;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumType findSuperEnumType() throws CoreException {
        IIpsSrcFile[] enumTypeSrcFiles = getIpsProject().findIpsSrcFiles(IpsObjectType.ENUM_TYPE);
        for (IIpsSrcFile currentIpsSrcFile : enumTypeSrcFiles) {
            IEnumType currentEnumType = (IEnumType)currentIpsSrcFile.getIpsObject();
            if (currentEnumType.getQualifiedName().equals(superEnumType)) {
                return currentEnumType;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute findLiteralNameAttribute() {
        for (IEnumAttribute currentEnumAttribute : getEnumAttributesIncludeSupertypeCopies()) {
            if (currentEnumAttribute.isLiteralName()) {
                return currentEnumAttribute;
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void deleteEnumAttributeWithValues(final IEnumAttribute enumAttribute) throws CoreException {
        ArgumentCheck.notNull(enumAttribute);
        ArgumentCheck.isTrue(enumAttributes.getBackingList().contains(enumAttribute));

        // Deleting an enum attribute consists of multiple operations that need to be batched.
        IWorkspaceRunnable workspaceRunnable = new IWorkspaceRunnable() {
            /**
             * {@inheritDoc}
             */
            public void run(IProgressMonitor monitor) throws CoreException {
                deleteEnumAttributeValues(enumAttribute, getEnumValues());
                enumAttribute.delete();
            }
        };
        getIpsModel().runAndQueueChangeEvents(workspaceRunnable, null);
    }

    /**
     * Deletes all enum attribute values in the given enum values that refer to the given enum
     * attribute.
     */
    private void deleteEnumAttributeValues(IEnumAttribute enumAttribute, List<IEnumValue> enumValues)
            throws CoreException {

        for (IEnumValue currentEnumValue : enumValues) {
            for (IEnumAttributeValue currentEnumAttributeValue : currentEnumValue.getEnumAttributeValues()) {
                if (currentEnumAttributeValue.findEnumAttribute() == enumAttribute) {
                    currentEnumAttributeValue.delete();
                    break;
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasSuperEnumType() {
        return !("".equals(superEnumType));
    }

    /**
     * {@inheritDoc}
     */
    public List<IEnumType> findAllSuperEnumTypes() throws CoreException {
        List<IEnumType> enumTypes = new ArrayList<IEnumType>();

        IEnumType currentEnumType = this;
        while (currentEnumType != null) {
            IEnumType superEnumType = currentEnumType.findSuperEnumType();
            currentEnumType = superEnumType;
            if (superEnumType != null) {
                enumTypes.add(superEnumType);
            }
        }

        return enumTypes;
    }

    /**
     * {@inheritDoc}
     */
    public String getEnumContentPackageFragment() {
        return enumContentPackageFragment;
    }

    /**
     * {@inheritDoc}
     */
    public void setEnumContentPackageFragment(String packageFragmentQualifiedName) {
        ArgumentCheck.notNull(packageFragmentQualifiedName);

        String oldEnumContentPackageFragment = enumContentPackageFragment;
        enumContentPackageFragment = packageFragmentQualifiedName;
        valueChanged(oldEnumContentPackageFragment, packageFragmentQualifiedName);
    }

    /**
     * {@inheritDoc}
     */
    public IEnumAttribute getLiteralNameAttribute() {
        for (IEnumAttribute attribute : getEnumAttributes()) {
            if (attribute.isLiteralName()) {
                return attribute;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    /*
     * TODO aw: check if it is correct that this method uses the ips project that it belongs to or
     * does the ips project need to be provided.
     */
    public String getJavaClassName() {
        return getIpsProject().getDatatypeHelper(this).getJavaClassName();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasNullObject() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPrimitive() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isValueDatatype() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isVoid() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(Object o) {
        EnumDatatype other = (EnumDatatype)o;
        return getQualifiedName().compareTo(other.getQualifiedName());
    }

    /**
     * Returns the ids of the values of this enum type. The literal name attribute value is
     * considered to be the id in this context. Returns <code>null</code> if the value of the
     * literal name attribute of a value of this enum type could not be determined.
     * 
     * @throws a <code>RuntimeException</code> if the process of determining the enum attribute
     *             values throws a <code>CoreException</code>.
     */
    public String[] getAllValueIds(boolean includeNull) {
        try {

            List<String> valueIds = new ArrayList<String>(getEnumValuesCount());
            for (IEnumValue enumValue : getEnumValues()) {
                IEnumAttribute literalNameEnumAttribute = findLiteralNameAttribute();
                if (literalNameEnumAttribute == null) {
                    return null;
                }
                IEnumAttributeValue value = enumValue.findEnumAttributeValue(literalNameEnumAttribute);
                if (value == null) {
                    return null;
                }
                valueIds.add(value.getValue());
            }

            return valueIds.toArray(new String[valueIds.size()]);

        } catch (CoreException e) {
            throw new RuntimeException("Unable to determine the value ids of this enum type.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public IEnumValue getEnumValue(String literalNameAttributeValue) throws CoreException {
        if (literalNameAttributeValue == null) {
            return null;
        }
        for (IEnumValue enumValue : getEnumValues()) {
            IEnumAttributeValue value = enumValue.findEnumAttributeValue(findLiteralNameAttribute());
            if (value == null) {
                continue;
            }
            if (literalNameAttributeValue.equals(value.getValue())) {
                return enumValue;
            }
        }
        return null;
    }

    /**
     * In the context of an enum type the id is the value of the literal name attribute. This method
     * checks if the provided id is one of the enum attribute values. If so the id is returned
     * otherwise <code>null</code> is returned.
     */
    public String getValueName(String id) {
        if (id == null) {
            return null;
        }

        try {

            for (IEnumValue enumValue : getEnumValues()) {
                IEnumAttributeValue value = enumValue.findEnumAttributeValue(findLiteralNameAttribute());
                if (value == null) {
                    continue;
                }
                if (id.equals(value.getValue())) {
                    return value.getValue();
                }
            }

            return null;

        } catch (CoreException e) {
            throw new RuntimeException("Unable to determine the value ids of this enum type.", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSupportingNames() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public boolean areValuesEqual(String valueA, String valueB) {
        String valueAName = getValueName(valueA);
        String valueBName = getValueName(valueB);
        if (valueAName != null && valueBName != null) {
            return ObjectUtils.equals(valueA, valueB);
        }

        throw new IllegalArgumentException("Either the value of parameter valueA=" + valueA
                + " or the one of parameter valueB="
                + " is not part of this enumeration type. Therefor the equality cannot be determined.");
    }

    /**
     * {@inheritDoc}
     */
    /*
     * TODO pk: this method needs IIpsProject as parameter but this collides with its original
     * intention which also manifests in being located in the commons project.
     */
    public MessageList checkReadyToUse() {
        try {
            return validate(getIpsProject());
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns true.
     */
    public boolean supportsCompare() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public int compare(String valueA, String valueB) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * {@inheritDoc}
     */
    public String getDefaultValue() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public ValueDatatype getWrapperType() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isImmutable() {
        return !isMutable();
    }

    /**
     * Returns true.
     */
    public boolean isMutable() {
        return false;
    }

    /**
     * Returns false.
     */
    public boolean isNull(String value) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isParsable(String value) {
        return getValueName(value) != null;
    }

    /**
     * Creates a new enum attribute. On every enum value that is contained in this enum type new
     * enum attribute value objects need to be created for the new enum attribute.
     * <p>
     * These operations are atomic and therefore need to be batched into a runanble.
     */
    private class NewEnumAttributeRunnable implements IWorkspaceRunnable {

        /** Handle to the enum attribute to be created. */
        private IEnumAttribute newEnumAttribute;

        /** Creates the <code>NewEnumAttributeRunnable</code>. */
        public NewEnumAttributeRunnable() {
            this.newEnumAttribute = null;
        }

        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) throws CoreException {
            // Create new enum attribute
            newEnumAttribute = (IEnumAttribute)newPart(IEnumAttribute.class);

            // Create new enum attribute value objects on the enum values of this enum type
            for (IEnumValue currentEnumValue : getEnumValues()) {
                currentEnumValue.newEnumAttributeValue();
            }
        }

    }

    /**
     * Moves an enum attribute. On every enum value that is contained in this enum type the enum
     * attribute values refering to this enum attribute need to be moved, too.
     * <p>
     * These operations are atomic and therefore need to be batched into a runanble.
     */
    private class MoveEnumAttributeRunnable implements IWorkspaceRunnable {

        /** The index of the enum attribute to be moved. */
        private int indexToMove;

        /** The new index of the enum attriubute that has been moved. */
        private int newIndex;

        /** Flag indicating whether to move up or down. */
        private boolean up;

        /** Creates the <code>MoveEnumAttributeRunnable</code>. */
        public MoveEnumAttributeRunnable(int indexToMove, boolean up) {
            this.indexToMove = indexToMove;
            this.newIndex = -1;
            this.up = up;
        }

        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) throws CoreException {
            // Move the enum attribute
            int[] newIndex = enumAttributes.moveParts(new int[] { indexToMove }, up);

            // Move the enum attribute values of the enum values of this enum type
            moveEnumAttributeValues(indexToMove, getEnumValues(), up);

            this.newIndex = newIndex[0];
        }

    }

}
