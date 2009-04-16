/*******************************************************************************
 * Copyright (c) 2005-2009 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.wizards.enumexport;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "org.faktorips.devtools.core.ui.wizards.enumexport.messages"; //$NON-NLS-1$
    public static String EnumExportPage_enum_label;
    public static String EnumExportPage_messagearea_title;
    public static String EnumExportPage_msgInvalidEnum;
    public static String EnumExportPage_msgNonExistingEnum;
    public static String EnumExportPage_msgValidateEnumError;
    public static String EnumExportPage_title;
    public static String EnumExportWizard_msgFileExists;
    public static String EnumExportWizard_msgFileExistsTitle;
    public static String EnumExportWizard_operationName;
    public static String EnumExportWizard_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
