/***************************************************************************************************
 * Copyright (c) 2005-2008 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/f10-org:lizenzen:community eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 * 
 **************************************************************************************************/

package org.faktorips.devtools.stdbuilder.productcmpttype;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.codegen.JavaCodeFragmentBuilder;
import org.faktorips.devtools.core.builder.AbstractProductCmptTypeBuilder;
import org.faktorips.devtools.core.model.ipsproject.IChangesOverTimeNamingConvention;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.pctype.IPolicyCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptType;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAssociation;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeAttribute;
import org.faktorips.devtools.core.model.productcmpttype.IProductCmptTypeMethod;
import org.faktorips.devtools.core.model.productcmpttype.ITableStructureUsage;
import org.faktorips.devtools.stdbuilder.StandardBuilderSet;
import org.faktorips.devtools.stdbuilder.productcmpttype.association.GenProdAssociation;
import org.faktorips.devtools.stdbuilder.productcmpttype.association.GenProdAssociationTo1;
import org.faktorips.devtools.stdbuilder.productcmpttype.association.GenProdAssociationToMany;
import org.faktorips.devtools.stdbuilder.productcmpttype.attribute.GenProdAttribute;
import org.faktorips.devtools.stdbuilder.productcmpttype.method.GenProdMethod;
import org.faktorips.devtools.stdbuilder.productcmpttype.tableusage.GenTableStructureUsage;
import org.faktorips.devtools.stdbuilder.type.GenType;
import org.faktorips.runtime.IllegalRepositoryModificationException;
import org.faktorips.runtime.internal.MethodNames;
import org.faktorips.util.LocalizedStringsSet;

/**
 * A generator for <code>IProductCmptType</code>s. It provides access to generators for attributes, methods and associations of the 
 * product component type. Typically when the generator is created all the generators of its parts are also greated accept the ones
 * in the super type hierarchy. These are created on demand since it is expected that only a few of them will be overridden. It is necessary
 * to provide an own generator instance for those overridden parts in this generator and not to delegate to the generator of the super
 * class since otherwise it would not be possible to determine if code has to be generated with respect to the super type.
 * 
 * @author Peter Erzberger
 */
public class GenProductCmptType extends GenType {

    private final static LocalizedStringsSet LOCALIZED_STRINGS = new LocalizedStringsSet(GenProductCmptType.class);
    
    private Map generatorsByPart = new HashMap();
    private List genProdAttributes = new ArrayList();
    private List genProdAssociations = new ArrayList();
    private List genMethods = new ArrayList();
    private List genTableStructureUsages = new ArrayList();
    
    public GenProductCmptType(IProductCmptType productCmptType, StandardBuilderSet builderSet) throws CoreException {
        super(productCmptType, builderSet, LOCALIZED_STRINGS);
        createGeneratorsForProdAttributes();
        createGeneratorsForProdAssociations();
        createGeneratorsForMethods();
        createGeneratorsForTableStructureUsages();
    }

    public IProductCmptType getProductCmptType() {
        return (IProductCmptType)getType();
    }

    /**
     * {@inheritDoc}
     */
    public String getUnqualifiedClassNameForProductCmptTypeGen(boolean forInterface) throws CoreException {
        if (forInterface) {
            String name = getType().getName() + getAbbreviationForGenerationConcept();
            return getJavaNamingConvention().getPublishedInterfaceName(name);
        }
        String generationAbb = getAbbreviationForGenerationConcept();
        return getJavaNamingConvention().getImplementationClassName(getType().getName() + generationAbb);
    }

    /**
     * Code sample:
     * 
     * <pre>
     * [Javadoc]
     * public IMotorProductGen getMotorProductGen();
     * </pre>
     */
    public void generateMethodGetProductCmptGeneration(IIpsProject ipsProject, JavaCodeFragmentBuilder methodsBuilder)
            throws CoreException {
        IPolicyCmptType pcType = getProductCmptType().findPolicyCmptType(ipsProject);
        String[] replacements = new String[] { getNameForGenerationConcept(), getType().getName(),
                pcType != null ? pcType.getName() : "missing" };
        appendLocalizedJavaDoc("METHOD_GET_PRODUCTCMPT_GENERATION", replacements, methodsBuilder);
        generateSignatureGetProductCmptGeneration(methodsBuilder);
        methodsBuilder.appendln(";");
    }

    /**
     * Returns the name of the method to access the product component generation, e.g.
     * getMotorProductGen
     */
    public String getMethodNameGetProductCmptGeneration() throws CoreException {
        String[] replacements = new String[] { getType().getName(), getAbbreviationForGenerationConcept(),
                getNameForGenerationConcept() };
        return getLocalizedText("METHOD_GET_PRODUCTCMPT_GENERATION_NAME", replacements);
    }

    /**
     * Returns the name of the method to set the product component, e.g. setMotorProduct
     */
    public String getMethodNameSetProductCmpt() throws CoreException {
        return getLocalizedText("METHOD_SET_PRODUCTCMPT_NAME", getType().getName());
    }

    /**
     * Code sample:
     * 
     * <pre>
     * public IMotorProductGen getMotorProductGen()
     * </pre>
     */
    public void generateSignatureGetProductCmptGeneration(JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        String methodName = getMethodNameGetProductCmptGeneration();
        methodsBuilder.signature(java.lang.reflect.Modifier.PUBLIC, getQualifiedClassNameForProductCmptTypeGen(true),
                methodName, new String[0], new String[0]);
    }

    /**
     * Returns the name of the method to access the product component, e.g. getMotorProduct
     */
    public String getMethodNameGetProductCmpt() throws CoreException {
        return getLocalizedText("METHOD_GET_PRODUCTCMPT_NAME", getProductCmptType().getName());
    }

    private void createGeneratorsForProdAttributes() throws CoreException {
        IProductCmptTypeAttribute[] attrs = getProductCmptType().getProductCmptTypeAttributes();
        for (int i = 0; i < attrs.length; i++) {
            if (attrs[i].isValid()) {
                GenProdAttribute generator = new GenProdAttribute(this, attrs[i]);
                genProdAttributes.add(generator);
                generatorsByPart.put(attrs[i], generator);
            }
        }
    }

    private void createGeneratorsForProdAssociations() throws CoreException {
        IProductCmptTypeAssociation[] ass = getProductCmptType().getProductCmptTypeAssociations();
        for (int i = 0; i < ass.length; i++) {
            if (ass[i].isValid()) {
                GenProdAssociation generator = createGenerator(ass[i]);
                genProdAssociations.add(generator);
                generatorsByPart.put(ass[i], generator);
            }
        }
    }

    private void createGeneratorsForMethods() throws CoreException {
        IProductCmptTypeMethod[] methods = getProductCmptType().getProductCmptTypeMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].isValid()) {
                GenProdMethod generator = new GenProdMethod(this, methods[i]);
                genMethods.add(generator);
                generatorsByPart.put(methods[i], generator);
            }
        }
    }
    
    private void createGeneratorsForTableStructureUsages() throws CoreException {
        ITableStructureUsage[] tsus = getProductCmptType().getTableStructureUsages();
        for (int i = 0; i < tsus.length; i++) {
            if (tsus[i].isValid()) {
                GenTableStructureUsage generator = new GenTableStructureUsage(this, tsus[i]);
                genTableStructureUsages.add(generator);
                generatorsByPart.put(tsus[i], generator);
            }
        }
    }

    private GenProdAssociation createGenerator(IProductCmptTypeAssociation association)
            throws CoreException {
        if (association.is1ToMany()) {
            return new GenProdAssociationToMany(this, association);
        }
        return new GenProdAssociationTo1(this, association);
    }

    public GenProdAttribute getGenerator(IProductCmptTypeAttribute a) throws CoreException {
        GenProdAttribute generator = (GenProdAttribute)generatorsByPart.get(a);
        if (generator == null && a.isValid()) {
            //generators for supertype attributes will be created on demand since it is expected that 
            //only a few exit. It will not be checked if the provided attribute is actually a supertype
            //attribute because of performance reasons.
            generator = new GenProdAttribute(this, a);
            genProdAttributes.add(generator);
            generatorsByPart.put(a, generator);
        }

        return generator;
    }

    public GenProdMethod getGenerator(IProductCmptTypeMethod method) throws CoreException {
        GenProdMethod generator = (GenProdMethod)generatorsByPart.get(method);
        if(generator == null && method.isValid()){
            //generators for supertype methods will be created on demand since it is expected that 
            //only a few exit. It will not be checked if the provided method is actually a supertype
            //method because of performance reasons.
            generator = new GenProdMethod(this, method);
            genMethods.add(generator);
            generatorsByPart.put(method, generator);
        }
        return generator;
    }

    public GenProdAssociation getGenerator(IProductCmptTypeAssociation a) throws CoreException {
        GenProdAssociation generator = (GenProdAssociation)generatorsByPart.get(a);
        if(generator == null && a.isValid()){
            //generators for supertype associations will be created on demand since it is expected that 
            //only a few exit. It will not be checked if the provided association is actually a supertype
            //assocation because of performance reasons.
            generator = createGenerator(a);
            genProdAssociations.add(generator);
            generatorsByPart.put(a, generator);
        }
        return generator;
    }

    public GenTableStructureUsage getGenerator(ITableStructureUsage tsu) throws CoreException {
        return (GenTableStructureUsage)generatorsByPart.get(tsu);
    }

    public Iterator getGenProdAttributes() {
        return genProdAttributes.iterator();
    }

    public JavaCodeFragment generateFragmentCheckIfRepositoryIsModifiable() {
        JavaCodeFragment frag = new JavaCodeFragment();
        frag.appendln("if (" + MethodNames.GET_REPOSITORY + "()!=null && !" + MethodNames.GET_REPOSITORY + "()."
                + MethodNames.IS_MODIFIABLE + "()) {");
        frag.append("throw new ");
        frag.appendClassName(IllegalRepositoryModificationException.class);
        frag.appendln("();");
        frag.appendln("}");
        return frag;
    }

    public String getMethodNameGetGeneration() throws CoreException {
        IChangesOverTimeNamingConvention convention = getProductCmptType().getIpsProject()
                .getChangesInTimeNamingConventionForGeneratedCode();
        String generationConceptName = convention.getGenerationConceptNameSingular(getProductCmptType().getIpsProject()
                .getGeneratedJavaSourcecodeDocumentationLanguage());
        String generationConceptAbbreviation = convention.getGenerationConceptNameAbbreviation(getProductCmptType()
                .getIpsProject().getGeneratedJavaSourcecodeDocumentationLanguage());
        return getLocalizedText("METHOD_GET_GENERATION_NAME", new String[] { getProductCmptType().getName(),
                generationConceptAbbreviation, generationConceptName });
    }

    public String getQualifiedClassNameForProductCmptTypeGen(boolean forInterface) throws CoreException {
        return getQualifiedName(forInterface)
                + getChangesInTimeNamingConvention().getGenerationConceptNameAbbreviation(
                        getLanguageUsedInGeneratedSourceCode());
    }

    /**
     * Code sample:
     * 
     * <pre>
     * public IProductGen getGeneration(Calendar effectiveDate)
     * </pre>
     */
    void generateSignatureGetGeneration(JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        String generationInterface = getQualifiedClassNameForProductCmptTypeGen(true);
        String methodName = getMethodNameGetGeneration();
        String paramName = getVarNameEffectiveDate();
        methodsBuilder.signature(Modifier.PUBLIC, generationInterface, methodName, new String[] { paramName },
                new String[] { Calendar.class.getName() });
    }

    /**
     * Returns the variable or parameter name for the effetiveDate.
     * 
     * @param element An isp element that gives access to the ips project.
     * @see org.faktorips.devtools.core.builder.AbstractProductCmptTypeBuilder#getVarNameEffectiveDate
     */
    public String getVarNameEffectiveDate() {
        return AbstractProductCmptTypeBuilder.getVarNameEffectiveDate(getProductCmptType());
    }

    /**
     * Code sample:
     * 
     * <pre>
     * public IMotorProduct getMotorProduct()
     * </pre>
     */
    public void generateSignatureGetProductCmpt(JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        String returnType = getQualifiedName(true);
        String methodName = getMethodNameGetProductCmpt();
        methodsBuilder.signature(Modifier.PUBLIC, returnType, methodName, EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY);
    }

    /**
     * Code sample:
     * 
     * <pre>
     * public void setMotorProduct(IMotorProduct motorProduct, boolean initPropertiesWithConfiguratedDefaults)
     * </pre>
     */
    public void generateSignatureSetProductCmpt(JavaCodeFragmentBuilder methodsBuilder) throws CoreException {
        String methodName = getMethodNameSetProductCmpt();
        String[] paramTypes = new String[] { getQualifiedName(true), "boolean" };
        methodsBuilder.signature(java.lang.reflect.Modifier.PUBLIC, "void", methodName,
                getMethodParamNamesSetProductCmpt(), paramTypes);
    }

    /**
     * Returns the method parameters for the method: setProductCmpt.
     */
    public String[] getMethodParamNamesSetProductCmpt() throws CoreException {
        return new String[] { StringUtils.uncapitalize(getType().getName()), "initPropertiesWithConfiguratedDefaults" };
    }

}
