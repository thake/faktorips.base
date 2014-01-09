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

package org.faktorips.devtools.core.ui;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.valueset.IValueSet;
import org.faktorips.devtools.core.ui.controller.EditField;
import org.faktorips.devtools.core.ui.table.IpsCellEditor;

/**
 * A factory to create controls and edit fields that allow to edit values for one or more value
 * datatypes.
 * 
 * @author Joerg Ortmann
 */
public abstract class ValueDatatypeControlFactory {

    /**
     * Returns <code>true</code> if this factory can create controls for the given datatype,
     * otherwise <code>false</code>.
     * 
     * @param datatype Datatype controls are needed for - might be <code>null</code>.
     */
    public abstract boolean isFactoryFor(ValueDatatype datatype);

    /**
     * Creates a control and edit field that allows to edit a value of one of the value datatypes
     * this is a factory for.
     * 
     * @param toolkit The toolkit used to create the control.
     * @param parent The parent composite to which the control is added.
     * @param datatype The value datatype a control should be created for.
     * @param valueSet An optional @Deprecated valueset. Future Implementations should use
     *            ValueSetOwner instead.
     * 
     */
    public abstract EditField<String> createEditField(UIToolkit toolkit,
            Composite parent,
            ValueDatatype datatype,
            @Deprecated IValueSet valueSet,
            IIpsProject ipsProject);

    /**
     * Creates a control that allows to edit a value of the value datatype this is a factory for.
     * 
     * @param toolkit The toolkit used to create the control.
     * @param parent The parent composite to which the control is added.
     * @param datatype The value datatype a control should be created for.
     * @param valueSet An optional @Deprecated valueset.Future Implementations should use
     *            ValueSetOwner instead.
     */
    public abstract Control createControl(UIToolkit toolkit,
            Composite parent,
            ValueDatatype datatype,
            @Deprecated IValueSet valueSet,
            IIpsProject ipsProject);

    /**
     * Creates a cell editor that allows to edit a value of the value datatype this is a factory
     * for.
     * 
     * @deprecated use
     *             {@link #createTableCellEditor(UIToolkit, ValueDatatype, IValueSet, TableViewer, int, IIpsProject)}
     *             instead.
     */
    @Deprecated
    public abstract IpsCellEditor createCellEditor(UIToolkit toolkit,
            ValueDatatype datatype,
            IValueSet valueSet,
            TableViewer tableViewer,
            int columnIndex,
            IIpsProject ipsProject);

    /**
     * Creates a cell editor that allows to edit a value of the value datatype this is a factory
     * for.
     * 
     * @param toolkit The ui toolkit to use for creating ui elements.
     * @param datatype The <code>ValueDatatype</code> to create a cell editor for.
     * @param valueSet An optional valueset.
     * @param tableViewer The viewer
     * @param columnIndex The index of the column.
     */
    public abstract IpsCellEditor createTableCellEditor(UIToolkit toolkit,
            ValueDatatype datatype,
            IValueSet valueSet,
            TableViewer tableViewer,
            int columnIndex,
            IIpsProject ipsProject);

    public abstract int getDefaultAlignment();

}
