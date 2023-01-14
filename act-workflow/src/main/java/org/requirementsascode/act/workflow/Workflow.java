package org.requirementsascode.act.workflow;

import static java.util.stream.Stream.concat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

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
	private final Flows flows;
	private final Statemachine<WorkflowState, Token> statemachine;
	
	private Workflow(Actions actions, Ports ports, Flows flows, InFlows startFlows) {
		this.flows = flows;
		this.statemachine = statemachineWith(actions, ports, flows, startFlows);		
	}

	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public WorkflowState start(ActionData actionData) {
		WorkflowState initialWorkflowState = new WorkflowState(this, new Tokens(Collections.emptyMap()));
		return nextStep(initialWorkflowState, actionData);
	}
	
	public WorkflowState nextStep(WorkflowState state, ActionData actionData) {
		Data<WorkflowState, Token> tokenizedData = tokenize(state, actionData);
		return actOn(tokenizedData).state();
	}
	
	public Flows dataFlows() {
		return flows;
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
	
	static Workflow create(Actions actions, Ports ports, Flows dataFlows, InFlows inFlows){
		return new Workflow(actions, ports, dataFlows, inFlows);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Statemachine<WorkflowState, Token> statemachineWith(Actions actions, Ports ports, Flows dataFlows,
			InFlows inFlows) {
		
		State[] portStates = ports.asStates().toArray(State[]::new);
		Transitionable[] transitionables = concat(actions.stream(),
				concat(inFlows.stream(), dataFlows.stream()))
			.toArray(Transitionable[]::new);

		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.mergeStrategy(new TokenMergeStrategy())
				.states(portStates)
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
			WorkflowState state = new WorkflowState(Workflow.this, mergedTokens);
			return data(state, null);
		}

		private Tokens mergeTokens(List<Data<WorkflowState, Token>> datasAfter) {	
			Map<Port<?>, List<Token>> mergedTokenMap = datasAfter.stream()
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
	}
}

