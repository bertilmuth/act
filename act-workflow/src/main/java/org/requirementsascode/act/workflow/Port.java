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
	
	public Optional<T> firstActionData(WorkflowState state) {
		return actionDatas(state).findFirst();
	}
	
	@SuppressWarnings("unchecked")
	public Stream<T> actionDatas(WorkflowState state) {
		return tokens(state)
			.map(Token::actionData)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(actionData -> (T)actionData);
	}
	
	public Optional<Token> firstToken(WorkflowState state) {
		return tokens(state).findFirst();
	}
	
	public Stream<Token> tokens(WorkflowState state) {
		return state.tokensIn(this);
	}
	
	public Data<WorkflowState, Token> addToken(WorkflowState state, Token token) {
		return state.addToken(this, token);
	}
	
	public Data<WorkflowState,Token> removeFirstToken(Data<WorkflowState,Token> inputData) {
		WorkflowState state = inputData.state();
		return firstToken(state)
			.map(token -> state.removeToken(this, token))
			.orElse(inputData);
	}

	public State<WorkflowState, Token> asState() {
		return state;
	}
	
	private State<WorkflowState, Token> createState(String name) {
		return state(name, this::areTokensInPort, 
				inCase(this::firstTokenHasWrongType, this::markAsDirty));
	}
	
	private boolean firstTokenHasWrongType(Data<WorkflowState, Token> data) {
		Optional<Class<?>> firstTokenType = typeOfFirstToken(data.state());
		boolean tokenHasRightType = firstTokenType.map(type::isAssignableFrom).orElse(false);
		return !tokenHasRightType;
	}
	
	private boolean areTokensInPort(WorkflowState state) {
		return state.areTokensIn(this);
	}
	
	private Optional<Class<?>> typeOfFirstToken(WorkflowState state) {
		return firstToken(state).get().actionData().map(ActionData::getClass);
	}
	
	private Data<WorkflowState, Token> markAsDirty(Data<WorkflowState, Token> inputData) {
		WorkflowState state = inputData.state();
		Token token = firstToken(state).get();
		WorkflowState newState = state.replaceToken(this, token, dirty(token)).state();
		return data(newState);
	}

	private Token dirty(Token token) {
		return token.replaceActionData(null);
	}

	@Override
	public String toString() {
		return "Port[" + name + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Named && 
			name().equals(((Named)obj).name());
	}
	
	@Override
	public int hashCode() {
		return name().hashCode();
	}
}
