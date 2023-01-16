package org.requirementsascode.act.workflow;

import static java.util.stream.Stream.concat;
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
	
	private Workflow(Actions actions, Ports ports, Flows flows, Ports inPorts) {
		this.flows = flows;
		this.statemachine = statemachineWith(actions, ports, flows, inPorts);		
	}

	public final static WorkflowBuilder builder() {
		return new WorkflowBuilder();
	}
	
	public static Workflow from(Data<Workflow, ?> data) {
		return data.state();
	}
	
	public <T extends ActionData> WorkflowState start(Port<T> inPort, T actionData) {
		WorkflowState initialWorkflowState = new WorkflowState(this, new Tokens(Collections.emptyMap()));
		return enterActionData(initialWorkflowState, inPort, actionData);
	}
	
	public <T extends ActionData> WorkflowState enterActionData(WorkflowState state, Port<T> inPort, T actionData) {
		Data<WorkflowState, Token> dataWithToken = state.addToken(inPort, token(actionData));
		return actOn(dataWithToken).state();
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
	
	static Workflow create(Actions actions, Ports ports, Flows flows, Ports inPorts){
		return new Workflow(actions, ports, flows, inPorts);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Statemachine<WorkflowState, Token> statemachineWith(Actions actions, Ports ports, Flows flows, Ports inPorts) {
		
		Transitionable[] transitionables = streamsOf(actions, flows).toArray(Transitionable[]::new);
		
		State[] states = concat(
			concat(ports.asStates(), inOutPortsStates(actions, flows)),
			inPorts.asStates())
			.toArray(State[]::new);
		
		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.mergeStrategy(new TokenMergeStrategy(this))
				.states(states)
				.transitions(
					transitionables
				)
				.build();
		return statemachine;
	}

	private Stream<State<WorkflowState, Token>> inOutPortsStates(Actions actions, Flows flows) {
		Stream<State<WorkflowState, Token>> inPortsStates = streamsOf(actions, flows)
			.map(ExecutableNode::inPorts)
			.map(Ports::asOneState);
		
		Stream<State<WorkflowState, Token>> outPortsStates = streamsOf(actions, flows)
				.map(ExecutableNode::outPorts)
				.map(Ports::asOneState);
		
		return concat(inPortsStates, outPortsStates);
	}

	private Stream<ExecutableNode> streamsOf(Actions actions, Flows flows) {
		return concat(actions.stream(), flows.stream());
	}
}

