/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.core.ui.editors.testcase;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.pctype.IRelation;
import org.faktorips.devtools.core.model.testcase.ITestPolicyCmpt;
import org.faktorips.devtools.core.model.testcase.ITestPolicyCmptRelation;
import org.faktorips.devtools.core.model.testcase.ITestValue;
import org.faktorips.devtools.core.model.testcasetype.ITestPolicyCmptTypeParameter;

/**
 * Label provider for the test case domain.
 * 
 * @author Joerg Ortmann
 */
public class TestCaseLabelProvider implements ILabelProvider {
	
	/**
	 * {@inheritDoc}
	 */
	public Image getImage(Object element) {
        if ((element instanceof ITestPolicyCmptRelation)) {
        	return getImageFromRelationType(null, (ITestPolicyCmptRelation) element);
        } else if (element instanceof ITestPolicyCmpt) {
        	ITestPolicyCmpt testPolicyCmpt = (ITestPolicyCmpt) element;
        	if (StringUtils.isNotEmpty(testPolicyCmpt.getProductCmpt())){
        		return IpsPlugin.getDefault().getImage("ProductCmpt.gif"); //$NON-NLS-1$ 
        	} else {
        		return IpsPlugin.getDefault().getImage("PolicyCmptType.gif"); //$NON-NLS-1$ 
        	}
        } else if(element instanceof ITestValue){
        	return IpsPlugin.getDefault().getImage("Datatype.gif"); //$NON-NLS-1$ 
	    } else if (element instanceof TestCaseTypeRelation){
	    	return getImageFromRelationType((TestCaseTypeRelation)element, null);
	    }
        return null; 
	}

	/**
	 * Returns the image of the given relation test case type parameter.
	 */
	private Image getImageFromRelationType(TestCaseTypeRelation dummyRelation, ITestPolicyCmptRelation testPolicyCmptRelation) {
		try {
			ITestPolicyCmptTypeParameter typeParam = null;
			if (dummyRelation == null){
				typeParam = testPolicyCmptRelation.findTestPolicyCmptTypeParameter();
			}else{
				typeParam = dummyRelation.getTestPolicyCmptTypeParam();
			}
			if (typeParam == null){
				return getImageFromRelation(testPolicyCmptRelation);
			}
			IRelation relation = null;
			relation = typeParam.findRelation();
			if (relation == null){
				return null;
			}
			      	
			if (relation.isAssoziation()){
				return IpsPlugin.getDefault().getImage("Relation.gif"); //$NON-NLS-1$
			}else {
				return IpsPlugin.getDefault().getImage("Aggregation.gif"); //$NON-NLS-1$
			}
		} catch (CoreException e) {
			return getImageFromRelation(testPolicyCmptRelation);
		}
	}
	
	/**
	 * Returns the image of the given test case relation.
	 */
	private Image getImageFromRelation(ITestPolicyCmptRelation testPcTypeRelation) {
		if (testPcTypeRelation == null)
			return null;
		if (testPcTypeRelation.isAccoziation()){
			return IpsPlugin.getDefault().getImage("Relation.gif"); //$NON-NLS-1$
		}else{
			return IpsPlugin.getDefault().getImage("Aggregation.gif"); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public String getText(Object element) {
		if (element instanceof ITestPolicyCmpt) {
			ITestPolicyCmpt tstPolicyCmpt = (ITestPolicyCmpt) element;
			return tstPolicyCmpt.getName();
		} else if (element instanceof ITestPolicyCmptRelation) {
			ITestPolicyCmptRelation testPcTypeRelation = (ITestPolicyCmptRelation) element;
			String text = ""; //$NON-NLS-1$
			try {
				text = TestCaseHierarchyPath.unqualifiedName(testPcTypeRelation.getTestPolicyCmptTypeParameter());
				
				ITestPolicyCmptTypeParameter typeParam = testPcTypeRelation.findTestPolicyCmptTypeParameter();
				if (typeParam != null && typeParam.isRequiresProductCmpt())
					text += TestCaseSection.REQUIRES_PRODUCT_CMPT_SUFFIX;
			} catch (CoreException e) {
				// ignore model error, the model consitence between the test case type and the test case
				// will be check when openening the editor, therefor we can ignore is here
			}
			return text;
		} else if(element instanceof ITestValue){
			ITestValue testValueParameter = (ITestValue) element;
			return testValueParameter.getTestValueParameter(); //$NON-NLS-1$ 
	    } else if(element instanceof TestCaseTypeRelation){
	    	TestCaseTypeRelation dummyRelation = (TestCaseTypeRelation) element;
	    	String text = dummyRelation.getName();
	    	if (dummyRelation.isRequiresProductCmpt()){
	    		text += TestCaseSection.REQUIRES_PRODUCT_CMPT_SUFFIX;
	    	}
	    	return text;
	    }
		return Messages.TestCaseLabelProvider_undefined;
	}

	/**
	 * {@inheritDoc}
	 */
	public void dispose() {
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

	/**
	 * {@inheritDoc}
	 */
	public void addListener(ILabelProviderListener listener) {
	}

}
