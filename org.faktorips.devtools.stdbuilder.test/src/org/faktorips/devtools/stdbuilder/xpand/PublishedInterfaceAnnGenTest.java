/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.stdbuilder.xpand;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyCmptClass;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductCmptClass;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductCmptGenerationClass;
import org.faktorips.runtime.model.annotation.IpsPublishedInterface;
import org.junit.Before;
import org.junit.Test;

public class PublishedInterfaceAnnGenTest {

    private PublishedInterfaceAnnGen gen;

    @Before
    public void setUp() {
        gen = new PublishedInterfaceAnnGen();
    }

    @Test
    public void testCreateAnnotationProd() {
        XProductCmptClass prod = mock(XProductCmptClass.class);
        when(prod.getImplClassName()).thenReturn("ProductCmptClass");
        when(prod.addImport(IpsPublishedInterface.class)).thenReturn("IpsPublishedInterface");

        assertEquals(
                "@IpsPublishedInterface(implementation = ProductCmptClass.class)"
                        + System.getProperty("line.separator"), gen.createAnnotation(prod).getSourcecode());
    }

    @Test
    public void testCreateAnnotationProdGen() {
        XProductCmptGenerationClass prodGen = mock(XProductCmptGenerationClass.class);
        when(prodGen.getImplClassName()).thenReturn("ProductCmptGenClass");
        when(prodGen.addImport(IpsPublishedInterface.class)).thenReturn("IpsPublishedInterface");

        assertEquals(
                "@IpsPublishedInterface(implementation = ProductCmptGenClass.class)"
                        + System.getProperty("line.separator"), gen.createAnnotation(prodGen).getSourcecode());
    }

    @Test
    public void testCreateAnnotationPolicy() {
        XPolicyCmptClass policy = mock(XPolicyCmptClass.class);
        when(policy.getImplClassName()).thenReturn("PolicyCmptClass");
        when(policy.addImport(IpsPublishedInterface.class)).thenReturn("IpsPublishedInterface");

        assertEquals(
                "@IpsPublishedInterface(implementation = PolicyCmptClass.class)" + System.getProperty("line.separator"),
                gen.createAnnotation(policy).getSourcecode());
    }

}