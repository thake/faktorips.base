/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.builder.flidentifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.MultiLanguageSupport;
import org.faktorips.devtools.core.builder.flidentifier.ast.AttributeNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.IdentifierNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.IdentifierNodeType;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.fl.IdentifierKind;
import org.faktorips.devtools.core.internal.fl.IdentifierFilter;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.method.IFormulaMethod;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpt.IFormula;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeMethod;
import org.faktorips.devtools.core.model.type.IAttribute;
import org.faktorips.devtools.core.model.type.IType;
import org.faktorips.fl.ExprCompiler;
import org.faktorips.util.message.Message;

/**
 * This parser tries to match the identifier part to an attribute of the context type. In case of
 * success it will return an {@link AttributeNode} otherwise it returns <code>null</code>.
 * <p>
 * Allows both policy- and the corresponding product attributes if the context type is a policy
 * component type.
 * 
 * @author dirmeier
 */
public class AttributeParser extends TypeBasedIdentifierParser {

    public static final char VALUE_SUFFIX_SEPARATOR_CHAR = '@';

    public static final String DEFAULT_VALUE_SUFFIX = VALUE_SUFFIX_SEPARATOR_CHAR + "default"; //$NON-NLS-1$

    private final IdentifierFilter identifierFilter;

    public AttributeParser(ParsingContext parsingContext, IdentifierFilter identifierFilter) {
        super(parsingContext);
        this.identifierFilter = identifierFilter;
    }

    @Override
    public IdentifierNode parseInternal() {
        try {
            return parseToNode();
        } catch (CoreRuntimeException e) {
            IpsPlugin.log(e);
            return nodeFactory().createInvalidIdentifier(
                    Message.newInfo(ExprCompiler.UNDEFINED_IDENTIFIER,
                            Messages.AbstractParameterIdentifierResolver_msgErrorRetrievingAttribute));
        }
    }

    private IdentifierNode parseToNode() {
        boolean defaultValueAccess = isDefaultValueAccess(getIdentifierPart());
        String attributeName = getAttributeName(getIdentifierPart(), defaultValueAccess);
        List<IAttribute> attributes = findAttributes();
        return createNode(defaultValueAccess, attributeName, attributes);
    }

    private boolean isDefaultValueAccess(String attributeName) {
        return getContextType() instanceof IPolicyCmptType && attributeName.endsWith(DEFAULT_VALUE_SUFFIX);
    }

    private String getAttributeName(String identifier, boolean defaultValueAccess) {
        if (defaultValueAccess) {
            return identifier.substring(0, identifier.lastIndexOf(VALUE_SUFFIX_SEPARATOR_CHAR));
        } else {
            return identifier;
        }
    }

    private IdentifierNode createNode(boolean defaultValueAccess, String attributeName, List<IAttribute> attributes) {
        for (IAttribute anAttribute : attributes) {
            if (attributeName.equals(anAttribute.getName())) {
                if (isAllowd(anAttribute, defaultValueAccess)) {
                    return nodeFactory().createAttributeNode(anAttribute, defaultValueAccess, isStaticContext(),
                            isListOfTypeContext());
                } else {
                    return createInvalidIdentifierNode();
                }
            }
        }
        return null;
    }

    private boolean isStaticContext() {
        if (isContextTypeFormulaType()) {
            IFormula expression = (IFormula)getParsingContext().getExpression();
            IProductCmptTypeMethod method = expression.findFormulaSignature(getIpsProject());
            return !method.isChangingOverTime();
        }
        return false;
    }

    protected List<IAttribute> findAttributes() {
        if (isAllowedType()) {
            return getPolicyAndProductAttributesFromIType();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public List<IdentifierProposal> getProposals(String prefix) {
        IdentifierProposalCollector collector = new IdentifierProposalCollector();
        List<IAttribute> attributes = findAttributes();
        for (IAttribute attribute : attributes) {
            addProposal(attribute, false, prefix, collector);
            addProposal(attribute, true, prefix, collector);
        }
        return collector.getProposals();
    }

    private void addProposal(IAttribute attribute,
            boolean defaultAccess,
            String prefix,
            IdentifierProposalCollector collector) {
        if (isProposalAllowed(attribute, defaultAccess)) {
            collector.addMatchingNode(getText(attribute, defaultAccess), getDescription(attribute, defaultAccess),
                    prefix, IdentifierNodeType.ATTRIBUTE);
        }
    }

    private boolean isProposalAllowed(IAttribute attribute, boolean defaultAccess) {
        return (attribute instanceof IPolicyCmptTypeAttribute || !defaultAccess) && isAllowd(attribute, defaultAccess);
    }

    private List<IAttribute> getPolicyAndProductAttributesFromIType() {
        List<IAttribute> attributes = new ArrayList<IAttribute>();
        IType contextType = getContextType();
        attributes.addAll(findAllAttributesFor(contextType));
        attributes.addAll(findProductAttributesIfAvailable(contextType));
        return attributes;
    }

    private List<IAttribute> findAllAttributesFor(IType contextType) {
        try {
            if (isNotChangingOverTimeMethod(contextType)) {
                return ((IProductCmptType)contextType).findNotChangingOverTimeAttributes(getIpsProject());
            }
            return contextType.findAllAttributes(getIpsProject());
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    private boolean isNotChangingOverTimeMethod(IType contextType) {
        if (contextType.getIpsObjectType().equals(IpsObjectType.PRODUCT_CMPT_TYPE)) {
            IFormulaMethod method = getExpression().findFormulaSignature(getIpsProject());
            if (method instanceof IProductCmptTypeMethod) {
                return !((IProductCmptTypeMethod)method).isChangingOverTime();
            }
        }
        return false;
    }

    private List<IAttribute> findProductAttributesIfAvailable(IType contextType) {
        if (contextType instanceof IPolicyCmptType) {
            IProductCmptType productCmptType = findProductCmptType((IPolicyCmptType)contextType);
            if (productCmptType != null) {
                return findAllAttributesFor(productCmptType);
            }
        }
        return Collections.emptyList();
    }

    private IProductCmptType findProductCmptType(IPolicyCmptType policyCmptType) {
        try {
            return policyCmptType.findProductCmptType(getIpsProject());
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    private boolean isAllowd(IAttribute anAttribute, boolean isDefaultIdentifier) {
        return identifierFilter.isIdentifierAllowed(anAttribute,
                IdentifierKind.getDefaultIdentifierOrAttribute(isDefaultIdentifier));
    }

    private IdentifierNode createInvalidIdentifierNode() {
        return nodeFactory().createInvalidIdentifier(
                Message.newError(ExprCompiler.UNDEFINED_IDENTIFIER, NLS.bind(
                        Messages.AbstractParameterIdentifierResolver_msgIdentifierNotAllowed, getIdentifierPart())));
    }

    public String getText(IAttribute attribute, boolean defaultValueAccess) {
        if (defaultValueAccess) {
            return attribute.getName() + AttributeParser.DEFAULT_VALUE_SUFFIX;
        } else {
            return attribute.getName();
        }
    }

    public String getDescription(IAttribute attribute, boolean defaultValueAccess) {
        MultiLanguageSupport multiLanguageSupport = getParsingContext().getMultiLanguageSupport();
        String name = getName(attribute, multiLanguageSupport);
        if (defaultValueAccess) {
            name = NLS.bind(Messages.AttributeParser_defaultOfName, name);
        }
        return getNameAndDescription(name, getDescription(attribute, multiLanguageSupport));
    }

}
