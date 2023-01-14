package org.requirementsascode.act.workflow;

import static java.util.stream.Stream.concat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.Collections;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transitionable;

public class Workflow implements Behavior<WorkflowState, Token, Token>{
	private final Flows flows;
	private final Statemachine<WorkflowState, Token> statemachine;
	
	private Workflow(Actions actions, Ports ports, Flows flows, InFlows inFlows) {
		this.flows = flows;
		this.statemachine = statemachineWith(actions, ports, flows, inFlows);		
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
	private Statemachine<WorkflowState, Token> statemachineWith(Actions actions, Ports ports, Flows flows,
			InFlows inFlows) {
		
		Stream<State<WorkflowState, Token>> portStates = ports.asStates();
		Stream<State<WorkflowState, Token>> actionPortsStates = actions.stream()
			.map(Action::flow)
			.map(Flow::inputPorts)
			.map(Ports::asState);
		State[] allStates = Stream.concat(portStates, actionPortsStates).toArray(State[]::new);
		
		Stream<Transitionable<WorkflowState, Token>> regularFlows = concat(actions.stream(), flows.stream());
		Transitionable[] transitionables = concat(inFlows.stream(), regularFlows)
			.toArray(Transitionable[]::new);

		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.mergeStrategy(new TokenMergeStrategy(this))
				.states(allStates)
				.transitions(
					transitionables
				)
				.build();
		return statemachine;
	}
}

