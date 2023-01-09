package org.requirementsascode.act.workflow;

import static java.util.stream.Stream.concat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.Collection;
import java.util.Collections;
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
	private final DataFlows dataFlows;
	private final Statemachine<WorkflowState, Token> statemachine;
	private final InitialNode initialNode;
	private final Nodes nodes;
	
	public Workflow(Nodes nodes, DataFlows dataFlows, StartFlows startFlows) {
		this.dataFlows = dataFlows;
		this.statemachine = statemachineWith(nodes, dataFlows, startFlows);		
		this.initialNode = new InitialNode(statemachine);
		this.nodes = createNodes(statemachine, nodes, initialNode);
	}
	
	private Nodes createNodes(Statemachine<WorkflowState, Token> statemachine, Nodes nodes, InitialNode initialNode) {
		List<Node> nodesIncludingInitialNode = Stream.concat(
			Stream.of(initialNode), 
			nodes.stream())
			.collect(Collectors.toList());
		return new Nodes(nodesIncludingInitialNode);
	}

	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public Data<WorkflowState, Token> start(ActionData actionData) {
		WorkflowState initialWorkflowState = new WorkflowState(this, new Tokens(Collections.emptyMap()), null);
		return nextStep(initialWorkflowState, actionData);
	}
	
	public Data<WorkflowState, Token> nextStep(WorkflowState state, ActionData actionData) {
		Data<WorkflowState, Token> tokenizedData = tokenize(state, actionData);
		return actOn(tokenizedData);
	}
	
	public Nodes nodes() {
		return nodes;
	}
	
	public DataFlows dataFlows() {
		return dataFlows;
	}
	
	Statemachine<WorkflowState, Token> statemachine() {
		return statemachine;
	}
	
	@Override
	public Data<WorkflowState, Token> actOn(Data<WorkflowState,Token> inputData) {
		return statemachine.actOn(inputData);
	}
	
	private Data<WorkflowState, Token> tokenize(WorkflowState state, ActionData actionData) {
		return data(state, token(actionData));
	}
	
	static Workflow create(Nodes nodes, DataFlows dataFlows, StartFlows startFlows){
		return new Workflow(nodes, dataFlows, startFlows);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Statemachine<WorkflowState, Token> statemachineWith(Nodes nodes, DataFlows dataFlows,
			StartFlows startFlows) {
		
		State[] nodeStates = nodes.asStates()
			.toArray(State[]::new);
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
	
	private class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token>{
		@Override
		public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Tokens mergedTokens = mergeTokens(datasAfter);
			ActionData actionData = actionDataOfFirstOf(mergedTokens);
			
			WorkflowState state = new WorkflowState(Workflow.this, mergedTokens, actionData);
			return data(state, null);
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

