package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Stream.concat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.workflow.WorkflowApi.token;
import static org.requirementsascode.act.workflow.WorkflowState.intialWorkflowState;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.Transitionable;
import org.requirementsascode.act.workflow.trigger.ConsumeToken;
import org.requirementsascode.act.workflow.trigger.MoveToken;
import org.requirementsascode.act.workflow.trigger.StartWorkflow;

public class Workflow implements Behavior<WorkflowState, ActionData, ActionData>{
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
	
	public Data<WorkflowState, ActionData> start(ActionData actionData) {
		Data<WorkflowState, ActionData> started = reactAndThenConsumeToken(initialWorkflowState, new StartWorkflow());
		return reactAndThenConsumeToken(started.state(), actionData);
	}
	
	public Data<WorkflowState, ActionData> nextStep(WorkflowState workflowState) {
		return consumeToken(workflowState);
	}
	
	public Data<WorkflowState, ActionData> nextStep(WorkflowState workflowState, ActionData actionData) {
		Token token = createTokenFrom(data(workflowState, actionData));
		return reactAndThenConsumeToken(workflowState, new MoveToken(token));
	}

	private Data<WorkflowState, ActionData> reactAndThenConsumeToken(WorkflowState workflowState, ActionData actionData) {
		Data<WorkflowState, ActionData> output = reactTo(workflowState, actionData);
		Data<WorkflowState, ActionData> consumeOutput = consumeToken(output.state());
		return consumeOutput;
	}

	private Data<WorkflowState, ActionData> reactTo(WorkflowState workflowState, ActionData actionData) {
		return actOn(data(workflowState, actionData));
	}
	
	private Data<WorkflowState, ActionData> consumeToken(WorkflowState workflowState) {
		return actOn(data(workflowState, new ConsumeToken()));
	}
	
	@Override
	public Data<WorkflowState, ActionData> actOn(Data<WorkflowState,ActionData> inputData) {
		Data<WorkflowState, Token> output = statemachine.actOn(tokenized(inputData));
		ActionData outputActionData = output.state().actionOutput().orElse(null);
		return data(output.state(), outputActionData);
	}

	private Data<WorkflowState, Token> tokenized(Data<WorkflowState,ActionData> inputData) {
		return data(inputData.state(), createTokenFrom(inputData));
	}

	private Token createTokenFrom(Data<WorkflowState, ActionData> inputData) {
		return token(anyNode, inputData.value().orElse(null));
	}
	
	static Workflow createInitialWorkflow(Nodes nodes, DataFlows dataFlows, InitialFlows initialNodes){
		requireNonNull(nodes, "nodes must be non-null!");
		requireNonNull(dataFlows, "dataFlows must be non-null!");
		requireNonNull(initialNodes, "initialNodes must be non-null!");

		Statemachine<WorkflowState, Token> statemachine = statemachineWith(nodes, dataFlows, initialNodes);		
		return new Workflow(statemachine);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static Statemachine<WorkflowState, Token> statemachineWith(Nodes nodes, DataFlows dataFlows,
			InitialFlows initialNodes) {
		
		State[] nodeStates = concat(nodes.asStates(), Stream.of(anyNode.asState())).toArray(State[]::new);
		Transitionable[] transitionables = concat(initialNodes.stream(), dataFlows.stream())
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
			return token.stream().findFirst().flatMap(Token::actionData).orElse(null);
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

