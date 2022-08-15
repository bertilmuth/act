package org.requirementsascode.act.statemachine.pbt;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import org.requirementsascode.act.core.Change;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.HandleChange;

public class PropertyValidator<S,V1,V2> implements HandleChange<S, V1, V2> {
	private List<Property<S, V1, V2>> properties;

	private PropertyValidator(List<Property<S, V1, V2>> properties) {
		this.properties = requireNonNull(properties, "properties must be non-null!"); 
	}
	
	@SafeVarargs
	public static <S, V1, V2> PropertyValidator<S, V1, V2> propertyValidator(Property<S, V1, V2>... properties) {
		return propertyValidator(Arrays.asList(properties));
	}
	
	public static <S, V1, V2> PropertyValidator<S, V1, V2> propertyValidator(List<Property<S, V1, V2>> properties) {
		return new PropertyValidator<>(properties);
	}
	
	@Override
	public Data<S, V2> handleChange(Change<S, V1, V2> change) {
		validateProperties(change);
		return change.after();
	}
	
	private void validateProperties(Change<S, V1, V2> change) {
		for (Property<S, V1, V2> property : properties) {
			property.validate(change);
		}
	}
}
