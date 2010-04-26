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

package org.faktorips.devtools.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IBundleGroupProvider;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.faktorips.devtools.core.builder.DependencyGraphPersistenceManager;
import org.faktorips.devtools.core.internal.model.IpsModel;
import org.faktorips.devtools.core.internal.model.testcase.IpsTestRunner;
import org.faktorips.devtools.core.internal.model.versionmanager.IpsFeatureMigrationOperation;
import org.faktorips.devtools.core.model.IIpsModel;
import org.faktorips.devtools.core.model.bf.BFElementType;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsproject.IIpsLoggingFrameworkConnector;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.testcase.IIpsTestRunner;
import org.faktorips.devtools.core.model.versionmanager.AbstractIpsFeatureMigrationOperation;
import org.faktorips.devtools.core.model.versionmanager.IIpsFeatureVersionManager;
import org.faktorips.devtools.core.model.versionmanager.IpsFeatureVersionManagerSorter;
import org.faktorips.devtools.tableconversion.ITableFormat;
import org.faktorips.devtools.tableconversion.IValueConverter;
import org.faktorips.util.ArgumentCheck;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;

/**
 * The main plugin class.
 * 
 * @author Jan Ortmann
 */
public class IpsPlugin extends AbstractUIPlugin {

    public final static String PLUGIN_ID = "org.faktorips.devtools.core"; //$NON-NLS-1$

    public final static String PROBLEM_MARKER = PLUGIN_ID + ".problemmarker"; //$NON-NLS-1$

    public final static boolean TRACE_UI = Boolean.valueOf(
            Platform.getDebugOption("org.faktorips.devtools.core/trace/ui")).booleanValue(); //$NON-NLS-1$

    /**
     * Returns the full extension id. This is the plugin's id plus the plugin relative extension id
     * separated by a dot.
     * 
     * @throws NullPointerException if pluginRelativeEnxtensionId is <code>null</code>.
     */
    public final static String getFullExtensionId(String pluginRelativeEnxtensionId) {
        ArgumentCheck.notNull(pluginRelativeEnxtensionId);
        return PLUGIN_ID + '.' + pluginRelativeEnxtensionId;
    }

    // The shared instance.
    private static IpsPlugin plugin;

    // the document builder factory provides the DocumentBuilder for this plugin
    private DocumentBuilderFactory docBuilderFactory;

    private IpsPreferences preferences;

    private IpsModel model;

    // Contains the ips test runner, which runs ips test and informs registered ips test run
    // listener
    private IIpsTestRunner ipsTestRunner;

    // All available external table formats
    private ITableFormat[] externalTableFormats;

    // All available feature version managers
    private IIpsFeatureVersionManager[] featureVersionManagers;

    private IIpsLoggingFrameworkConnector[] loggingFrameworkConnectors;

    private IFunctionResolverFactory[] flFunctionResolvers;

    private boolean testMode = false;
    private ITestAnswerProvider testAnswerProvider;

    /**
     * @see
     */
    private boolean suppressLoggingDuringTestExecution = false;

    private DependencyGraphPersistenceManager dependencyGraphPersistenceManager;

    /**
     * Returns the number of the installed Faktor-IPS version.
     */
    public final static String getInstalledFaktorIpsVersion() {
        return (String)Platform.getBundle("org.faktorips.devtools.core").getHeaders().get("Bundle-Version"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the shared instance.
     */
    public static IpsPlugin getDefault() {
        return plugin;
    }

    /**
     * The constructor.
     */
    public IpsPlugin() {
        super();
        plugin = this;
    }

    /**
     * Logs the core exception
     */
    public final static void log(CoreException e) {
        log(e.getStatus());
    }

    /**
     * Returns the version of the Faktor-IPS feature. If the Faktor-IPS feature is not found
     * <code>null</code> will be returned.
     */
    public String getIpsFeatureVersion() {
        IBundleGroupProvider[] bundleGroupProviders = Platform.getBundleGroupProviders();
        for (int i = 0; i < bundleGroupProviders.length; i++) {
            IBundleGroup[] bundleGroups = bundleGroupProviders[i].getBundleGroups();
            for (IBundleGroup bundleGroup : bundleGroups) {
                if (bundleGroups[i].getIdentifier().equals("org.faktorips.feature")) { //$NON-NLS-1$
                    return bundleGroups[i].getVersion();
                }
            }
        }
        return null;
    }

    /**
     * This method is called upon plug-in activation
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        preferences = new IpsPreferences(getPreferenceStore());
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        dependencyGraphPersistenceManager = new DependencyGraphPersistenceManager();

        IpsCompositeSaveParticipant saveParticipant = new IpsCompositeSaveParticipant();
        saveParticipant.addSaveParticipant(dependencyGraphPersistenceManager);
        ResourcesPlugin.getWorkspace().addSaveParticipant(this, saveParticipant);

        IpsObjectType.POLICY_CMPT_TYPE.getId(); // force loading of class before model is created!
        // ensure that this class is loaded in time
        BFElementType.ACTION_BUSINESSFUNCTIONCALL.getClass();
        model = new IpsModel();
        model.startListeningToResourceChanges();
    }

    /**
     * This method is called when the plug-in is stopped
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        super.stop(context);
        ((IpsModel)getIpsModel()).stopListeningToResourceChanges();
        model = null;
    }

    /**
     * Reinits the model (so all data in the cache is cleared). Should only be called in test cases
     * to ensure a clean environment.
     */
    public void reinitModel() {
        model.stopListeningToResourceChanges();
        model = new IpsModel();
        model.startListeningToResourceChanges();
    }

    /**
     * Returns the plugin's version identifier.
     */
    public Version getVersionIdentifier() {
        String version = (String)getBundle().getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
        return Version.parseVersion(version);
    }

    /**
     * Logs the status.
     */
    public final static void log(IStatus status) {
        if (!plugin.suppressLoggingDuringTestExecution) {
            plugin.getLog().log(status);
        }
    }

    /**
     * Logs the exception
     */
    public final static void log(Throwable t) {
        log(new IpsStatus(t));
    }

    /**
     * Logs the status and shows the status in a standard error dialog.
     */
    public final static void logAndShowErrorDialog(final IStatus status) {
        plugin.getLog().log(status);
        Display display = Display.getCurrent() != null ? Display.getCurrent() : Display.getDefault();
        display.asyncExec(new Runnable() {
            public void run() {
                ErrorDialog.openError(Display.getDefault().getActiveShell(), Messages.IpsPlugin_titleErrorDialog,
                        Messages.IpsPlugin_msgUnexpectedError, status);
            }
        });
    }

    /**
     * Logs the status and shows the status in a standard error dialog.
     */
    public final static void logAndShowErrorDialog(Exception e) {
        logAndShowErrorDialog(new IpsStatus(e));
    }

    /**
     * Logs the status and shows the status in a standard error dialog.
     */
    public final static void logAndShowErrorDialog(CoreException e) {
        logAndShowErrorDialog(e.getStatus());
    }

    /**
     * Does not log the status but show an error dialog with the status message
     * 
     * @param status
     */
    public final static void showErrorDialog(final IStatus status) {
        Display display = Display.getCurrent() != null ? Display.getCurrent() : Display.getDefault();
        display.asyncExec(new Runnable() {
            public void run() {
                ErrorDialog.openError(Display.getDefault().getActiveShell(), Messages.IpsPlugin_titleErrorDialog, null,
                        status);
            }
        });
    }

    /**
     * Returns a new document builder.
     * 
     * @throws RuntimeException if the factory throws a ParserConfigurationException. The
     *             ParserConfigurationException is wrapped in a runtime exception as we can't do
     *             anything to resolve it.
     */
    public DocumentBuilder newDocumentBuilder() {
        try {
            return docBuilderFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Returns the IPS model.
     */
    public IIpsModel getIpsModel() {
        return model;
    }

    /**
     * Returns preferences for this plugin.
     */
    public IpsPreferences getIpsPreferences() {
        return preferences;
    }

    /**
     * /** <strong>FOR INTNERNAL TEST USE ONLY.</strong>
     * <p>
     * Activate or deactivate test mode.
     */
    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /**
     * <strong>FOR INTNERNAL TEST USE ONLY.</strong>
     * 
     * @supress <code>true</code> if logging should be disabled during test execution. The default
     *          behaviour is not to supresse logging. However, in some test cases we use test data
     *          with an invalid state which results in exceptions in the error log which is the
     *          correct behaviour. However if looking at the error log after all tests have been
     *          run, these "correct" exceptions make it difficult, to see the unexpected exceptions.
     *          You see the exception, and think something has gone wrong. In this test cases it is
     *          appropriate to turn off logging. In the setup of the
     *          <cdoe>AbstractIpsPluginTest</code> logging is explicitly turned on, so there is no
     *          need to reset this flag, after your test method.
     * 
     * @see #log(CoreException)
     * @see #log(IpsStatus)
     */
    public void setSuppressLoggingDuringTest(boolean suppress) {
        suppressLoggingDuringTestExecution = suppress;
    }

    /**
     * <strong>FOR INTNERNAL TEST USE ONLY.</strong>
     * <p>
     * Returns <code>true</code> when test mode is active. If so, the method getTestAnswerProvider
     * must not return null (which means that setTestAnswerProvider has to be called with a non-null
     * value).
     */
    public boolean isTestMode() {
        return testMode;
    }

    /**
     * <strong>FOR INTNERNAL TEST USE ONLY.</strong>
     * <p>
     * Returns the answer provider for testing purpose.
     */
    public ITestAnswerProvider getTestAnswerProvider() {
        return testAnswerProvider;
    }

    /**
     * <strong>FOR INTNERNAL TEST USE ONLY.</strong>
     * <p>
     * Returns the answer provider for testing purpose.
     */
    public void setTestAnswerProvider(ITestAnswerProvider testAnswerProvider) {
        this.testAnswerProvider = testAnswerProvider;
    }

    /**
     * Returns the locale used by the localization. The returned locale is not the locale the
     * localization <strong>should</strong> use, it is the locale the localization
     * <strong>can</strong> use. That means if the default locale this plugin runs is for example
     * de_DE, but no language pack for german is installed, the localization uses the english
     * language, and this method will return the Locale for "en".
     */
    public Locale getUsedLanguagePackLocale() {
        Locale retValue = new Locale(Messages.IpsPlugin_languagePackLanguage, Messages.IpsPlugin_languagePackCountry,
                Messages.IpsPlugin_languagePackVariant);
        return retValue;
    }

    /**
     * Returns the ips test runner.
     */
    public IIpsTestRunner getIpsTestRunner() {
        if (ipsTestRunner == null) {
            ipsTestRunner = IpsTestRunner.getDefault();
        }

        return ipsTestRunner;
    }

    /**
     * @return An array of all available external table formats.
     */
    public ITableFormat[] getExternalTableFormats() {
        if (externalTableFormats == null) {
            initExternalTableFormats();
        }
        return externalTableFormats;
    }

    /**
     * Initialize the array of all available table formats
     */
    private void initExternalTableFormats() {
        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                "org.faktorips.devtools.core.externalTableFormat"); //$NON-NLS-1$
        List<ITableFormat> result = new ArrayList<ITableFormat>();
        for (IConfigurationElement element : elements) {
            try {
                ITableFormat format = (ITableFormat)element.createExecutableExtension("class"); //$NON-NLS-1$
                initExternalTableFormat(format, element);
                result.add(format);
            } catch (CoreException e) {
                log(e);
            }
        }
        externalTableFormats = result.toArray(new ITableFormat[result.size()]);
    }

    /**
     * Initialize the given format (fill with values provided by the given formatElement and with
     * <code>IValueConverter</code>s configured in other extension points.
     * 
     * @param format The external table format to initialize.
     * @param formatElement The configuration element which defines the given external table format.
     */
    private void initExternalTableFormat(ITableFormat format, IConfigurationElement formatElement) {
        format.setName(formatElement.getAttribute("name")); //$NON-NLS-1$
        format.setDefaultExtension(formatElement.getAttribute("defaultExtension")); //$NON-NLS-1$

        IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                "org.faktorips.devtools.core.externalValueConverter"); //$NON-NLS-1$

        IConfigurationElement tableFormatElement = null;
        for (IConfigurationElement element : elements) {
            String tableFormatId = formatElement.getAttribute("id");
            if (element.getAttribute("tableFormatId").equals(tableFormatId)) { //$NON-NLS-1$") 
                // converter found for current table format id
                tableFormatElement = element;
                break;
            }
        }

        IConfigurationElement[] valueConverters = tableFormatElement.getChildren();
        for (IConfigurationElement valueConverter : valueConverters) {
            try {
                IValueConverter converter = (IValueConverter)valueConverter.createExecutableExtension("class"); //$NON-NLS-1$
                format.addValueConverter(converter);
            } catch (CoreException e) {
                IpsPlugin.log(e);
            }
        }
    }

    /**
     * Returns the <code>IIpsLoggingFrameworkConnector</code> for the provided id. If no
     * <code>IIpsLoggingFrameworkConnector</code> with the provided id is found <code>null</code>
     * will be returned.
     */
    public IIpsLoggingFrameworkConnector getIpsLoggingFrameworkConnector(String id) {
        IIpsLoggingFrameworkConnector[] builders = getIpsLoggingFrameworkConnectors();
        for (IIpsLoggingFrameworkConnector builder : builders) {
            if (id.equals(builder.getId())) {
                return builder;
            }
        }
        return null;
    }

    /**
     * Returns the <code>IIpsLoggingFrameworkConnector</code> that are registered at the according
     * extension-point.
     */
    public IIpsLoggingFrameworkConnector[] getIpsLoggingFrameworkConnectors() {
        if (loggingFrameworkConnectors == null) {
            ArrayList<IIpsLoggingFrameworkConnector> builders = new ArrayList<IIpsLoggingFrameworkConnector>();
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(IpsPlugin.PLUGIN_ID,
                    "loggingFrameworkConnector"); //$NON-NLS-1$
            IExtension[] extensions = extensionPoint.getExtensions();
            for (IExtension extension : extensions) {
                IConfigurationElement[] configElements = extension.getConfigurationElements();
                for (IConfigurationElement configElement : configElements) {
                    if ("loggingFrameworkConnector".equals(configElement.getName())) { //$NON-NLS-1$
                        try {
                            IIpsLoggingFrameworkConnector connector = (IIpsLoggingFrameworkConnector)configElement
                                    .createExecutableExtension("class"); //$NON-NLS-1$
                            connector.setId(extension.getUniqueIdentifier() == null ? "" : extension //$NON-NLS-1$
                                    .getUniqueIdentifier());
                            builders.add(connector);

                        } catch (CoreException e) {
                            log(new IpsStatus("Unable to create the log statement builder with the id " //$NON-NLS-1$
                                    + extension.getUniqueIdentifier(), e));
                        }
                    }
                }
            }
            loggingFrameworkConnectors = builders.toArray(new IIpsLoggingFrameworkConnector[builders.size()]);
        }
        return loggingFrameworkConnectors;
    }

    /**
     * Returns the <code>org.faktorips.fl.FunctionResolver</code>s that are registered at the
     * according extension-point.
     */
    public IFunctionResolverFactory[] getFlFunctionResolverFactories() {
        if (flFunctionResolvers == null) {
            ArrayList<IFunctionResolverFactory> flFunctionResolverFactoryList = new ArrayList<IFunctionResolverFactory>();
            IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(IpsPlugin.PLUGIN_ID,
                    "flFunctionResolverFactory"); //$NON-NLS-1$
            IExtension[] extensions = extensionPoint.getExtensions();
            for (IExtension extension : extensions) {
                IConfigurationElement[] configElements = extension.getConfigurationElements();
                for (IConfigurationElement configElement : configElements) {
                    if ("functionResolverFactory".equals(configElement.getName())) { //$NON-NLS-1$
                        try {
                            IFunctionResolverFactory functionResolverFactory = (IFunctionResolverFactory)configElement
                                    .createExecutableExtension("class"); //$NON-NLS-1$
                            flFunctionResolverFactoryList.add(functionResolverFactory);

                        } catch (CoreException e) {
                            log(new IpsStatus(
                                    "Unable to create the flfunctionResolverFactory identified by the extension unique identifier: " //$NON-NLS-1$
                                            + extension.getUniqueIdentifier(), e));
                        }
                    }
                }
            }
            flFunctionResolvers = flFunctionResolverFactoryList
                    .toArray(new IFunctionResolverFactory[flFunctionResolverFactoryList.size()]);
        }
        return flFunctionResolvers;
    }

    /**
     * @return All installed ips feature version managers.
     */
    public IIpsFeatureVersionManager[] getIpsFeatureVersionManagers() {
        if (featureVersionManagers == null) {
            IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(
                    "org.faktorips.devtools.core.faktorIpsFeatureVersionManager"); //$NON-NLS-1$
            List<IIpsFeatureVersionManager> result = new ArrayList<IIpsFeatureVersionManager>();
            for (IConfigurationElement element : elements) {
                try {
                    IIpsFeatureVersionManager manager = (IIpsFeatureVersionManager)element
                            .createExecutableExtension("class"); //$NON-NLS-1$
                    manager.setFeatureId(element.getAttribute("featureId")); //$NON-NLS-1$
                    manager.setId(element.getAttribute("id")); //$NON-NLS-1$
                    manager.setPredecessorId(element.getAttribute("basedOnFeatureManager")); //$NON-NLS-1$
                    result.add(manager);
                } catch (CoreException e) {
                    log(e);
                }
            }
            featureVersionManagers = result.toArray(new IIpsFeatureVersionManager[result.size()]);
            if (featureVersionManagers.length == 0) {
                featureVersionManagers = new IIpsFeatureVersionManager[] { EmptyIpsFeatureVersionManager.INSTANCE };
            }
        }
        return featureVersionManagers;
    }

    /**
     * @param featureId The id of the feature the manager has to be returned.
     * @return The manager for the feature with the given id or <code>null</code> if no manager was
     *         found.
     */
    public IIpsFeatureVersionManager getIpsFeatureVersionManager(String featureId) {
        IIpsFeatureVersionManager[] managers = getIpsFeatureVersionManagers();
        for (IIpsFeatureVersionManager manager : managers) {
            if (manager.getFeatureId().equals(featureId)) {
                return manager;
            }
        }
        return null;
    }

    /**
     * THIS METHOD SHOULD ONLY BE CALLED FROM TEST CASES.
     * 
     * Sets the feature version managers. This method overwrites all feature managers registered via
     * extension points.
     * 
     * @param managers
     */
    public void setFeatureVersionManagers(IIpsFeatureVersionManager[] managers) {
        featureVersionManagers = managers;
    }

    /**
     * @param projectToMigrate The project the migration operation should be returned for.
     * @return A migration operation migrating the content of the given IpsProject to match the
     *         needs of the current version of FaktorIps.
     * @throws CoreException
     */
    public AbstractIpsFeatureMigrationOperation getMigrationOperation(IIpsProject projectToMigrate)
            throws CoreException {

        IIpsFeatureVersionManager[] managers = getIpsFeatureVersionManagers();
        if (isTestMode()) {
            Object obj = getTestAnswerProvider().getAnswer();
            if (obj instanceof IIpsFeatureVersionManager[]) {
                managers = (IIpsFeatureVersionManager[])obj;
            }
        }

        /*
         * Sort the managers to match the required order given by the basedOnFeatureManager-property
         * of the extension-point.
         */
        IpsFeatureVersionManagerSorter sorter = new IpsFeatureVersionManagerSorter();
        managers = sorter.sortForMigartionOrder(managers);
        IpsFeatureMigrationOperation operation = new IpsFeatureMigrationOperation(projectToMigrate);
        for (IIpsFeatureVersionManager manager : managers) {
            operation.addMigrationPath(manager.getMigrationOperations(projectToMigrate));
        }
        return operation;
    }

    /**
     * Returns the persistence manager of the dependency graphs.
     */
    public DependencyGraphPersistenceManager getDependencyGraphPersistenceManager() {
        return dependencyGraphPersistenceManager;
    }

}
