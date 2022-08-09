package org.requirementsascode.act.core;

@FunctionalInterface
public interface HandleChange<S,V> {
	void handleChange(Data<S,V> inputData, Data<S,V> outputData);
}
