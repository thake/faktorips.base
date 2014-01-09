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

package org.faktorips.devtools.core.builder.flidentifier;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.builder.flidentifier.ast.AssociationNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.IdentifierNode;
import org.faktorips.devtools.core.builder.flidentifier.ast.QualifierNode;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpt.IExpression;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.fl.ExprCompiler;
import org.faktorips.util.message.Message;

/**
 * This parser handles qualifiers and index identifiers. The identifier part always have to end with
 * ']'. The starting '[' is already cut off by the {@link IdentifierParser} when splitting the whole
 * identifier.
 * <p>
 * If the statement begins with " and ends with "] we assume it is a qualifier. If there are no
 * quotation marks it is parsed as an index.
 * 
 * @since 3.11
 * @author dirmeier
 */
public class QualifierAndIndexParser extends TypeBasedIdentifierParser {

    private static final String QUALIFIER_END = "]"; //$NON-NLS-1$

    private static final String QUALIFIER_QUOTATION = "\""; //$NON-NLS-1$

    public QualifierAndIndexParser(IExpression expression, IIpsProject ipsProject) {
        super(expression, ipsProject);
    }

    @Override
    protected boolean isAllowedType() {
        return super.isAllowedType() && super.getContextType() instanceof IPolicyCmptType;
    }

    @Override
    protected IdentifierNode parseInternal() {
        if (!isValidPreviousNode()) {
            return invalidAssociationNode();
        }
        if ((isIndex()) && !isListOfTypeContext()) {
            return invalidIndexNode();
        }
        if (isQualifier()) {
            return createQualifierNode();
        } else if (isIndex()) {
            return createIndexNode();
        } else {
            return null;
        }
    }

    private boolean isValidPreviousNode() {
        if (getPreviousNode() instanceof AssociationNode) {
            return true;
        }
        if (getPreviousNode() instanceof QualifierNode && isIndex()) {
            return true;
        }
        return false;
    }

    private IdentifierNode invalidAssociationNode() {
        return nodeFactory()
                .createInvalidIdentifier(
                        Message.newError(ExprCompiler.UNDEFINED_IDENTIFIER, NLS.bind(
                                Messages.QualifierAndIndexParser_errorMsg_qualifierMustFollowAssociation,
                                getIdentifierPart())));

    }

    private IdentifierNode invalidIndexNode() {
        return nodeFactory().createInvalidIdentifier(
                Message.newError(ExprCompiler.NO_INDEX_FOR_1TO1_ASSOCIATION, NLS.bind(
                        Messages.AbstractParameterIdentifierResolver_noIndexFor1to1Association0, getIdentifierPart())));
    }

    private IdentifierNode createQualifierNode() {
        IProductCmpt productCmpt;
        try {
            productCmpt = findProductCmpt();
            AssociationNode associationNode = (AssociationNode)getPreviousNode();
            boolean listOfType = associationNode.isListContext()
                    || associationNode.getAssociation().is1ToManyIgnoringQualifier();
            return nodeFactory().createQualifierNode(productCmpt, getQualifier(), listOfType);
        } catch (CoreException e) {
            IpsPlugin.log(e);
            return nodeFactory().createInvalidIdentifier(
                    Message.newError(ExprCompiler.UNKNOWN_QUALIFIER, NLS.bind(
                            Messages.QualifierAndIndexParser_errorMsg_errorWhileSearchingProductCmpt, getQualifier())));
        }
    }

    private IProductCmpt findProductCmpt() throws CoreException {
        IProductCmptType productCmptType = findProductCmptType();
        IIpsSrcFile[] allProductCmptSrcFiles = getIpsProject().findAllProductCmptSrcFiles(productCmptType, true);
        IProductCmpt productCmpt = null;
        for (IIpsSrcFile ipsSrcFile : allProductCmptSrcFiles) {
            if (ipsSrcFile.getIpsObjectName().equals(getQualifier())
                    || ipsSrcFile.getQualifiedNameType().getName().equals(getQualifier())) {
                IIpsObject ipsObject = ipsSrcFile.getIpsObject();
                if (ipsObject instanceof IProductCmpt) {
                    productCmpt = (IProductCmpt)ipsObject;
                    break;
                }
            }
        }
        return productCmpt;
    }

    private IProductCmptType findProductCmptType() throws CoreException {
        return ((IPolicyCmptType)getContextType()).findProductCmptType(getIpsProject());
    }

    private IdentifierNode createIndexNode() {
        try {
            return nodeFactory().createIndexBasedAssociationNode(getIndex(), getContextType());
        } catch (NumberFormatException e) {
            return handleInvalidIndex(e);
        }
    }

    private IdentifierNode handleInvalidIndex(NumberFormatException e) {
        IpsPlugin.log(e);
        return nodeFactory().createInvalidIdentifier(
                Message.newError(ExprCompiler.UNKNOWN_QUALIFIER, NLS.bind(
                        Messages.AssociationParser_msgErrorAssociationQualifierOrIndex, getQualifierOrIndex(),
                        getIdentifierPart())));
    }

    private boolean isQualifier() {
        return getIdentifierPart().startsWith(QUALIFIER_QUOTATION)
                && getIdentifierPart().endsWith(QUALIFIER_QUOTATION + QUALIFIER_END);
    }

    private boolean isIndex() {
        return getIdentifierPart().endsWith(QUALIFIER_END) && !isQualifier();
    }

    private int getIndexOrQualifierEnd() {
        return getIdentifierPart().indexOf(QUALIFIER_END);
    }

    private String getQualifier() {
        return getQualifierOrIndex().substring(1, getQualifierOrIndex().length() - 1);
    }

    private int getIndex() {
        return Integer.valueOf(getQualifierOrIndex());
    }

    private String getQualifierOrIndex() {
        return getIdentifierPart().substring(0, getIndexOrQualifierEnd());
    }

}
