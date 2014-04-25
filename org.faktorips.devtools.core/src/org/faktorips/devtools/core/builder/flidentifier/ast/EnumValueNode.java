/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.core.builder.flidentifier.ast;

import org.faktorips.datatype.Datatype;
import org.faktorips.datatype.EnumDatatype;
import org.faktorips.devtools.core.MultiLanguageSupport;
import org.faktorips.devtools.core.util.TextRegion;

/**
 * The enum value node represents the value part of an enum access. It have to follow an
 * {@link EnumClassNode}. The resulting {@link Datatype} will be a {@link EnumDatatype}.
 * 
 * @author dirmeier
 */
public class EnumValueNode extends IdentifierNode {

    private final String enumValueName;

    EnumValueNode(String enumValueName, EnumDatatype datatype, TextRegion textRegion) {
        super(datatype, textRegion);
        this.enumValueName = enumValueName;
    }

    public String getEnumValueName() {
        return enumValueName;
    }

    @Override
    public EnumDatatype getDatatype() {
        return (EnumDatatype)super.getDatatype();
    }

    @Override
    public String getText() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDescription(MultiLanguageSupport multiLanguageSupport) {
        // TODO Auto-generated method stub
        return null;
    }

}
