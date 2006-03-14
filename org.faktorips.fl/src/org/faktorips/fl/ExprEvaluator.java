/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.fl;

import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.util.ArgumentCheck;

import bsh.Interpreter;

/**
 * The <code>ExprEvaluator</code> evaluates a given expression and returns it's result.
 * <p>
 * Technically this is done by first compiling the expression to Java sourcecode using the
 * {@link ExprCompiler}. After that a java bean shell {@link http://www.beanshell.org} interpreter
 * interpretes the expression and the result is return by the evaluate method of this class.
 * 
 * @author Jan Ortmann
 */
public class ExprEvaluator {

    // The compiler used to compile the formula into standard Java sourcecode
    private ExprCompiler compiler_;

    /**
     * Constructs a new processor for the given compiler.
     */
    public ExprEvaluator(ExprCompiler compiler) {

        ArgumentCheck.notNull(compiler);
        compiler_ = compiler;

    }

    public Object evaluate(String expression) throws Exception {
        // compiles the expression to Java sourcecode
        JavaCodeFragment fragment = compileExpressionToJava(expression);
        Interpreter i = new Interpreter(); // Construct an interpreter

        StringBuffer sb = new StringBuffer();
        sb.append(fragment.getImportDeclaration().toString());
        sb.append(System.getProperty("line.separator")); //$NON-NLS-1$
        sb.append(fragment.getSourcecode());

        // execute the expression.
        return i.eval(sb.toString());
    }

    /**
     * Compiles expression to Java and returns the CompilationResult.
     */
    private JavaCodeFragment compileExpressionToJava(String expression) throws Exception {
        CompilationResult result = compiler_.compile(expression);
        if (result.failed()) {
            throw new Exception(result.getMessages().toString());
        }
        return result.getCodeFragment();
    }

}
