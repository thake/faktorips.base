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

package org.faktorips.devtools.stdbuilder.xpand.policycmpt.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.faktorips.devtools.core.builder.JavaNamingConvention;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAssociation;
import org.faktorips.devtools.stdbuilder.xpand.model.GeneratorModelContext;
import org.faktorips.devtools.stdbuilder.xpand.model.ModelService;
import org.faktorips.devtools.stdbuilder.xpand.model.XAssociation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class XPolicyAssociationTest {

    @Mock
    private IPolicyCmptTypeAssociation typeAssoc;

    @Mock
    private GeneratorModelContext context;

    @Mock
    private ModelService modelService;

    @Mock
    private XAssociation inverseAssoc;
    private XPolicyAssociation assoc;

    @Before
    public void setUp() {
        assoc = spy(new XPolicyAssociation(typeAssoc, context, modelService));
        doReturn(new JavaNamingConvention()).when(assoc).getJavaNamingConvention();
    }

    @Test
    public void testGetInverseAssociationOrCompositionName_WithInverse() {
        doReturn(true).when(assoc).hasInverseAssociation();
        doReturn(inverseAssoc).when(assoc).getInverseAssociation();
        when(inverseAssoc.getName()).thenReturn("ArbitraryInverseAssocName");

        assertEquals("setArbitraryInverseAssocNameInternal", assoc.getMethodNameInverseAssociationSetInternal());
    }

    @Test(expected = NullPointerException.class)
    public void testGetInverseAssociationName_NoInverse() {
        doReturn(true).when(assoc).hasInverseAssociation();
        doReturn(null).when(assoc).getInverseAssociation();

        assoc.getMethodNameInverseAssociationSetInternal();
    }

    @Test
    public void testGenerateSynchronizeForAdd() {
        doReturn(true).when(assoc).isValidComposition();
        doReturn(false).when(assoc).hasInverseAssociation();
        assertFalse(assoc.isGenerateCodeToSynchronizeInverseCompositionForAdd());

        doReturn(false).when(assoc).isValidComposition();
        doReturn(true).when(assoc).hasInverseAssociation();
        assertFalse(assoc.isGenerateCodeToSynchronizeInverseCompositionForAdd());

        doReturn(false).when(assoc).isValidComposition();
        doReturn(false).when(assoc).hasInverseAssociation();
        assertFalse(assoc.isGenerateCodeToSynchronizeInverseCompositionForAdd());

        doReturn(true).when(assoc).isValidComposition();
        doReturn(true).when(assoc).hasInverseAssociation();
        assertTrue(assoc.isGenerateCodeToSynchronizeInverseCompositionForAdd());
    }

    @Test
    public void testGenerateSynchronizeForRemove() {
        doReturn(true).when(assoc).isValidComposition();
        doReturn(false).when(assoc).hasInverseAssociation();
        assertTrue(assoc.isGenerateCodeToSynchronizeInverseCompositionForRemove());

        doReturn(false).when(assoc).isValidComposition();
        doReturn(true).when(assoc).hasInverseAssociation();
        assertTrue(assoc.isGenerateCodeToSynchronizeInverseCompositionForRemove());

        doReturn(false).when(assoc).isValidComposition();
        doReturn(false).when(assoc).hasInverseAssociation();
        assertFalse(assoc.isGenerateCodeToSynchronizeInverseCompositionForRemove());

        doReturn(true).when(assoc).isValidComposition();
        doReturn(true).when(assoc).hasInverseAssociation();
        assertTrue(assoc.isGenerateCodeToSynchronizeInverseCompositionForRemove());
    }

}
