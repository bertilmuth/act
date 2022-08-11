package org.requirementsascode.act.core;

public interface HandleChange<S,V1,V2> {
	Data<S,V2> handleChange(Change<S,V1,V2> change);
}
