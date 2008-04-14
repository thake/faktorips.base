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

package org.faktorips.devtools.stdbuilder.policycmpttype;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.codegen.JavaCodeFragmentBuilder;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.builder.DefaultJavaGeneratorForIpsPart;
import org.faktorips.devtools.core.builder.DefaultJavaSourceFileBuilder;
import org.faktorips.devtools.core.builder.JavaSourceFileBuilder;
import org.faktorips.devtools.core.builder.MessageFragment;
import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPartContainer;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptTypeAttribute;
import org.faktorips.devtools.core.model.pctype.IValidationRule;
import org.faktorips.devtools.core.model.valueset.ValueSetType;
import org.faktorips.devtools.stdbuilder.policycmpttype.attribute.GenAttribute;
import org.faktorips.runtime.Message;
import org.faktorips.runtime.MessageList;
import org.faktorips.runtime.MsgReplacementParameter;
import org.faktorips.runtime.ObjectProperty;
import org.faktorips.util.LocalizedStringsSet;

/**
 * Genertor for validation rules.
 * 
 * @author Peter Erzberger
 */
public class GenValidationRule extends DefaultJavaGeneratorForIpsPart {

    
    public GenValidationRule(IIpsObjectPartContainer part, DefaultJavaSourceFileBuilder builder, LocalizedStringsSet stringsSet, boolean generateImplementation) throws CoreException {
        super(part, builder, stringsSet, generateImplementation);
    }

    /**
     * {@inheritDoc}
     */
    protected void generateConstants(JavaCodeFragmentBuilder builder) throws CoreException {
        if(isGeneratingInterface()){
            generateFieldForMsgCode(builder);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void generateMemberVariables(JavaCodeFragmentBuilder builder) throws CoreException {
        //nothing to do
    }

    /**
     * {@inheritDoc}
     */
    protected void generateMethods(JavaCodeFragmentBuilder builder) throws CoreException {
        if(isGeneratingImplementationClass()){
            generateMethodExecRule(builder);
            generateMethodCreateMessageForRule(builder);
        }
    }

    /**
     * Returns the policy component implementation class builder.
     */
//  TODO needs to be removed when the GenPolicyCmptType is introduced
    private PolicyCmptImplClassBuilder getImplClassBuilder() {
        if (getJavaSourceFileBuilder() instanceof PolicyCmptImplClassBuilder) {
            return (PolicyCmptImplClassBuilder)getJavaSourceFileBuilder();
        }
        return null;
    }

    /**
     * Code sample:
     * <pre>
     * [Javadoc]
     *   if ("rules.businessProcess1".equals(businessFunction) || "rules.businessProcess2".equals(businessFunction)) {
     *      //begin-user-code
     *      boolean condition = getA().equals(new Integer(1));
     *      if (condition) {
     *          ml.add(createMessageForRuleARule(String.valueOf(getA()), String.valueOf(getB()), String.valueOf(getHallo())));
     *          return false;
     *      }
     *      return true;
     *      //end-user-code
     *  }
     *  return true;
     * </pre>
     */
    private void generateMethodExecRule(JavaCodeFragmentBuilder builder) throws CoreException {
        IValidationRule rule = (IValidationRule)getIpsPart();
        String parameterBusinessFunction = "businessFunction";
        String javaDoc = getLocalizedText("EXEC_RULE_JAVADOC", rule.getName());
        JavaCodeFragment body = new JavaCodeFragment();
        body.appendln();
        String[] businessFunctions = rule.getBusinessFunctions();
        if(!rule.isAppliedForAllBusinessFunctions()){
            if(businessFunctions.length > 0){
                body.append("if(");
                for (int j = 0; j < businessFunctions.length; j++) {
                    body.append("\"");
                    body.append(businessFunctions[j]);
                    body.append("\"");
                    body.append(".equals(");
                    body.append(parameterBusinessFunction);
                    body.append(")");
                    if(j < businessFunctions.length - 1){
                        body.appendln(" || ");
                    }
                }
                body.append(")");
                body.appendOpenBracket();
            }
        }
        if(!rule.isCheckValueAgainstValueSetRule()) {
            body.appendln("//begin-user-code");
            body.appendln(getLocalizedToDo("EXEC_RULE_IMPLEMENT", rule.getName()));
        }
        
        body.append("if(");
        String[] javaDocAnnotation = JavaSourceFileBuilder.ANNOTATION_RESTRAINED_MODIFIABLE;
        if(rule.isCheckValueAgainstValueSetRule()){
            javaDocAnnotation = JavaSourceFileBuilder.ANNOTATION_GENERATED;
            IPolicyCmptTypeAttribute attr = ((IPolicyCmptType)rule.getIpsObject()).getPolicyCmptTypeAttribute(rule.getValidatedAttributeAt(0));
            body.append('!');
            
            GenAttribute genAttribute = getImplClassBuilder().getGenerator(attr);
            if(attr.getValueSet().getValueSetType().equals(ValueSetType.ENUM)){
                body.append(genAttribute.getMethodNameGetAllowedValuesFor());
            }
            else if(attr.getValueSet().getValueSetType().equals(ValueSetType.RANGE)){
                body.append(genAttribute.getMethodNameGetRangeFor());
            }
            body.append("(");
            body.append(parameterBusinessFunction);
            body.append(").contains(");
            body.append(genAttribute.getGetterMethodName());
            body.append("()))");
        }
        else{
            body.append("true) ");
        }
        body.appendOpenBracket();
        boolean generateToDo = false;
        body.append("ml.add(");
        body.append(getMethodNameCreateMessageForRule(rule));
        MessageFragment msgFrag = MessageFragment.createMessageFragment(rule.getMessageText(), MessageFragment.VALUES_AS_PARAMETER_NAMES);
        body.append('(');
        if(msgFrag.hasParameters()){
            String[] parameterNames = msgFrag.getParameterNames();
            for (int j = 0; j < parameterNames.length; j++) {
                body.append("null");
                generateToDo = true;
                if(j < parameterNames.length - 1){
                    body.append(", ");
                }
            }
        }

        if(rule.isValidatedAttrSpecifiedInSrc()){
            generateToDo = true;
            if(msgFrag.hasParameters()){
                body.append(", ");
            }
            body.append("new ");
            body.appendClassName(ObjectProperty.class);
            body.append("[0]");
        }
        
        body.append("));");
        if (generateToDo) {
            body.append(getLocalizedToDo("EXEC_RULE_COMPLETE_CALL_CREATE_MSG", rule.getName()));
        }
        body.appendln();
        body.appendCloseBracket();
        body.appendln(" return true;");
        if(!rule.isCheckValueAgainstValueSetRule()) {
            body.appendln("//end-user-code");
        }
        if(!rule.isAppliedForAllBusinessFunctions()){
            if(businessFunctions.length > 0){
                body.appendCloseBracket();
                body.appendln(" return true;");
            }
        }
        
        builder.method(java.lang.reflect.Modifier.PROTECTED, Datatype.PRIMITIVE_BOOLEAN.getJavaClassName(),
            getMethodNameExecRule(rule), new String[] { "ml", parameterBusinessFunction },
            new String[] { MessageList.class.getName(), String.class.getName() }, body, javaDoc, javaDocAnnotation);
    }

    /**
     * Code sample:
     * <pre>
     * [Javadoc]
     *   protected Message createMessageForRuleARule(String p0, String p1, String p2) {
     *      ObjectProperty[] objectProperties = new ObjectProperty[] { new ObjectProperty(this, PROPERTY_NAME_A),
     *              new ObjectProperty(this, PROPERTY_NAME_B) };
     *      StringBuffer text = new StringBuffer();
     *      text.append("Check parameters ");
     *      text.append(p0);
     *      text.append(", check if line break works in generated code\n");
     *      text.append(p1);
     *      text.append(" and ");
     *      text.append(p2);
     *      return new Message(MSG_CODE_ARULE, text.toString(), Message.ERROR, objectProperties);
     *  }
     * </pre>
     */
    private void generateMethodCreateMessageForRule(JavaCodeFragmentBuilder builder) throws CoreException {
        IValidationRule rule = (IValidationRule)getIpsPart();
        String localVarObjectProperties = "invalidObjectProperties";
        String localVarReplacementParams = "replacementParameters";
        MessageFragment msgFrag = MessageFragment.createMessageFragment(rule.getMessageText(), MessageFragment.VALUES_AS_PARAMETER_NAMES);

        // determine method parameters (name and type)
        String[] methodParamNames;
        String[] methodParamTypes;
        if (!rule.isValidatedAttrSpecifiedInSrc()) {
            methodParamNames = msgFrag.getParameterNames();
            methodParamTypes = msgFrag.getParameterClasses();
        } else {
            int numberOfMethodParams = msgFrag.getNumberOfParameters() + 1;
            methodParamNames = new String[numberOfMethodParams];
            methodParamTypes = new String[numberOfMethodParams];
            System.arraycopy(msgFrag.getParameterNames(), 0, methodParamNames, 0, msgFrag.getNumberOfParameters());
            System.arraycopy(msgFrag.getParameterClasses(), 0, methodParamTypes, 0, msgFrag.getNumberOfParameters());
            methodParamNames[methodParamNames.length-1] = localVarObjectProperties;
            methodParamTypes[methodParamTypes.length-1] = ObjectProperty.class.getName() + "[]";
        }
        
        // code for objectProperties
        JavaCodeFragment body = new JavaCodeFragment();
        String[] validatedAttributes = rule.getValidatedAttributes();
        if(!rule.isValidatedAttrSpecifiedInSrc()){
            body.append(generateCodeForInvalidObjectProperties(localVarObjectProperties, validatedAttributes));
        }
        // code for replacement parameters
        if (msgFrag.hasParameters()) {
            body.append(generateCodeForMsgReplacementParameters(localVarReplacementParams, msgFrag.getParameterNames()));
        }
        
        // code to construct the message's text
        body.append(msgFrag.getFrag());

        // code to create the message and return it.
        body.append("return new ");
        body.appendClassName(Message.class);
        body.append('(');
        body.append(getFieldNameForMsgCode(rule));
        body.append(", ");
        body.append(msgFrag.getMsgTextExpression());
        body.append(", ");
        body.append(rule.getMessageSeverity().getJavaSourcecode());
        body.append(", ");
        body.append(localVarObjectProperties);
        if (msgFrag.hasParameters()) {
            body.append(", ");
            body.append(localVarReplacementParams);
        }
        body.append(");");

        String javaDoc = getLocalizedText("CREATE_MESSAGE_JAVADOC", rule.getName());
        builder.method(java.lang.reflect.Modifier.PROTECTED, Message.class.getName(),
                getMethodNameCreateMessageForRule(rule), methodParamNames, methodParamTypes, body, javaDoc, JavaSourceFileBuilder.ANNOTATION_GENERATED);
    }
    
    private JavaCodeFragment generateCodeForInvalidObjectProperties(String pObjectProperties, String[] validatedAttributes) throws CoreException {
        JavaCodeFragment code = new JavaCodeFragment();
        if(validatedAttributes.length > 0){
            code.appendClassName(ObjectProperty.class);
            code.append("[] ");
            code.append(pObjectProperties);
            code.append(" = new ");
            code.appendClassName(ObjectProperty.class);
            code.append("[]{");
            for (int j = 0; j < validatedAttributes.length; j++) {
                IPolicyCmptTypeAttribute attr = ((IPolicyCmptType)getIpsPart().getIpsObject()).findPolicyCmptTypeAttribute(validatedAttributes[j], getIpsProject());
                String propertyConstName = getImplClassBuilder().getGenerator(attr).getStaticConstantPropertyName();
                code.append(" new ");
                code.appendClassName(ObjectProperty.class);
                code.append("(this, ");
                code.append(propertyConstName);
                code.append(")");
                if(j < validatedAttributes.length -1){
                    code.append(',');
                }
            }
            code.appendln("};");
        }
        else{
            code.appendClassName(ObjectProperty.class);
            code.append(" ");
            code.append(pObjectProperties);
            code.append(" = new ");
            code.appendClassName(ObjectProperty.class);
            code.appendln("(this);");
        }
        return code;
    }
    
    /**
     * Code sample:
     * <pre>
     *   MsgReplacementParameter[] replacementParameters = new MsgReplacementParameter[] {
     *       new MsgReplacementParameter("maxVs", maxVs),
     *   };
     * 
     * </pre>
     */
    private JavaCodeFragment generateCodeForMsgReplacementParameters(String localVar, String[] parameterNames) {
        JavaCodeFragment code = new JavaCodeFragment();
        // MsgReplacementParameter[] replacementParameters = new MsgReplacementParameter[] {
        code.appendClassName(MsgReplacementParameter.class);
        code.append("[] " + localVar + " = new ");
        code.appendClassName(MsgReplacementParameter.class);
        code.appendln("[] {");

        for (int i = 0; i < parameterNames.length; i++) {

            //     new MsgReplacementParameter("paramName", paramName),
            code.append("new ");
            code.appendClassName(MsgReplacementParameter.class);
            code.append("(");
            code.appendQuoted(parameterNames[i]);
            code.append(", ");
            code.append(parameterNames[i]);
            code.append(")");
            if (i!=parameterNames.length-1) {
                code.append(", ");
            }
            code.appendln();
        }
        
        code.appendln("};");
        return code;
    }

    private IValidationRule getValidationRule(){
        return (IValidationRule)getIpsPart();
    }
    
    private void generateFieldForMsgCode(JavaCodeFragmentBuilder membersBuilder){
        appendLocalizedJavaDoc("FIELD_MSG_CODE", getValidationRule().getName(), membersBuilder);
        membersBuilder.append("public final static ");
        membersBuilder.appendClassName(String.class);
        membersBuilder.append(' ');
        membersBuilder.append(getFieldNameForMsgCode(getValidationRule()));
        membersBuilder.append(" = \"");
        membersBuilder.append(getValidationRule().getMessageCode());
        membersBuilder.appendln("\";");
    }

    public String getFieldNameForMsgCode(IValidationRule rule){
        return getLocalizedText("FIELD_MSG_CODE_NAME", StringUtils.upperCase(rule.getName()));
    }

    private String getMethodNameCreateMessageForRule(IValidationRule rule) {
        return "createMessageForRule" + StringUtils.capitalize(rule.getName());
    }

    private String getMethodNameExecRule(IValidationRule r){
        return "execRule" + StringUtils.capitalize(r.getName());
    }
}
