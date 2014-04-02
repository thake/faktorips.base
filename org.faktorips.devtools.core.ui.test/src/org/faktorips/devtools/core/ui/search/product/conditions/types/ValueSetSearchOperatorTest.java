/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.search.product.conditions.types;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;

import org.faktorips.abstracttest.AbstractIpsPluginTest;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.datatype.classtypes.IntegerDatatype;
import org.faktorips.devtools.core.internal.model.pctype.PolicyCmptType;
import org.faktorips.devtools.core.internal.model.valueset.RangeValueSet;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpt.IConfigElement;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.valueset.IValueSet;
import org.junit.Before;
import org.junit.Test;

public class ValueSetSearchOperatorTest extends AbstractIpsPluginTest {

    private static final GregorianCalendar VALID_FROM = new GregorianCalendar(2013, 11, 1);
    private ValueDatatype valueDatatype;
    private IOperandProvider operandProvider;
    private IProductCmpt productCmpt;
    private IProductCmptGeneration generation;

    @Override
    @Before
    public void setUp() throws Exception {
        valueDatatype = new IntegerDatatype();

        IIpsProject ipsProject = newIpsProject("Project");
        PolicyCmptType policyCmptType = newPolicyAndProductCmptType(ipsProject, "PolicyType", "ProductType");
        IPolicyCmptTypeAttribute attribute = policyCmptType.newPolicyCmptTypeAttribute("attribute");
        attribute.setDatatype(valueDatatype.getName());
        attribute.setProductRelevant(true);
        operandProvider = new PolicyAttributeConditionOperandProvider(attribute);

        productCmpt = newProductCmpt(ipsProject, "Product");
        productCmpt.setProductCmptType(policyCmptType.getProductCmptType());
        generation = (IProductCmptGeneration)productCmpt.newGeneration(VALID_FROM);

        IConfigElement configElement = generation.newConfigElement(attribute);
        IValueSet source = new RangeValueSet(configElement, "partIDXXXX", "2", "5", "1");
        configElement.setValueSet(source);

    }

    @Test
    public void testValueSetOperatorHit() {
        ValueSetSearchOperator operator = new ValueSetSearchOperator(valueDatatype, ValueSetSearchOperatorType.ALLOWED,
                operandProvider, "3");

        assertTrue(operator.check(generation));
    }

    @Test
    public void testValueSetOperatorHitEnumValue() {
        ValueSetSearchOperator operator = new ValueSetSearchOperator(valueDatatype, ValueSetSearchOperatorType.ALLOWED,
                operandProvider, "3");

        assertTrue(operator.check(generation));
    }

    @Test
    public void testValueSetOperatorMiss() {
        ValueSetSearchOperator operator = new ValueSetSearchOperator(valueDatatype, ValueSetSearchOperatorType.ALLOWED,
                operandProvider, "7");

        assertFalse(operator.check(generation));
    }

    @Test
    public void testValueSetOperatorMissProductCmpt() {
        ValueSetSearchOperator operator = new ValueSetSearchOperator(valueDatatype, ValueSetSearchOperatorType.ALLOWED,
                operandProvider, "7");

        assertFalse(operator.check(productCmpt));
    }
}