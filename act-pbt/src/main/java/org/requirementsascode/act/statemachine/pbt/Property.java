package org.requirementsascode.act.statemachine.pbt;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Change;

public class Property<S,V> {
	private final Predicate<Change<S, V, V>> predicate;
	private final Function<Change<S, V, V>, String> failToString;
	
	private Property(Predicate<Change<S, V, V>> predicate, Function<Change<S, V, V>, String> failToString) {
		this.predicate = requireNonNull(predicate, "predicate must be non-null!"); 
		this.failToString = requireNonNull(failToString, "failToString must be non-null!");	
	}

	public static <S,V> Property<S,V> property(Predicate<Change<S, V, V>> predicate, Function<Change<S, V, V>, String> failToString) {
		return new Property<>(predicate, failToString);
	}

	public void validate(Change<S, V, V> change) {
		if(!predicate.test(change)) {
			String failureString = failToString.apply(change);
			throw new IllegalStateException(failureString);
		}		
	}
}
