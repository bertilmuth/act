package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;

public class Port<T extends ActionData> implements Named {
	private final String name;
	private final Class<T> type;
	private State<WorkflowState, Token> state;

	Port(String name, Class<T> type) {
		this.name = requireNonNull(name, "name must be non-null!");
		this.type = requireNonNull(type, "type must be non-null!");		
		this.state = createState(name);
	}

	@Override
	public String name() {
		return name;
	}
	
	public Class<T> type() {
		return type;
	}
	
	public Optional<ActionData> firstActionData(WorkflowState state) {
		return allActionData(state).findFirst();
	}
	
	public Stream<ActionData> allActionData(WorkflowState state) {
		return state.tokensIn(this)
			.map(Token::actionData)
			.filter(Optional::isPresent)
			.map(Optional::get);
	}

	public State<WorkflowState, Token> asState() {
		return state;
	}
	
	private State<WorkflowState, Token> createState(String name) {
		return state(name, this::areTokensInPort, 
				inCase(this::firstTokenHasRightType, d -> data(d.state(), firstToken(d.state())), this::markForDeletion));
	}
	
	private boolean areTokensInPort(WorkflowState state) {
		boolean result = state.areTokensIn(this);
		return result;
	}
	
	private Token firstToken(WorkflowState state) {
		return state.firstTokenIn(this).get();
	}
	
	private Optional<Class<?>> typeOfFirstToken(WorkflowState state) {
		return firstToken(state).actionData().map(ActionData::getClass);
	}
	
	private boolean firstTokenHasRightType(Data<WorkflowState, Token> data) {
		Optional<Class<?>> firstTokenType = typeOfFirstToken(data.state());
		return firstTokenType.map(type::isAssignableFrom).orElse(false);
	}
	
	private Data<WorkflowState, Token> markForDeletion(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		Token token = firstToken(state);
		Token tokenMarkedForDeletion = token.replaceActionData(null);
		WorkflowState newState = state.replaceToken(this, token, tokenMarkedForDeletion).state();
		return data(newState);
	}

	@Override
	public String toString() {
		return "Port[" + name + "]";
	}
}
