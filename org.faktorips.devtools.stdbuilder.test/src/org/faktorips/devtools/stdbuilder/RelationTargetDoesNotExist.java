/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) dürfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung – Version 0.1 (vor Gründung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation 
 *
 *******************************************************************************/

package org.faktorips.devtools.stdbuilder;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.AbstractIpsPluginTest;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAssociation;

/**
 * 
 * @author Jan Ortmann
 */
public class RelationTargetDoesNotExist extends AbstractIpsPluginTest {

    public RelationTargetDoesNotExist() {
        super();
    }

    public RelationTargetDoesNotExist(String name) {
        super(name);
    }

    public void test() throws CoreException {
        IIpsProject project = newIpsProject();
        IPolicyCmptType sourceType = newPolicyCmptType(project, "Source");
        IPolicyCmptType targetType = newPolicyCmptType(project, "target");
        
        IPolicyCmptTypeAssociation fromSourceToTarget = sourceType.newPolicyCmptTypeAssociation();
        fromSourceToTarget.setTarget(targetType.getQualifiedName());
        fromSourceToTarget.setTargetRoleSingular("Target");
        fromSourceToTarget.setTargetRolePlural("Targets");
        fromSourceToTarget.setTargetRoleSingularProductSide("TargetProduct");
        fromSourceToTarget.setTargetRolePluralProductSide("TargetProducts");
        
        IPolicyCmptTypeAssociation fromTargetToSource= targetType.newPolicyCmptTypeAssociation();
        fromTargetToSource.setTarget(sourceType.getQualifiedName());
        fromTargetToSource.setTargetRoleSingular("Source");
        fromTargetToSource.setTargetRolePlural("Sources");
        fromTargetToSource.setTargetRoleSingularProductSide("SourceProduct");
        fromTargetToSource.setTargetRolePluralProductSide("SourceProducts");
        
        ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
        
        targetType.getIpsSrcFile().getCorrespondingFile().delete(true, false, null);
        ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
        
    }
}
