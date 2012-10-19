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

package org.faktorips.devtools.core.internal.model.testcase;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.UUID;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.abstracttest.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAssociation;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptLink;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.core.model.testcase.ITestCase;
import org.faktorips.devtools.core.model.testcase.ITestPolicyCmpt;
import org.faktorips.devtools.core.model.testcase.ITestPolicyCmptLink;
import org.faktorips.devtools.core.model.testcasetype.ITestCaseType;
import org.faktorips.devtools.core.model.testcasetype.ITestPolicyCmptTypeParameter;
import org.faktorips.devtools.core.model.type.AssociationType;
import org.faktorips.devtools.core.model.type.IAssociation;
import org.faktorips.devtools.core.model.type.IType;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test cases for
 * {@link TestPolicyCmpt#addTestPcTypeLink(ITestPolicyCmptTypeParameter, String, String, String, boolean)}
 */
public class TestPolicyCmpt_AddPcTypeLinkTest extends AbstractIpsPluginTest {

    private IIpsProject ipsProject;

    @Override
    @Before
    public void setUp() throws CoreException {
        ipsProject = newIpsProject();
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Three policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(1..1)
     * <li>2 -&gt; 3 &nbsp;(1..1)
     * </ul>
     * <li>Product components are non-ambiguous
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * One further link to the third policy component must be added. This is because the
     * associations are not optional.
     */
    @Test
    public void testAddPcTypeLink_MinCardinalityOne() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3", "ProductType3");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3 = policyType3.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3 = createAssociation(policyType2, policyType3, 1, 1);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3 = createAssociation(productType2, productType3, 1, 1);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 1, 1);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 1, 1);
        ITestPolicyCmptTypeParameter parameter3 = createTestParameter(parameter2, policyType3, policy2ToPolicy3, 1, 1);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        IProductCmpt productCmpt3 = newProductCmpt(productType3, "Product3");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3, product2ToProduct3, 1, 1);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmpt child1 = rootTestPolicyCmpt.getTestPolicyCmptLinks(parameter2.getName())[0].findTarget();
        ITestPolicyCmpt child2 = child1.getTestPolicyCmptLinks(parameter3.getName())[0].findTarget();
        assertSame(productCmpt3, child2.findProductCmpt(ipsProject));
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Three policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(0..1)
     * <li>2 -&gt; 3 &nbsp;(0..1)
     * </ul>
     * <li>Product components are non-ambiguous
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * No additional link must be added because the associations are all optional.
     */
    // TODO PA-709 test for optional but mandatory in product component
    @Test
    public void testAddPcTypeLink_MinCardinalityZero() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3", "ProductType3");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3 = policyType3.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 0, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3 = createAssociation(policyType2, policyType3, 0, 1);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 0, 1);
        IProductCmptTypeAssociation product2ToProduct3 = createAssociation(productType2, productType3, 0, 1);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 0, 1);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 0, 1);
        ITestPolicyCmptTypeParameter parameter3 = createTestParameter(parameter2, policyType3, policy2ToPolicy3, 0, 1);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        IProductCmpt productCmpt3 = newProductCmpt(productType3, "Product3");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 0, 1);
        createProductCmptLink(productCmpt2, productCmpt3, product2ToProduct3, 0, 1);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmptLink link1 = rootTestPolicyCmpt.getTestPolicyCmptLinks(parameter2.getName())[0];
        assertEquals(0, link1.findTarget().getTestPolicyCmptLinks(parameter3.getName()).length);
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Three policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(2..3)
     * <li>2 -&gt; 3 &nbsp;(2..3)
     * </ul>
     * <li>Product components are non-ambiguous
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * Two further links to the third policy component must be added automatically. This is because
     * the associations require at least two policy components.
     */
    @Test
    public void testAddPcTypeLink_MinCardinalityTwo() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3", "ProductType3");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3 = policyType3.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 2, 3);
        IPolicyCmptTypeAssociation policy2ToPolicy3 = createAssociation(policyType2, policyType3, 2, 3);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 2, 3);
        IProductCmptTypeAssociation product2ToProduct3 = createAssociation(productType2, productType3, 2, 3);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 2, 3);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 2, 3);
        ITestPolicyCmptTypeParameter parameter3 = createTestParameter(parameter2, policyType3, policy2ToPolicy3, 2, 3);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        IProductCmpt productCmpt3 = newProductCmpt(productType3, "Product3");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3, product2ToProduct3, 1, 1);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmptLink link1_1 = rootTestPolicyCmpt.getTestPolicyCmptLinks(parameter2.getName())[0];
        assertSame(productCmpt2, link1_1.findTarget().findProductCmpt(ipsProject));

        ITestPolicyCmptLink link2_1 = link1_1.findTarget().getTestPolicyCmptLinks(parameter3.getName())[0];
        assertSame(productCmpt3, link2_1.findTarget().findProductCmpt(ipsProject));

        ITestPolicyCmptLink link2_2 = link1_1.findTarget().getTestPolicyCmptLinks(parameter3.getName())[1];
        assertSame(productCmpt3, link2_2.findTarget().findProductCmpt(ipsProject));
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Four policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(1..1)
     * <li>2 -&gt; 3 &nbsp;(3..3)
     * <li>3 -&gt; 4 &nbsp;(0..1)
     * </ul>
     * <li>Product components are non-ambiguous
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * Three further links to the third policy component must be added automatically. This is
     * because the second association requires at least three policy components. No link to the
     * fourth policy component must be added, because it is optional.
     */
    @Test
    public void testAddPcTypeLink_MixedMinCardinalities() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3", "ProductType3");
        IPolicyCmptType policyType4 = newPolicyAndProductCmptType(ipsProject, "PolicyType4", "ProductType4");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3 = policyType3.findProductCmptType(ipsProject);
        IProductCmptType productType4 = policyType4.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3 = createAssociation(policyType2, policyType3, 3, 3);
        IPolicyCmptTypeAssociation policy3ToPolicy4 = createAssociation(policyType3, policyType4, 0, 1);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3 = createAssociation(productType2, productType3, 2, 3);
        IProductCmptTypeAssociation product3ToProduct4 = createAssociation(productType3, productType4, 0, 1);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 1, 1);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 1, 1);
        ITestPolicyCmptTypeParameter parameter3 = createTestParameter(parameter2, policyType3, policy2ToPolicy3, 3, 3);
        ITestPolicyCmptTypeParameter parameter4 = createTestParameter(parameter3, policyType4, policy3ToPolicy4, 0, 1);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        IProductCmpt productCmpt3 = newProductCmpt(productType3, "Product3");
        IProductCmpt productCmpt4 = newProductCmpt(productType4, "Product4");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3, product2ToProduct3, 1, 1);
        createProductCmptLink(productCmpt3, productCmpt4, product3ToProduct4, 0, 1);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmptLink link1_1 = rootTestPolicyCmpt.getTestPolicyCmptLinks(parameter2.getName())[0];
        ITestPolicyCmptLink link2_1 = link1_1.findTarget().getTestPolicyCmptLinks(parameter3.getName())[0];
        ITestPolicyCmptLink link2_2 = link1_1.findTarget().getTestPolicyCmptLinks(parameter3.getName())[1];
        ITestPolicyCmptLink link2_3 = link1_1.findTarget().getTestPolicyCmptLinks(parameter3.getName())[2];
        assertEquals(0, link2_1.findTarget().getTestPolicyCmptLinks(parameter4.getName()).length);
        assertEquals(0, link2_2.findTarget().getTestPolicyCmptLinks(parameter4.getName()).length);
        assertEquals(0, link2_3.findTarget().getTestPolicyCmptLinks(parameter4.getName()).length);
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Multiple policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(1..1)
     * <li>2 -&gt; 3_1 &nbsp;(1..1)
     * <li>2 -&gt; 3_2 &nbsp;(1..1)
     * <li>2 -&gt; 3_3 &nbsp;(1..1)
     * </ul>
     * <li>Product components are non-ambiguous
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * Three further links have to be added originating from policy component 2 to each policy
     * component on the third level.
     */
    @Test
    public void testAddPcTypeLink_MultipleAssociations() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3_1 = newPolicyAndProductCmptType(ipsProject, "PolicyType3_1", "ProductType3_1");
        IPolicyCmptType policyType3_2 = newPolicyAndProductCmptType(ipsProject, "PolicyType3_2", "ProductType3_2");
        IPolicyCmptType policyType3_3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3_3", "ProductType3_3");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3_1 = policyType3_1.findProductCmptType(ipsProject);
        IProductCmptType productType3_2 = policyType3_2.findProductCmptType(ipsProject);
        IProductCmptType productType3_3 = policyType3_3.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3_1 = createAssociation(policyType2, policyType3_1, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3_2 = createAssociation(policyType2, policyType3_2, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3_3 = createAssociation(policyType2, policyType3_3, 1, 1);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3_1 = createAssociation(productType2, productType3_1, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3_2 = createAssociation(productType2, productType3_2, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3_3 = createAssociation(productType2, productType3_3, 1, 1);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 1, 1);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 1, 1);
        ITestPolicyCmptTypeParameter parameter3_1 = createTestParameter(parameter2, policyType3_1, policy2ToPolicy3_1,
                1, 1);
        ITestPolicyCmptTypeParameter parameter3_2 = createTestParameter(parameter2, policyType3_2, policy2ToPolicy3_2,
                1, 1);
        ITestPolicyCmptTypeParameter parameter3_3 = createTestParameter(parameter2, policyType3_3, policy2ToPolicy3_3,
                1, 1);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        IProductCmpt productCmpt3_1 = newProductCmpt(productType3_1, "Product3_1");
        IProductCmpt productCmpt3_2 = newProductCmpt(productType3_2, "Product3_2");
        IProductCmpt productCmpt3_3 = newProductCmpt(productType3_3, "Product3_3");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3_1, product2ToProduct3_1, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3_2, product2ToProduct3_2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3_3, product2ToProduct3_3, 1, 1);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmpt child1 = rootTestPolicyCmpt.getTestPolicyCmptLink(policyType2.getQualifiedName()).findTarget();
        ITestPolicyCmpt child2_1 = child1.getTestPolicyCmptLinks(parameter3_1.getName())[0].findTarget();
        ITestPolicyCmpt child2_2 = child1.getTestPolicyCmptLinks(parameter3_2.getName())[0].findTarget();
        ITestPolicyCmpt child2_3 = child1.getTestPolicyCmptLinks(parameter3_3.getName())[0].findTarget();
        assertSame(productCmpt3_1, child2_1.findProductCmpt(ipsProject));
        assertSame(productCmpt3_2, child2_2.findProductCmpt(ipsProject));
        assertSame(productCmpt3_3, child2_3.findProductCmpt(ipsProject));
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Multiple policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(1..1)
     * <li>2 -&gt; 3 &nbsp;(1..1)
     * </ul>
     * <li>Product components are ambiguous: Product components 3_1 and 3_2 come into consideration
     * for policy type 3. However, only product component 3_2 is actually linked with product
     * component 2.
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * One further link must be added originating from policy component 2. The correct product
     * component 3_2 must be determined by analyzing the available product component links.
     */
    @Test
    public void testAddPcTypeLink_AmbiguousProductComponents_OneLink_MinCardinalityOne() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3", "ProductType3");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3 = policyType3.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3 = createAssociation(policyType2, policyType3, 1, 1);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3 = createAssociation(productType2, productType3, 1, 1);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 1, 1);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 1, 1);
        ITestPolicyCmptTypeParameter parameter3 = createTestParameter(parameter2, policyType3, policy2ToPolicy3, 1, 1);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        newProductCmpt(productType3, "Product3_1");
        IProductCmpt productCmpt3_2 = newProductCmpt(productType3, "Product3_2");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3_2, product2ToProduct3, 1, 1);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmpt child1 = rootTestPolicyCmpt.getTestPolicyCmptLinks(parameter2.getName())[0].findTarget();
        ITestPolicyCmpt child2 = child1.getTestPolicyCmptLinks(parameter3.getName())[0].findTarget();
        assertSame(productCmpt3_2, child2.findProductCmpt(ipsProject));
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Multiple policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(1..1)
     * <li>2 -&gt; 3 &nbsp;(1..1)
     * </ul>
     * <li>Product components are ambiguous: Product components 3_1 and 3_2 come into consideration
     * for policy type 3. However, only product component 3_1 is actually linked with product
     * component 2. Furthermore, this link defines a minimum cardinality of 0, so the link is
     * optional.
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * No further link must be added originating from policy component 2. This is because the only
     * available link from product component 2 is optional.
     */
    @Test
    public void testAddPcTypeLink_AmbiguousProductComponents_OneLink_MinCardinalityZero() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3", "ProductType3");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3 = policyType3.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3 = createAssociation(policyType2, policyType3, 1, 1);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3 = createAssociation(productType2, productType3, 1, 1);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 1, 1);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 1, 1);
        createTestParameter(parameter2, policyType3, policy2ToPolicy3, 1, 1);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        IProductCmpt productCmpt3_1 = newProductCmpt(productType3, "Product3_1");
        newProductCmpt(productType3, "Product3_2");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3_1, product2ToProduct3, 0, 1);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmpt child1 = rootTestPolicyCmpt.getTestPolicyCmptLinks(parameter2.getName())[0].findTarget();
        assertNull(child1.getTestPolicyCmptLink(policyType3.getQualifiedName()));
    }

    /**
     * <strong>Scenario:</strong><br>
     * <ul>
     * <li>Multiple policy types are linked together as following:
     * <ul>
     * <li>1 -&gt; 2 &nbsp;(1..1)
     * <li>2 -&gt; 3 &nbsp;(1..1)
     * </ul>
     * <li>Product components are ambiguous: Product components 3_1 and 3_2 come into consideration
     * for policy type 3. However, only product component 3_1 is actually linked with product
     * component 2. Furthermore, this link defines a minimum cardinality of 2.
     * <li>A first link is being added to the test case
     * </ul>
     * <p>
     * <strong>Expected Outcome:</strong><br>
     * Two further links must be added originating from policy component 2. The correct product
     * component is 3_1 for both links.
     */
    @Test
    public void testAddPcTypeLink_AmbiguousProductComponents_OneLink_MinCardinalityTwo() throws CoreException {
        // Create model types
        IPolicyCmptType policyType1 = newPolicyAndProductCmptType(ipsProject, "PolicyType1", "ProductType1");
        IPolicyCmptType policyType2 = newPolicyAndProductCmptType(ipsProject, "PolicyType2", "ProductType2");
        IPolicyCmptType policyType3 = newPolicyAndProductCmptType(ipsProject, "PolicyType3", "ProductType3");
        IProductCmptType productType1 = policyType1.findProductCmptType(ipsProject);
        IProductCmptType productType2 = policyType2.findProductCmptType(ipsProject);
        IProductCmptType productType3 = policyType3.findProductCmptType(ipsProject);

        // Create associations
        IPolicyCmptTypeAssociation policy1ToPolicy2 = createAssociation(policyType1, policyType2, 1, 1);
        IPolicyCmptTypeAssociation policy2ToPolicy3 = createAssociation(policyType2, policyType3, 1, 1);
        IProductCmptTypeAssociation product1ToProduct2 = createAssociation(productType1, productType2, 1, 1);
        IProductCmptTypeAssociation product2ToProduct3 = createAssociation(productType2, productType3, 1, 1);

        // Create test case type
        ITestCaseType testCaseType = newTestCaseType(ipsProject, "MyTestCaseType");
        ITestPolicyCmptTypeParameter parameter1 = createTestParameter(testCaseType, policyType1, 1, 1);
        ITestPolicyCmptTypeParameter parameter2 = createTestParameter(parameter1, policyType2, policy1ToPolicy2, 1, 1);
        ITestPolicyCmptTypeParameter parameter3 = createTestParameter(parameter2, policyType3, policy2ToPolicy3, 1, 1);

        // Create product components
        IProductCmpt productCmpt1 = newProductCmpt(productType1, "Product1");
        IProductCmpt productCmpt2 = newProductCmpt(productType2, "Product2");
        IProductCmpt productCmpt3_1 = newProductCmpt(productType3, "Product3_1");
        newProductCmpt(productType3, "Product3_2");

        // Create product links
        createProductCmptLink(productCmpt1, productCmpt2, product1ToProduct2, 1, 1);
        createProductCmptLink(productCmpt2, productCmpt3_1, product2ToProduct3, 2, 2);

        // Create test case
        ITestPolicyCmpt rootTestPolicyCmpt = createTestCase(testCaseType, policyType1, productCmpt1);

        // Execute
        rootTestPolicyCmpt.addTestPcTypeLink(parameter2, productCmpt2.getQualifiedName(), null, null, true);

        // Verify
        ITestPolicyCmpt child1 = rootTestPolicyCmpt.getTestPolicyCmptLinks(parameter2.getName())[0].findTarget();
        ITestPolicyCmpt child2 = child1.getTestPolicyCmptLinks(parameter3.getName())[0].findTarget();
        ITestPolicyCmpt child3 = child1.getTestPolicyCmptLinks(parameter3.getName())[1].findTarget();
        assertSame(productCmpt3_1, child2.findProductCmpt(ipsProject));
        assertSame(productCmpt3_1, child3.findProductCmpt(ipsProject));
    }

    @Ignore
    @Test
    public void testAddPcTypeLink_AmbiguousProductComponents_TwoLinks_MinCardinalityOne() throws CoreException {
        // TODO
    }

    @Ignore
    @Test
    public void testAddPcTypeLink_AmbiguousProductComponents_ThreeLinks_MixedCardinalities() throws CoreException {
        // TODO
    }

    private IPolicyCmptTypeAssociation createAssociation(IPolicyCmptType source,
            IPolicyCmptType target,
            int minCardinality,
            int maxCardinality) {

        IPolicyCmptTypeAssociation association = (IPolicyCmptTypeAssociation)createAssociation((IType)source,
                (IType)target, minCardinality, maxCardinality);
        association.setAssociationType(AssociationType.COMPOSITION_MASTER_TO_DETAIL);
        association.setConfigured(true);
        return association;
    }

    private IProductCmptTypeAssociation createAssociation(IProductCmptType source,
            IProductCmptType target,
            int minCardinality,
            int maxCardinality) {

        IProductCmptTypeAssociation association = (IProductCmptTypeAssociation)createAssociation((IType)source,
                (IType)target, minCardinality, maxCardinality);
        association.setAssociationType(AssociationType.AGGREGATION);
        return association;
    }

    private IAssociation createAssociation(IType source, IType target, int minCardinality, int maxCardinality) {
        IAssociation association = source.newAssociation();
        association.setTarget(target.getQualifiedName());
        association.setTargetRoleSingular(target.getName() + '_' + UUID.randomUUID());
        association.setTargetRolePlural(target.getName() + 's' + '_' + UUID.randomUUID());
        association.setMinCardinality(minCardinality);
        association.setMinCardinality(maxCardinality);
        return association;
    }

    private ITestPolicyCmptTypeParameter createTestParameter(ITestCaseType testCaseType,
            IPolicyCmptType policyCmptType,
            int minCardinality,
            int maxCardinality) {

        ITestPolicyCmptTypeParameter parameter = testCaseType.newInputTestPolicyCmptTypeParameter();
        configureTestParameter(parameter, policyCmptType, minCardinality, maxCardinality);
        return parameter;
    }

    private ITestPolicyCmptTypeParameter createTestParameter(ITestPolicyCmptTypeParameter testPolicyCmptTypeParameter,
            IPolicyCmptType policyCmptType,
            IAssociation association,
            int minCardinality,
            int maxCardinality) {

        ITestPolicyCmptTypeParameter parameter = testPolicyCmptTypeParameter.newTestPolicyCmptTypeParamChild();
        configureTestParameter(parameter, policyCmptType, minCardinality, maxCardinality);
        parameter.setAssociation(association.getName());
        return parameter;
    }

    private void configureTestParameter(ITestPolicyCmptTypeParameter testPolicyCmptTypeParameter,
            IPolicyCmptType policyCmptType,
            int minCardinality,
            int maxCardinality) {

        testPolicyCmptTypeParameter.setName(policyCmptType.getName());
        testPolicyCmptTypeParameter.setPolicyCmptType(policyCmptType.getQualifiedName());
        testPolicyCmptTypeParameter.setRequiresProductCmpt(true);
        testPolicyCmptTypeParameter.setMinInstances(minCardinality);
        testPolicyCmptTypeParameter.setMaxInstances(maxCardinality);
    }

    private IProductCmptLink createProductCmptLink(IProductCmpt source,
            IProductCmpt target,
            IProductCmptTypeAssociation association,
            int minCardinality,
            int maxCardinality) {

        IProductCmptLink link = source.getFirstGeneration().newLink(association);
        link.setTarget(target.getQualifiedName());
        link.setMinCardinality(minCardinality);
        link.setMaxCardinality(maxCardinality);
        return link;
    }

    /**
     * Creates a new {@link ITestCase} and returns the root {@link ITestPolicyCmpt}.
     */
    private ITestPolicyCmpt createTestCase(ITestCaseType testCaseType,
            IPolicyCmptType rootPolicyType,
            IProductCmpt rootProductCmpt) throws CoreException {

        ITestCase testCase = newTestCase(testCaseType, "MyTestCase");
        ITestPolicyCmpt testPolicyCmpt = testCase.newTestPolicyCmpt();
        testPolicyCmpt.setProductCmptAndNameAfterIfApplicable(rootProductCmpt.getQualifiedName());
        testPolicyCmpt.setPolicyCmptType(rootPolicyType.getQualifiedName());
        testPolicyCmpt.setTestPolicyCmptTypeParameter(testCaseType.getTestParameters()[0].getName());
        return testPolicyCmpt;
    }

}
