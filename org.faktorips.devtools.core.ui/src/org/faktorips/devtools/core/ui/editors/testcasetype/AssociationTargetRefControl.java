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

package org.faktorips.devtools.core.ui.editors.testcasetype;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Composite;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.type.IType;
import org.faktorips.devtools.core.model.type.ITypeHierarchy;
import org.faktorips.devtools.core.ui.UIToolkit;
import org.faktorips.devtools.core.ui.controls.PcTypeRefControl;

/**
 * Control to select a specific target of association.
 * 
 * @author Joerg Ortmann
 */
public class AssociationTargetRefControl extends PcTypeRefControl {

    private IPolicyCmptType policyCmptTypeTarget;

    public AssociationTargetRefControl(IIpsProject project, Composite parent, UIToolkit toolkit,
            IPolicyCmptType policyCmptTypeTarget) {
        super(project, parent, toolkit);

        this.policyCmptTypeTarget = policyCmptTypeTarget;
    }

    public void setPolicyCmptTypeTarget(IPolicyCmptType policyCmptTypeTarget) {
        this.policyCmptTypeTarget = policyCmptTypeTarget;
    }

    protected List<IIpsObject> getIpsObjects() throws CoreException {
        if (policyCmptTypeTarget == null) {
            return new ArrayList<IIpsObject>();
        }

        /*
         * find all policy component of the given type (incl. subclasses) (the result could be
         * candidates for the target policy cmpt type of the association) when defining the test
         * case subclasses of the current policy cmpt target could be assigned as target for the
         * associations target Remark: this is a operation in the ui, therefore it is acceptable if
         * the operation takes a long time, because the user has triggered this operation to chose a
         * policy cmpt from this selection of policy cmpt types
         */
        ITypeHierarchy subTypeHierarchy = policyCmptTypeTarget.getSubtypeHierarchy();
        List<IType> subTypes = subTypeHierarchy.getAllSubtypes(policyCmptTypeTarget);
        if (subTypes == null) {
            subTypes = new ArrayList<IType>();
        }
        ArrayList<IIpsObject> result = new ArrayList<IIpsObject>(subTypes);
        result.add(policyCmptTypeTarget);
        return result;
    }
}
