package org.requirementsascode.act.core;

public interface HandleChangeWith<S,V> {
	Data<S,V> handleChangeWith(Change<S,V> change);
}
