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

public class Workflow implements Behavior<WorkflowState, Token, Token>{
	private final WorkflowState initialWorkflowState;
	private final Statemachine<WorkflowState, Token> statemachine;
	private static final AnyNode anyNode = new AnyNode();
	
	Workflow(Statemachine<WorkflowState, Token> statemachine) {
		this.statemachine = statemachine;
		this.initialWorkflowState = intialWorkflowState(statemachine);
	}
	
	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public Data<WorkflowState, Token> start(ActionData actionData) {
		return nextStep(initialWorkflowState, actionData);
	}
	
	public Data<WorkflowState, Token> nextStep(WorkflowState workflowState, ActionData actionData) {
		Data<WorkflowState, ActionData> inputData = data(workflowState, actionData);
		return actOn(tokenize(inputData));
	}
	
	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState,Token> inputData) {
		return statemachine.actOn(inputData);
	}
	
	private Data<WorkflowState, Token> tokenize(Data<WorkflowState,ActionData> inputData) {
		ActionData actionData = inputData.value().orElse(null);
		return data(inputData.state(), tokenInAnyNode(actionData));
	}

	private Token tokenInAnyNode(ActionData actionData) {
		return token(anyNode, actionData);
	}
	
	static Workflow create(Nodes nodes, DataFlows dataFlows, StartFlows startFlows){
		requireNonNull(nodes, "nodes must be non-null!");
		requireNonNull(dataFlows, "dataFlows must be non-null!");
		requireNonNull(startFlows, "startFlows must be non-null!");

		Statemachine<WorkflowState, Token> statemachine = statemachineWith(nodes, dataFlows, startFlows);		
		return new Workflow(statemachine);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<WorkflowState, Token> statemachineWith(Nodes nodes, DataFlows dataFlows,
			StartFlows startFlows) {
		
		State[] nodeStates = nodes.asStates().toArray(State[]::new);
		Transitionable[] transitionables = concat(startFlows.stream(), dataFlows.stream())
				.toArray(Transitionable[]::new);

		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.mergeStrategy(new TokenMergeStrategy())
				.states(nodeStates)
				.transitions(
					transitionables
				)
				.build();
		return statemachine;
	}
	
	private static class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token>{
		@Override
		public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Tokens mergedTokens = mergeTokens(dataBefore, datasAfter);
			WorkflowState state = new WorkflowState(statemachineOf(dataBefore), mergedTokens, actionDataOfFirstOf(mergedTokens));
			return data(state, null);
		}

		private ActionData actionDataOfFirstOf(Tokens token) {
			return token.streamAsList().findFirst().flatMap(Token::actionData).orElse(null);
		}

		private Statemachine<WorkflowState, Token> statemachineOf(Data<WorkflowState, Token> dataBefore) {
			return dataBefore.state().statemachine();
		}

		private Tokens mergeTokens(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {	
			List<Token> tokensAfterList = new ArrayList<>();
			tokensAfterList.addAll(addedTokensList(dataBefore, datasAfter));
			List<Token> updatedTokenList = tokensAfterList.stream().filter(t -> t.actionData().isPresent()).collect(Collectors.toList());
			
			Tokens updatedTokens = new Tokens(updatedTokenList);
			return updatedTokens;
		}

		private List<Token> addedTokensList(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			List<Token> addedTokensList = datasAfter.stream()
				.map(Data::state).map(s -> s.tokens())
				.flatMap(tkns -> tkns.streamAsList())
				.collect(Collectors.toList());
			return addedTokensList;
		}	
	}
}

