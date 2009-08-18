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

package org.faktorips.devtools.core.ui.views.modelexplorer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.bf.IBusinessFunction;
import org.faktorips.devtools.core.model.enums.IEnumAttribute;
import org.faktorips.devtools.core.model.enums.IEnumContent;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.ipsobject.IIpsObject;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpt.IProductCmpt;
import org.faktorips.devtools.core.model.productcmpt.IProductCmptGeneration;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.ITableStructureUsage;
import org.faktorips.devtools.core.model.tablecontents.ITableContents;
import org.faktorips.devtools.core.model.tablestructure.ITableStructure;
import org.faktorips.devtools.core.model.testcase.ITestCase;
import org.faktorips.devtools.core.model.testcasetype.ITestCaseType;
import org.faktorips.devtools.core.model.type.IAssociation;
import org.faktorips.devtools.core.model.type.IAttribute;
import org.faktorips.devtools.core.model.type.IMethod;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.devtools.core.ui.actions.ExpandCollapseAllAction;
import org.faktorips.devtools.core.ui.actions.TreeViewerRefreshAction;
import org.faktorips.devtools.core.ui.editors.IpsObjectEditor;
import org.faktorips.devtools.core.ui.views.IpsElementDragListener;
import org.faktorips.devtools.core.ui.views.IpsProblemsLabelDecorator;
import org.faktorips.devtools.core.ui.views.IpsResourceChangeListener;
import org.faktorips.devtools.core.ui.views.TreeViewerDoubleclickListener;

/**
 * The <tt>ModelExplorer</tt> is a <tt>ViewPart</tt> for displaying <tt>IIpsObject</tt>s along with
 * their attributes.
 * <p>
 * The view uses a <code>TreeViewer</code> to represent the hierarchical data structure. It can be
 * configured to show the tree of package fragments in a hierarchical (default) or a flat layout
 * style.
 * 
 * @author Stefan Widmaier
 */

public class ModelExplorer extends ViewPart implements IShowInTarget {

    /** Extension id of this viewer extension. */
    public static final String EXTENSION_ID = "org.faktorips.devtools.core.ui.views.modelExplorer"; //$NON-NLS-1$

    /** The filter group in the context menu of the model explorer. */
    // TODO goup -> group
    protected static String MENU_FILTER_GROUP = "goup.filter"; //$NON-NLS-1$

    /** Used for saving the current filter into an eclipse <tt>Memento</tt>. */
    protected static final String FILTER_KEY = "filter"; //$NON-NLS-1$

    protected static final String LINK_TO_EDITOR_KEY = "linktoeditor"; //$NON-NLS-1$

    /** Identification number for hierarchical layout. */
    private static final int HIERARCHICAL_LAYOUT = 0;

    /** Identification number for flat layout. */
    private static final int FLAT_LAYOUT = 1;

    private static final String MEMENTO = "modelExplorer.memento"; //$NON-NLS-1$

    private static final String LAYOUT_STYLE_KEY = "style"; //$NON-NLS-1$

    /** The tree viewer displaying the object model. */
    protected TreeViewer treeViewer;

    /** Label provider for the tree viewer. */
    protected ModelLabelProvider labelProvider;

    /** The model explorer configuration containing the allowed types. */
    protected ModelExplorerConfiguration config;

    /**
     * Flag that indicates whether the current layout style is flat (<code>true</code>) or
     * hierarchical (<code>false</code>).
     */
    protected boolean isFlatLayout = false;

    /** Flag that indicates whether linking is enabled. */
    protected boolean linkingEnabled;

    /** Listener for activation of editors. */
    private ActivationListener editorActivationListener;

    /** Content provider for the tree viewer. */
    private ModelContentProvider contentProvider;

    private IpsResourceChangeListener resourceListener;

    /** Flag that indicates if non ips projects will be excluded or not. */
    private boolean excludeNoIpsProjects = false;

    /**
     * Decorator for problems in ips objects. This decorator is adjusted according to the current
     * layout style.
     */
    private IpsProblemsLabelDecorator ipsDecorator = new IpsProblemsLabelDecorator();

    private ToggleLinkingAction toggleLinking;

    /** Creates a new <tt>ModelExplorer</tt>. */
    public ModelExplorer() {
        super();
        config = createConfig();
        contentProvider = createContentProvider();
    }

    /**
     * Creates and returns a <code>ModelExplorerConfiguration</code> with the following allowed
     * types:
     * 
     * <ul>
     * <li>IPolicyCmptType</li>
     * <li>IProductCmptType</li>
     * <li>IEnumType</li>
     * <li>IEnumContent</li>
     * <li>IProductCmpt</li>
     * <li>IProductCmptGeneration</li>
     * <li>ITableStructure</li>
     * <li>ITableContents</li>
     * <li>IBusinessFunction</li>
     * <li>IEnumAttribute</li>
     * <li>IAttribute</li>
     * <li>IAssociation</li>
     * <li>IMethod</li>
     * <li>ITableStructureUsage</li>
     * <li>ITestCase</li>
     * <li>ITestCaseType</li>
     * </ul>
     * 
     * @see #ModelExplorerConfiguration
     */
    protected ModelExplorerConfiguration createConfig() {
        return new ModelExplorerConfiguration(new Class[] { IPolicyCmptType.class, IProductCmptType.class,
                IEnumType.class, IEnumContent.class, IProductCmpt.class, IProductCmptGeneration.class,
                ITableStructure.class, ITableContents.class, IBusinessFunction.class, IAttribute.class,
                IEnumAttribute.class, IAssociation.class, IMethod.class, ITableStructureUsage.class, ITestCase.class,
                ITestCaseType.class }, new Class[] { IFolder.class, IFile.class, IProject.class });
    }

    /**
     * Creates the <code>ModelContentProvider</code> that is used by the model explorer to show
     * contents.
     */
    protected ModelContentProvider createContentProvider() {
        return new ModelContentProvider(config, isFlatLayout);
    }

    @Override
    public void createPartControl(Composite parent) {
        // Init saved state
        contentProvider.setExcludeNoIpsProjects(excludeNoIpsProjects);

        labelProvider = new ModelLabelProvider();
        treeViewer = new TreeViewer(parent);
        treeViewer.setContentProvider(contentProvider);
        IDecoratorManager decoManager = IpsPlugin.getDefault().getWorkbench().getDecoratorManager();
        DecoratingLabelProvider decoProvider = new DecoratingLabelProvider(labelProvider, decoManager
                .getLabelDecorator());
        treeViewer.setLabelProvider(decoProvider);
        treeViewer.setSorter(new ModelExplorerSorter());
        treeViewer.setInput(IpsPlugin.getDefault().getIpsModel());

        treeViewer.addDoubleClickListener(new ModelExplorerDoubleclickListener(treeViewer));
        treeViewer.addDragSupport(DND.DROP_LINK | DND.DROP_MOVE, new Transfer[] { FileTransfer.getInstance() },
                new IpsElementDragListener(treeViewer));
        treeViewer.addDropSupport(DND.DROP_MOVE, new Transfer[] { FileTransfer.getInstance() },
                new ModelExplorerDropListener());

        createFilters(treeViewer);

        getSite().setSelectionProvider(treeViewer);

        resourceListener = new IpsResourceChangeListener(treeViewer) {
            /*
             * TODO Optimize refresh: Refresh folders if files were added or removed, additionally
             * refresh changed files.
             */
            @Override
            protected IResource[] internalResourceChanged(IResourceChangeEvent event) {
                IResourceDelta delta = event.getDelta();
                IResource res = delta.getResource();
                if (res != null) {
                    return new IResource[] { res };
                }
                return new IResource[] {};
            }
        };

        ResourcesPlugin.getWorkspace().addResourceChangeListener(resourceListener,
                IResourceChangeEvent.POST_BUILD | IResourceChangeEvent.POST_CHANGE);

        /*
         * Use the current value of isFlatLayout, which is set by loading the memento/viewState
         * before this method is called
         */
        setFlatLayout(isFlatLayout);

        // Create 'link with editor' ection
        toggleLinking = new ToggleLinkingAction(this);

        IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
        createMenu(menuManager);
        createAdditionalMenuEntries(menuManager);
        createContextMenu();
        createToolBar();

        if (isLinkingEnabled()) {
            IEditorPart editorPart = getSite().getPage().getActiveEditor();
            if (editorPart != null) {
                editorActivated(editorPart);
            }
        }

        editorActivationListener = new ActivationListener(getSite().getPage());
    }

    /**
     * Show an ips file or a normal file in the navigator view corresponding to the active editor if
     * "link with editor" is enabled.
     * 
     * @param editorPart The editor that has been activated.
     */
    private void editorActivated(IEditorPart editorPart) {
        if (!isLinkingEnabled() || editorPart == null) {
            return;
        }

        if (editorPart instanceof IpsObjectEditor) {
            IpsObjectEditor ipsEditor = (IpsObjectEditor)editorPart;
            setSelectionInTree(ipsEditor.getIpsSrcFile());
        } else if (editorPart.getEditorInput() instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput)editorPart.getEditorInput()).getFile();
            IIpsElement ipsElement = IpsPlugin.getDefault().getIpsModel().getIpsElement(file);
            if (ipsElement == null || !ipsElement.exists()) {
                setSelectionInTree(file);
            } else {
                setSelectionInTree(ipsElement);
            }
        }
    }

    private void setSelectionInTree(Object objectToSelect) {
        IStructuredSelection newSelection = new StructuredSelection(objectToSelect);
        if (treeViewer.getSelection().equals(newSelection)) {
            treeViewer.getTree().showSelection();
        } else {
            treeViewer.setSelection(newSelection, true);
        }
    }

    /**
     * This operation is empty by default. Subclasses may overwrite to create filters for filtering
     * out specific content from the model explorer.
     * 
     * @param tree
     */
    protected void createFilters(TreeViewer tree) {

    }

    /** Create menu for layout styles */
    private void createMenu(IMenuManager menuManager) {
        IAction flatLayoutAction = new LayoutAction(this, true);
        flatLayoutAction.setText(Messages.ModelExplorer_actionFlatLayout);
        flatLayoutAction.setImageDescriptor(IpsUIPlugin.getDefault().getImageDescriptor("ModelExplorerFlatLayout.gif")); //$NON-NLS-1$
        IAction hierarchicalLayoutAction = new LayoutAction(this, false);
        hierarchicalLayoutAction.setText(Messages.ModelExplorer_actionHierarchicalLayout);
        hierarchicalLayoutAction.setImageDescriptor(IpsPlugin.getDefault().getImageDescriptor(
                "ModelExplorerHierarchicalLayout.gif")); //$NON-NLS-1$

        // Actions are unchecked as per default, check action for current layout
        if (isFlatLayout()) {
            flatLayoutAction.setChecked(true);
        } else {
            hierarchicalLayoutAction.setChecked(true);
        }

        IMenuManager layoutMenu = new MenuManager(Messages.ModelExplorer_submenuLayout);
        layoutMenu.add(flatLayoutAction);
        layoutMenu.add(hierarchicalLayoutAction);
        menuManager.add(layoutMenu);
    }

    /**
     * Create additional menu entries e.g. filters.
     * 
     * @param menuManager
     */
    protected void createAdditionalMenuEntries(IMenuManager menuManager) {
        menuManager.add(new Separator(MENU_FILTER_GROUP));
        Action showNoIpsProjectsAction = createShowNoIpsProjectsAction();
        showNoIpsProjectsAction.setChecked(excludeNoIpsProjects);
        menuManager.appendToGroup(MENU_FILTER_GROUP, showNoIpsProjectsAction);
        menuManager.add(toggleLinking);

    }

    /** Creates the context menu for the model explorer. */
    protected void createContextMenu() {
        MenuManager manager = new MenuManager();
        manager.setRemoveAllWhenShown(true);
        manager
                .addMenuListener(new ModelExplorerContextMenuBuilder(this, config, getViewSite(), getSite(), treeViewer));
        Menu contextMenu = manager.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(contextMenu);
        getSite().registerContextMenu(manager, treeViewer);
    }

    private void createToolBar() {
        Action refreshAction = new TreeViewerRefreshAction(getSite());
        getViewSite().getActionBars().setGlobalActionHandler(ActionFactory.REFRESH.getId(), refreshAction);
        IWorkbenchAction retargetAction = ActionFactory.REFRESH.create(getViewSite().getWorkbenchWindow());
        retargetAction.setImageDescriptor(refreshAction.getImageDescriptor());
        retargetAction.setToolTipText(refreshAction.getToolTipText());
        getViewSite().getActionBars().getToolBarManager().add(retargetAction);
        getViewSite().getActionBars().getToolBarManager().add(new ExpandCollapseAllAction(treeViewer));
        getViewSite().getActionBars().getToolBarManager().add(toggleLinking);

    }

    @Override
    public void setFocus() {

    }

    /** Answers whether this part shows the packagFragments flat or hierarchical */
    private boolean isFlatLayout() {
        return isFlatLayout;
    }

    /**
     * Sets the layout style to flat respectively hierarchical. Informs label and contentProvider,
     * activates emptyPackageFilter for flat layout to hide empty PackageFragments.
     */
    private void setFlatLayout(boolean flatLayout) {
        isFlatLayout = flatLayout;

        ipsDecorator.setFlatLayout(isFlatLayout());
        contentProvider.setIsFlatLayout(isFlatLayout());
        labelProvider.setIsFlatLayout(isFlatLayout());

        treeViewer.getControl().setRedraw(false);
        treeViewer.refresh();
        treeViewer.getControl().setRedraw(true);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Loads the layout style from the given Memento Object.
     */
    @Override
    public void init(IViewSite site, IMemento memento) throws PartInitException {
        super.init(site, memento);
        if (memento != null) {
            IMemento layout = memento.getChild(MEMENTO);
            if (layout != null) {
                Integer layoutValue = layout.getInteger(LAYOUT_STYLE_KEY);
                Integer filterValue = layout.getInteger(FILTER_KEY);
                Integer linkingValue = layout.getInteger(LINK_TO_EDITOR_KEY);
                isFlatLayout = layoutValue == null ? false : layoutValue.intValue() == FLAT_LAYOUT;
                excludeNoIpsProjects = filterValue == null ? false : filterValue.intValue() == 1;
                linkingEnabled = linkingValue == null ? false : linkingValue.intValue() == 1;
            }
        }
    }

    /** Returns whether the editor is linked to the navigator. */
    public boolean isLinkingEnabled() {
        return linkingEnabled;
    }

    /**
     * Save the state and link editor with navigator if linkingEnabled is <code>true</code>.
     * 
     * @param linkingEnabled Flag indicating whether linking should be enabled.
     */
    public void setLinkingEnabled(boolean linkingEnabled) {
        this.linkingEnabled = linkingEnabled;

        if (linkingEnabled) {
            IEditorPart editorPart = getSite().getPage().getActiveEditor();
            if (editorPart != null) {
                editorActivated(editorPart);
            }
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves the current layout style into the given memento object.
     */
    @Override
    public void saveState(IMemento memento) {
        super.saveState(memento);
        IMemento layout = memento.createChild(MEMENTO);
        layout.putInteger(LAYOUT_STYLE_KEY, isFlatLayout() ? FLAT_LAYOUT : HIERARCHICAL_LAYOUT);
        layout.putInteger(FILTER_KEY, excludeNoIpsProjects ? 1 : 0);
        layout.putInteger(LINK_TO_EDITOR_KEY, linkingEnabled ? 1 : 0);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Unregisters this part as resource-listener in the workspace and disposes of it.
     */
    @Override
    public void dispose() {
        editorActivationListener.dispose();

        ResourcesPlugin.getWorkspace().removeResourceChangeListener(resourceListener);

        super.dispose();
    }

    public boolean show(ShowInContext context) {
        ISelection selection = context.getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = ((IStructuredSelection)selection);
            if (structuredSelection.size() >= 1) {
                return reveal(structuredSelection.getFirstElement());
            }
        }

        Object input = context.getInput();
        if (input instanceof IProductCmpt) {
            return reveal(context.getInput());
        } else if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput)input).getFile();
            return reveal(file);
        }

        return false;
    }

    private boolean reveal(Object toReveal) {
        Object node;
        if (toReveal instanceof Object[]) {
            node = ((Object[])toReveal)[0];
        } else {
            node = toReveal;
        }

        if (node instanceof IProductCmptGeneration) {
            treeViewer.setSelection(new StructuredSelection(node), true);
            return true;
        } else if (node instanceof IProductCmpt) {
            treeViewer.setSelection(new StructuredSelection(node), true);
            return true;
        } else if (node instanceof IFile) {
            try {
                IIpsSrcFile file = (IIpsSrcFile)IpsPlugin.getDefault().getIpsModel().getIpsElement((IFile)node);
                if (file == null || !file.exists()) {
                    return false;
                }
                IIpsObject obj = file.getIpsObject();
                treeViewer.setSelection(new StructuredSelection(obj), true);
                return true;
            } catch (CoreException e) {
                IpsPlugin.log(e);
            }
        }

        return false;
    }

    /** Returns the content provider. */
    protected ModelContentProvider getContentProvider() {
        return contentProvider;
    }

    /**
     * Returns whether this class is a kind of model explorer.
     * <p>
     * A model explorer is an explorer with enhanced functionality. Other derived explorer classes
     * should return <code>false</code> if restriced menu operations should be provided.
     */
    protected boolean isModelExplorer() {
        return true;
    }

    /** Internal part and shell activation listener. */
    private class ActivationListener implements IPartListener, IWindowListener {

        private IPartService partService;

        /** indicates if the last activation event was triggered by the model explorer. */
        private boolean activatedByModelExplorer;

        /**
         * Creates a new activation listener.
         * 
         * @param partService
         */
        public ActivationListener(IPartService partService) {
            this.partService = partService;
            partService.addPartListener(this);
            PlatformUI.getWorkbench().addWindowListener(this);
        }

        /**
         * Sets the "activation by model explorer" flag. Set <code>true</code> if the next
         * activation event was initiated by the model explorer.
         * 
         * @param activatedByModelExplorer Flag indicating whether the next activation event was
         *            initiated by the model explorer.
         */
        public void setActivatedByModelExplorer(boolean activatedByModelExplorer) {
            this.activatedByModelExplorer = activatedByModelExplorer;
        }

        /**
         * Disposes this activation listener.
         */
        public void dispose() {
            partService.removePartListener(this);
            PlatformUI.getWorkbench().removeWindowListener(this);
            partService = null;
        }

        public void partActivated(IWorkbenchPart part) {
            if (wasActivatedByModelExplorer()) {
                return;
            }
            if (part instanceof IEditorPart) {
                editorActivated((IEditorPart)part);
            }
        }

        public void windowActivated(IWorkbenchWindow window) {
            if (wasActivatedByModelExplorer()) {
                return;
            }
            editorActivated(window.getActivePage().getActiveEditor());
        }

        /**
         * Returns <tt>true</tt> if the activation was triggered by the model explorer. Additionally
         * resets the activation flag.
         */
        private boolean wasActivatedByModelExplorer() {
            if (activatedByModelExplorer) {
                activatedByModelExplorer = false;
                return true;
            }
            return false;
        }

        public void partBroughtToTop(IWorkbenchPart part) {

        }

        public void partClosed(IWorkbenchPart part) {

        }

        public void partDeactivated(IWorkbenchPart part) {

        }

        public void partOpened(IWorkbenchPart part) {

        }

        public void windowDeactivated(IWorkbenchWindow window) {

        }

        public void windowClosed(IWorkbenchWindow window) {

        }

        public void windowOpened(IWorkbenchWindow window) {

        }

    }

    /**
     * DoubleClickListener which informs the model explorer about double clicking inside the tree.
     * <p>
     * Used to avoid the handling of editor activation (linking) if the editor was activated by the
     * model explorer.
     * <p>
     * Avoid the following scenario: If a child is double clicked then the editor will be opened
     * (activated) afterwards the activation will be catched by this model explorer and the model
     * explorer selects the corresponding parent object (linking), the selection of the child will
     * be lost!
     */
    private class ModelExplorerDoubleclickListener extends TreeViewerDoubleclickListener {

        /**
         * Creates a new model explorer double click listener.
         * 
         * @param tree
         */
        public ModelExplorerDoubleclickListener(TreeViewer tree) {
            super(tree);
        }

        @Override
        public void doubleClick(DoubleClickEvent event) {
            editorActivationListener.setActivatedByModelExplorer(true);
            IEditorPart editorPart = openEditorsForSelection();
            if (editorPart == null) {
                // editor wasn't opened, therfore the activation flag must be reseted
                editorActivationListener.setActivatedByModelExplorer(false);
            }

            collapsOrExpandTree(event);
        }

    }

    private class LayoutAction extends Action implements IAction {

        private boolean isFlatLayout;

        private ModelExplorer modelExplorer;

        public LayoutAction(ModelExplorer modelExplorer, boolean flat) {
            super("", AS_RADIO_BUTTON); //$NON-NLS-1$

            isFlatLayout = flat;
            this.modelExplorer = modelExplorer;
        }

        @Override
        public void run() {
            if (modelExplorer.isFlatLayout() != isFlatLayout) {
                modelExplorer.setFlatLayout(isFlatLayout);
            }
        }

    }

    private Action createShowNoIpsProjectsAction() {
        return new Action(Messages.ModelExplorer_menuShowIpsProjectsOnly_Title, IAction.AS_CHECK_BOX) {
            @Override
            public ImageDescriptor getImageDescriptor() {
                return null;
            }

            @Override
            public void run() {
                excludeNoIpsProjects = !excludeNoIpsProjects;
                contentProvider.setExcludeNoIpsProjects(excludeNoIpsProjects);
                treeViewer.refresh();
            }

            @Override
            public String getToolTipText() {
                return Messages.ModelExplorer_menuShowIpsProjectsOnly_Tooltip;
            }
        };
    }

}
