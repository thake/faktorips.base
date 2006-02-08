package org.faktorips.devtools.stdbuilder;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.faktorips.codegen.JavaCodeFragment;
import org.faktorips.datatype.Datatype;
import org.faktorips.devtools.core.builder.AbstractParameterIdentifierResolver;
import org.faktorips.devtools.core.builder.DefaultBuilderSet;
import org.faktorips.devtools.core.model.IIpsArtefactBuilder;
import org.faktorips.devtools.core.model.IParameterIdentifierResolver;
import org.faktorips.devtools.core.model.IpsObjectType;
import org.faktorips.devtools.core.model.pctype.IAttribute;
import org.faktorips.devtools.core.model.tablestructure.ITableAccessFunction;
import org.faktorips.devtools.stdbuilder.policycmpttype.PolicyCmptImplClassBuilder;
import org.faktorips.devtools.stdbuilder.policycmpttype.PolicyCmptInterfaceBuilder;
import org.faktorips.devtools.stdbuilder.productcmpt.ProductCmptBuilder;
import org.faktorips.devtools.stdbuilder.productcmpt.ProductCmptTocFileUpdateBuilder;
import org.faktorips.devtools.stdbuilder.productcmpttype.ProductCmptGenImplClassBuilder;
import org.faktorips.devtools.stdbuilder.productcmpttype.ProductCmptGenInterfaceBuilder;
import org.faktorips.devtools.stdbuilder.productcmpttype.ProductCmptImplClassBuilder;
import org.faktorips.devtools.stdbuilder.productcmpttype.ProductCmptInterfaceBuilder;
import org.faktorips.devtools.stdbuilder.table.TableImplBuilder;
import org.faktorips.devtools.stdbuilder.table.TableRowBuilder;
import org.faktorips.devtools.stdbuilder.table.TableTocFileUpdateBuilder;
import org.faktorips.fl.CompilationResult;
import org.faktorips.fl.CompilationResultImpl;

/**
 * A IpsArtefactBuilderSet implementation that assembles the standard FaktorIPS artefact builders.
 * 
 * @author Peter Erzberger
 */
public class StandardBuilderSet extends DefaultBuilderSet {

    private IIpsArtefactBuilder[] builders;
    private TableImplBuilder tableImplBuilder;
    private PolicyCmptInterfaceBuilder policyCmptInterfaceBuilder;

    /**
     * Overridden.
     */
    public IIpsArtefactBuilder[] getArtefactBuilders() {
        return builders;
    }

    /**
     * Overridden.
     */
    public boolean isSupportTableAccess() {
        return true;
    }

    /**
     * Overridden.
     */
    public CompilationResult getTableAccessCode(ITableAccessFunction fct, CompilationResult[] argResults) throws CoreException {
        Datatype returnType = fct.getIpsProject().findDatatype(fct.getType());
        JavaCodeFragment code = new JavaCodeFragment();
        CompilationResultImpl result = new CompilationResultImpl(code, returnType);
        code.appendClassName(tableImplBuilder.getQualifiedClassName(fct.getTableStructure().getIpsSrcFile()));
        code.append(".getInstance(");
        code.append(fct.getIpsProject().getCodeToGetTheRuntimeRepository());
        //TODO pk: findRow is not correct in general
        code.append(").findRow(");
        for (int i = 0; i < argResults.length; i++) {
            if (i>0) {
                code.append(", ");
            }
            code.append(argResults[i].getCodeFragment());
            result.addMessages(argResults[i].getMessages());
        }
        code.append(").get");
        code.append(StringUtils.capitalise(fct.findAccessedColumn().getName()));
        code.append("()");
        return result;
    }

    
    public IParameterIdentifierResolver getFlParameterIdentifierResolver() {
        return new AbstractParameterIdentifierResolver(){

            protected String getParameterAttributGetterName(IAttribute attribute, Datatype datatype) {
                return policyCmptInterfaceBuilder.getAttributeGetterMethodName(attribute, datatype);    
            }
            
        };
    }

    public boolean isSupportFlIdentifierResolver() {
        return true;
    }

    /**
     * Instantiates the artefact builders for this set.
     */
    public void initialize() throws CoreException {

        // create policy component type builders
        PolicyCmptImplClassBuilder policyCmptImplClassBuilder = new PolicyCmptImplClassBuilder(
                this, KIND_POLICY_CMPT_IMPL);
        policyCmptInterfaceBuilder = new PolicyCmptInterfaceBuilder(
                this, KIND_POLICY_CMPT_INTERFACE);
        
        
        // create product component type builders
        ProductCmptInterfaceBuilder productCmptInterfaceBuilder = new ProductCmptInterfaceBuilder(this,
                KIND_PRODUCT_CMPT_INTERFACE);
        ProductCmptImplClassBuilder productCmptImplClassBuilder = new ProductCmptImplClassBuilder(this, KIND_PRODUCT_CMPT_IMPL);
        ProductCmptGenInterfaceBuilder productCmptGenInterfaceBuilder = new ProductCmptGenInterfaceBuilder(this, DefaultBuilderSet.KIND_PRODUCT_CMPT_GENERATION_INTERFACE);
        ProductCmptGenImplClassBuilder productCmptGenImplClassBuilder = new ProductCmptGenImplClassBuilder(this, DefaultBuilderSet.KIND_PRODUCT_CMPT_GENERATION_IMPL);

        // product component builders
        ProductCmptBuilder productCmptGenerationImplBuilder = new ProductCmptBuilder(
                this, KIND_PRODUCT_CMPT_GENERATION_IMPL);
        IIpsArtefactBuilder productCmptContentCopyBuilder = new XmlContentFileCopyBuilder(
                IpsObjectType.PRODUCT_CMPT, this, KIND_PRODUCT_CMPT_CONTENT);
        ProductCmptTocFileUpdateBuilder productCmptTocUpdateBuilder = new ProductCmptTocFileUpdateBuilder(
                this, KIND_PRODUCT_CMPT_TOCENTRY);
        
        // table structure builders
        tableImplBuilder = new TableImplBuilder(this, KIND_TABLE_IMPL);
        TableRowBuilder tableRowBuilder = new TableRowBuilder(this, KIND_TABLE_ROW);
        tableImplBuilder.setTableRowBuilder(tableRowBuilder);

        // table content builders
        IIpsArtefactBuilder tableContentCopyBuilder = new XmlContentFileCopyBuilder(
                IpsObjectType.TABLE_CONTENTS, this, KIND_TABLE_CONTENT);
        TableTocFileUpdateBuilder tableContentTocUpdateBuilder = new TableTocFileUpdateBuilder(
                this, KIND_TABLE_TOCENTRY);
        
        // wire up the builders
        
        // policy component type builders
        policyCmptImplClassBuilder.setInterfaceBuilder(policyCmptInterfaceBuilder);
        policyCmptInterfaceBuilder.setProductCmptInterfaceBuilder(productCmptInterfaceBuilder);
        policyCmptInterfaceBuilder.setProductCmptGenInterfaceBuilder(productCmptGenInterfaceBuilder);
        policyCmptImplClassBuilder.setProductCmptGenInterfaceBuilder(productCmptGenInterfaceBuilder);
        policyCmptImplClassBuilder.setProductCmptGenImplBuilder(productCmptGenImplClassBuilder);
        policyCmptImplClassBuilder.setProductCmptImplBuilder(productCmptImplClassBuilder);
        policyCmptImplClassBuilder.setProductCmptInterfaceBuilder(productCmptInterfaceBuilder);
        
        // product component type builders
        productCmptInterfaceBuilder.setPolicyCmptTypeInterfaceBuilder(policyCmptInterfaceBuilder);
        productCmptInterfaceBuilder.setProductCmptGenInterfaceBuilder(productCmptGenInterfaceBuilder);

        productCmptImplClassBuilder.setInterfaceBuilder(productCmptInterfaceBuilder);
        productCmptImplClassBuilder.setProductCmptGenInterfaceBuilder(productCmptGenInterfaceBuilder);
        productCmptImplClassBuilder.setPolicyCmptImplClassBuilder(policyCmptImplClassBuilder);
        
        productCmptGenInterfaceBuilder.setImplementationBuilder(productCmptGenImplClassBuilder);
        productCmptGenInterfaceBuilder.setProductCmptTypeInterfaceBuilder(productCmptInterfaceBuilder);

        productCmptGenImplClassBuilder.setInterfaceBuilder(productCmptGenInterfaceBuilder);
        productCmptGenImplClassBuilder.setProductCmptTypeImplBuilder(productCmptImplClassBuilder);
        productCmptGenImplClassBuilder.setProductCmptTypeInterfaceBuilder(productCmptInterfaceBuilder);
        productCmptGenImplClassBuilder.setPolicyCmptTypeImplBuilder(policyCmptImplClassBuilder);
        
        // product component builders.
        productCmptGenerationImplBuilder.setProductCmptImplBuilder(productCmptImplClassBuilder);
        productCmptGenerationImplBuilder.setProductCmptGenImplBuilder(productCmptGenImplClassBuilder);
        productCmptTocUpdateBuilder.setPolicyCmptTypeInterfaceBuilder(policyCmptInterfaceBuilder);
        productCmptTocUpdateBuilder.setProductCmptTypeImplClassBuilder(productCmptImplClassBuilder);
        productCmptTocUpdateBuilder.setProductCmptBuilder(productCmptGenerationImplBuilder);
        productCmptTocUpdateBuilder.setProductCmptGenImplClassBuilder(productCmptGenImplClassBuilder);
        
        // table builders
        tableContentTocUpdateBuilder.setTableImplBuilder(tableImplBuilder);

        
        builders = new IIpsArtefactBuilder[] { 
                tableImplBuilder, 
                tableRowBuilder,
                productCmptGenInterfaceBuilder,
                productCmptGenImplClassBuilder,
                productCmptInterfaceBuilder, 
                productCmptImplClassBuilder, 
                policyCmptImplClassBuilder,
                policyCmptInterfaceBuilder, 
                productCmptGenerationImplBuilder,
                tableContentCopyBuilder, 
                productCmptContentCopyBuilder,
                productCmptTocUpdateBuilder, 
                tableContentTocUpdateBuilder };
    }
}
