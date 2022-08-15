package org.requirementsascode.act.statemachine.pbt;

import static java.util.Objects.requireNonNull;

import java.util.function.Function;
import java.util.function.Predicate;

import org.requirementsascode.act.core.Change;

public class Property<S,V1,V2> {
	private final Predicate<Change<S, V1, V2>> predicate;
	private final Function<Change<S, V1, V2>, String> failToString;
	
	private Property(Predicate<Change<S, V1, V2>> predicate, Function<Change<S, V1, V2>, String> failToString) {
		this.predicate = requireNonNull(predicate, "predicate must be non-null!"); 
		this.failToString = requireNonNull(failToString, "failToString must be non-null!");	
	}

	public static <S,V1,V2> Property<S,V1,V2> property(Predicate<Change<S, V1, V2>> predicate, Function<Change<S, V1, V2>, String> failToString) {
		return new Property<>(predicate, failToString);
	}

	public void validate(Change<S, V1, V2> change) {
		if(!predicate.test(change)) {
			String failureString = failToString.apply(change);
			throw new IllegalStateException(failureString);
		}		
	}
}
