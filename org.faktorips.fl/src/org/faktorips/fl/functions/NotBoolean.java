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

package org.faktorips.fl.functions;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.CompilationResultImpl;
import org.faktorips.fl.FunctionSignatures;

/**
 * Boolean function NOT for the wrapper type.
 * 
 * @author Jan Ortmann
 */
public class NotBoolean extends AbstractFlFunction {

    public NotBoolean(String name, String description) {
        super(name, description, FunctionSignatures.NotBoolean);
    }

    /**
     * {@inheritDoc}
     */
    public CompilationResult<JavaCodeFragment> compile(CompilationResult<JavaCodeFragment>[] argResults) {
        CompilationResultImpl result = (CompilationResultImpl)argResults[0];
        JavaCodeFragment code = result.getCodeFragment();
        JavaCodeFragment newCode = new JavaCodeFragment();
        newCode.append("((");
        newCode.append(code);
        newCode.append(")==null ? (Boolean)null : ");
        newCode.append("Boolean.valueOf(!(" + code + ").booleanValue())");
        newCode.append(')');
        result.setCodeFragment(newCode);
        return result;
    }

}
