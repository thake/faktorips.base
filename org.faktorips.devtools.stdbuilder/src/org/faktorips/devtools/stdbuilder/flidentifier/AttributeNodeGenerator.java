/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3
 * and if and when this source code belongs to the faktorips-runtime or faktorips-valuetype
 * component under the terms of the LGPL Lesser General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder.flidentifier;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.ListOfTypeDatatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.builder.flidentifier.IdentifierNodeGeneratorFactory;
import org.faktorips.devtools.core.builder.flidentifier.ast.AttributeNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.IdentifierNode;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.faktorips.devtools.core.model.type.IAttribute;
import org.faktorips.devtools.stdbuilder.GeneratorRuntimeException;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyAttribute;
import org.faktorips.devtools.stdbuilder.xpand.policycmpt.model.XPolicyCmptClass;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductAttribute;
import org.faktorips.devtools.stdbuilder.xpand.productcmpt.model.XProductCmptClass;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.CompilationResultImpl;

/**
 * JavaGenerator for an {@link AttributeNode}. Example in formula language: "policy.premium" (get
 * the value of attribute "premium" from policy).
 * 
 * @author widmaier
 * @since 3.11.0
 */
public class AttributeNodeGenerator extends StdBuilderIdentifierNodeGenerator {

    public AttributeNodeGenerator(IdentifierNodeGeneratorFactory<JavaCodeFragment> nodeBuilderFactory,
            StandardBuilderSet builderSet) {
        super(nodeBuilderFactory, builderSet);
    }

    @Override
    protected CompilationResult<JavaCodeFragment> getCompilationResultForCurrentNode(IdentifierNode identifierNode,
            CompilationResult<JavaCodeFragment> contextCompilationResult) {
        AttributeNode node = (AttributeNode)identifierNode;
        if (isListOfTypeDatatype(contextCompilationResult)) {
            return createListCompilationResult(node, contextCompilationResult);
        } else {
            return createNormalCompilationResult(node, contextCompilationResult);
        }
    }

    private CompilationResult<JavaCodeFragment> createNormalCompilationResult(final AttributeNode node,
            CompilationResult<JavaCodeFragment> contextCompilationResult) {
        String attributGetterName = getAttributeGetterName(node.getAttribute(), node.isDefaultValueAccess());
        JavaCodeFragment attributeFragment = createCodeFragment(attributGetterName,
                contextCompilationResult.getCodeFragment());
        return new CompilationResultImpl(attributeFragment, node.getDatatype());
    }

    private boolean isListOfTypeDatatype(CompilationResult<JavaCodeFragment> compilationResult) {
        return compilationResult.getDatatype() instanceof ListOfTypeDatatype;
    }

    private CompilationResult<JavaCodeFragment> createListCompilationResult(AttributeNode node,
            CompilationResult<JavaCodeFragment> contextCompilationResult) {

        if (!node.isListOfTypeDatatype()) {
            throw new GeneratorRuntimeException("The datatype of this node is not a ListOfTypeDatatype: " + node); //$NON-NLS-1$
        }

        Datatype conextDatatype = getBasicDatatype(contextCompilationResult);
        IAttribute attribute = node.getAttribute();
        String attributeDatatypeClassName = getDatatypeClassname(attribute);
        String parameterAttributGetterName = getAttributeGetterName(attribute, node.isDefaultValueAccess());

        JavaCodeFragment getTargetCode = new JavaCodeFragment("new "); //$NON-NLS-1$
        getTargetCode.appendClassName(org.faktorips.runtime.formula.FormulaEvaluatorUtil.AttributeAccessorHelper.class);
        getTargetCode.append("<"); //$NON-NLS-1$
        getTargetCode.appendClassName(getJavaClassName(conextDatatype));
        getTargetCode.append(", "); //$NON-NLS-1$
        getTargetCode.appendClassName(attributeDatatypeClassName);
        getTargetCode.append(">(){\n@Override protected "); //$NON-NLS-1$
        getTargetCode.appendClassName(attributeDatatypeClassName);
        getTargetCode.append(" getValueInternal("); //$NON-NLS-1$
        getTargetCode.appendClassName(getJavaClassName(conextDatatype));
        getTargetCode.append(" sourceObject){return sourceObject." + parameterAttributGetterName); //$NON-NLS-1$
        getTargetCode.append("();}}.getAttributeValues("); //$NON-NLS-1$
        getTargetCode.append(contextCompilationResult.getCodeFragment());
        getTargetCode.append(")"); //$NON-NLS-1$

        return new CompilationResultImpl(getTargetCode, node.getDatatype());
    }

    private String getDatatypeClassname(IAttribute attribute) {
        try {
            ValueDatatype datatype = attribute.findDatatype(getIpsProject());
            if (datatype.isPrimitive()) {
                datatype = datatype.getWrapperType();
            }
            return getJavaClassName(datatype);
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    private Datatype getBasicDatatype(CompilationResult<JavaCodeFragment> contextCompilationResult) {
        ListOfTypeDatatype contextListofTypeDatatype = (ListOfTypeDatatype)contextCompilationResult.getDatatype();
        Datatype conextDatatype = contextListofTypeDatatype.getBasicDatatype();
        return conextDatatype;
    }

    private JavaCodeFragment createCodeFragment(final String parameterAttributGetterName,
            JavaCodeFragment contextCodeFragment) {
        JavaCodeFragment javaCodeFragment = new JavaCodeFragment();
        javaCodeFragment.append(contextCodeFragment);
        javaCodeFragment.append('.' + parameterAttributGetterName + "()"); //$NON-NLS-1$
        return javaCodeFragment;
    }

    protected String getAttributeGetterName(IAttribute attribute, boolean isDefaultValueAccess) {
        String parameterAttributGetterName = isDefaultValueAccess ? getParameterAttributDefaultValueGetterName(attribute)
                : getParameterAttributGetterName(attribute);
        return parameterAttributGetterName;
    }

    private String getParameterAttributGetterName(IAttribute attribute) {
        if (attribute instanceof IPolicyCmptTypeAttribute) {
            XPolicyAttribute xPolicyAttribute = getModelNode(attribute, XPolicyAttribute.class);
            return xPolicyAttribute.getMethodNameGetter();
        } else if (attribute instanceof IProductCmptTypeAttribute) {
            XProductAttribute xProductAttribute = getModelNode(attribute, XProductAttribute.class);
            if (xProductAttribute.isChangingOverTime()) {
                return xProductAttribute.getMethodNameGetter();
            } else {
                XProductCmptClass xProductCmptClass = getModelNode(attribute.getType(), XProductCmptClass.class);
                return xProductCmptClass.getMethodNameGetProductCmpt() + "()." //$NON-NLS-1$
                        + xProductAttribute.getMethodNameGetter();
            }
        }
        throw new GeneratorRuntimeException("This type of attribute is not supported: " + attribute.getClass()); //$NON-NLS-1$
    }

    private String getParameterAttributDefaultValueGetterName(IAttribute attribute) {
        XPolicyCmptClass xPolicyCmptClass = getModelNode(attribute.getType(), XPolicyCmptClass.class);
        XPolicyAttribute xPolicyAttribute = getModelNode(attribute, XPolicyAttribute.class);
        return xPolicyCmptClass.getMethodNameGetProductCmptGeneration() + "()." //$NON-NLS-1$
                + xPolicyAttribute.getMethodNameGetDefaultValue();
    }
}
