package org.requirementsascode.act.core;

@FunctionalInterface
public interface HandleChange<S,T> {
	void handleChange(Data<S,T> inputData, Data<S,T> outputData);
}
