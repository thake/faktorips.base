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

package org.faktorips.runtime.formula;

import java.util.Map;

import org.faktorips.runtime.IProductComponentGeneration;

/**
 * Evaluates the formulas of a product component generation. This interface only provides methods to
 * get the configured product component generation and to evaluate already configured formulas. The
 * code of the formulas is set while creating the evaluator by an {@link IFormulaEvaluatorFactory}.
 * 
 * 
 * @author dirmeier
 */
public interface IFormulaEvaluator {

    /**
     * Returns the product component generation this is an evaluator for.
     */
    public IProductComponentGeneration getProductComponentGeneration();

    /**
     * Evaluates the formula with the given name and the specified parameters.
     * 
     * @param formularName The name of the formula to evaluate
     * @param parameters the parameters the formula requires when being evaluated
     * @return the result of the evaluated formula
     */
    public Object evaluate(String formularName, Object... parameters);

    /**
     * Returns a defensive copy of the map of expressions/formulas held by this evaluator.
     * 
     * @return a map containing the expressions (with their names as keys) held by this formula
     *         evaluator
     */
    public Map<String, String> getNameToExpressionMap();

}
