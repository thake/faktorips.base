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

package org.faktorips.devtools.core.model.ipsobject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.faktorips.devtools.core.IpsPlugin;
import org.faktorips.devtools.core.internal.model.ipsproject.IpsPackageFragment;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragment;
import org.faktorips.devtools.core.model.ipsproject.IIpsPackageFragmentRoot;
import org.faktorips.util.ArgumentCheck;

/**
 * Combines the qualified name and ips object type.
 * 
 * @author Jan Ortmann
 */
public class QualifiedNameType implements Serializable{

    private static final long serialVersionUID = -5891585006868536302L;
    
    private String qualifiedName;
    private transient IpsObjectType type;
    private transient int hashCode;
    
    /**
     * Returns the qualified name type for he given path. 
     * 
     * @param pathToFile a relative path to an ips src file, e.g. base/motor/MotorPolicy.ipspct
     * @return The qualified name type
     * 
     * @throws IllegalArgumentException if the path can't be parsed to a qualified name type
     */
    public final static QualifiedNameType newQualifedNameType(String pathToFile) {
        int index = pathToFile.lastIndexOf('.');
        if (index==-1 || index==pathToFile.length()-1) {
            throw new IllegalArgumentException("Path " + pathToFile + " can't be parsed to a qualified name type."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        IpsObjectType type = IpsObjectType.getTypeForExtension(pathToFile.substring(index+1));
        if (type==null) {
            throw new IllegalArgumentException("Path " + pathToFile + " does not specifiy an ips object type."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String qName = pathToFile.substring(0, index).replace(IPath.SEPARATOR, IpsPackageFragment.SEPARATOR);
        if (qName.equals("")) { //$NON-NLS-1$
            throw new IllegalArgumentException("Path " + pathToFile + " does not specifiy a qualified name."); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return new QualifiedNameType(qName, type);
    }
    
    /**
     * @param name
     * @param type
     */
    public QualifiedNameType(String name, IpsObjectType type) {
        ArgumentCheck.notNull(name, this);
        ArgumentCheck.notNull(type, this);
        this.qualifiedName = name;
        this.type = type;
        calculateHashCode();
    }

    /**
     * Returns the qualified name.
     */
    public String getName() {
    	return qualifiedName;
    }

    /**
     * Returns the ips object type.
     */
    public IpsObjectType getIpsObjectType(){
        return type;
    }

    /**
     * Returns the package name part of the qualified name.
     */
    public String getPackageName() {
        int index = qualifiedName.lastIndexOf('.');
        if (index==-1) {
            return ""; //$NON-NLS-1$
        }
        return qualifiedName.substring(0, index);
    }

    /**
     * Returns the unqualified name.
     */
    public String getUnqualifiedName() {
        int index = qualifiedName.lastIndexOf('.');
        if (index==-1) {
            return qualifiedName;
        }
        if (index==qualifiedName.length()-1) {
            return ""; //$NON-NLS-1$
        }
        return qualifiedName.substring(index+1);
    }
    
    /**
     * Transforms this qualified name part into an IPath.
     * E.g.: mycompany.motor.MotorPolicy of type PolicyCmptType becomes mycompany/motor/MotorPolicy.ipspct
     */
    public IPath toPath() {
        return new Path(qualifiedName.replace(IIpsPackageFragment.SEPARATOR, IPath.SEPARATOR)
            + '.' + type.getFileExtension());
    }
    
    /**
     * Returns the name for files in that an ips object with this qualified name type is stored.
     * E.g.: for "mycompany.motor.MotorPolicy" of type PolicyCmptType the method returns "MotorPolicy.ipspct"
     */
    public String getFileName() {
        return type.getFileName(getUnqualifiedName());
    }
    
    public IIpsObject findIpsObject(IIpsPackageFragmentRoot root) throws CoreException{
        if(root == null) {
            return null;
        }
        return root.findIpsObject(type, qualifiedName);
    }
    
    private void calculateHashCode(){
        int result = 17;
        result = result*37 + qualifiedName.hashCode();
        result = result*37 + type.hashCode();
        hashCode = result;
    }
    
    /**
     * {@inheritDoc}
     */
    public int hashCode() {
        return hashCode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if(obj instanceof QualifiedNameType){
            QualifiedNameType other = (QualifiedNameType)obj;
            return type.equals(other.type) && qualifiedName.equals(other.qualifiedName);
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        return type + ": " + qualifiedName; //$NON-NLS-1$
    }

    /**
     * @serialData the default serialization is called followed by the IpsObjectType name
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
        s.writeObject(type.getName());
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        String typeName = (String)s.readObject();
        type = IpsPlugin.getDefault().getIpsModel().getIpsObjectType(typeName);
        if (type == null) {
            throw new IllegalStateException(
                    "Unable to deserialize this qualified name type because the IpsObjectType could not be resolved: "
                            + type);
        }
        calculateHashCode();
    }
}
