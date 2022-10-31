package org.requirementsascode.act.token;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.token.Token.token;

import java.util.function.BiFunction;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

public class SystemFunction<T extends ActionData, U extends ActionData> implements ActionBehavior{		
	private final Behavior<Workflow, ActionData, ActionData> functionBehavior;

	private SystemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		Behavior<Workflow, T, U> behavior = d -> apply(function, d);
		this.functionBehavior = when(inputClass, behavior);
	}
	
	public static <T extends ActionData, U extends ActionData> SystemFunction<T,U> systemFunction(Class<T> inputClass, BiFunction<Workflow, T, U> function) {
		return new SystemFunction<>(inputClass, function);
	}
	
	@Override
	public Behavior<Workflow, Token, Token> asBehavior(Action owningAction) {
		return this::executeFunction;
	}
	
	private Data<Workflow, Token> executeFunction(Data<Workflow, Token> inputData) {
		Workflow workflow = Workflow.from(inputData);
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<Workflow, ActionData> functionInput = data(workflow, token.actionData().orElse(null));
		Data<Workflow, ActionData> functionOutput = functionBehavior.actOn(functionInput);
		Token tokenAfter = tokenFor(token.node(), functionOutput);
		Data<Workflow, Token> resultWorkflow = workflow.replaceToken(token, tokenAfter);
		return resultWorkflow;
	}
	
	private Data<Workflow, U> apply(BiFunction<Workflow, T, U> function, Data<Workflow, T> input){
		Workflow workflow = Workflow.from(input);
		T inputActionData = input.value().orElse(null);
		U outputActionData = function.apply(workflow, inputActionData);
		return data(workflow, outputActionData);
	}
	
	private Token tokenFor(Node node, Data<Workflow, ActionData> actionData) {
		return token(node, actionData.value().orElse(null));
	}
}
