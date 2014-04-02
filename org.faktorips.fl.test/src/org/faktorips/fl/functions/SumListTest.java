/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.fl.functions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.faktorips.codegen.DatatypeHelper;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.ListOfTypeDatatype;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.CompilationResultImpl;
import org.faktorips.fl.JavaExprCompiler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SumListTest {
    private SumList sumList = new SumList("sum", "");
    private JavaCodeFragment argumentFragment = new JavaCodeFragment("valueList");
    private ListOfTypeDatatype datatype = new ListOfTypeDatatype(Datatype.DECIMAL);

    @Mock
    CompilationResultImpl argumentCompilationResult;

    @Mock
    JavaExprCompiler compiler;

    @Mock
    DatatypeHelper helper;

    @Test(expected = IllegalArgumentException.class)
    public void testCompile_NumberOfArgumentsZero() {
        sumList.compile(new CompilationResultImpl[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCompile_NumberOfArgumentsMany() {
        sumList.compile(new CompilationResultImpl[2]);
    }

    @Test
    public void testCompile_Max() {
        CompilationResultImpl arg1Result = new CompilationResultImpl("currentResult", Datatype.DECIMAL);
        CompilationResultImpl arg2Result = new CompilationResultImpl("nextValue", Datatype.DECIMAL);
        when(argumentCompilationResult.getCodeFragment()).thenReturn(argumentFragment);
        when(argumentCompilationResult.getDatatype()).thenReturn(datatype);
        sumList = spy(sumList);
        JavaCodeFragment fragment = new JavaCodeFragment("currentResult.add(nextValue)");
        doReturn(fragment).when(sumList).generateFunctionCall(arg1Result, arg2Result);

        doReturn(compiler).when(sumList).getCompiler();
        doReturn(Datatype.DECIMAL).when(sumList).getDatatype();
        when(sumList.getCompiler().getDatatypeHelper(sumList.getDatatype())).thenReturn(helper);
        when(helper.newInstance("0")).thenReturn(new JavaCodeFragment("Decimal.valueOf(0)"));

        CompilationResultImpl[] argument = new CompilationResultImpl[] { argumentCompilationResult };

        CompilationResult<JavaCodeFragment> compile = sumList.compile(argument);
        assertNotNull(compile);

        assertEquals(
                "new FunctionWithListAsArgumentHelper<Decimal>(){\n@Override public Decimal getPreliminaryResult(Decimal currentResult, Decimal nextValue){return currentResult.add(nextValue);}\n@Override public Decimal getFallBackValue(){return Decimal.valueOf(0);}}.getResult(valueList)",
                compile.getCodeFragment().getSourcecode());
    }
}