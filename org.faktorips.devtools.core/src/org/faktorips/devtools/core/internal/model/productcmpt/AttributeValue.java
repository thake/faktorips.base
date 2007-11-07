/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model.productcmpt;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.internal.model.ValidationUtils;
import org.faktorips.devtools.core.internal.model.ipsobject.AtomicIpsObjectPart;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.productcmpt.IAttributeValue;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpttype.IProdDefProperty;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.ProdDefPropertyType;
import org.faktorips.devtools.core.model.valueset.ValueSetType;
import org.faktorips.runtime.internal.ValueToXmlHelper;
import org.faktorips.util.ArgumentCheck;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Jan Ortmann
 */
public class AttributeValue extends AtomicIpsObjectPart implements IAttributeValue {

    final static String TAG_NAME = "AttributeValue"; //$NON-NLS-1$
    
    private String attribute = ""; //$NON-NLS-1$
    private String value = ""; //$NON-NLS-1$
    
    public AttributeValue(IIpsObjectPart parent, int id) {
        super(parent, id);
    }

    public AttributeValue(IIpsObjectPart parent, int id, String attribute, String value) {
        super(parent, id);
        ArgumentCheck.notNull(attribute);
        this.attribute = attribute;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    public IProductCmptGeneration getProductCmptGeneration() {
        return (IProductCmptGeneration)getParent();
    }

    /**
     * {@inheritDoc}
     */
    protected Element createElement(Document doc) {
        return doc.createElement(TAG_NAME);
    }

    /**
     * {@inheritDoc}
     */
    public Image getImage() {
        return IpsPlugin.getDefault().getImage("AttributePublic.gif"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public String getAttribute() {
        return attribute;
    }

    /**
     * {@inheritDoc}
     */
    public void setAttribute(String newAttribute) {
        String oldAttr = attribute;
        attribute = newAttribute;
        name = attribute;
        valueChanged(oldAttr, attribute);
    }

    /**
     * {@inheritDoc}
     */
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public void setValue(String newValue) {
        String oldValue = value;
        value = newValue;
        valueChanged(oldValue, newValue);
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyName() {
        return attribute;
    }
    
    /**
     * {@inheritDoc}
     */
    public IProdDefProperty findProperty(IIpsProject ipsProject) throws CoreException {
        return findAttribute(ipsProject);
    }

    /**
     * {@inheritDoc}
     */
    public ProdDefPropertyType getPropertyType() {
        return ProdDefPropertyType.VALUE;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValue() {
        return value;
    }
    
    /**
     * {@inheritDoc}
     */
    public IProductCmptTypeAttribute findAttribute(IIpsProject ipsProject) throws CoreException {
        IProductCmptType type = getProductCmptGeneration().findProductCmptType(ipsProject);
        if (type==null) {
            return null;
        }
        return type.findProductCmptTypeAttribute(attribute, ipsProject);
    }

    /**
     * {@inheritDoc}
     */
    protected void initPropertiesFromXml(Element element, Integer id) {
        super.initPropertiesFromXml(element, id);
        this.attribute = element.getAttribute(PROPERTY_ATTRIBUTE);
        this.value = ValueToXmlHelper.getValueFromElement(element, "Value"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    protected void propertiesToXml(Element element) {
        super.propertiesToXml(element);
        element.setAttribute(PROPERTY_ATTRIBUTE, this.attribute);
        ValueToXmlHelper.addValueToElement(this.value, element, "Value"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    protected void validateThis(MessageList list) throws CoreException {
        super.validateThis(list);
        IIpsProject ipsProject = getIpsProject();
        IProductCmptTypeAttribute attr = findAttribute(ipsProject);
        if (attr==null) {
            String text = NLS.bind(Messages.AttributeValue_attributeNotFound, attribute, getProductCmptGeneration().getProductCmpt().getProductCmptType());
            list.add(new Message(MSGCODE_UNKNWON_ATTRIBUTE, text, Message.ERROR, this, PROPERTY_ATTRIBUTE));
            return;
        }
        if (!ValidationUtils.checkValue(attr.getDatatype(), value, this, PROPERTY_VALUE, list)) {
            return;
        }
        if (!attr.getValueSet().containsValue(value)) {
            String text;
            if (attr.getValueSet().getValueSetType()==ValueSetType.RANGE) {
                text = NLS.bind(Messages.AttributeValue_AllowedValuesAre, value, attr.getValueSet().toShortString());
            } else {
                text = NLS.bind(Messages.AttributeValue_ValueNotAllowed, value);
            }
            list.add(new Message(MSGCODE_VALUE_NOT_IN_SET, text, Message.ERROR, this, PROPERTY_VALUE));
        }
    }

    public String toString() {
        return attribute + "=" + value; //$NON-NLS-1$
    }
}
