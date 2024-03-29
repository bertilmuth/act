package org.requirementsascode.act.workflow;

import static java.util.stream.Stream.concat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

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
	
	public <T extends ActionData> WorkflowState enterData(WorkflowState state, Port<T> inPort, T actionData) {
		WorkflowState stateWithTokenAdded = inPort.addToken(state, token(actionData));
		return nextStep(stateWithTokenAdded);
	}
	
	public WorkflowState nextStep(WorkflowState state) {
		return actOn(data(state, Token.empty())).state();
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
		
		Transitionable[] transitionables = allFlowsOf(actions, flows).toArray(Transitionable[]::new);
		
		State[] states = concat(
			concat(ports.asStates(), inOutPortsStates(actions, flows)),
			inPorts.asStates())
			.toArray(State[]::new);
		
		Statemachine<WorkflowState, Token> statemachine = 
			Statemachine.builder()
				.states(states)
				.transitions(
					transitionables
				)
				.mergeStrategy(new TokenMergeStrategy())
				.build();
		return statemachine;
	}

	private Stream<State<WorkflowState, Token>> inOutPortsStates(Actions actions, Flows flows) {
		Stream<State<WorkflowState, Token>> inPortsStates = allFlowsOf(actions, flows)
			.map(TokenFlow::inPorts)
			.map(Ports::asOneState);
		
		Stream<State<WorkflowState, Token>> outPortsStates = allFlowsOf(actions, flows)
				.map(TokenFlow::outPorts)
				.map(Ports::asOneState);
		
		return concat(inPortsStates, outPortsStates);
	}

	private Stream<TokenFlow> allFlowsOf(Actions actions, Flows flows) {
		return concat(actions.asFlows(), flows.stream());
	}
}

