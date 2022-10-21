package org.requirementsascode.act.token.function;

import static org.requirementsascode.act.statemachine.StatemachineApi.when;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.token.ActionData;
import org.requirementsascode.act.token.Workflow;

public class SystemFunction{	
	public static <T extends ActionData, U extends ActionData> Behavior<Workflow, ActionData, ActionData> systemFunction(Class<T> inputClass, Behavior<Workflow, T, U> function) {
		return when(inputClass, function);
	}
}
