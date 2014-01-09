/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3
 * and if and when this source code belongs to the faktorips-runtime or faktorips-valuetype
 * component under the terms of the LGPL Lesser General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.runtime;

import org.faktorips.runtime.DummyTocEntryFactory.DummyTypedTocEntryObject;
import org.faktorips.runtime.internal.RuntimeObject;
import org.faktorips.runtime.internal.toc.CustomTocEntryObject;
import org.faktorips.runtime.internal.toc.ITocEntryFactory;
import org.faktorips.runtime.internal.toc.TocEntryObject;
import org.w3c.dom.Element;

public class DummyTocEntryFactory implements ITocEntryFactory<DummyTypedTocEntryObject> {

    public DummyTypedTocEntryObject createFromXml(Element entryElement) {
        String ipsObjectName = entryElement.getAttribute(TocEntryObject.PROPERTY_IPS_OBJECT_QNAME);
        return new DummyTypedTocEntryObject(ipsObjectName);
    }

    public String getXmlTag() {
        return DummyTypedTocEntryObject.DUMMY_RUNTIME_OBJECT;
    }

    public static class DummyRuntimeObject extends RuntimeObject {

    }

    public static class DummyTypedTocEntryObject extends CustomTocEntryObject<DummyRuntimeObject> {

        static final String DUMMY_RUNTIME_OBJECT = "DummyRuntimeObject";

        public DummyTypedTocEntryObject(String ipsObjectQualifiedName) {
            super(ipsObjectQualifiedName, "", "");
        }

        @Override
        public DummyRuntimeObject createRuntimeObject(IRuntimeRepository repository) {
            return new DummyRuntimeObject();
        }

        @Override
        public Class<DummyRuntimeObject> getRuntimeObjectClass() {
            return DummyRuntimeObject.class;
        }

        @Override
        public String getIpsObjectTypeId() {
            return "ProductCmpt";
        }

        @Override
        protected String getXmlElementTag() {
            return DUMMY_RUNTIME_OBJECT;
        }

    }
}
