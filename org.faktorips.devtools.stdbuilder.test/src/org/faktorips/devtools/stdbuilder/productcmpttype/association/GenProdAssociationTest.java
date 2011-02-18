/*******************************************************************************
 * Copyright (c) 2005-2010 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.stdbuilder.productcmpttype.association;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAssociation;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.stdbuilder.productcmpttype.ProductCmptTypeBuilderTest;
import org.junit.Before;

public abstract class GenProdAssociationTest extends ProductCmptTypeBuilderTest {

    protected static final String TARGET_PRODUCT_NAME = "Product2";

    protected static final String TARGET_ROLE_SINGULAR = "Product2";

    protected static final String TARGET_ROLE_PLURAL = "Product2s";

    protected IType javaInterfaceTargetType;

    protected IProductCmptTypeAssociation association;

    protected IProductCmptType targetProductCmptType;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        targetProductCmptType = newProductCmptType(ipsProject, TARGET_PRODUCT_NAME);

        javaInterfaceTargetType = getGeneratedJavaType(targetProductCmptType, false, false, "I" + TARGET_PRODUCT_NAME);

        association = productCmptType.newProductCmptTypeAssociation();
        association.setMinCardinality(0);
        association.setTarget(targetProductCmptType.getQualifiedName());
        association.setTargetRoleSingular(TARGET_ROLE_SINGULAR);
        association.setTargetRolePlural(TARGET_ROLE_PLURAL);
    }

    protected final void expectMethodGetRelatedCmptLink(GenProdAssociation genProdAssociation, IType javaType) {
        IMethod expectedMethod = javaType.getMethod(genProdAssociation.getMethodNameGet1RelatedCmptLink(),
                new String[] { "Q" + javaInterfaceTargetType.getElementName() + ";" });
        assertTrue(generatedJavaElements.contains(expectedMethod));
    }

    protected final void expectMethodGetCardinalityForAssociation(GenProdAssociation genProdAssociation, IType javaType) {
        try {
            IMethod expectedMethod = javaType.getMethod(genProdAssociation.getMethodNameGetCardinalityForAssociation(),
                    new String[] { "Q" + javaInterfaceTargetType.getElementName() + ";" });
            assertTrue(generatedJavaElements.contains(expectedMethod));
        } catch (CoreException e) {
            throw new RuntimeException(e);
        }
    }

    protected final void setUpConstrainsPolicyCmptTypeAssociation() throws CoreException {
        IPolicyCmptType targetPolicyCmptType = newPolicyCmptType(ipsProject, "Policy2");
        targetProductCmptType.setConfigurationForPolicyCmptType(true);
        targetProductCmptType.setPolicyCmptType(targetPolicyCmptType.getQualifiedName());
        targetPolicyCmptType.setConfigurableByProductCmptType(true);
        targetPolicyCmptType.setProductCmptType(targetProductCmptType.getQualifiedName());
        IPolicyCmptTypeAssociation policyCmptTypeAssociation = policyCmptType.newPolicyCmptTypeAssociation();
        policyCmptTypeAssociation.setTarget(targetPolicyCmptType.getQualifiedName());
        policyCmptTypeAssociation.setMinCardinality(0);
        policyCmptTypeAssociation.setMaxCardinality(1);
    }

}
