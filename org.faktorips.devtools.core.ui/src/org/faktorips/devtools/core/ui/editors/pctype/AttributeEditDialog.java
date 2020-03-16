/*******************************************************************************
 * Copyright (c) Faktor Zehn GmbH. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.ui.editors.pctype;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.exception.CoreRuntimeException;
import org.faktorips.devtools.core.internal.model.SingleEventModification;
import org.faktorips.devtools.core.internal.model.pctype.PolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.ContentChangeEvent;
import org.faktorips.devtools.core.model.IIpsElement;
import org.faktorips.devtools.core.model.ipsobject.IExtensionPropertyDefinition;
import org.faktorips.devtools.core.model.ipsobject.IIpsSrcFile;
import org.faktorips.devtools.core.model.ipsobject.Modifier;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.AttributeType;
import org.faktorips.devtools.core.model.pctype.IPersistentAttributeInfo;
import org.faktorips.devtools.core.model.pctype.IPersistentAttributeInfo.DateTimeMapping;
import org.faktorips.devtools.core.model.pctype.IPersistentTypeInfo;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.pctype.IValidationRule;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptCategory;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeMethod;
import org.faktorips.devtools.core.model.type.IAttribute;
import org.faktorips.devtools.core.model.type.IMethod;
import org.faktorips.devtools.core.model.type.IType;
import org.faktorips.devtools.core.model.valueset.ValueSetType;
import org.faktorips.devtools.core.refactor.IIpsRefactoring;
import org.faktorips.devtools.core.ui.AbstractCompletionProcessor;
import org.faktorips.devtools.core.ui.CompletionUtil;
import org.faktorips.devtools.core.ui.ExtensionPropertyControlFactory;
import org.faktorips.devtools.core.ui.IpsUIPlugin;
import org.faktorips.devtools.core.ui.ValueDatatypeControlFactory;
import org.faktorips.devtools.core.ui.binding.BindingContext;
import org.faktorips.devtools.core.ui.binding.ControlPropertyBinding;
import org.faktorips.devtools.core.ui.binding.PresentationModelObject;
import org.faktorips.devtools.core.ui.controller.EditField;
import org.faktorips.devtools.core.ui.controller.fields.ComboViewerField;
import org.faktorips.devtools.core.ui.controller.fields.EnumField;
import org.faktorips.devtools.core.ui.controller.fields.IntegerField;
import org.faktorips.devtools.core.ui.controller.fields.MessageDecoration;
import org.faktorips.devtools.core.ui.controls.Checkbox;
import org.faktorips.devtools.core.ui.controls.DatatypeRefControl;
import org.faktorips.devtools.core.ui.controls.valuesets.ValueSetControlEditMode;
import org.faktorips.devtools.core.ui.controls.valuesets.ValueSetSpecificationControl;
import org.faktorips.devtools.core.ui.editors.CategoryPmo;
import org.faktorips.devtools.core.ui.editors.IpsPartEditDialog2;
import org.faktorips.devtools.core.ui.editors.pctype.rule.ValidationRuleEditingUI;
import org.faktorips.devtools.core.ui.editors.pctype.rule.ValidationRuleMarkerPMO;
import org.faktorips.devtools.core.ui.editors.pctype.rule.ValidationRuleMarkerUI;
import org.faktorips.devtools.core.ui.editors.productcmpttype.ProductCmptTypeMethodEditDialog;
import org.faktorips.devtools.core.ui.refactor.IpsRefactoringOperation;
import org.faktorips.devtools.core.util.PersistenceUtil;
import org.faktorips.devtools.core.util.QNameUtil;
import org.faktorips.util.memento.Memento;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;

/**
 * Dialog to edit an attribute.
 * 
 * @author Jan Ortmann
 */
public class AttributeEditDialog extends IpsPartEditDialog2 {

    /** the attribute being edited. */
    private final IPolicyCmptTypeAttribute attribute;

    /**
     * Keep track of the content of the name field to be able to determine whether it has changed.
     */
    private final String initialName;

    private IIpsProject ipsProject;

    private Text nameText;

    private EditField<String> defaultValueField;

    private ValueSetSpecificationControl valueSetSpecificationControl;

    private DatatypeRefControl datatypeControl;

    private Label labelDefaultValue;

    private ExtensionPropertyControlFactory extFactory;

    private Group configGroup;

    private Checkbox validationRuleAdded;

    /**
     * Holds controls for defining a validation rule.
     */
    private ValidationRuleEditingUI ruleDefinitionUI = new ValidationRuleEditingUI(getToolkit());
    private ValidationRuleMarkerUI ruleMarkerUI = new ValidationRuleMarkerUI(getToolkit());

    /**
     * Manages a rule. Model is bound to above UI by the {@link BindingContext}.
     */
    private RuleUIModel ruleModel;
    private ValidationRuleMarkerPMO ruleMarkerPMO;

    /**
     * Folder which contains the pages shown by this editor. Used to modify which page is shown.
     */
    private TabFolder tabFolder;

    /**
     * Flag that indicates whether this dialog should startUp with the rule page on top (
     * <code>true</code>) or not.
     */
    private boolean startWithRulePage = false;

    private ValueDatatype currentDatatype;

    private AttributeType currentAttributeType;

    private IntegerField sizeField;

    private IntegerField precisionField;

    private IntegerField scaleField;

    private EnumField<DateTimeMapping> temporalMappingField;

    private Text sqlColumnDefinition;

    private Checkbox uniqueCheckbox;

    private Checkbox nullableCheckbox;

    private Composite defaultEditFieldPlaceholder;

    private MessageDecoration validationRuleAddedDecoration;

    private Composite ruleComposite;

    public AttributeEditDialog(IPolicyCmptTypeAttribute attribute, Shell parentShell) {
        super(attribute, parentShell, Messages.AttributeEditDialog_title, true);
        this.attribute = attribute;
        initialName = attribute.getName();
        ipsProject = attribute.getIpsProject();
        ruleModel = new RuleUIModel(this.attribute);
        IValidationRule validationRule = attribute.findValueSetRule(ipsProject);
        ruleModel.setValidationRule(validationRule);
        ruleMarkerPMO = ValidationRuleMarkerPMO.createFor(ipsProject, validationRule);
        currentAttributeType = attribute.getAttributeType();
        extFactory = new ExtensionPropertyControlFactory(attribute);
    }

    @Override
    protected Composite createWorkAreaThis(Composite parent) {
        tabFolder = (TabFolder)parent;

        TabItem page = new TabItem(tabFolder, SWT.NONE);
        page.setText(Messages.AttributeEditDialog_propertiesTitle);
        page.setControl(createGeneralPage(tabFolder));

        page = new TabItem(tabFolder, SWT.NONE);
        page.setText(Messages.AttributeEditDialog_valuesetTitle);
        try {
            page.setControl(createValueSetPage(tabFolder));
        } catch (CoreException e) {
            IpsPlugin.logAndShowErrorDialog(e);
        }

        final TabItem validationRulePage = new TabItem(tabFolder, SWT.NONE);
        validationRulePage.setText(Messages.AttributeEditDialog_validationRuleTitle);
        validationRulePage.setControl(createValidationRulePage(tabFolder));
        if (startWithRulePage) {
            tabFolder.setSelection(2);
            ruleDefinitionUI.setFocusToNameField();
        } else {
            tabFolder.setSelection(0);
            nameText.setFocus();
        }

        createPersistenceTabItemIfNecessary(tabFolder);

        tabFolder.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                getBindingContext().updateUI();
            }
        });

        final ContentsChangeListenerForWidget listener = new ContentsChangeListenerForWidget() {
            @Override
            public void contentsChangedAndWidgetIsNotDisposed(ContentChangeEvent event) {
                if (!event.getIpsSrcFile().exists()) {
                    return;
                }
                if (event.getIpsSrcFile().equals(attribute.getIpsObject().getIpsSrcFile())) {
                    updateErrorMessages();
                }
            }
        };
        listener.setWidget(parent);
        attribute.getIpsModel().addChangeListener(listener);

        // initial update of the checkBox "validationRuleAdded"
        updateErrorMessages();
        getBindingContext().updateUI();

        return tabFolder;
    }

    private void updateErrorMessages() {
        IValidationRule rule = attribute.findValueSetRule(ipsProject);
        if (rule != null) {
            MessageList msgList;
            try {
                if (rule.isDeleted()) {
                    validationRuleAddedDecoration.setMessageList(new MessageList());
                    return;
                }
                msgList = rule.validate(rule.getIpsProject());
                msgList = msgList.getMessagesFor(rule, IValidationRule.PROPERTY_CHECK_AGAINST_VALUE_SET_RULE);
                validationRuleAddedDecoration.setMessageList(msgList);
            } catch (CoreException e) {
                IpsPlugin.log(e);
            }
        } else {
            validationRuleAddedDecoration.setMessageList(new MessageList());
        }
        updateDialogErrorMessage();
    }

    private void updateDialogErrorMessage() {
        try {
            MessageList msgList = attribute.validate(ipsProject);
            IValidationRule validationRule = attribute.findValueSetRule(ipsProject);
            if (validationRule != null && !validationRule.isDeleted()) {
                /*
                 * Validate both Attribute and rule, as rule is not a child of the attribute but of
                 * the containing PolicyCmptType.
                 */
                msgList.add(validationRule.validate(ipsProject));
            }
            Message firstMessage = msgList.getFirstMessage(Message.ERROR);
            if (firstMessage != null) {
                setErrorMessage(firstMessage.getText());
            } else {
                setErrorMessage(null);
            }
        } catch (CoreException e) {
            IpsPlugin.log(e);
            setErrorMessage(null);
        }
    }

    /**
     * Brings the page for the validation rule to front.
     */
    protected void showValidationRulePage() {
        startWithRulePage = true;
        if (tabFolder != null) {
            tabFolder.setSelection(3);
        }
    }

    private Control createGeneralPage(TabFolder folder) {
        Composite c = createTabItemComposite(folder, 1, false);
        Group generalGroup = getToolkit().createGroup(c, Messages.AttributeEditDialog_generalGroup);
        generalGroup.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        createGeneralGroupContent(generalGroup);
        if (attribute.isProductRelevant() || attribute.getPolicyCmptType().isConfigurableByProductCmptType()) {
            configGroup = getToolkit().createGroup(c, Messages.AttributeEditDialog_ConfigurationGroup);
            configGroup.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
            createConfigGroupContent();
        }
        return c;
    }

    private void createGeneralGroupContent(Composite c) {
        Composite workArea = getToolkit().createLabelEditColumnComposite(c);
        extFactory.createControls(workArea, getToolkit(), attribute, IExtensionPropertyDefinition.POSITION_TOP);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelName);
        nameText = getToolkit().createText(workArea);
        getBindingContext().bindContent(nameText, attribute, IIpsElement.PROPERTY_NAME);

        getToolkit().createFormLabel(workArea, ""); //$NON-NLS-1$
        final Checkbox cb = new Checkbox(workArea, getToolkit());
        cb.setText(Messages.AttributeEditDialog_overwritesNote);
        getBindingContext().bindContent(cb, attribute, IPolicyCmptTypeAttribute.PROPERTY_OVERWRITES);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelDatatype);
        datatypeControl = getToolkit().createDatatypeRefEdit(attribute.getIpsProject(), workArea);
        datatypeControl.setVoidAllowed(false);
        datatypeControl.setOnlyValueDatatypesAllowed(true);
        datatypeControl.setAbstractAllowed(true);
        getBindingContext().bindContent(datatypeControl, attribute, IAttribute.PROPERTY_DATATYPE);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelModifier);
        Combo modifierCombo = getToolkit().createCombo(workArea);
        getBindingContext().bindContent(modifierCombo, attribute, IAttribute.PROPERTY_MODIFIER, Modifier.class);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelAttrType);
        Combo typeCombo = getToolkit().createCombo(workArea);
        getBindingContext().bindContent(typeCombo, attribute, IPolicyCmptTypeAttribute.PROPERTY_ATTRIBUTE_TYPE,
                AttributeType.class);

        extFactory.createControls(workArea, getToolkit(), attribute, IExtensionPropertyDefinition.POSITION_BOTTOM);
        extFactory.bind(getBindingContext());
    }

    private void createCategoryCombo(Composite workArea) {
        Combo categoryCombo = getToolkit().createCombo(workArea);
        ComboViewerField<IProductCmptCategory> comboViewerField = new ComboViewerField<IProductCmptCategory>(
                categoryCombo, IProductCmptCategory.class);

        CategoryPmo pmo = new CategoryPmo(attribute);
        comboViewerField.setInput(pmo.getCategories());
        comboViewerField.setAllowEmptySelection(true);
        comboViewerField.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                IProductCmptCategory category = (IProductCmptCategory)element;
                return IpsPlugin.getMultiLanguageSupport().getLocalizedLabel(category);
            }
        });

        getBindingContext().bindContent(comboViewerField, pmo, CategoryPmo.PROPERTY_CATEGORY);
    }

    private void recreateConfigGroupContent() {
        if (configGroup == null) {
            return;
        }

        getBindingContext().clearValidationStatus();

        Control[] children = configGroup.getChildren();
        for (Control element : children) {
            element.dispose();
        }

        createConfigGroupContent();
        configGroup.layout();

        getBindingContext().updateUI();
    }

    private void createConfigGroupContent() {
        Composite area = getToolkit().createGridComposite(configGroup, 1, true, false);
        GridData gridData = (GridData)area.getLayoutData();
        gridData.heightHint = 100;

        if (attribute.isChangeable()) {
            Composite labelEditColumnComposite = getToolkit().createLabelEditColumnComposite(area);

            createProductRelevantCheckbox(labelEditColumnComposite);
            createChangingOverTimeCheckbox(labelEditColumnComposite);

            getToolkit().createFormLabel(labelEditColumnComposite, Messages.AttributeEditDialog_labelCategory);
            createCategoryCombo(labelEditColumnComposite);
            return;
        }

        if (attribute.getAttributeType() == AttributeType.CONSTANT) {
            getToolkit().createFormLabel(area, Messages.AttributeEditDialog_ConstantAttributesCantBeConfigured);
            return;
        }

        String productCmptType = QNameUtil.getUnqualifiedName(attribute.getPolicyCmptType().getProductCmptType());
        String checkboxText = NLS.bind(Messages.AttributeEditDialog_attributeComputed, productCmptType);
        Checkbox checkbox = getToolkit().createCheckbox(area, checkboxText);
        getBindingContext().bindContent(checkbox, attribute, IPolicyCmptTypeAttribute.PROPERTY_PRODUCT_RELEVANT);
        getToolkit().createLabel(area, Messages.AttributeEditDialog_methodNote);

        Composite temp = getToolkit().createLabelEditColumnComposite(area);
        Link label = new Link(temp, SWT.NONE);
        label.setText(Messages.AttributeEditDialog_methodLink);
        label.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                editMethodInDialog();
            }
        });
        Text compuationMethodText = getToolkit().createText(temp);
        MethodSignatureCompletionProcessor processor = new MethodSignatureCompletionProcessor(getProductCmptType());
        CompletionUtil.createHandlerForText(compuationMethodText, processor);

        getBindingContext().bindContent(compuationMethodText, attribute,
                IPolicyCmptTypeAttribute.PROPERTY_COMPUTATION_METHOD_SIGNATURE);
        getBindingContext().bindEnabled(compuationMethodText, attribute,
                IPolicyCmptTypeAttribute.PROPERTY_PRODUCT_RELEVANT);

        Link link = new Link(area, SWT.NONE);
        link.setText(Messages.AttributeEditDialog_createNewMethod);
        link.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                createMethodAndOpenDialog();
            }
        });
    }

    private void createProductRelevantCheckbox(Composite parent) {
        Checkbox checkbox = getToolkit().createCheckbox(parent, Messages.AttributeEditDialog_defaultValueConfigured);
        GridData checkboxLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        checkboxLayoutData.horizontalSpan = 2;
        checkbox.setLayoutData(checkboxLayoutData);
        getBindingContext().bindContent(checkbox, attribute, IPolicyCmptTypeAttribute.PROPERTY_PRODUCT_RELEVANT);
        bindRefreshAllowedValueSetTypes(checkbox);
    }

    private void createChangingOverTimeCheckbox(Composite parent) {
        String checkboxLabel = NLS.bind(Messages.AttributeEditDialog_changeOverTimeCheckbox, IpsPlugin.getDefault()
                .getIpsPreferences().getChangesOverTimeNamingConvention().getGenerationConceptNamePlural());
        Checkbox checkbox = getToolkit().createCheckbox(parent, checkboxLabel);
        GridData checkboxLayoutData = new GridData(GridData.FILL_HORIZONTAL);
        checkboxLayoutData.horizontalSpan = 2;
        checkbox.setLayoutData(checkboxLayoutData);
        getBindingContext().bindContent(checkbox, attribute, IPolicyCmptTypeAttribute.PROPERTY_CHANGING_OVER_TIME);
        getBindingContext().bindEnabled(checkbox, attribute, IPolicyCmptTypeAttribute.PROPERTY_PRODUCT_RELEVANT, true);
    }

    private void bindRefreshAllowedValueSetTypes(Checkbox checkbox) {
        getBindingContext().add(new ControlPropertyBinding(checkbox, attribute,
                PolicyCmptTypeAttribute.PROPERTY_PRODUCT_RELEVANT, Boolean.TYPE) {

            @Override
            public void updateUiIfNotDisposed(String nameOfChangedProperty) {
                if (PolicyCmptTypeAttribute.PROPERTY_PRODUCT_RELEVANT.equals(nameOfChangedProperty)) {
                    updateAllowedValueSetTypes();
                }
            }
        });
    }

    private IProductCmptType getProductCmptType() {
        return attribute.getPolicyCmptType().findProductCmptType(ipsProject);
    }

    private void createMethodAndOpenDialog() {
        IProductCmptType productCmptType = findProductCmptTypeAndInformUserIfNotExists();
        if (productCmptType == null) {
            return;
        }
        Memento productCmptTypeMemento = productCmptType.newMemento();
        boolean productCmptTypeDirty = productCmptType.getIpsSrcFile().isDirty();
        Memento policyCmptTypeMemento = attribute.getPolicyCmptType().newMemento();
        IProductCmptTypeMethod newMethod = productCmptType.newFormulaSignature(attribute.getName());
        String qName = attribute.getPolicyCmptType().getQualifiedName();
        newMethod.newParameter(qName, StringUtils.uncapitalize(QNameUtil.getUnqualifiedName(qName)));
        attribute.setComputationMethodSignature(newMethod.getSignatureString());
        newMethod.setDatatype(attribute.getDatatype());
        int rc = openEditMethodDialog(newMethod, productCmptTypeMemento, productCmptTypeDirty);
        if (rc == Window.CANCEL) {
            attribute.getPolicyCmptType().setState(policyCmptTypeMemento);
        }
    }

    private void editMethodInDialog() {
        IProductCmptType productCmptType = findProductCmptTypeAndInformUserIfNotExists();
        if (productCmptType == null) {
            return;
        }
        IProductCmptTypeMethod method = null;
        try {
            method = attribute.findComputationMethod(ipsProject);
            if (method == null) {
                String signature = attribute.getComputationMethodSignature();
                if (StringUtils.isEmpty(signature)) {
                    signature = Messages.AttributeEditDialog_emptyString;
                }
                String text = NLS.bind(Messages.AttributeEditDialog_questionCreateMethod,
                        productCmptType.getQualifiedName(), signature);
                if (MessageDialog.openQuestion(getShell(), Messages.AttributeEditDialog_MethodDoesNotExist, text)) {
                    createMethodAndOpenDialog();
                }
                return;
            }
        } catch (CoreException e) {
            IpsPlugin.logAndShowErrorDialog(e);
            return;
        }
        Memento memento = productCmptType.newMemento();
        openEditMethodDialog(method, memento, method.getIpsSrcFile().isDirty());
    }

    private int openEditMethodDialog(IProductCmptTypeMethod method,
            Memento memento,
            boolean productCmptTypeDirtyBeforeDialog) {

        IProductCmptType productCmptType = method.getProductCmptType();
        IIpsSrcFile file = productCmptType.getIpsSrcFile();
        ProductCmptTypeMethodEditDialog dialog = new ProductCmptTypeMethodEditDialog(method, getShell());
        dialog.open();
        if (dialog.getReturnCode() == Window.CANCEL) {
            productCmptType.setState(memento);
            if (!productCmptTypeDirtyBeforeDialog) {
                file.markAsClean();
            }
        } else {
            attribute.setComputationMethodSignature(method.getSignatureString());
            if (!productCmptTypeDirtyBeforeDialog) {
                try {
                    file.save(true, null);
                } catch (CoreException e) {
                    IpsPlugin.logAndShowErrorDialog(e);
                }
            }
        }
        return dialog.getReturnCode();
    }

    private IProductCmptType findProductCmptTypeAndInformUserIfNotExists() {
        IPolicyCmptType policyCmptType = attribute.getPolicyCmptType();
        IProductCmptType productCmptType = null;
        productCmptType = policyCmptType.findProductCmptType(ipsProject);
        if (productCmptType == null) {
            String text = NLS.bind(Messages.AttributeEditDialog_TypeCantBeFound, policyCmptType.getProductCmptType());
            MessageDialog.openInformation(getShell(), Messages.AttributeEditDialog_Info, text);
        }
        return productCmptType;
    }

    private Control createValueSetPage(TabFolder folder) throws CoreException {
        Composite pageControl = createTabItemComposite(folder, 1, false);

        Composite valueSetWorkArea = getToolkit().createLabelEditColumnComposite(pageControl);
        labelDefaultValue = getToolkit().createLabel(valueSetWorkArea, Messages.AttributeEditDialog_labelDefaultValue);

        defaultEditFieldPlaceholder = getToolkit().createComposite(valueSetWorkArea);
        defaultEditFieldPlaceholder.setLayout(getToolkit().createNoMarginGridLayout(1, true));
        defaultEditFieldPlaceholder.setLayoutData(new GridData(GridData.FILL_BOTH));

        List<ValueSetType> valueSetTypes = attribute.getAllowedValueSetTypes(attribute.getIpsProject());
        valueSetSpecificationControl = new ValueSetSpecificationControl(pageControl, getToolkit(), getBindingContext(),
                attribute, valueSetTypes, ValueSetControlEditMode.ALL_KIND_OF_SETS);

        updateDefaultAndValueSet();

        Object layoutData = valueSetSpecificationControl.getLayoutData();
        if (layoutData instanceof GridData) {
            // set the minimum height to show at least the maximum size of the selected
            // ValueSetEditControl
            GridData gd = (GridData)layoutData;
            gd.heightHint = 300;
        }

        adjustLabelWidth();
        return pageControl;
    }

    private void adjustLabelWidth() {
        if (valueSetSpecificationControl == null) {
            return;
        }
        /*
         * sets the label width of the value set control label, so the control will be horizontal
         * aligned to the default value text the offset of 7 is calculated by the corresponding
         * composites horizontal spacing and margins
         */
        int widthDefaultLabel = labelDefaultValue.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
        int widthValueSetTypeLabel = valueSetSpecificationControl.getPreferredLabelWidth();
        if (widthDefaultLabel > widthValueSetTypeLabel) {
            valueSetSpecificationControl.setLabelWidthHint(widthDefaultLabel);
        } else {
            Object ld = labelDefaultValue.getLayoutData();
            if (ld instanceof GridData) {
                ((GridData)ld).widthHint = widthValueSetTypeLabel;
                labelDefaultValue.setLayoutData(ld);
            }
        }
    }

    private void createDefaultValueEditField(Composite workArea) {
        ValueDatatypeControlFactory datatypeCtrlFactory = IpsUIPlugin.getDefault()
                .getValueDatatypeControlFactory(currentDatatype);
        defaultValueField = datatypeCtrlFactory.createEditField(getToolkit(), workArea, currentDatatype,
                attribute.getValueSet(), ipsProject);
        adjustLabelWidth();
        getBindingContext().bindContent(defaultValueField, attribute, IAttribute.PROPERTY_DEFAULT_VALUE);

        defaultEditFieldPlaceholder.layout();
        // Relayout parent, so new default value controls are displayed correctly
        defaultEditFieldPlaceholder.getParent().getParent().layout();
    }

    @Override
    protected void contentsChangedInternal(ContentChangeEvent event) {
        super.contentsChangedInternal(event);
        try {
            if (attribute.getAttributeType() != currentAttributeType) {
                currentAttributeType = attribute.getAttributeType();
                recreateConfigGroupContent();
            }

            if (isOverwriteEvent(event)) {
                final IPolicyCmptTypeAttribute overwrittenAttribute = (IPolicyCmptTypeAttribute)attribute
                        .findOverwrittenAttribute(ipsProject);
                if (overwrittenAttribute != null) {
                    IpsPlugin.getDefault().getIpsModel().executeModificationsWithSingleEvent(
                            new SingleEventModification<Object>(attribute.getIpsSrcFile()) {

                                @Override
                                protected boolean execute() throws CoreException {
                                    attribute.setDatatype(overwrittenAttribute.getDatatype());
                                    attribute.setModifier(overwrittenAttribute.getModifier());
                                    attribute.setValueSetConfiguredByProduct(overwrittenAttribute.isProductRelevant());
                                    attribute.setAttributeType(overwrittenAttribute.getAttributeType());
                                    attribute.setValueSetCopy(overwrittenAttribute.getValueSet());
                                    attribute.setCategory(overwrittenAttribute.getCategory());
                                    return true;
                                }
                            });
                }
            }

            updateDefaultAndValueSet();
        } catch (CoreException e) {
            throw new CoreRuntimeException(e);
        }

    }

    private void updateDefaultAndValueSet() {
        ValueDatatype newDatatype = attribute.findDatatype(ipsProject);
        boolean enabled = newDatatype != null;
        if (currentDatatype != null && currentDatatype.equals(newDatatype)) {
            return;
        }
        currentDatatype = newDatatype;
        if (defaultValueField != null) {
            getBindingContext().removeBindings(defaultValueField.getControl());
        }
        if (defaultEditFieldPlaceholder != null && !defaultEditFieldPlaceholder.isDisposed()) {
            disposeChildrenOf(defaultEditFieldPlaceholder);
            createDefaultValueEditField(defaultEditFieldPlaceholder);
        }
        updateAllowedValueSetTypes();
        enableValueFieldAndValueSetControl(enabled);
    }

    private void disposeChildrenOf(Composite composite) {
        for (Control control : composite.getChildren()) {
            control.dispose();
        }
    }

    private void enableValueFieldAndValueSetControl(boolean enabled) {
        if (defaultValueField != null) {
            defaultValueField.getControl().setEnabled(enabled);
        }
        if (valueSetSpecificationControl != null) {
            valueSetSpecificationControl
                    .setEditMode(attribute.isProductRelevant() ? ValueSetControlEditMode.ALL_KIND_OF_SETS
                            : ValueSetControlEditMode.ONLY_NONE_ABSTRACT_SETS);
            valueSetSpecificationControl.setDataChangeable(enabled);
        }
    }

    private boolean isOverwriteEvent(ContentChangeEvent event) {
        return event.isAffected(attribute) && attribute.isOverwrite()
                && event.isPropertyAffected(IAttribute.PROPERTY_OVERWRITES);
    }

    private void updateAllowedValueSetTypes() {
        ValueSetType currentValueSetType = valueSetSpecificationControl.getValueSetType();
        try {
            valueSetSpecificationControl
                    .setAllowedValueSetTypes(attribute.getAllowedValueSetTypes(attribute.getIpsProject()));
        } catch (CoreException e) {
            IpsPlugin.log(e);
            valueSetSpecificationControl.setAllowedValueSetTypes(new ArrayList<ValueSetType>());
        }
        if (currentValueSetType != null) {
            // if the previous selction was a valid selection use this one as new selection in drop
            // down, otherwise the default (first one) is selected
            valueSetSpecificationControl.setValueSetType(currentValueSetType);
        }
    }

    private Control createValidationRulePage(TabFolder folder) {
        Composite workArea = createTabItemComposite(folder, 1, false);

        validationRuleAdded = getToolkit().createCheckbox(workArea,
                Messages.AttributeEditDialog_labelActivateValidationRule);
        validationRuleAddedDecoration = getToolkit().createMessageDecoration(validationRuleAdded);
        validationRuleAdded.setToolTipText(Messages.AttributeEditDialog_tooltipActivateValidationRule);

        ruleComposite = getToolkit().createGridComposite(workArea, 1, false, false);

        ruleDefinitionUI.initUI(ruleComposite);
        ruleModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (ruleDefinitionUI.isUiInitialized()
                        && RuleUIModel.PROPERTY_VALIDATION_RULE.equals(evt.getPropertyName())) {
                    IValidationRule newRule = (IValidationRule)evt.getNewValue();
                    rebindTo(newRule);
                }
            }
        });

        if (ruleMarkerUI.isMarkerEnumsEnabled(ipsProject)) {
            ruleMarkerUI.setTableVisibleLines(5);
            ruleMarkerUI.createUI(ruleComposite, ruleMarkerPMO);
        }
        bindEnablement();

        // initialize ruleDefintionUI state.
        ruleModel.fireRuleChange();

        return workArea;
    }

    /**
     * By activating/deactivating a validation rule for an attribute, not only can the rule be
     * <code>null</code>, but also new new rule instances can be created repeatedly. To keep all UI
     * elements bound to the correct rule instance, it has to be re-bound in that case.
     * 
     * SW 10.12.2014: Note that this is a workaround. A cleaner solution would be to introduce a
     * rule-PMO that is bound to the UI and hides the fact that the rule may be replaced or deleted.
     * See private/sw/3572_2 for a work-in-progress of this idea.
     */
    private void rebindTo(IValidationRule newRule) {
        ruleDefinitionUI.removeBindingsFromContext(getBindingContext());
        if (newRule != null) {
            if (allowMarkerEditing()) {
                ruleMarkerPMO.setRule(newRule);
                ruleMarkerUI.getMarkerTable().setInput(ruleMarkerPMO.getItems());
            }
            ruleDefinitionUI.bindFields(newRule, getBindingContext());
        }
        getBindingContext().updateUI();
    }

    private boolean allowMarkerEditing() {
        return ruleMarkerUI.isMarkerEnumsEnabled(ipsProject) && ruleMarkerUI.hasMarkerTable();
    }

    private void bindEnablement() {
        if (validationRuleAdded != null) {
            getBindingContext().bindContent(validationRuleAdded.getButton(), ruleModel, RuleUIModel.PROPERTY_ENABLED);
        }
        getBindingContext()
                .add(new ControlPropertyBinding(ruleComposite, ruleModel, RuleUIModel.PROPERTY_ENABLED, null) {
                    @Override
                    public void updateUiIfNotDisposed(String nameOfChangedProperty) {
                        if (nameOfChangedProperty == null || nameOfChangedProperty.equals(getPropertyName())) {
                            getToolkit().setDataChangeable(getControl(), ruleModel.isEnabled());
                        }
                    }
                });

        if (allowMarkerEditing()) {
            Table markerTable = ruleMarkerUI.getMarkerTableControl();
            getBindingContext().bindEnabled(markerTable, ruleModel, RuleUIModel.PROPERTY_ENABLED);
        }
    }

    private void createPersistenceTabItemIfNecessary(TabFolder tabFolder) {
        if (!ipsProject.getReadOnlyProperties().isPersistenceSupportEnabled()) {
            return;
        }
        final TabItem persistencePage = new TabItem(tabFolder, SWT.NONE);
        persistencePage.setText(Messages.AttributeEditDialog_labelPersistence);

        Composite c = createTabItemComposite(tabFolder, 1, false);
        persistencePage.setControl(c);

        Composite checkComposite = getToolkit().createGridComposite(c, 1, true, false);
        Checkbox checkTransient = getToolkit().createCheckbox(checkComposite,
                Messages.AttributeEditDialog_labelAttributeIsTransient);
        getBindingContext().bindContent(checkTransient, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TRANSIENT);

        final Group group = getToolkit().createGroup(checkComposite,
                Messages.AttributeEditDialog_labelPersistentProperties);
        Composite workArea = getToolkit().createLabelEditColumnComposite(group);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelColumnName);
        Text columnNameText = getToolkit().createText(workArea);
        getBindingContext().bindContent(columnNameText, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TABLE_COLUMN_NAME);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelUnique);
        uniqueCheckbox = getToolkit().createCheckbox(workArea);
        getBindingContext().bindContent(uniqueCheckbox, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TABLE_COLUMN_UNIQE);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelNullable);
        nullableCheckbox = getToolkit().createCheckbox(workArea);
        getBindingContext().bindContent(nullableCheckbox, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TABLE_COLUMN_NULLABLE);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelColumnSize);
        sizeField = new IntegerField(getToolkit().createText(workArea));
        getBindingContext().bindContent(sizeField, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TABLE_COLUMN_SIZE);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelPrecision);
        precisionField = new IntegerField(getToolkit().createText(workArea));
        getBindingContext().bindContent(precisionField, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TABLE_COLUMN_PRECISION);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelColumnScale);
        scaleField = new IntegerField(getToolkit().createText(workArea));
        getBindingContext().bindContent(scaleField, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TABLE_COLUMN_SCALE);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelTemporalType);
        Combo temporalMappingCombo = getToolkit().createCombo(workArea);
        temporalMappingField = new EnumField<DateTimeMapping>(temporalMappingCombo, DateTimeMapping.class);
        getBindingContext().bindContent(temporalMappingField, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TEMPORAL_MAPPING);

        getToolkit().createFormLabel(workArea, Messages.AttributeEditDialog_labelSqlColumnDefinition);
        sqlColumnDefinition = getToolkit().createText(workArea);
        getBindingContext().bindContent(sqlColumnDefinition, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_SQL_COLUMN_DEFINITION);

        addConverterText(workArea);
        addIndexText(workArea);

        // disable all tab page controls if policy component type shouldn't persist
        getBindingContext().add(new ControlPropertyBinding(c, attribute.getPolicyCmptType().getPersistenceTypeInfo(),
                "enabled", Boolean.TYPE) { //$NON-NLS-1$
            @Override
            public void updateUiIfNotDisposed(String nameOfChangedProperty) {
                if (!isPersistentEnabled()) {
                    getToolkit().setDataChangeable(persistencePage.getControl(), false);
                    return;
                }
                if (attribute.getPersistenceAttributeInfo().isTransient()) {
                    getToolkit().setDataChangeable(group, false);
                    return;
                }
                getToolkit().setDataChangeable(persistencePage.getControl(),
                        ((IPersistentTypeInfo)getObject()).isEnabled());
            }
        });

        // disable property controls if attribute is marked as transient
        getBindingContext().add(new ControlPropertyBinding(c, attribute.getPersistenceAttributeInfo(),
                IPersistentAttributeInfo.PROPERTY_TRANSIENT, Boolean.TYPE) {
            @Override
            public void updateUiIfNotDisposed(String nameOfChangedProperty) {
                if (!isPersistentEnabled()) {
                    getToolkit().setDataChangeable(group, false);
                    return;
                }
                if (attribute.getPersistenceAttributeInfo().isTransient()) {
                    getToolkit().setDataChangeable(group, false);
                    return;
                }
                enableOrDisableDatatypeDependingControls();
            }

        });
        // datatype depending enabled or disabled controls
        getBindingContext().add(new ControlPropertyBinding(c, attribute, IAttribute.PROPERTY_DATATYPE, String.class) {
            @Override
            public void updateUiIfNotDisposed(String nameOfChangedProperty) {
                boolean enabled = isPersistentEnabled();
                if (!enabled) {
                    getToolkit().setDataChangeable(group, false);
                    return;
                }
                if (attribute.getPersistenceAttributeInfo().isTransient()) {
                    getToolkit().setDataChangeable(group, false);
                    return;
                }
                if (enabled) {
                    enableOrDisableDatatypeDependingControls();
                }
            }
        });

    }

    private void addConverterText(Composite workArea) {
        addText(workArea, isPersistenceProviderSupportingConverters(),
                Messages.AttributeEditDialog_labelDatatypeConverterClass,
                IPersistentAttributeInfo.PROPERTY_CONVERTER_QUALIFIED_CLASS_NAME);
    }

    private void addIndexText(Composite workArea) {
        addText(workArea, isPersistenceProviderSupportingIndex(), Messages.AttributeEditDialog_labelIndexName,
                IPersistentAttributeInfo.PROPERTY_INDEX_NAME);
    }

    private void addText(Composite workArea, boolean supportedByProvider, String labelText, String bindingProperty) {
        getToolkit().createFormLabel(workArea, labelText);
        Text textField = getToolkit().createText(workArea);
        if (supportedByProvider) {
            getBindingContext().bindContent(textField, attribute.getPersistenceAttributeInfo(), bindingProperty);
        } else {
            textField.setEnabled(false);
            textField.setText(Messages.AttributeEditDialog_textNotSupportedByPersistenceProvider);
        }
    }

    private boolean isPersistenceProviderSupportingIndex() {
        return ipsProject.getIpsArtefactBuilderSet().getPersistenceProvider() == null
                || ipsProject.getIpsArtefactBuilderSet().getPersistenceProvider().isSupportingIndex();
    }

    private boolean isPersistenceProviderSupportingConverters() {
        return ipsProject.getIpsArtefactBuilderSet().getPersistenceProvider() != null
                && ipsProject.getIpsArtefactBuilderSet().getPersistenceProvider().isSupportingConverters();
    }

    private boolean isPersistentEnabled() {
        if (!isDataChangeable()) {
            return false;
        }
        return attribute.getPolicyCmptType().isPersistentEnabled();
    }

    private void enableOrDisableDatatypeDependingControls() {
        ValueDatatype datatype = attribute.findDatatype(ipsProject);

        boolean hasDecimalPlaces = PersistenceUtil.isSupportingDecimalPlaces(datatype);
        boolean hasLength = PersistenceUtil.isSupportingLenght(datatype);
        boolean needsTemporalType = PersistenceUtil.isSupportingTemporalType(datatype);
        boolean canBeNullable = true;
        boolean canBeUnique = true;

        // if a column definition is given, then all properties are specified using the sql
        // definition
        if (StringUtils.isNotEmpty(sqlColumnDefinition.getText())) {
            hasDecimalPlaces = false;
            hasLength = false;
            needsTemporalType = false;
            canBeNullable = false;
            canBeUnique = false;
        }

        getToolkit().setDataChangeable(nullableCheckbox, canBeNullable);
        getToolkit().setDataChangeable(uniqueCheckbox, canBeUnique);
        getToolkit().setDataChangeable(precisionField.getControl(), hasDecimalPlaces);
        getToolkit().setDataChangeable(scaleField.getControl(), hasDecimalPlaces);
        getToolkit().setDataChangeable(sizeField.getControl(), hasLength);
        getToolkit().setDataChangeable(temporalMappingField.getControl(), needsTemporalType);
    }

    @Override
    protected void okPressed() {
        if (IpsPlugin.getDefault().getIpsPreferences().isRefactoringModeDirect()) {
            String newName = attribute.getName();
            if (!(newName.equals(initialName))) {
                applyRenameRefactoring(newName);
            }
        }
        super.okPressed();
    }

    private void applyRenameRefactoring(String newName) {
        // First, reset the initial name as otherwise the error 'names must not equal' will occur
        attribute.setName(initialName);

        IIpsRefactoring ipsRenameRefactoring = IpsPlugin.getIpsRefactoringFactory().createRenameRefactoring(attribute,
                newName, null, false);
        IpsRefactoringOperation refactoringOperation = new IpsRefactoringOperation(ipsRenameRefactoring, getShell());
        refactoringOperation.runDirectExecution();
    }

    private static class MethodSignatureCompletionProcessor extends AbstractCompletionProcessor {

        private final IType type;

        public MethodSignatureCompletionProcessor(IType type) {
            super(type == null ? null : type.getIpsProject());
            this.type = type;
            setComputeProposalForEmptyPrefix(true);
        }

        @Override
        protected void doComputeCompletionProposals(String prefix, int documentOffset, List<ICompletionProposal> result)
                throws Exception {

            if (type == null) {
                return;
            }
            List<IMethod> methods = type.getMethods();
            for (IMethod method : methods) {
                if (method.getSignatureString().startsWith(prefix)) {
                    addToResult(result, method, documentOffset);
                }
            }
        }

        private void addToResult(List<ICompletionProposal> result, IMethod method, int documentOffset) {
            String name = method.getSignatureString();
            String localizedDescription = IpsPlugin.getMultiLanguageSupport().getLocalizedDescription(method);
            CompletionProposal proposal = new CompletionProposal(name, 0, documentOffset, name.length(),
                    IpsUIPlugin.getImageHandling().getImage(method), name, null, localizedDescription);
            result.add(proposal);
        }

    }

    public static class RuleUIModel extends PresentationModelObject {

        public static final String PROPERTY_ENABLED = "enabled"; //$NON-NLS-1$
        public static final String PROPERTY_VALIDATION_RULE = "validationRule"; //$NON-NLS-1$

        private IValidationRule rule;
        private IPolicyCmptTypeAttribute attribute;

        public RuleUIModel(IPolicyCmptTypeAttribute attribute) {
            this.attribute = attribute;
        }

        public void setValidationRule(IValidationRule r) {
            IValidationRule oldRule = rule;
            boolean oldEnablement = isEnabled();
            rule = r;
            /*
             * This notification order is crucial! First inform about enablement change, then about
             * data change. This way controls, whose enabled state is dependent on the data rather
             * than the enablement, will be activated/de-activated correctly.
             */
            notifyListeners(new PropertyChangeEvent(this, PROPERTY_ENABLED, oldEnablement, isEnabled()));
            notifyListeners(new PropertyChangeEvent(this, PROPERTY_VALIDATION_RULE, oldRule, rule));
        }

        public void fireRuleChange() {
            notifyListeners(new PropertyChangeEvent(this, PROPERTY_VALIDATION_RULE, rule, rule));
        }

        public IValidationRule getValidationRule() {
            return rule;
        }

        public void setEnabled(boolean enabled) {
            if (enabled) {
                setValidationRule(attribute.createValueSetRule());
            } else {
                rule.delete();
                setValidationRule(null);
            }
        }

        public boolean isEnabled() {
            return rule != null;
        }
    }

}
