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

package org.faktorips.devtools.core.model.testcase;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.testcasetype.ITestAttribute;
import org.faktorips.devtools.core.model.type.IAttribute;

/**
 *  Specification of a test attribute value.
 *  
 * @author Joerg Ortmann
 */
public interface ITestAttributeValue extends IIpsObjectPart{
	
	/** Property names */
    public final static String PROPERTY_ATTRIBUTE = "testAttribute"; //$NON-NLS-1$
    public final static String PROPERTY_VALUE = "value"; //$NON-NLS-1$
    
    /**
     * Prefix for all message codes of this class.
     */
    public final static String MSGCODE_PREFIX = "TESTATTRIBUTEVALUE-"; //$NON-NLS-1$
    
    /**
	 * Validation message code to indicate that the corresponding test attribute not exists.
	 */
	public final static String MSGCODE_TESTATTRIBUTE_NOT_FOUND = MSGCODE_PREFIX
		+ "TestAttributeNotFound"; //$NON-NLS-1$
	
    /**
     * Returns the attribute. 
     */
	public String getTestAttribute();
	
	/**
	 * Sets the given attribute.
	 */
	public void setTestAttribute(String attribute);
	
    /**
     * Returns the test attribute or <code>null</code> if the test attribute does not exists.
     * 
     * @param ipsProject The ips project which object path is used to search.
     * 
     * @throws CoreException if an error occurs while searching for the test attribute.
     */		
	public ITestAttribute findTestAttribute(IIpsProject ipsProject) throws CoreException;
	
    /**
     * Search and returns the corresponding attribute.<br>
     * If the given test policy cmpt is not product relevant then the attribute will be searched
     * using the policy cmpt super-/subtype hierarchy. If the test policy cmpt is product relevant
     * then the corresponding product cmpt type will be used to start the searching the supertype
     * hierarchy.
     */    
    public IAttribute findAttribute(IIpsProject ipsProject) throws CoreException;
    
	/**
	 * Returns value of the attribute.
	 */
	public String getValue();
	
	/**
	 * Sets the value of the attribute.
	 */
	public void setValue(String newValue);
    
    /**
     * Returns <code>true</code> if the test attribute value is an input attribute, 
     * otherwise <code>false</code>.
     * 
     * @param ipsProject The ips project which object path is used to search the attribute.
     */
    public boolean isInputAttribute(IIpsProject ipsProject);
    
    /**
     * Returns <code>true</code> if the test attribute value is an expected result attribute, 
     * otherwise <code>false</code>.
     * 
     * @param ipsProject The ips project which object path is used to search the attribute.
     */
    public boolean isExpextedResultAttribute(IIpsProject ipsProject);
    
    /**
     * Updates the default for the test attribute value. The default will be retrieved from the
     * product cmpt or if no product cmpt is available or the attribute isn't configurated by product 
     * then from the policy cmpt. Don't update the value if not default is specified.
     * 
     * @throws CoreException in case of an error.
     */
    public void updateDefaultTestAttributeValue() throws CoreException;
}
