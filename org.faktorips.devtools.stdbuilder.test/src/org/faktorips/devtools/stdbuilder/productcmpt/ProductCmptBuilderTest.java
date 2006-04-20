/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.productcmpt;

import java.util.GregorianCalendar;
import java.util.Locale;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.IpsPluginTest;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.IIpsProjectProperties;
import org.faktorips.devtools.core.model.pctype.AttributeType;
import org.faktorips.devtools.core.model.pctype.IAttribute;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.product.ConfigElementType;
import org.faktorips.devtools.core.model.product.IConfigElement;
import org.faktorips.devtools.core.model.product.IProductCmpt;
import org.faktorips.devtools.core.model.product.IProductCmptGeneration;

/**
 * 
 * @author Jan Ortmann
 */
public class ProductCmptBuilderTest extends IpsPluginTest {

    private IIpsProject project;
    private IPolicyCmptType type;
    private IProductCmpt productCmpt;
    
    /*
     * @see IpsPluginTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        project = newIpsProject("TestProject");
        IIpsProjectProperties props = project.getProperties();
        props.setJavaSrcLanguage(Locale.GERMAN);
        project.setProperties(props);
        type = newPolicyCmptType(project, "Policy");
        IAttribute a = type.newAttribute();
        a.setAttributeType(AttributeType.COMPUTED);
        a.setDatatype(Datatype.INTEGER.getQualifiedName());
        a.setName("age");
        a.setProductRelevant(true);
        assertFalse(type.validate().containsErrorMsg());
        type.getIpsSrcFile().save(true, null);
        
        productCmpt = newProductCmpt(project, "Product");
        productCmpt.setPolicyCmptType(type.getQualifiedName());
        IProductCmptGeneration gen = (IProductCmptGeneration)productCmpt.newGeneration();
        gen.setValidFrom(new GregorianCalendar(2006, 0, 1));
        IConfigElement ce = gen.newConfigElement();
        ce.setPcTypeAttribute(a.getName());
        ce.setType(ConfigElementType.FORMULA);
        ce.setValue("42");
        productCmpt.getIpsSrcFile().save(true, null);
        assertFalse(productCmpt.validate().containsErrorMsg());
    }
    
    public void testBuild() throws CoreException {
        // build should not throw an exception even if the reference to the type is missing
        productCmpt.setPolicyCmptType("");
        productCmpt.getIpsSrcFile().save(true, null);
        project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
    }

    /*
     * Test method for 'org.faktorips.devtools.stdbuilder.productcmpt.ProductCmptBuilder.delete(IIpsSrcFile)'
     */
    public void testDelete() throws CoreException {
        project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
        
        productCmpt.getIpsSrcFile().getCorrespondingFile().delete(true, false, null);
        project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
    }

}
