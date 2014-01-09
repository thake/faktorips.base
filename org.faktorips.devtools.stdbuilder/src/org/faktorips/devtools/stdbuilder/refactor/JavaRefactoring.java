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

package org.faktorips.devtools.stdbuilder.refactor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;

/**
 * Abstract base class for refactorings that modify Java code.
 * <p>
 * This class was introduced to have a type that describes Java modifications. A Java modification
 * usually is realized as JDT refactoring but we also do custom modifications, e.g. copying methods
 * from interface to interface for the pull up refactoring (as JDT does not support this).
 * 
 * @author Alexander Weickmann
 */
public abstract class JavaRefactoring {

    /**
     * Checks the initial and final conditions of this Java refactoring and returns an appropriate
     * status object.
     * 
     * @param pm The progress monitor to report progress to or null if no progress reporting is
     *            needed
     * 
     * @throws CoreException If an error occurs during condition checking
     */
    public abstract RefactoringStatus checkAllConditions(final IProgressMonitor pm) throws CoreException;

    /**
     * Executes this Java refactoring.
     * 
     * @param pm The progress monitor to report progress to or null if no progress reporting is
     *            needed
     * 
     * @throws CoreException If an error occurs during the refactoring
     */
    public abstract void perform(final IProgressMonitor pm) throws CoreException;

}
