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

package org.faktorips.devtools.core.internal.model.product;

import java.util.GregorianCalendar;

import org.eclipse.core.runtime.CoreException;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.IpsPluginTest;
import org.faktorips.devtools.core.model.IIpsProject;
import org.faktorips.devtools.core.model.product.IProductCmpt;
import org.faktorips.devtools.core.model.product.IProductCmptNamingStrategy;
import org.faktorips.util.message.MessageList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * 
 * @author Jan Ortmann
 */
public class DateBasedProductCmptNamingStrategyTest extends IpsPluginTest  {

	private IIpsProject ipsProject;
	private DateBasedProductCmptNamingStrategy strategy;
	
	
	/**
	 * {@inheritDoc}
	 */
	protected void setUp() throws Exception {
		super.setUp();
		ipsProject = newIpsProject("TestProject");
		strategy = new DateBasedProductCmptNamingStrategy();
		strategy.setIpsProject(ipsProject);
		strategy.setVersionIdSeparator(" ");
		strategy.setDateFormatPattern("yyyy-MM-dd");
		strategy.setPostfixAllowed(false);
	}

	/*
	 * Test method for 'org.faktorips.devtools.core.internal.model.product.DateBasedProductCmptNamingStrategy.validateVersionId(String)'
	 */
	public void testValidateVersionId() {
		MessageList list = new MessageList();
		list = strategy.validateVersionId("a2006-01-31");
		assertNotNull(list.getMessageByCode(IProductCmptNamingStrategy.MSGCODE_ILLEGAL_VERSION_ID));

		list = strategy.validateVersionId("2006-01-31x");
		assertNotNull(list.getMessageByCode(IProductCmptNamingStrategy.MSGCODE_ILLEGAL_VERSION_ID));

		list = strategy.validateVersionId("2006");
		assertNotNull(list.getMessageByCode(IProductCmptNamingStrategy.MSGCODE_ILLEGAL_VERSION_ID));

		list = strategy.validateVersionId("");
		assertNotNull(list.getMessageByCode(IProductCmptNamingStrategy.MSGCODE_ILLEGAL_VERSION_ID));

		list = strategy.validateVersionId("2006-01-31");
		assertFalse(list.containsErrorMsg());
		
		strategy.setPostfixAllowed(true);
		list = strategy.validateVersionId("2006-01-31a");
		assertFalse(list.containsErrorMsg());
		
		list = strategy.validateVersionId("2006");
		assertNotNull(list.getMessageByCode(IProductCmptNamingStrategy.MSGCODE_ILLEGAL_VERSION_ID));
	}

	/*
	 * Test method for 'org.faktorips.devtools.core.internal.model.product.DateBasedProductCmptNamingStrategy.getNextVersionId(IProductCmpt)'
	 */
	public void testGetNextVersionId() throws CoreException {
		IpsPlugin.getDefault().getIpsPreferences().setWorkingDate(new GregorianCalendar(2006, 0, 31));
		IProductCmpt pc = newProductCmpt(ipsProject, "TestProduct 2005-01-01");
		assertEquals("2006-01-31", strategy.getNextVersionId(pc));
	}

	public void testInitFromXml() {
		Element el = getTestDocument().getDocumentElement();
		strategy.setPostfixAllowed(false);
		strategy.setDateFormatPattern("MM-yyyy");
		strategy.initFromXml(el);
		assertEquals("-", strategy.getVersionIdSeparator());
		assertEquals("yyyy-MM", strategy.getDateFormatPattern());
		assertTrue(strategy.isPostfixAllowed());
		assertFalse(strategy.validateVersionId("2006-12").containsErrorMsg());
		assertFalse(strategy.validateVersionId("2006-12b").containsErrorMsg());
		assertTrue(strategy.validateVersionId("12-2006").containsErrorMsg());
	}

	public void testToXml() {
		Document doc = newDocument();
		Element el = strategy.toXml(doc);
		assertEquals(IProductCmptNamingStrategy.XML_TAG_NAME, el.getNodeName());
		strategy = new DateBasedProductCmptNamingStrategy();
		strategy.initFromXml(el);
		assertEquals(" ", strategy.getVersionIdSeparator());
		assertEquals("yyyy-MM-dd", strategy.getDateFormatPattern());
	}
	
}
