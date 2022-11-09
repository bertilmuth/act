package org.requirementsascode.act.workflow;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.workflow.TokensDifference.tokensAdded;
import static org.requirementsascode.act.workflow.WorkflowApi.step;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.workflow.testdata.StringData;

class TokenDifferenceTest {
	private static final ActionBehavior STEP = step(StringData.class, TokenDifferenceTest::runStep);
	private static final Action ACTION = new Action("Action1", STEP);

	@Test
	void differenceBetweenEmptyLists() {
		Tokens emptyTokens = new Tokens(emptyList());
		Tokens difference = TokensDifference.tokensAdded(emptyTokens, emptyTokens);
		assertEquals(emptyList(), difference.stream().toList());
	}

	@Test
	void oneTokenAddedToEmptyList() {
		Tokens tokensBefore = new Tokens(emptyList());
		List<Token> tokensAfterList = asList(token(ACTION, null));
		Tokens tokensAfter = new Tokens(tokensAfterList);

		Tokens difference = tokensAdded(tokensBefore, tokensAfter);
		assertEquals(tokensAfterList, difference.stream().toList());
	}
	
	private static StringData runStep(WorkflowState state, StringData inputData) {
		return inputData;
	}
}
