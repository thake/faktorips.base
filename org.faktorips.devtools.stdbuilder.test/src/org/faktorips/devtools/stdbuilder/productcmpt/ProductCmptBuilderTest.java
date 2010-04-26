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

package org.faktorips.devtools.stdbuilder.productcmpt;

import java.util.GregorianCalendar;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.ipsproject.IIpsArtefactBuilder;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProjectProperties;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpt.IFormula;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeMethod;

/**
 * 
 * @author Jan Ortmann
 */
public class ProductCmptBuilderTest extends AbstractIpsPluginTest {

    private IIpsProject project;
    private IPolicyCmptType type;
    private IProductCmptType productCmptType;
    private IProductCmpt productCmpt;
    private IProductCmptGeneration productCmptGen;

    private ProductCmptBuilder builder;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        project = newIpsProject("TestProject");
        IIpsProjectProperties props = project.getProperties();
        project.setProperties(props);
        type = newPolicyAndProductCmptType(project, "Policy", "Product");

        productCmptType = type.findProductCmptType(project);
        IProductCmptTypeMethod method = productCmptType.newProductCmptTypeMethod();
        method.setDatatype(Datatype.INTEGER.getQualifiedName());
        method.setName("age");
        method.setFormulaSignatureDefinition(true);
        method.setFormulaName("AgeCalculation");
        assertFalse(type.validate(project).containsErrorMsg());
        type.getIpsSrcFile().save(true, null);

        productCmpt = newProductCmpt(productCmptType, "Product");
        productCmptGen = (IProductCmptGeneration)productCmpt.newGeneration();
        productCmptGen.setValidFrom(new GregorianCalendar(2006, 0, 1));
        IFormula ce = productCmptGen.newFormula();
        ce.setFormulaSignature(method.getFormulaName());
        ce.setExpression("42");
        productCmpt.getIpsSrcFile().save(true, null);
        assertFalse(productCmpt.validate(project).containsErrorMsg());

        IIpsArtefactBuilder[] builders = project.getIpsArtefactBuilderSet().getArtefactBuilders();
        for (IIpsArtefactBuilder builder2 : builders) {
            if (builder2 instanceof ProductCmptBuilder) {
                builder = (ProductCmptBuilder)builder2;
            }
        }
        assertNotNull(builder);
    }

    public void testBuild() throws CoreException {
        // build should not throw an exception even if the reference to the type is missing
        productCmpt.getIpsSrcFile().save(true, null);
        project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
        assertTrue(builder.getGeneratedJavaFile(productCmptGen).exists());
    }

    public void testBuildMissingType() throws CoreException {
        productCmpt.setProductCmptType("");
        productCmpt.getIpsSrcFile().save(true, null);
        project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
        assertNull(builder.getGeneratedJavaFile(productCmptGen));
    }

    public void testDelete() throws CoreException {
        project.getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
        IFile javaFile = builder.getGeneratedJavaFile(productCmptGen);
        assertTrue(javaFile.exists());

        productCmpt.getIpsSrcFile().getCorrespondingFile().delete(true, false, null);
        project.getProject().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        assertFalse(javaFile.exists());
    }

}
