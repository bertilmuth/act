package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token>{
		public TokenMergeStrategy() {
		}
	
		@Override
		public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> dataBefore, List<Data<WorkflowState, Token>> datasAfter) {
			Tokens tokensBefore = dataBefore.state().tokens();
			Tokens mergedTokens = mergeTokens(tokensBefore, datasAfter);
			WorkflowState state = new WorkflowState(mergedTokens);
			return data(state, null);
		}

		private Tokens mergeTokens(Tokens tokensBefore, List<Data<WorkflowState, Token>> datasAfter) {	
			Tokens unitedTokens = tokensStream(datasAfter)
				.reduce(tokensBefore, (result, tokensAfter) -> {
					Tokens added = tokensAfter.minus(tokensBefore);
					Tokens removed = tokensBefore.minus(tokensAfter);
					Tokens changed = added.union(removed);
					return result.minus(changed).union(added);
				});
			
			Tokens mergedTokens = unitedTokens.removeDirtyTokens();
			return mergedTokens;
		}
		
		private Stream<Tokens> tokensStream(List<Data<WorkflowState, Token>> datasAfter) {
			Stream<Tokens> tokensStream = datasAfter.stream()
				.map(Data::state)
				.map(WorkflowState::tokens);
			return tokensStream;
		}
	}