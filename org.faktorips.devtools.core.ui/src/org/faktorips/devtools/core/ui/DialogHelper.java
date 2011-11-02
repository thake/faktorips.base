/*******************************************************************************
 * Copyright (c) 2005-2011 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.devtools.core.ui;

import org.eclipse.jface.window.Window;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;
import org.faktorips.devtools.core.ui.editors.EditDialog;
import org.faktorips.util.memento.Memento;

/**
 * Provides utility methods regarding dialogs.
 * 
 * @author Alexander Weickmann
 */
public class DialogHelper {

    /**
     * Opens the provided {@link EditDialog} and takes care of resetting the state of the
     * {@link IIpsObjectPart} being edited to the state as it was before opening the dialog if the
     * user presses the cancel button.
     * 
     * @param editDialog the {@link EditDialog} to open
     * @param editedPart the {@link IIpsObjectPart} that is being edited
     */
    public void openEditDialogWithMemento(EditDialog editDialog, IIpsObjectPart editedPart) {
        boolean dirty = editedPart.getIpsSrcFile().isDirty();
        Memento memento = editedPart.newMemento();

        editDialog.open();

        if (editDialog.getReturnCode() == Window.CANCEL && editedPart.getIpsSrcFile().isMutable()) {
            editedPart.setState(memento);
            if (!dirty) {
                editedPart.getIpsSrcFile().markAsClean();
            }
        }
    }

}
