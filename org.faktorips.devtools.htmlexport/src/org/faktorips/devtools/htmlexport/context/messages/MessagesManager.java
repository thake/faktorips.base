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

package org.faktorips.devtools.htmlexport.context.messages;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;

import org.eclipse.core.runtime.IStatus;
import org.faktorips.devtools.core.IpsStatus;
import org.faktorips.devtools.core.model.ipsobject.IpsObjectType;
import org.faktorips.devtools.htmlexport.context.DocumentationContext;
import org.faktorips.util.LocalizedStringsSet;

/**
 * This class manages the Messages for the HtmlExport. This is necessary, because the {@link Locale}
 * of the HtmlExport may not be the {@link Locale} of the workspace. Because of this the eclipse
 * mechanism for localized messages is not useful here.
 * 
 * @author dicker
 */
public final class MessagesManager {

    private final LocalizedStringsSet localizedStringsSet;
    private final DocumentationContext context;
    private final List<String> notFoundMessages;

    public MessagesManager(DocumentationContext context) {
        this.context = context;
        this.notFoundMessages = new ArrayList<String>();
        localizedStringsSet = new LocalizedStringsSet(this.getClass());
    }

    /**
     * 
     * returns a message for the given messageId
     */
    public String getMessage(String messageId) {
        try {
            return localizedStringsSet.getString(messageId, context.getDocumentationLocale());
        } catch (MissingResourceException e) {
            context.addStatus(new IpsStatus(IStatus.INFO, "Message with Id " + messageId + " not found.")); //$NON-NLS-1$ //$NON-NLS-2$
            notFoundMessages.add(messageId);
        }
        return messageId;
    }

    /**
     * returns a message for the given object
     */
    public String getMessage(Object object) {
        if (object instanceof IpsObjectType) {
            IpsObjectType objectType = (IpsObjectType)object;
            String messageId = "IpsObjectType_name" + objectType.getId(); //$NON-NLS-1$
            String message = getMessage(messageId);
            return messageId.equals(message) ? objectType.getDisplayName() : message;
        }
        return object.toString();
    }
}
