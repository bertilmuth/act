package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import java.util.Collections;
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
				.reduce(emptyTokens(), Tokens::union);
			
			Tokens tokensToRemove = tokensStream(datasAfter)
				.map(tafter -> tokensBefore.minus(tafter))
				.reduce(emptyTokens(), Tokens::union);
			
			Tokens mergedTokens = tokensBefore
				.union(unitedTokens)
				.minus(tokensToRemove)
				.removeDirtyTokens();
			return mergedTokens;
		}

		private Tokens emptyTokens() {
			return new Tokens(Collections.emptyMap());
		}
		
		private Stream<Tokens> tokensStream(List<Data<WorkflowState, Token>> datasAfter) {
			Stream<Tokens> tokensStream = datasAfter.stream()
				.map(Data::state)
				.map(WorkflowState::tokens);
			return tokensStream;
		}
	}