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

package org.faktorips.devtools.stdbuilder.xpand.model;

import java.util.LinkedHashSet;
import java.util.Set;

import org.faktorips.codegen.JavaCodeFragment;

/**
 * Delegates to a {@link JavaCodeFragment} to ensure the same behavior as the standard
 * {@link ImportHandler} via test cases.
 * 
 * @author widmaier
 */
public class DelegateImportHandler extends ImportHandler {

    private JavaCodeFragment fragment;

    public DelegateImportHandler() {
        super("");
        fragment = new JavaCodeFragment();
    }

    @Override
    public ImportStatement add(String importStatement) {
        ImportStatement statement = new ImportStatement(importStatement);
        fragment.appendClassName(importStatement);
        return statement;
    }

    @Override
    public Set<ImportStatement> getImports() {
        Set<ImportStatement> result = new LinkedHashSet<ImportStatement>();
        Set<String> impDecl = fragment.getImportDeclaration().getImports();
        for (String importString : impDecl) {
            result.add(new ImportStatement(importString));
        }
        return result;
    }
}
