package org.requirementsascode.act.core;

@FunctionalInterface
public interface HandleChange<S,V> {
	Data<S,V> handleChange(Change<S,V> change);
}
