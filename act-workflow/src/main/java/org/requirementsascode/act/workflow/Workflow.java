package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;
import static org.requirementsascode.act.workflow.WorkflowState.intialWorkflowState;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transitionable;

public class Workflow implements Behavior<WorkflowState, Token, Token>{
	private final WorkflowState initialWorkflowState;
	private final Statemachine<WorkflowState, Token> statemachine;
	
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
	
	public Data<WorkflowState, Token> nextStep(WorkflowState state, ActionData actionData) {
		Data<WorkflowState, Token> tokenizedData = tokenize(state, actionData);
		return actOn(tokenizedData);
	}
	
	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState,Token> inputData) {
		return statemachine.actOn(inputData);
	}
	
	private Data<WorkflowState, Token> tokenize(WorkflowState state, ActionData actionData) {
		return data(state, token(actionData));
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
			Statemachine<WorkflowState, Token> statemachine = statemachineOf(dataBefore);
			Tokens mergedTokens = mergeTokens(datasAfter);
			ActionData actionData = actionDataOfFirstOf(mergedTokens);
			
			WorkflowState state = new WorkflowState(statemachine, mergedTokens, actionData);
			return data(state, null);
		}

		private Statemachine<WorkflowState, Token> statemachineOf(Data<WorkflowState, Token> dataBefore) {
			return dataBefore.state().statemachine();
		}

		private Tokens mergeTokens(List<Data<WorkflowState, Token>> datasAfter) {	
			Map<Node, List<Token>> mergedTokenMap = datasAfter.stream()
				.map(Data::state).map(WorkflowState::tokens)
				.flatMap(tkns -> tkns.asMap().entrySet().stream())
			    .collect(Collectors.toMap(
			        Map.Entry::getKey,
			        Map.Entry::getValue,
			        (v1, v2) -> { 
			        	List<Token> mergedList = Stream.concat(v1.stream(), v2.stream())
			        		.collect(Collectors.toList());
			        	return mergedList; 
			        })
			    );
			
			// Remove all elements that don't have actionData set
			mergedTokenMap.replaceAll((key, value) -> value.stream()
				.filter(t -> t.actionData().isPresent())
			    .collect(Collectors.toList()));
			
			return new Tokens(mergedTokenMap);
		}
		
		private ActionData actionDataOfFirstOf(Tokens tokens) {
			return tokens.asMap().values().stream()
				.flatMap(Collection::stream).findFirst()
				.flatMap(Token::actionData)
				.orElse(null);
		}
	}
}

