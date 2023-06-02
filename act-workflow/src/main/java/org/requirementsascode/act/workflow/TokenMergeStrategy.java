package org.requirementsascode.act.workflow;

import static org.requirementsascode.act.statemachine.StatemachineApi.data;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.merge.MergeStrategy;

class TokenMergeStrategy implements MergeStrategy<WorkflowState, Token> {
	@Override
	public Data<WorkflowState, Token> merge(Data<WorkflowState, Token> before, Data<WorkflowState, Token> beforeNow,
			Data<WorkflowState, Token> now) {
		Tokens tokensBefore = before.state().tokens();
		Tokens tokensBeforeNow = beforeNow.state().tokens();
		Tokens tokensNow = now.state().tokens();

		Tokens mergedTokens = mergeTokens(tokensBefore, tokensBeforeNow, tokensNow);
		WorkflowState state = new WorkflowState(mergedTokens);
		return data(state, null);
	}

	private Tokens mergeTokens(Tokens tokensBefore, Tokens tokensBeforeNow, Tokens tokensNow) {
		Tokens added = tokensNow.minus(tokensBefore);
		Tokens removed = tokensBefore.minus(tokensNow);
		Tokens changed = added.union(removed);
		Tokens unitedTokens = tokensBeforeNow.minus(changed).union(added).removeDirtyTokens();
		return unitedTokens;
	}
}