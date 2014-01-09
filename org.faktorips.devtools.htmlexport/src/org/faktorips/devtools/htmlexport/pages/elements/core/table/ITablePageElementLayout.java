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

package org.faktorips.devtools.htmlexport.pages.elements.core.table;

/**
 * The {@link ITablePageElementLayout} is a layout for tables
 * 
 * 
 * 
 * @author dicker
 * 
 */
public interface ITablePageElementLayout {

    /**
     * layouts the given {@link TableRowPageElement}.
     * <p>
     * Use the given row to layout a specific {@link TableRowPageElement}
     * </p>
     * <p>
     * This method is called by the {@link TablePageElement}
     * </p>
     * 
     */
    public void layoutRow(int row, TableRowPageElement rowPageElement);

    /**
     * layouts the given {@link TableCellPageElement}
     * <p>
     * Use the given row and columns to layout a specific {@link TableCellPageElement}. If you want
     * to layout all cells of a column check the value of columns
     * </p>
     * <p>
     * This method is called by the {@link TableRowPageElement}
     * </p>
     * 
     */
    public void layoutCell(int row, int column, TableCellPageElement cellPageElement);
}
