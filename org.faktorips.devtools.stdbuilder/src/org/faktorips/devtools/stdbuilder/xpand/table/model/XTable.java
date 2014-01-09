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

package org.faktorips.devtools.stdbuilder.xpand.table.model;

import java.util.LinkedHashSet;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.stdbuilder.xpand.GeneratorModelContext;
import org.faktorips.devtools.stdbuilder.xpand.model.ModelService;
import org.faktorips.devtools.stdbuilder.xpand.model.XClass;
import org.faktorips.runtime.ITable;
import org.faktorips.runtime.internal.Table;

/**
 * This is the generator model node representing a {@link ITableStructure}.
 * <p>
 * Note: At the moment only the table is not generated by the new Xpand builder. This class is only
 * used to get the correct interface and implementation name.
 * 
 * @author dirmeier
 */
public class XTable extends XClass {

    public XTable(ITableStructure policyCmptType, GeneratorModelContext context, ModelService modelService) {
        super(policyCmptType, context, modelService);
    }

    @Override
    public boolean isValidForCodeGeneration() {
        try {
            return getIpsObjectPartContainer().isValid(getIpsProject());
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }
    }

    @Override
    protected String getBaseSuperclassName() {
        return addImport(Table.class);
    }

    @Override
    public LinkedHashSet<String> getExtendedInterfaces() {
        return new LinkedHashSet<String>();
    }

    @Override
    public LinkedHashSet<String> getImplementedInterfaces() {
        LinkedHashSet<String> interfaces = new LinkedHashSet<String>();
        interfaces.add(addImport(ITable.class));
        return interfaces;
    }

    @Override
    protected LinkedHashSet<String> getExtendedOrImplementedInterfaces() {
        return new LinkedHashSet<String>();
    }
}
