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

package org.faktorips.devtools.core.internal.model.product;

import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.CycleException;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IRelation;
import org.faktorips.devtools.core.model.pctype.RelationType;
import org.faktorips.devtools.core.model.product.IProductCmpt;
import org.faktorips.devtools.core.model.product.IProductCmptGeneration;
import org.faktorips.devtools.core.model.product.IProductCmptRelation;
import org.faktorips.devtools.core.model.product.IProductCmptStructure;
import org.faktorips.devtools.core.model.product.IProductCmptStructureReference;
import org.faktorips.devtools.core.model.product.IProductCmptStructureTblUsageReference;
import org.faktorips.devtools.core.model.product.ITableContentUsage;
import org.faktorips.devtools.core.model.productcmpttype2.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype2.ITableStructureUsage;

/**
 * Tests for product component structure.
 * 
 * @author Thorsten Guenther
 */
public class ProductCmptStructureTest extends AbstractIpsPluginTest {
    
    private IProductCmpt productCmpt;
    private IProductCmpt productCmptTarget;
    private IRelation relation;
    private IIpsProject ipsProject;
    private IProductCmptStructure structure;
    
    /*
     * @see PluginTest#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        ipsProject = this.newIpsProject("TestProject");

        // Build policy component types
        IPolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, "TestPolicy", "dummy1");
        IProductCmptType productCmptType = policyCmptType.findProductCmptType(ipsProject);
        ITableStructureUsage tsu1 = productCmptType.newTableStructureUsage();
        tsu1.setRoleName("usage1");
        tsu1.addTableStructure("tableStructure1");
        
        IPolicyCmptType policyCmptTypeTarget = newPolicyAndProductCmptType(ipsProject, "TestTarget", "dummy2");
        IProductCmptType productCmptTypeTarget = policyCmptTypeTarget.findProductCmptType(ipsProject);
        ITableStructureUsage tsu2 = productCmptType.newTableStructureUsage();
        tsu2.setRoleName("usage2");
        tsu2.addTableStructure("tableStructure2");
        
        relation = policyCmptType.newRelation();
        relation.setRelationType(RelationType.COMPOSITION_MASTER_TO_DETAIL);
        relation.setTargetRoleSingularProductSide("TestRelation");
        relation.setTargetRoleSingular("TestRelation");
        relation.setTarget(policyCmptTypeTarget.getQualifiedName());
        relation.setProductRelevant(true);
        
        // Build product component types
        productCmpt = newProductCmpt(productCmptType, "products.TestProduct");
        IProductCmptGeneration generation = productCmpt.getProductCmptGeneration(0);
        ITableContentUsage tcu = generation.newTableContentUsage();
        tcu.setStructureUsage(tsu1.getRoleName());
        tcu.setTableContentName("tableContent1");
        
        productCmptTarget = newProductCmpt(productCmptTypeTarget, "products.TestProductTarget");
        IProductCmptGeneration targetGen = productCmptTarget.getProductCmptGeneration(0);
        tcu = targetGen.newTableContentUsage();
        tcu.setStructureUsage(tsu2.getRoleName());
        tcu.setTableContentName("tableContent2");
        
        IProductCmptRelation cmptRelation = generation.newRelation(relation.getName());
        cmptRelation.setTarget(productCmptTarget.getQualifiedName());
        
        cmptRelation = generation.newRelation(relation.getName());
        cmptRelation.setTarget(productCmptTarget.getQualifiedName());
        
        policyCmptType.getIpsSrcFile().save(true, null);
        policyCmptTypeTarget.getIpsSrcFile().save(true, null);
        productCmpt.getIpsSrcFile().save(true, null);
        productCmptTarget.getIpsSrcFile().save(true, null);
        
        structure = productCmpt.getStructure();
    }

    public void testGetRoot() {
    	IProductCmpt root = structure.getRoot().getProductCmpt();
    	assertSame(productCmpt, root);
    }
    
    public void testNoGeneration() throws CycleException {
    	productCmpt.getGenerations()[0].delete();
    	structure.refresh();
    }
    
    public void testCircleDetection() throws Exception {
    	// this has to work without any exception
    	productCmpt.getStructure();
        productCmptTarget.getStructure();
    	
    	// create a circle
        IProductCmptType type = productCmpt.findProductCmptType(ipsProject);
        relation.setTarget(type.getQualifiedName());
        productCmptTarget.setProductCmptType(type.getQualifiedName());
        IProductCmptGeneration targetGen= (IProductCmptGeneration)productCmptTarget.getGeneration(0);
        IProductCmptRelation rel = targetGen.newRelation(relation.getName());
    	rel.setTarget(productCmpt.getQualifiedName());
    	
    	try {
			productCmpt.getStructure();
			fail();
		} catch (CycleException e) {
			// success
		} 
    }
    
    public void testTblContentUsageReferences() throws Exception {
        IProductCmptStructureTblUsageReference[] ptsus = structure
                .getChildProductCmptStructureTblUsageReference(structure.getRoot());
        assertEquals(1, ptsus.length);
        ITableContentUsage tcu = ptsus[0].getTableContentUsage();
        assertEquals("tableContent1", tcu.getTableContentName());
        
        IProductCmptStructure structureTarget = productCmptTarget.getStructure();
        ptsus = structure.getChildProductCmptStructureTblUsageReference(structureTarget.getRoot());
        assertEquals(1, ptsus.length);
        tcu = ptsus[0].getTableContentUsage();
        assertEquals("tableContent2", tcu.getTableContentName());
    }
    
    public void testToArray() throws Exception {
        IProductCmptStructureReference[] array = structure.toArray(true);
        assertEquals(3, array.length);
        
    }
}
