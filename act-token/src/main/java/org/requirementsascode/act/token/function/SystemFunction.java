package org.requirementsascode.act.token.function;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.token.Token.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.token.ActionData;
import org.requirementsascode.act.token.Node;
import org.requirementsascode.act.token.Token;
import org.requirementsascode.act.token.Workflow;

public class SystemFunction<T extends ActionData, U extends ActionData>{		
	private Behavior<Workflow, ActionData, ActionData> functionBehavior;

	public SystemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		Behavior<Workflow, T, U> behavior = d -> apply(function, d);
		this.functionBehavior = when(inputClass, behavior);
	}
	
	public static <T extends ActionData, U extends ActionData> SystemFunction<T,U> systemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		return new SystemFunction<>(inputClass, function);
	}
	
	public Behavior<Workflow, ActionData, ActionData> functionBehavior(){
		return functionBehavior;
	}
	
	private Data<Workflow, U> apply(BiFunction<Workflow, T, U> function, Data<Workflow, T> input){
		Workflow workflow = Workflow.from(input);
		T inputActionData = input.value().orElse(null);
		U outputActionData = function.apply(workflow, inputActionData);
		return data(workflow, outputActionData);
	}
	
	Data<Workflow, Token> executeFunction(Data<Workflow, Token> inputData) {
		Workflow workflow = Workflow.from(inputData);
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, ActionData> functionInput = data(workflow, token.actionData());
		Data<Workflow, ActionData> functionOutput = functionBehavior.actOn(functionInput);
		Token tokenAfter = tokenFor(token.node(), functionOutput);

		return workflow.replaceToken(token, tokenAfter);
	}
	
	private static Token tokenFor(Node node, Data<Workflow, ActionData> actionData) {
		return token(node, actionData.value().orElse(null));
	}
}
