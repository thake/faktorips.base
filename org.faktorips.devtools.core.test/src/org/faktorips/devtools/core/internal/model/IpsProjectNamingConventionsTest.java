/***************************************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorips.org/legal/cl-v01.html eingesehen werden kann.
 * 
 * Mitwirkende:  Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de  
 **************************************************************************************************/

package org.faktorips.devtools.core.internal.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.IIpsProjectNamingConventions;
import org.faktorips.devtools.core.model.IpsObjectType;
import org.faktorips.devtools.core.model.businessfct.BusinessFunction;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.product.IProductCmpt;
import org.faktorips.devtools.core.model.product.IProductCmptNamingStrategy;
import org.faktorips.devtools.core.model.tablecontents.ITableContents;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.core.model.testcase.ITestCase;
import org.faktorips.devtools.core.model.testcasetype.ITestCaseType;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;

/**
 * 
 * @author Daniel Hohenberger
 */
public class IpsProjectNamingConventionsTest extends AbstractIpsPluginTest {

    private IpsProject ipsProject;
    private IIpsProjectNamingConventions namingConventions;
    
    /**
     * {@inheritDoc}
     */
    protected void setUp() throws Exception {
        super.setUp();
        ipsProject = (IpsProject)this.newIpsProject("TestProject");
        namingConventions = ipsProject.getNamingConventions();
    }

    /**
     * Test method for {@link org.faktorips.devtools.core.internal.model.DefaultIpsProjectNamingConventions#validateIpsPackageName(java.lang.String)}.
     * @throws CoreException 
     */
    public void testValidateIpsPackageName() throws CoreException {
        MessageList ml = namingConventions.validateIpsPackageName("validName");
        assertFalse(ml.containsErrorMsg());
        assertEquals(0, ml.getNoOfMessages());
        
        ml = namingConventions.validateIpsPackageName("1_invalid");
        assertTrue(ml.containsErrorMsg());
        assertEquals(1, ml.getNoOfMessages());
        assertEquals(Message.ERROR, ml.getSeverity());
        assertEquals(IIpsProjectNamingConventions.INVALID_NAME, ml.getMessage(0).getCode());
        
        ml = namingConventions.validateIpsPackageName("no blanks allowed");
        assertTrue(ml.containsErrorMsg());
        assertEquals(1, ml.getNoOfMessages());
        assertEquals(Message.ERROR, ml.getSeverity());
        assertEquals(IIpsProjectNamingConventions.INVALID_NAME, ml.getMessage(0).getCode());
        
        ml = namingConventions.validateIpsPackageName("new");
        assertTrue(ml.containsErrorMsg());
        assertEquals(1, ml.getNoOfMessages());
        assertEquals(Message.ERROR, ml.getSeverity());
        assertEquals(IIpsProjectNamingConventions.INVALID_NAME, ml.getMessage(0).getCode());
        
        ml = namingConventions.validateIpsPackageName("BIG");
        assertFalse(ml.containsErrorMsg());
        assertEquals(1, ml.getNoOfMessages());
        assertEquals(Message.WARNING, ml.getSeverity());
        assertEquals(IIpsProjectNamingConventions.DISCOURAGED_NAME, ml.getMessage(0).getCode());
    }

    public void testValidateNameForBusinessFunction() throws CoreException {
        IpsObjectType type = IpsObjectType.BUSINESS_FUNCTION;
        testCommonJavaTypeNameValidation(type); 
    }
    
    public void testValidateNameForPolicyCmptType() throws CoreException {
        IpsObjectType type = IpsObjectType.POLICY_CMPT_TYPE;
        testCommonJavaTypeNameValidation(type);        
    }

    public void testValidateNameForProductCmptType() throws CoreException {
        IpsObjectType type = IpsObjectType.PRODUCT_CMPT_TYPE;
        testCommonJavaTypeNameValidation(type); 
    }
    
    public void testValidateNameForTableStructure() throws CoreException {
        IpsObjectType type = IpsObjectType.TABLE_STRUCTURE;
        testCommonJavaTypeNameValidation(type); 
    }

    public void testValidateNameForProductCmpt() throws CoreException {
        IpsObjectType type = IpsObjectType.PRODUCT_CMPT;
        
        // check only that the name must not be empty and is not qualified,
        // the other naming conventions are specified in the implementation of IProductCmptNamingStrategy
        List validNames = new ArrayList();
        List invalidNames = new ArrayList();
        List invalidNamesMsgCodes = new ArrayList();
        
        // check unqualified names
        invalidNames.add("");
        invalidNames.add("test.test");
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_MISSING);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_QUALIFIED);
        validateAndAssertNames(type, false, validNames, invalidNames, invalidNamesMsgCodes);

        validNames.clear();
        invalidNames.clear();
        invalidNamesMsgCodes.clear();
        
        // check qualified names
        validNames.add("test.test");
        invalidNames.add("");
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_MISSING);
        validateAndAssertNames(type, true, validNames, invalidNames, invalidNamesMsgCodes);        
    }

    public void testValidateNameForTableContents() throws CoreException {
        IpsObjectType type = IpsObjectType.TABLE_CONTENTS;
        testCommonOSNameValidation(type); 
    }

    public void testValidateNameForTestCaseType() throws CoreException {
        IpsObjectType type = IpsObjectType.TEST_CASE_TYPE;
        testCommonJavaTypeNameValidation(type); 
    }

    public void testValidateNameForTestCase() throws CoreException {
        IpsObjectType type = IpsObjectType.TEST_CASE;
        testCommonOSNameValidation(type); 
    }
    
    private void testCommonJavaTypeNameValidation(IpsObjectType type) throws CoreException {
        List validNames = new ArrayList();
        List invalidNames = new ArrayList();
        List invalidNamesMsgCodes = new ArrayList();
        
        // check unqualified names
        getCommonJavaTypeQualifiedNamesTestData(validNames, invalidNames, invalidNamesMsgCodes);
        validateAndAssertNames(type, false, validNames, invalidNames, invalidNamesMsgCodes);

        validNames.clear();
        invalidNames.clear();
        invalidNamesMsgCodes.clear();
        
        // check qualified names
        validNames.add("test.test");
        invalidNames.add("");
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_MISSING);
        validateAndAssertNames(type, true, validNames, invalidNames, invalidNamesMsgCodes);
    }
    
    private void testCommonOSNameValidation(IpsObjectType type) throws CoreException {
        List validNames = new ArrayList();
        List invalidNames = new ArrayList();
        List invalidNamesMsgCodes = new ArrayList();
        
        // check unqualified names
        validNames.add("test test");
        invalidNames.add("");
        invalidNames.add("test\\");
        invalidNames.add("test/");
        invalidNames.add("test:");
        invalidNames.add("test*");
        invalidNames.add("test?");
        invalidNames.add("test\"");
        invalidNames.add("test<");
        invalidNames.add("test>");
        invalidNames.add("test|");
        invalidNames.add("test/");
        invalidNames.add("test\0");        
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_MISSING);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        validateAndAssertNames(type, false, validNames, invalidNames, invalidNamesMsgCodes);

        validNames.clear();
        invalidNames.clear();
        invalidNamesMsgCodes.clear();
        
        // check qualified names
        validNames.add("test.test");
        invalidNames.add("");
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_MISSING);
        validateAndAssertNames(type, true, validNames, invalidNames, invalidNamesMsgCodes);
    }
    
    private void getCommonJavaTypeQualifiedNamesTestData(List validNames, List invalidNames, List invalidNamesMsgCodes){
        validNames.add("Test");
        validNames.add("TEST");
        
        invalidNames.add("");
        invalidNames.add("test.test");
        invalidNames.add("test test");
        invalidNames.add("1test");
        invalidNames.add("tEST");
        invalidNames.add("test");
        
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_MISSING);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.NAME_IS_QUALIFIED);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.INVALID_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.DISCOURAGED_NAME);
        invalidNamesMsgCodes.add(IIpsProjectNamingConventions.DISCOURAGED_NAME);
    }
    
    /*
     * Validates the given valid- and invalid names. 
     * For each invalid name the relevant message code must be given, to perform the assert against this message code.
     */
    private void validateAndAssertNames(IpsObjectType type, boolean qualifiedNames,
            List validNames,
            List invalidNames,
            List invalidNamesMsgCodes) throws CoreException {
        assertEquals("Wrong usage of assert method, each invalid name should have a message code", invalidNames.size(),
                invalidNamesMsgCodes.size());
        
        for (Iterator iter = validNames.iterator(); iter.hasNext();) {
            String validName= (String )iter.next();
            MessageList ml = null;
            if (qualifiedNames){
                 ml = namingConventions.validateQualifiedIpsObjectName(type, validName);
            } else {
                ml = namingConventions.validateUnqualifiedIpsObjectName(type, validName);
            }
            assertEquals("\"" + validName + "\"" + " is invalid, expected was valid!", 0, ml.getNoOfMessages()); 
        }
 
        for (int i = 0; i < invalidNames.size(); i++) {
            String invalidName = (String)invalidNames.get(i);
            String msgCode = (String)invalidNamesMsgCodes.get(i);
            MessageList ml = null;
            if (qualifiedNames) {
                ml = namingConventions.validateQualifiedIpsObjectName(type, invalidName);
            }
            else {
                ml = namingConventions.validateUnqualifiedIpsObjectName(type, invalidName);
            }
            assertNotNull("\"" + invalidName + "\"" + " is valid against " + msgCode
                    + ", expected was invalid!", ml.getMessageByCode(msgCode));
        }
    }
    
    /*
     * Test if the validate method of the ips objects includes the naming validation.
     */
    public void testValidateNameWithIpsObjects() throws Exception{
        int testTypesCount = 0;
        
        // Policy cmpt type
        IPolicyCmptType pct = newPolicyCmptType(ipsProject, "1test");
        pct.setConfigurableByProductCmptType(false);
        MessageList ml = pct.validate();
        assertNotNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME));
        pct = newPolicyCmptType(ipsProject, "test.test");
        pct.setConfigurableByProductCmptType(false);
        ml = pct.validate();
        assertNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME));
        testTypesCount ++;
            
        // Product cmpt type
        pct = newPolicyCmptType(ipsProject, "test.test");
        pct.setConfigurableByProductCmptType(true);
        pct.setUnqualifiedProductCmptType("1testType");
        ml = pct.validate();
        assertNotNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME));
        assertEquals(IPolicyCmptType.PROPERTY_UNQUALIFIED_PRODUCT_CMPT_TYPE, ml.getMessageByCode(
                IIpsProjectNamingConventions.INVALID_NAME).getInvalidObjectProperties()[0].getProperty());
        // testTypesCount ++;  is currently no ips object type
        
        // Product cmpt
        IProductCmpt pc = newProductCmpt(ipsProject, "/test");
        ml = pc.validate();
        assertNotNull(ml.getMessageByCode(IProductCmptNamingStrategy.MSGCODE_ILLEGAL_CHARACTERS)); 
        testTypesCount ++;

        // Test case type
        ITestCaseType tct = (ITestCaseType) newIpsObject(ipsProject, IpsObjectType.TEST_CASE_TYPE, "1test");
        ml = tct.validate();
        assertNotNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME));
        testTypesCount ++;
        
        // Test case
        ITestCase tc = (ITestCase) newIpsObject(ipsProject, IpsObjectType.TEST_CASE, "/test");
        ml = tc.validate();
        assertNotNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME)); 
        testTypesCount ++;
        
        // Business function
        BusinessFunction bf = (BusinessFunction) newIpsObject(ipsProject, IpsObjectType.BUSINESS_FUNCTION, "1test");
        ml = bf.validate();
        assertNotNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME)); 
        testTypesCount ++;
        
        // Table structure
        ITableStructure ts = (ITableStructure) newIpsObject(ipsProject, IpsObjectType.TABLE_STRUCTURE, "1test");
        ml = ts.validate();
        assertNotNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME));         
        testTypesCount ++;
        
        // Table contents
        ITableContents tco = (ITableContents) newIpsObject(ipsProject, IpsObjectType.TABLE_CONTENTS, "/test");
        ml = tco.validate();
        assertNotNull(ml.getMessageByCode(IIpsProjectNamingConventions.INVALID_NAME));    
        testTypesCount ++;
        
        
        // assert that all types are tested in this test method
        assertEquals(IpsObjectType.ALL_TYPES.length, testTypesCount);
    }
}
