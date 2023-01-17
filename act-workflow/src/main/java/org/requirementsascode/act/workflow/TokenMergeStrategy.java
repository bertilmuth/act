package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.List;
import java.util.Optional;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token>{
		private final Workflow workflow;

		public TokenMergeStrategy(Workflow workflow) {
			this.workflow = workflow;
		}
	
		@Override
		public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Tokens tokensBefore = dataBefore.state().tokens();
			Tokens mergedTokens = mergeTokens(tokensBefore, datasAfter);
			WorkflowState state = new WorkflowState(workflow, mergedTokens);
			return data(state, null);
		}

		private Tokens mergeTokens(Tokens tokensBefore, List<Data<WorkflowState, Token>> datasAfter) {	
			Optional<Tokens> mergedTokenMap = datasAfter.stream()
				.map(Data::state)
				.map(WorkflowState::tokens)
				.reduce((t1,t2) -> {
					Tokens delta = t2.minus(tokensBefore);
					Tokens union = t1.union(t2);
					return union;
				})
				.map(Tokens::removeDirtyTokens);
			return mergedTokenMap.orElse(null);
		}
	}