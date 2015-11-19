/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.abstracttest.matcher;

import org.faktorips.util.message.MessageList;
import org.hamcrest.Matcher;

public class Matchers {

    private Matchers() {
        // avoid default constructor for utility class
    }

    public static Matcher<MessageList> hasMessageCode(final String msgCode) {
        return new MessageCodeMatcher(msgCode, true);
    }

    public static Matcher<MessageList> lacksMessageCode(final String msgCode) {
        return new MessageCodeMatcher(msgCode, false);
    }

    public static Matcher<MessageList> isEmpty() {
        return new EmptyMessageListMatcher();
    }
}
