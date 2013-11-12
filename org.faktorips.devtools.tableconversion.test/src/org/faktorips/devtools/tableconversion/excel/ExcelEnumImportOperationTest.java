/*******************************************************************************
 * Copyright (c) 2005-2012 Faktor Zehn AG und andere.
 * 
 * Alle Rechte vorbehalten.
 * 
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele, Konfigurationen,
 * etc.) duerfen nur unter den Bedingungen der Faktor-Zehn-Community Lizenzvereinbarung - Version
 * 0.1 (vor Gruendung Community) genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 * http://www.faktorzehn.org/fips:lizenz eingesehen werden kann.
 * 
 * Mitwirkende: Faktor Zehn AG - initial API and implementation - http://www.faktorzehn.de
 *******************************************************************************/

package org.faktorips.devtools.tableconversion.excel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.faktorips.devtools.core.model.enums.IEnumAttributeValue;
import org.faktorips.devtools.core.model.enums.IEnumType;
import org.faktorips.devtools.core.model.ipsproject.IIpsProject;
import org.faktorips.devtools.core.model.ipsproject.IIpsProjectProperties;
import org.faktorips.devtools.tableconversion.AbstractTableTest;
import org.faktorips.util.message.MessageList;
import org.junit.Before;
import org.junit.Test;

public class ExcelEnumImportOperationTest extends AbstractTableTest {

    ExcelTableFormat format;
    IIpsProject ipsProject;

    File file;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        ipsProject = newIpsProject("test");
        IIpsProjectProperties props = ipsProject.getProperties();
        String[] datatypes = getColumnDatatypes();
        props.setPredefinedDatatypesUsed(datatypes);
        ipsProject.setProperties(props);

        format = new ExcelTableFormat();
        format.setName("Excel");
        format.setDefaultExtension(".xls");
        format.addValueConverter(new BooleanValueConverter());
        format.addValueConverter(new DecimalValueConverter());
        format.addValueConverter(new DoubleValueConverter());
        format.addValueConverter(new DateValueConverter());
        format.addValueConverter(new GregorianCalendarValueConverter());
        format.addValueConverter(new IntegerValueConverter());
        format.addValueConverter(new LongValueConverter());
        format.addValueConverter(new MoneyValueConverter());
        format.addValueConverter(new StringValueConverter());

        file = new File("enum" + format.getDefaultExtension());
        file.delete();
    }

    @Override
    protected void tearDownExtension() throws Exception {
        file.delete();
    }

    @Test
    public void testImportValid() throws Exception {
        MessageList ml = new MessageList();
        executeImport(ml, true);
        assertTrue(ml.isEmpty());
    }

    @Test
    public void testImportFirstRowContainsNoColumnHeader() throws Exception {
        MessageList ml = new MessageList();
        IEnumType enumType = executeImport(ml, false);
        assertEquals(4, enumType.getEnumValuesCount());
    }

    @Test
    public void testImportFirstRowContainsColumnHeader() throws Exception {
        MessageList ml = new MessageList();
        IEnumType enumType = executeImport(ml, true);
        assertEquals(3, enumType.getEnumValuesCount());
    }

    @Test
    public void testImportInvalid() throws Exception {
        MessageList ml = new MessageList();
        IEnumType enumType = createExternalEnumType();
        enumType.clear();

        createInvalid();

        ExcelEnumImportOperation op = new ExcelEnumImportOperation(enumType, file.getName(), format, "NULL", true, ml,
                true);
        op.run(new NullProgressMonitor());
        assertEquals(7, ml.size());
    }

    private IEnumType createExternalEnumType() throws Exception, CoreException {
        // create ips src file
        IEnumType enumType = createValidEnumTypeWithValues(ipsProject);

        // create enum.xls
        ExcelEnumExportOperation excelEnumExportOperation = new ExcelEnumExportOperation(enumType, file.getName(),
                format, "NULL", true, new MessageList());
        excelEnumExportOperation.run(null);
        return enumType;
    }

    private IEnumType executeImport(MessageList ml, boolean containsHeader) throws Exception, CoreException {
        IEnumType enumType = createExternalEnumType();

        // clear the exported file for reimport (keeping the attributes)
        enumType.clear();

        ExcelEnumImportOperation op = new ExcelEnumImportOperation(enumType, file.getName(), format, "NULL",
                containsHeader, ml, true);
        op.run(new NullProgressMonitor());
        return enumType;
    }

    private void createInvalid() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        sheet.createRow(0); // header
        HSSFRow row1 = sheet.createRow(1);
        HSSFCellStyle dateStyle = wb.createCellStyle();
        dateStyle.setDataFormat((short)27);

        row1.createCell(0).setCellValue("INVALID BOOLEAN");
        row1.createCell(1).setCellValue("INVALID");
        row1.createCell(2).setCellValue("INVALID");
        HSSFCell cell = row1.createCell(3);
        cell.setCellValue("INVALID");
        cell.setCellStyle(dateStyle);
        row1.createCell(4).setCellValue("INVALID");
        row1.createCell(5).setCellValue("INVALID");
        row1.createCell(6).setCellValue("INVALID");
        row1.createCell(7).setCellValue("INVALID not a legal java identifier");

        FileOutputStream fos = new FileOutputStream(file);
        wb.write(fos);
        fos.close();
    }

    @Test
    public void testImportNullCell() throws Exception {
        MessageList ml = new MessageList();
        IEnumType enumType = createValidEnumTypeWithValues(ipsProject);

        // create enum.xls
        ExcelEnumExportOperation excelEnumExportOperation = new ExcelEnumExportOperation(enumType, file.getName(),
                format, "NULL", true, new MessageList());
        excelEnumExportOperation.run(null);
        enumType.clear();

        createEmpty();

        ExcelEnumImportOperation op = new ExcelEnumImportOperation(enumType, file.getName(), format, "NULL", true, ml,
                true);
        op.run(new NullProgressMonitor());
        assertEquals(9, ml.size());
        String[] row2 = new String[] { null, null, null, null, null, null, null, null };
        assertEnumAttributeValues(row2, enumType.getEnumValues().get(0).getEnumAttributeValues());
    }

    private void assertEnumAttributeValues(String[] stringRow, List<IEnumAttributeValue> enumAttributeValues) {
        for (int i = 0; i < stringRow.length; i++) {
            assertEquals(stringRow[i], enumAttributeValues.get(i).getValue().getContentAsString());
        }

    }

    private void createEmpty() throws Exception {
        HSSFWorkbook wb = new HSSFWorkbook();
        HSSFSheet sheet = wb.createSheet();

        sheet.createRow(0); // header
        sheet.createRow(1);

        FileOutputStream fos = new FileOutputStream(file);
        wb.write(fos);
        fos.close();
    }

}
