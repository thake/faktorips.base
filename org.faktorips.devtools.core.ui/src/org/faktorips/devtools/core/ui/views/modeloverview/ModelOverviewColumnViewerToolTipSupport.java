/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
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

package org.faktorips.devtools.core.ui.views.modeloverview;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

public class ModelOverviewColumnViewerToolTipSupport extends ColumnViewerToolTipSupport {

    protected ModelOverviewColumnViewerToolTipSupport(ColumnViewer viewer, int style, boolean manualActivation) {
        super(viewer, style, manualActivation);
    }

    @Override
    protected Composite createViewerToolTipContentArea(Event event, ViewerCell cell, Composite parent) {
        // TODO Auto-generated method stub
        return super.createViewerToolTipContentArea(event, cell, parent);
    }

    public static void enableFor(ColumnViewer viewer) {
        new ModelOverviewColumnViewerToolTipSupport(viewer, ToolTip.NO_RECREATE, false);
    }

    public static void enableFor(ColumnViewer viewer, int style) {
        new ModelOverviewColumnViewerToolTipSupport(viewer, style, false);
    }

    @Override
    protected Composite createToolTipContentArea(Event event, Composite parent) {
        // TODO Auto-generated method stub
        return super.createToolTipContentArea(event, parent);
    }
}
