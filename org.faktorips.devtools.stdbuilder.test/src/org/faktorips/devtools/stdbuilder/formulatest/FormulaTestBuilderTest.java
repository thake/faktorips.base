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

package org.faktorips.devtools.stdbuilder.formulatest;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.builder.DefaultBuilderSet;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpt.IFormula;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeMethod;
import org.faktorips.devtools.stdbuilder.AbstractStdBuilderTest;

public class FormulaTestBuilderTest extends AbstractStdBuilderTest {

    private static final String PRODUCT_NAME = "ProductCmpt";

    private static final String POLICY_TYPE_NAME = "Policy";

    private static final String PRODUCT_TYPE_NAME = "Product";

    private IProductCmpt productCmpt;

    private IProductCmptType productCmptType;

    private IFormula formula;

    private FormulaTestBuilder builder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        IPolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, POLICY_TYPE_NAME, PRODUCT_TYPE_NAME);
        productCmptType = policyCmptType.findProductCmptType(ipsProject);
        IProductCmptTypeMethod formulaMethod = productCmptType.newFormulaSignature("testFormula");
        formulaMethod.setDatatype(Datatype.INTEGER.getQualifiedName());

        productCmpt = newProductCmpt(productCmptType, PRODUCT_NAME);
        IProductCmptGeneration generation = productCmpt.getProductCmptGeneration(0);
        formula = generation.newFormula();
        formula.setExpression("1 + 1");
        formula.newFormulaTestCase();
        formula.setFormulaSignature("testFormula");

        builder = new FormulaTestBuilder(builderSet, DefaultBuilderSet.KIND_FORMULA_TEST_CASE);
    }

    public void testDelete() throws CoreException {
        productCmpt.getIpsSrcFile().save(true, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);

        assertTrue(productCmpt.getIpsSrcFile().exists());

        productCmpt.getIpsSrcFile().getCorrespondingFile().delete(true, false, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
        assertFalse(productCmpt.getIpsSrcFile().exists());

        IProductCmpt productCmpt2 = newProductCmpt(productCmptType, "productCmptWithoutFormula");
        productCmpt2.getIpsSrcFile().save(true, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
        productCmpt2.getIpsSrcFile().getCorrespondingFile().delete(true, false, null);
        ipsProject.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
    }

    private IType getGeneratedJavaClass() {
        return getGeneratedJavaType(productCmpt, true, true, PRODUCT_NAME + FormulaTestBuilder.RUNTIME_EXTENSION);
    }

    public void testGetGeneratedJavaElements() {
        generatedJavaElements = builder.getGeneratedJavaElements(productCmpt);
        assertTrue(generatedJavaElements.contains(getGeneratedJavaClass()));
    }

}
