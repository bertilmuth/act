package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;
import static org.requirementsascode.act.workflow.WorkflowState.intialWorkflowState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transitionable;

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
		Data<WorkflowState, ActionData> output = actOn(inputData);
		return nextStep(output.state());
	}

	public Data<WorkflowState, ActionData> nextStep(WorkflowState workflowState) {
		return actOn(data(workflowState, Step.proceed));
	}
	
	@Override
	public Data<WorkflowState, ActionData> actOn(Data<WorkflowState,ActionData> inputData) {
		Data<WorkflowState, Token> output = statemachine.actOn(tokenized(inputData));
		ActionData outputActionData = output.state().actionOutput().orElse(null);
		return data(output.state(), outputActionData);
	}

	private Data<WorkflowState, Token> tokenized(Data<WorkflowState,ActionData> inputData) {
		DefaultNode defaultNode = new DefaultNode(statemachine);
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<WorkflowState, Token> statemachineWith(Actions actions, DataFlows dataFlows, InitialActions initialActions) {
		State[] actionsArray = actions.asStates().toArray(State[]::new);
		Transitionable[] transitionablesArray = concat(initialActions.stream(), dataFlows.stream())
			.toArray(Transitionable[]::new);
		
		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.mergeStrategy(new TokenMergeStrategy())
				.states(actionsArray)
				.transitions(
					transitionablesArray
				)
				.build();
		return statemachine;
	}
	
	private static class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token>{
		@Override
		public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Statemachine<WorkflowState, Token> statemachine = statemachineOf(dataBefore);
			Tokens mergedTokens = mergeTokens(dataBefore, datasAfter);
			ActionData firstActionData = mergedTokens.stream().findFirst().flatMap(Token::actionData).orElse(null);
			WorkflowState state = new WorkflowState(statemachine, mergedTokens, firstActionData);
			return data(state, null);
		}

		private Statemachine<WorkflowState, Token> statemachineOf(Data<WorkflowState, Token> dataBefore) {
			return dataBefore.state().statemachine();
		}

		private Tokens mergeTokens(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {	
			List<Token> tokensAfterList = new ArrayList<>(tokensBeforeList(dataBefore));
			tokensAfterList.addAll(addedTokensList(dataBefore, datasAfter));
			removedTokensList(dataBefore, datasAfter).stream().forEach(tokensAfterList::remove);
			List<Token> updatedTokenList = tokensAfterList.stream().filter(t -> t.actionData().isPresent()).collect(Collectors.toList());
			
			Tokens updatedTokens = new Tokens(updatedTokenList);
			return updatedTokens;
		}

		private List<Token> tokensBeforeList(Data<WorkflowState, Token> dataBefore) {
			return dataBefore.state().tokens().stream().collect(Collectors.toList());
		}

		private List<Token> removedTokensList(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Tokens tokensBefore = dataBefore.state().tokens();

			List<Token> removedTokensList = datasAfter.stream()
				.map(Data::state).map(s -> s.tokens())
				.flatMap(tkns -> TokensDifference.removedTokens(tokensBefore, tkns).stream())
				.collect(Collectors.toList());
			return removedTokensList;
		}

		private List<Token> addedTokensList(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Tokens tokensBefore = dataBefore.state().tokens();

			List<Token> addedTokensList = datasAfter.stream()
				.map(Data::state).map(s -> s.tokens())
				.flatMap(tkns -> TokensDifference.addedTokens(tokensBefore, tkns).stream())
				.collect(Collectors.toList());
			return addedTokensList;
		}	
	}
}

