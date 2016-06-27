/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/
package org.faktorips.devtools.core.internal.model.valueset;

import static org.faktorips.devtools.core.model.DatatypeUtil.isNonNull;
import static org.faktorips.devtools.core.model.DatatypeUtil.isNullValue;
import static org.faktorips.devtools.core.model.valueset.IRangeValueSet.MSGCODE_LBOUND_GREATER_UBOUND;
import static org.faktorips.devtools.core.model.valueset.IRangeValueSet.MSGCODE_NOT_NUMERIC_DATATYPE;
import static org.faktorips.devtools.core.model.valueset.IRangeValueSet.MSGCODE_STEP_RANGE_MISMATCH;
import static org.faktorips.devtools.core.model.valueset.IRangeValueSet.PROPERTY_LOWERBOUND;
import static org.faktorips.devtools.core.model.valueset.IRangeValueSet.PROPERTY_STEP;
import static org.faktorips.devtools.core.model.valueset.IRangeValueSet.PROPERTY_UPPERBOUND;
import static org.faktorips.devtools.core.model.valueset.IValueSet.MSGCODE_UNKNOWN_DATATYPE;
import static org.faktorips.devtools.core.model.valueset.IValueSet.MSGCODE_VALUE_NOT_PARSABLE;

import org.eclipse.osgi.util.NLS;
import org.faktorips.datatype.NumericDatatype;
import org.faktorips.datatype.ValueDatatype;
import org.faktorips.devtools.core.model.valueset.IValueSetOwner;
import org.faktorips.util.message.Message;
import org.faktorips.util.message.MessageList;
import org.faktorips.util.message.ObjectProperty;

public class RangeValueSetValidator extends AbstractValueSetValidator<RangeValueSet> {

    public RangeValueSetValidator(RangeValueSet valueSet, IValueSetOwner owner, ValueDatatype datatype) {
        super(valueSet, owner, datatype);
    }

    @Override
    public MessageList validate() {
        MessageList messages = new MessageList();

        ObjectProperty parentObjectProperty = new ObjectProperty(getOwner(), IValueSetOwner.PROPERTY_VALUE_SET);
        ObjectProperty lowerBoundProperty = new ObjectProperty(getValueSet(), PROPERTY_LOWERBOUND);
        ObjectProperty upperBoundProperty = new ObjectProperty(getValueSet(), PROPERTY_UPPERBOUND);
        ObjectProperty stepProperty = new ObjectProperty(getValueSet(), PROPERTY_STEP);

        if (getDatatype() == null) {
            String text = Messages.Range_msgUnknownDatatype;
            messages.newError(MSGCODE_UNKNOWN_DATATYPE, text, parentObjectProperty, lowerBoundProperty,
                    upperBoundProperty, stepProperty);
            return messages;
        }
        String lowerBound = getValueSet().getLowerBound();
        String upperBound = getValueSet().getUpperBound();
        String step = getValueSet().getStep();

        ValueDatatype datatypeToValidate = getDatatypeOrWrapperForPrimitivDatatype(getDatatype());

        validateParsable(datatypeToValidate, lowerBound, messages, this, PROPERTY_LOWERBOUND);
        validateParsable(datatypeToValidate, upperBound, messages, this, PROPERTY_UPPERBOUND);
        boolean stepParsable = validateParsable(datatypeToValidate, getValueSet().getStep(), messages, this,
                PROPERTY_STEP);

        NumericDatatype numDatatype = getAndValidateNumericDatatype(datatypeToValidate, messages);

        if (messages.containsErrorMsg()) {
            return messages;
        }

        if (isNonNull(numDatatype, lowerBound, upperBound)) {
            if (datatypeToValidate.compare(lowerBound, upperBound) > 0) {
                String text = Messages.Range_msgLowerboundGreaterUpperbound;
                messages.newError(MSGCODE_LBOUND_GREATER_UBOUND, text, parentObjectProperty, lowerBoundProperty,
                        upperBoundProperty);
                return messages;
            }
        }

        if (isNullValue(numDatatype, lowerBound) && !isNullValue(numDatatype, step)) {
            String msg = Messages.RangeValueSet_msgStepWithLowerNull;
            messages.newError(MSGCODE_STEP_RANGE_MISMATCH, msg, stepProperty, lowerBoundProperty);
        }

        if (stepParsable && isNonNull(numDatatype, upperBound, lowerBound, step)) {
            String range = numDatatype.subtract(upperBound, lowerBound);
            if (!numDatatype.divisibleWithoutRemainder(range, step)) {
                String msg = NLS.bind(Messages.RangeValueSet_msgStepRangeMismatch, new String[] { lowerBound,
                        upperBound, step });
                messages.newError(MSGCODE_STEP_RANGE_MISMATCH, msg, parentObjectProperty, lowerBoundProperty,
                        upperBoundProperty, stepProperty);
            }
        }

        return messages;
    }

    private ValueDatatype getDatatypeOrWrapperForPrimitivDatatype(ValueDatatype datatype) {
        if (datatype.isPrimitive()) {
            // even if the basic datatype is a primitive, null is allowed for upper, lower bound and
            // step.
            return datatype.getWrapperType();
        }
        return datatype;
    }

    private boolean validateParsable(ValueDatatype datatype,
            String value,
            MessageList list,
            Object invalidObject,
            String property) {
        if (!datatype.isParsable(value)) {
            String msg = NLS.bind(Messages.Range_msgPropertyValueNotParsable, value, datatype.getName());
            list.newError(MSGCODE_VALUE_NOT_PARSABLE, msg, invalidObject, property);
            return false;
        }
        return true;
    }

    private NumericDatatype getAndValidateNumericDatatype(ValueDatatype datatype, MessageList list) {
        if (datatype instanceof NumericDatatype) {
            return (NumericDatatype)datatype;
        }

        String text = Messages.RangeValueSet_msgDatatypeNotNumeric;
        list.add(new Message(MSGCODE_NOT_NUMERIC_DATATYPE, text, Message.ERROR, getValueSet()));

        return null;
    }
}