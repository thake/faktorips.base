/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

package org.faktorips.devtools.core.internal.model;

import java.util.Arrays;
import java.util.List;

import org.faktorips.devtools.core.IpsPluginTest;
import org.faktorips.devtools.core.TestEnumType;

/**
 * 
 * @author Peter Erzberger
 */
public class DynamicEnumDatatypeTest extends IpsPluginTest {

	private DynamicEnumDatatype dataType;
	
	public void setUp() throws Exception{
		super.setUp();
		IpsProject project = (IpsProject)newIpsProject("Testproject");
		dataType = newDefinedEnumDatatype(project, new Class[]{TestEnumType.class})[0];
	}
	/*
	 * Test method for 'org.faktorips.devtools.core.internal.model.DynamicEnumDatatype.getAllValueIds()'
	 */
	public void testGetAllValueIds() {
		List allValues = Arrays.asList(dataType.getAllValueIds(false));
		assertTrue(allValues.contains(TestEnumType.FIRSTVALUE.getId()));
		assertTrue(allValues.contains(TestEnumType.SECONDVALUE.getId()));
		assertTrue(allValues.contains(TestEnumType.THIRDVALUE.getId()));

		allValues = Arrays.asList(dataType.getAllValueIds(true));
		assertTrue(allValues.contains(null));
	}


	/*
	 * Test method for 'org.faktorips.devtools.core.internal.model.DynamicEnumDatatype.isSupportingNames()'
	 */
	public void testIsSupportingNames() {
		assertTrue(dataType.isSupportingNames());
	}

	/*
	 * Test method for 'org.faktorips.devtools.core.internal.model.DynamicEnumDatatype.getValueName(String)'
	 */
	public void testGetValueName() {
		assertEquals(TestEnumType.FIRSTVALUE.getName(), dataType.getValueName(TestEnumType.FIRSTVALUE.getId()));
		assertEquals(TestEnumType.SECONDVALUE.getName(), dataType.getValueName(TestEnumType.SECONDVALUE.getId()));
		assertEquals(TestEnumType.THIRDVALUE.getName(), dataType.getValueName(TestEnumType.THIRDVALUE.getId()));
	}

}
