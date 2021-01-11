/*******************************************************************************
 * Copyright (c) Faktor Zehn GmbH - faktorzehn.org
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.model.pctype;

import org.faktorips.devtools.core.model.ipsobject.IIpsObjectPart;

public interface IPersistentTypePartInfo extends IIpsObjectPart {

    /**
     * The name of a property that indicates the index name.
     */
    public static final String PROPERTY_INDEX_NAME = "indexName"; //$NON-NLS-1$

    /**
     * The name of a property that indicates that the association is transient.
     */
    public static final String PROPERTY_TRANSIENT = "transient"; //$NON-NLS-1$

    /**
     * Validation message code to indicate that the index name is invalid.
     */
    public static final String MSGCODE_INDEX_NAME_INVALID = MSGCODE_PREFIX + "IndexNameInvalid"; //$NON-NLS-1$

    /**
     * Returns the name of the index if the index name is set. Returns an empty String if there is
     * no index. Call {@link #isIndexNameDefined()} to know whether there is an index or not.
     */
    public String getIndexName();

    /**
     * Sets the name for an index that is may be defined for this type part. If there is a name for
     * an index the persistence provider would write according information for the database. If you
     * do not have an index for this type part set an empty string to disable the index.
     */
    public void setIndexName(String indexName);

    /**
     * Returns <code>true</code> if the type part has set an index name.
     */
    boolean isIndexNameDefined();

    /**
     * Returns true if the association is transient.
     */
    public boolean isTransient();

    /**
     * Set to <code>true</code> if the type part should be transient. Set to <code>false</code> if
     * the it is not transient and will be persists.
     */
    public void setTransient(boolean transientPart);

}
