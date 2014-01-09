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

package org.faktorips.devtools.core.model;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.core.model.ipsobject.QualifiedNameType;
import org.junit.Before;
import org.junit.Test;

public class DatatypeDependencyTest {

    private QualifiedNameType source;
    private DatatypeDependency dependency;

    @Before
    public void setUp() {
        source = new QualifiedNameType("a.b.c", IpsObjectType.POLICY_CMPT_TYPE);
        dependency = new DatatypeDependency(source, "a.b.e");
    }

    @Test
    public void testHashCode() {
        QualifiedNameType source = new QualifiedNameType("a.b.c", IpsObjectType.POLICY_CMPT_TYPE);
        DatatypeDependency dependency = new DatatypeDependency(source, "a.b.e");

        QualifiedNameType source2 = new QualifiedNameType("a.b.c", IpsObjectType.POLICY_CMPT_TYPE);
        DatatypeDependency dependency2 = new DatatypeDependency(source2, "a.b.e");
        assertEquals(dependency.hashCode(), dependency2.hashCode());
    }

    @Test
    public void testGetSource() {
        assertEquals(source, dependency.getSource());
    }

    @Test
    public void testGetTargetAsQualifiedName() {
        assertEquals("a.b.e", dependency.getTargetAsQualifiedName());
    }

    @Test
    public void testGetTarget() {
        assertEquals("a.b.e", dependency.getTarget());
    }

    @Test
    public void testGetType() {
        assertEquals(DependencyType.DATATYPE, dependency.getType());
    }

    @Test
    public void testEqualsObject() {
        QualifiedNameType source = new QualifiedNameType("a.b.c", IpsObjectType.POLICY_CMPT_TYPE);
        DatatypeDependency dependency = new DatatypeDependency(source, "a.b.e");

        QualifiedNameType source2 = new QualifiedNameType("a.b.c", IpsObjectType.POLICY_CMPT_TYPE);
        DatatypeDependency dependency2 = new DatatypeDependency(source2, "a.b.e");
        assertEquals(dependency, dependency2);
    }

    @Test
    public void testSerializable() throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(10);
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(dependency);

        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        DatatypeDependency dependency = (DatatypeDependency)ois.readObject();
        assertEquals(this.dependency, dependency);
    }

    @Test
    public void testToString() {
        QualifiedNameType source = new QualifiedNameType("a.b.c", IpsObjectType.POLICY_CMPT_TYPE);
        DatatypeDependency dependency = new DatatypeDependency(source, "a.b.e");
        assertEquals("(PolicyCmptType: a.b.c -> a.b.e, type: datatype dependency)", dependency.toString());
    }
}
