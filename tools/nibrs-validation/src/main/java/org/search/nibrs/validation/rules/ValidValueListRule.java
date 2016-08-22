package org.search.nibrs.validation.rules;

import java.util.Set;

import org.search.nibrs.common.ValidationTarget;
import org.search.nibrs.model.codes.NIBRSErrorCode;

/**
 * A rule implementation that tests a named property against a valid list of values.
 *
 * @param <T> The class of subjects to which this rule applies
 */
public class ValidValueListRule<T extends ValidationTarget> extends AbstractBeanPropertyRule<T> {
	
	private Set<String> allowedValueSet;
	private boolean nullAllowed;

	public ValidValueListRule(String propertyName, String dataElementIdentifier, Class<T> subjectClass, NIBRSErrorCode errorCode, Set<String> allowedValueSet, boolean nullAllowed) {
		super(propertyName, dataElementIdentifier, subjectClass, errorCode);
		this.allowedValueSet = allowedValueSet;
		this.nullAllowed = nullAllowed;
	}
	public ValidValueListRule(String propertyName, String dataElementIdentifier, Class<T> subjectClass, NIBRSErrorCode errorCode, Set<String> allowedValueSet) {
		this(propertyName, dataElementIdentifier, subjectClass,  errorCode, allowedValueSet, true);
	}

	@Override
	protected boolean propertyViolatesRule(Object value) {
		return (!nullAllowed && value == null) || (value != null && !allowedValueSet.contains(value));
	}

}
