package org.faktorips.devtools.core.model.pctype;

import org.faktorips.devtools.core.model.IIpsObjectPart;

/**
 * Represents a validation rule. Instances of this interface just say that a
 * rule with a specified name, message e.g. exist. The actual rule condition is
 * part of the concept of this interface. It has to be implemented within the
 * generated source code.
 */
public interface IValidationRule extends IIpsObjectPart {

	public final static String PROPERTY_BUSINESS_FUNCTIONS = "businessFunctions"; //$NON-NLS-1$

	public final static String PROPERTY_APPLIED_IN_ALL_FUNCTIONS = "appliedInAllBusinessFunctions"; //$NON-NLS-1$

	public final static String PROPERTY_MESSAGE_TEXT = "messageText"; //$NON-NLS-1$

	public final static String PROPERTY_MESSAGE_CODE = "messageCode"; //$NON-NLS-1$

	public final static String PROPERTY_MESSAGE_SEVERITY = "messageSeverity"; //$NON-NLS-1$

	public final static String PROPERTY_VALIDATED_ATTRIBUTES = "validatedAttributes"; //$NON-NLS-1$

	public final static String PROPERTY_VALIDATIED_ATTR_SPECIFIED_IN_SRC = "validatedAttrSpecifiedInSrc"; //$NON-NLS-1$

	public final static String PROPERTY_VALIDATES_ATTR_VALUE_AGAINST_VALUESET = "validatesAttrValueAgainstValueSet"; //$NON-NLS-1$
	/**
	 * Sets the rules name.
	 */
	public void setName(String newName);

	/**
	 * Returns the message text that is presented to the user, if the rule
	 * determines that a given policy is invalid.
	 */
	public String getMessageText();

	/**
	 * Sets the message text, that is presented to the user, if the rule
	 * determines that a given policy is invalid.
	 */
	public void setMessageText(String newText);

	/**
	 * Returns the code of the message that is generated, if the rule determines
	 * that a given policy is invalid.
	 */
	public String getMessageCode();

	/**
	 * Sets the code of the message that is generated, if the rule determines
	 * that a given policy is invalid.
	 */
	public void setMessageCode(String newCode);

	/**
	 * Returns the message's severity.
	 */
	public MessageSeverity getMessageSeverity();

	/**
	 * Sets the message's severity.
	 */
	public void setMessageSeverity(MessageSeverity newSeverity);

	/**
	 * Returns the qualified name of the business functions in that the rule is
	 * applied.
	 */
	public String[] getBusinessFunctions();

	/**
	 * Returns the number of business functions in which the rule is applied.
	 */
	public int getNumOfBusinessFunctions();

	/**
	 * Sets the qualified name of the business functions in that the rule is
	 * applied.
	 */
	public void setBusinessFunctions(String[] functionNames);

	/**
	 * Returns the business function name at the given index.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	public String getBusinessFunction(int index);

	/**
	 * Sets the business function at the specified index.
	 * 
	 * @throws IndexOutOfBoundsException
	 *             if the index is invalid.
	 */
	public void setBusinessFunctions(int index, String functionName);

	/**
	 * Adds a new business function with the given name.
	 */
	public void addBusinessFunction(String functionName);

	/**
	 * Removes the business functions at the specified indices.
	 */
	public void removeBusinessFunction(int index);

	/**
	 * Returns true if the rule is applied in all business functions.
	 */
	public boolean isAppliedInAllBusinessFunctions();

	/**
	 * Sets if the rule should be applied in all business functions.
	 */
	public void setAppliedInAllBusinessFunctions(boolean newValue);

	/**
	 * Adds the name of an attribute that is evaluated in the rules condition.
	 * 
	 * @param attributeName
	 *            the name of the validated attribute
	 * @return returns the name of the validated attribute
	 */
	public String addValidatedAttribute(String attributeName);

	/**
	 * Sets the name of a validated attribute at the indexed position. Throws a
	 * runtime exception is the position does not exist.
	 * 
	 * @param index
	 *            the position where the provided attribute name is stored
	 * @param attributeName
	 *            the validated attributes name
	 */
	public void setValidatedAttributeAt(int index, String attributeName);

	/**
	 * Removes the validated attribute name at the given position.
	 * 
	 * @param index
	 *            the array position at which the attribute name is stored
	 */
	public void removeValidatedAttribute(int index);

	/**
	 * Returns the names of the attributes that are part of the rule's
	 * condition.
	 */
	public String[] getValidatedAttributes();

	/**
	 * Returns the validated attribute at the indexed position.
	 * 
	 * @param index
	 *            the position of the requested validated attribute
	 * @return the validated attribute name
	 */
	public String getValidatedAttributeAt(int index);

	/**
	 * Returns if the validated attributes are specified in the source code
	 * instead of this rule.
	 */
	public boolean isValidatedAttrSpecifiedInSrc();

	/**
	 * Sets the specifiedInSourceCode flag that indicates if the validated
	 * attributes are specified in the source code instead of this rule.
	 * 
	 * @param specifiedInSourceCode
	 */
	public void setValidatedAttrSpecifiedInSrc(
			boolean validatedAttrSpecifiedInSrc);
	
	/**
	 * Returns <code>true</code> if this rule is a default rule for validating the value of an attribute
	 * against the value set defined for the attribute. Default means, that the rule is not a manually 
	 * build rule - it is an automatically created rule. The creation of this rule has to be allowed by 
	 * the user.
	 */
	public boolean isCheckValueAgainstValueSetRule();

	/**
	 * Sets the isAttributeValueValidationRule flag that indicates whether this 
	 * is a default rule or not.
	 * 
	 * @see #isCheckValueAgainstValueSetRule()
	 */
	public void setCheckValueAgainstValueSetRule(boolean isCheckValueAgainstValueSetRule);
	
}
