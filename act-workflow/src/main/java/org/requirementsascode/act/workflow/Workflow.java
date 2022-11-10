package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.workflow.WorkflowApi.token;
import static org.requirementsascode.act.workflow.WorkflowState.intialWorkflowState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.Flow;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transition;

public class Workflow implements Behavior<WorkflowState, ActionData, ActionData>{
	private final WorkflowState initialState;
	private final Statemachine<WorkflowState, Token> statemachine;
	
	Workflow(Statemachine<WorkflowState, Token> statemachine) {
		this.statemachine = statemachine;
		this.initialState = intialWorkflowState(statemachine);
	}
	
	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public Data<WorkflowState, ActionData> start(ActionData actionData) {
		Data<WorkflowState, ActionData> inputData = data(initialState, actionData);
		Data<WorkflowState, Token> output = statemachine().actOn(tokenized(inputData));
		return nextStep(output.state());
	}

	public Data<WorkflowState, ActionData> nextStep(WorkflowState workflowState) {
		return actOn(data(workflowState, Step.proceed));
	}
	
	@Override
	public Data<WorkflowState, ActionData> actOn(Data<WorkflowState,ActionData> inputData) {
		Data<WorkflowState, Token> output = statemachine().actOn(tokenized(inputData));
		ActionData outputActionData = output.state().actionOutput().orElse(null);
		return data(output.state(), outputActionData);
	}

	private Data<WorkflowState, Token> tokenized(Data<WorkflowState,ActionData> inputData) {
		DefaultNode defaultNode = new DefaultNode(statemachine());
		Token token = token(defaultNode, inputData.value().orElse(null));
		Data<WorkflowState, Token> data = data(inputData.state(), token);
		return data;
	}
	
	static Workflow createInitialWorkflow(Actions actions, DataFlows dataFlows, InitialActions initialActions){
		requireNonNull(actions, "actions must be non-null!");
		requireNonNull(dataFlows, "dataFlows must be non-null!");
		requireNonNull(initialActions, "initialActions must be non-null!");

		Statemachine<WorkflowState, Token> statemachine = statemachineWith(actions, dataFlows, initialActions);		
		return new Workflow(statemachine);
	}
	
	Statemachine<WorkflowState, Token> statemachine() {
		return statemachine;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<WorkflowState, Token> statemachineWith(Actions actions, DataFlows dataFlows, InitialActions initialActions) {
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		Flow[] flowsArray = Stream.concat(initialActions.stream(), dataFlows.stream())
			.toArray(Flow[]::new);
		
		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.mergeStrategy(new TokenMergeStrategy())
				.states(actionsArray)
				.transitions(
					removeTokensWithoutActionData()
				)
				.flows(flowsArray)
				.build();
		return statemachine;
	}
	
	private static Transition<WorkflowState, Token> removeTokensWithoutActionData() {
		return transition(anyState(), anyState(), 
			whenInCase(Token.class, Workflow::hasNoActionData, Workflow::removeToken));
	}

	private static boolean hasNoActionData(Data<WorkflowState, Token> d) {
		return d.value().map(t -> !t.actionData().isPresent()).orElse(false);
	}

	private static Data<WorkflowState, Token> removeToken(Data<WorkflowState, Token> inputData) {
		WorkflowState workflowState = inputData.state();
		Token token = Token.from(inputData).orElseThrow(() -> new IllegalStateException("Token missing!"));
		Data<WorkflowState, Token> resultWorkflowWithRemovedToken = workflowState.removeToken(token);
		return resultWorkflowWithRemovedToken;
	}
	
	private static class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token>{
		@Override
		public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Statemachine<WorkflowState, Token> statemachine = statemachineOf(dataBefore);
			WorkflowState state = new WorkflowState(statemachine, mergeTokens(dataBefore, datasAfter), null);
			return data(state, null);
		}

		private Statemachine<WorkflowState, Token> statemachineOf(Data<WorkflowState, Token> dataBefore) {
			return dataBefore.state().statemachine();
		}

		private Tokens mergeTokens(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {	
			Tokens tokensBefore = dataBefore.state().tokens();
			
			List<Token> addedTokensList = datasAfter.stream()
				.map(Data::state).map(s -> s.tokens())
				.flatMap(tkns -> TokensDifference.addedTokens(tokensBefore, tkns).stream())
				.collect(Collectors.toList());
			List<Token> removedTokensList = datasAfter.stream()
				.map(Data::state).map(s -> s.tokens())
				.flatMap(tkns -> TokensDifference.removedTokens(tokensBefore, tkns).stream())
				.collect(Collectors.toList());
			List<Token> tokensAfterList = new ArrayList<>(tokensBefore.stream().toList());
			tokensAfterList.addAll(addedTokensList);
			removedTokensList.stream().forEach(tokensAfterList::remove);
			
			Tokens updatedTokens = new Tokens(tokensAfterList);

			return updatedTokens;
		}	
	}
}

