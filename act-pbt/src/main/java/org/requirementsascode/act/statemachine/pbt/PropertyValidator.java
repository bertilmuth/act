package org.requirementsascode.act.statemachine.pbt;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.List;

import org.requirementsascode.act.core.Change;
import org.requirementsascode.act.core.ChangeHandler;
import org.requirementsascode.act.core.Data;

public class PropertyValidator<S,V> implements ChangeHandler<S, V, V> {
	private List<Property<S, V>> properties;

	private PropertyValidator(List<Property<S, V>> properties) {
		this.properties = requireNonNull(properties, "properties must be non-null!"); 
	}
	
	@SafeVarargs
	public static <S, V> PropertyValidator<S, V> validate(Property<S, V>... properties) {
		return validate(Arrays.asList(properties));
	}
	
	public static <S, V> PropertyValidator<S, V> validate(List<Property<S, V>> properties) {
		return new PropertyValidator<>(properties);
	}
	
	@Override
	public Data<S, V> handleChange(Change<S, V, V> change) {
		properties.forEach(p -> p.validate(change));
		return change.after();
	}
}
