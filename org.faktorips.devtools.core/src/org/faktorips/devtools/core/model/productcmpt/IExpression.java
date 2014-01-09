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

package org.faktorips.devtools.core.model.productcmpt;

import java.util.List;

import org.faktorips.datatype.EnumDatatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.internal.model.productcmpttype.ProductCmptType;
import org.faktorips.devtools.core.model.ipsobject.IDescribedElement;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.method.IFormulaMethod;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.type.IAttribute;
import org.faktorips.fl.JavaExprCompiler;

/**
 * Base interface for expressions using the formula language.
 * 
 * @author Jan Ortmann
 */
public interface IExpression extends IIpsObjectPart, IDescribedElement {

    public static final String PROPERTY_FORMULA_SIGNATURE_NAME = "formulaSignature"; //$NON-NLS-1$
    public static final String PROPERTY_EXPRESSION = "expression"; //$NON-NLS-1$

    /**
     * Prefix for all message codes of this class.
     */
    public static final String MSGCODE_PREFIX = "CONFIGELEMENT-"; //$NON-NLS-1$

    /**
     * Validation message code to indicate that the formula signature's can't be found.
     */
    public static final String MSGCODE_SIGNATURE_CANT_BE_FOUND = MSGCODE_PREFIX + "SignatureCantBeFound"; //$NON-NLS-1$

    /**
     * Validation message code to indicate that the formula signature's data type can't be found and
     * so the formula's data type can't be checked against it.
     */
    public static final String MSGCODE_UNKNOWN_DATATYPE_FORMULA = MSGCODE_PREFIX + "UnknownDatatypeFormula"; //$NON-NLS-1$

    /**
     * Validation message code to indicate that the formula's data type is not compatible with the
     * one defined by the signature.
     */
    public static final String MSGCODE_WRONG_FORMULA_DATATYPE = MSGCODE_PREFIX + "WrongFormulaDatatype"; //$NON-NLS-1$

    /**
     * Validation message code to indicate that the expression is empty (and it should not.
     */
    public static final String MSGCODE_EXPRESSION_IS_EMPTY = MSGCODE_PREFIX + "ExpressionIsEmpty"; //$NON-NLS-1$

    /**
     * For formulas this IIpsElement method returns the formula signature name.
     * 
     * @see #getFormulaSignature()
     */
    @Override
    String getName();

    /**
     * Returns the name of the product component type method signature this formula is an
     * implementation of.
     */
    String getFormulaSignature();

    /**
     * Sets the name of the product component type method signature this formula is an
     * implementation of.
     */
    void setFormulaSignature(String newName);

    /**
     * Returns the method signature this formula implements. Returns <code>null</code> if the method
     * signature is not found.
     */
    IFormulaMethod findFormulaSignature(IIpsProject ipsProject);

    /**
     * Returns the product component type of the product component this formula belongs to.
     */
    IProductCmptType findProductCmptType(IIpsProject ipsProject);

    /**
     * Returns the value data types the results of this formula expression are instances of. This
     * data type is equal to the formula signatures data type. If the formula signature's return
     * type is not a value data type (which is an error in the model and is reported via
     * validation), this method returns <code>null</code>.
     */
    ValueDatatype findValueDatatype(IIpsProject ipsProject);

    /**
     * Returns the formula expression.
     */
    String getExpression();

    /**
     * Sets the new formula expression.
     */
    void setExpression(String newExpression);

    /**
     * Returns <code>true</code> if this formulas expression is empty. Returns <code>false</code> if
     * it is not empty.
     */
    boolean isEmpty();

    /**
     * Returns an expression compiler that can be used to compile the formula. or <code>null</code>
     * if the element does not contain a formula.
     */
    JavaExprCompiler newExprCompiler(IIpsProject ipsProject);

    /**
     * Returns the enumeration data types that can be use in this formula. Allowed enumeration types
     * are those that are used as data type in one of the parameters or in a table used by the
     * product component generation this configuration element belongs to. If the data type of the
     * formula is itself an enumeration data type, this one is also returned.
     * <p>
     * Returns an empty array if this configuration element does not represent a formula.
     */
    EnumDatatype[] getEnumDatatypesAllowedInFormula();

    /**
     * Returns all Attributes of the {@link ProductCmptType}
     */
    List<IAttribute> findMatchingProductCmptTypeAttributes();

    /**
     * Returns <code>true</code> if the {@link IExpression} is mandatory or <code>false</code> is
     * ist not.
     */
    boolean isFormulaMandatory();
}
