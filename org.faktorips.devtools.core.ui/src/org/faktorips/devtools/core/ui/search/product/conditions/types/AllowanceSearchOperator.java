/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.search.product.conditions.types;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.valueset.IValueSet;

public class AllowanceSearchOperator extends AbstractSearchOperator<AllowanceSearchOperatorType> {

    protected AllowanceSearchOperator(ValueDatatype valueDatatype, AllowanceSearchOperatorType searchOperatorType,
            IOperandProvider operandProvider, String argument) {
        super(valueDatatype, searchOperatorType, operandProvider, argument);
    }

    @Override
    protected boolean check(Object searchOperand, IProductCmptGeneration productCmptGeneration) {
        try {
            IValueSet valueSet = (IValueSet)searchOperand;
            boolean isContained = valueSet.containsValue(argument, productCmptGeneration.getIpsProject());

            return isContained != searchOperatorType.isNegation();
        } catch (CoreException e) {
            // TODO exception handlen
            throw new RuntimeException(e);
        }

    }

}