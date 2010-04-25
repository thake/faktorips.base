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

package org.faktorips.devtools.core.internal.model.ipsobject.refactor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.bf.BusinessFunctionIpsObjectType;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragment;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragmentRoot;
import org.faktorips.devtools.core.refactor.IIpsMoveProcessor;

/**
 * 
 * 
 * @author Alexander Weickmann
 */
public class MoveIpsObjectProcessorTest extends AbstractMoveRenameIpsObjectTest {

    private static final String TARGET_PACKAGE_NAME = "level.targetipspackage";

    private IIpsPackageFragment targetIpsPackageFragment;

    private IIpsPackageFragment originalIpsPackageFragment;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        IIpsPackageFragmentRoot fragmentRoot = policyCmptType.getIpsPackageFragment().getRoot();
        originalIpsPackageFragment = fragmentRoot.getIpsPackageFragment(PACKAGE_NAME);
        targetIpsPackageFragment = fragmentRoot.createPackageFragment(TARGET_PACKAGE_NAME, true, null);
    }

    public void testCheckFinalConditionsValid() throws CoreException {
        ProcessorBasedRefactoring refactoring = policyCmptType.getMoveRefactoring();
        IIpsMoveProcessor moveProcessor = (IIpsMoveProcessor)refactoring.getProcessor();
        moveProcessor.setTargetIpsPackageFragment(targetIpsPackageFragment);
        RefactoringStatus status = refactoring.getProcessor().checkFinalConditions(new NullProgressMonitor(),
                new CheckConditionsContext());
        assertFalse(status.hasError());
    }

    public void testCheckFinalConditionsFileAlreadyExists() throws CoreException {
        ProcessorBasedRefactoring refactoring = policyCmptType.getMoveRefactoring();
        IIpsMoveProcessor moveProcessor = (IIpsMoveProcessor)refactoring.getProcessor();
        moveProcessor.setTargetIpsPackageFragment(targetIpsPackageFragment);
        newProductCmptType(ipsProject, TARGET_PACKAGE_NAME + "." + POLICY_CMPT_TYPE_NAME);
        RefactoringStatus status = refactoring.getProcessor().checkFinalConditions(new NullProgressMonitor(),
                new CheckConditionsContext());
        assertTrue(status.hasFatalError());
    }

    public void testMovePolicyCmptType() throws CoreException {
        performMoveRefactoring(policyCmptType, targetIpsPackageFragment);

        checkIpsSourceFiles(POLICY_CMPT_TYPE_NAME, POLICY_CMPT_TYPE_NAME, originalIpsPackageFragment,
                targetIpsPackageFragment, IpsObjectType.POLICY_CMPT_TYPE);

        checkPolicyCmptTypeReferences(TARGET_PACKAGE_NAME + "." + POLICY_CMPT_TYPE_NAME);
    }

    public void testMoveSuperPolicyCmptType() throws CoreException {
        performMoveRefactoring(superPolicyCmptType, targetIpsPackageFragment);

        checkSuperPolicyCmptTypeReferences(TARGET_PACKAGE_NAME + "." + SUPER_POLICY_CMPT_TYPE_NAME);
    }

    public void testMoveProductCmptType() throws CoreException {
        performMoveRefactoring(productCmptType, targetIpsPackageFragment);

        checkIpsSourceFiles(PRODUCT_CMPT_TYPE_NAME, PRODUCT_CMPT_TYPE_NAME, originalIpsPackageFragment,
                targetIpsPackageFragment, IpsObjectType.PRODUCT_CMPT_TYPE);

        checkProductCmptTypeReferences(TARGET_PACKAGE_NAME + "." + PRODUCT_CMPT_TYPE_NAME);
    }

    public void testMoveSuperProductCmptType() throws CoreException {
        performMoveRefactoring(superProductCmptType, targetIpsPackageFragment);

        checkSuperProductCmptTypeReferences(TARGET_PACKAGE_NAME + "." + SUPER_PRODUCT_CMPT_TYPE_NAME);
    }

    public void testMoveTestCaseType() throws CoreException {
        performMoveRefactoring(testCaseType, targetIpsPackageFragment);

        checkIpsSourceFiles(TEST_CASE_TYPE_NAME, TEST_CASE_TYPE_NAME, originalIpsPackageFragment,
                targetIpsPackageFragment, IpsObjectType.TEST_CASE_TYPE);

        checkTestCaseTypeReferences(TARGET_PACKAGE_NAME + "." + TEST_CASE_TYPE_NAME);
    }

    public void testMoveEnumType() throws CoreException {
        performMoveRefactoring(enumType, targetIpsPackageFragment);

        checkIpsSourceFiles(ENUM_TYPE_NAME, ENUM_TYPE_NAME, originalIpsPackageFragment, targetIpsPackageFragment,
                IpsObjectType.ENUM_TYPE);

        checkEnumTypeReferences(TARGET_PACKAGE_NAME + "." + ENUM_TYPE_NAME);
    }

    public void testMoveTableStructure() throws CoreException {
        performMoveRefactoring(tableStructure, targetIpsPackageFragment);

        checkIpsSourceFiles(TABLE_STRUCTURE_NAME, TABLE_STRUCTURE_NAME, originalIpsPackageFragment,
                targetIpsPackageFragment, IpsObjectType.TABLE_STRUCTURE);

        checkTableStructureReferences(TARGET_PACKAGE_NAME + "." + TABLE_STRUCTURE_NAME);
    }

    public void testMoveBusinessFunction() throws CoreException {
        performMoveRefactoring(businessFunction, targetIpsPackageFragment);

        checkIpsSourceFiles(BUSINESS_FUNCTION_NAME, BUSINESS_FUNCTION_NAME, originalIpsPackageFragment,
                targetIpsPackageFragment, BusinessFunctionIpsObjectType.getInstance());

        checkBusinessFunctionReferences(TARGET_PACKAGE_NAME + "." + BUSINESS_FUNCTION_NAME);
    }

    public void testMoveProductCmpt() throws CoreException {
        performMoveRefactoring(productCmpt, targetIpsPackageFragment);

        checkIpsSourceFiles(PRODUCT_NAME, PRODUCT_NAME, originalIpsPackageFragment, targetIpsPackageFragment,
                IpsObjectType.PRODUCT_CMPT);

        checkProductCmptReferences(TARGET_PACKAGE_NAME + "." + PRODUCT_NAME);
    }

    public void testMoveTestCase() throws CoreException {
        performMoveRefactoring(testCase, targetIpsPackageFragment);

        checkIpsSourceFiles(TEST_CASE_NAME, TEST_CASE_NAME, originalIpsPackageFragment, targetIpsPackageFragment,
                IpsObjectType.TEST_CASE);

        checkTestCaseReferences(TARGET_PACKAGE_NAME + "." + TEST_CASE_NAME);
    }

    public void testMoveEnumContent() throws CoreException {
        performMoveRefactoring(enumContent, targetIpsPackageFragment);

        checkIpsSourceFiles(ENUM_CONTENT_NAME, ENUM_CONTENT_NAME, originalIpsPackageFragment, targetIpsPackageFragment,
                IpsObjectType.ENUM_CONTENT);

        checkEnumContentReferences(TARGET_PACKAGE_NAME + "." + ENUM_CONTENT_NAME);
    }

    public void testMoveTableContent() throws CoreException {
        performMoveRefactoring(tableContents, targetIpsPackageFragment);

        checkIpsSourceFiles(TABLE_CONTENTS_NAME, TABLE_CONTENTS_NAME, originalIpsPackageFragment,
                targetIpsPackageFragment, IpsObjectType.TABLE_CONTENTS);

        checkTableContentsReferences(TARGET_PACKAGE_NAME + "." + TABLE_CONTENTS_NAME);
    }

    @Override
    protected ProcessorBasedRefactoring getRefactoring(IIpsElement ipsElement) {
        return ipsElement.getMoveRefactoring();
    }

}
