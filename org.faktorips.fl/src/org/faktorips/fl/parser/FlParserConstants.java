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

/* Generated By:JJTree&JavaCC: Do not edit this line. FlParserConstants.java */
package org.faktorips.fl.parser;

public interface FlParserConstants {

    int EOF = 0;
    int BOOLEAN_LITERAL = 7;
    int INTEGER_LITERAL = 8;
    int DECIMAL_LITERAL = 9;
    int EXPONENT = 10;
    int STRING_LITERAL = 11;
    int MONEY_LITERAL = 12;
    int LETTER = 13;
    int NULL_LITERAL = 14;
    int IDENTIFIER = 15;
    int IDENTIFIER_PART = 16;
    int LETTER_OR_UNDERSCORE = 17;
    int DIGIT = 18;
    int DATE_IDENTIFIER = 19;

    int DEFAULT = 0;

    String[] tokenImage = { "<EOF>", "\" \"", "\"\\t\"", "\"\\n\"", "\"\\r\"", "<token of kind 5>",
            "<token of kind 6>", "<BOOLEAN_LITERAL>", "<INTEGER_LITERAL>", "<DECIMAL_LITERAL>", "<EXPONENT>",
            "<STRING_LITERAL>", "<MONEY_LITERAL>", "<LETTER>", "<NULL_LITERAL>", "<IDENTIFIER>", "<IDENTIFIER_PART>",
            "<LETTER_OR_UNDERSCORE>", "<DIGIT>", "<DATE_IDENTIFIER>", "\"=\"", "\"!=\"", "\"<\"", "\">\"", "\"<=\"",
            "\">=\"", "\"+\"", "\"-\"", "\"*\"", "\"/\"", "\"!\"", "\"(\"", "\")\"", "\";\"", };

}
