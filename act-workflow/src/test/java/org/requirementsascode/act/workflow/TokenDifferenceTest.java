package org.requirementsascode.act.workflow;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.workflow.TokensDifference.*;
import static org.requirementsascode.act.workflow.WorkflowApi.step;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.workflow.testdata.StringData;

class TokenDifferenceTest {
	private static final Action ACTION = new Action("Action1", step(StringData.class, TokenDifferenceTest::runStep));
	private static final Token TOKEN1 = token(ACTION, new StringData("Action1"));
	private static final Token TOKEN2 = token(ACTION, new StringData("Action2"));

	@Test
	void differenceBetweenEmptyLists() {
		Tokens emptyTokens = new Tokens(emptyList());
		Tokens difference = TokensDifference.addedTokens(emptyTokens, emptyTokens);
		assertEquals(emptyList(), difference.stream().toList());
	}

	@Test
	void oneTokenAddedToEmptyList() {
		Tokens tokensBefore = new Tokens(emptyList());
		Tokens tokensAfter = new Tokens(asList(TOKEN1));

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN1), tokensAdded.stream().toList());
	}
	
	@Test
	void oneDifferentTokenAddedToOneElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1));
		Tokens tokensAfter = new Tokens(asList(TOKEN1, TOKEN2));

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN2), tokensAdded.stream().toList());
	}
	
	@Test
	void sameTokenAddedToOneElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1));
		Tokens tokensAfter = new Tokens(asList(TOKEN1, TOKEN1));

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN1), tokensAdded.stream().toList());
	}
	
	@Test
	void oneTokenRemovedFromOneElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1));
		Tokens tokensAfter = new Tokens(emptyList());

		Tokens tokensRemoved = removedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN1), tokensRemoved.stream().toList());
	}
	
	@Test
	void tokenRemovedFromTwoElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1, TOKEN2));
		Tokens tokensAfter = new Tokens(asList(TOKEN1));

		Tokens tokensRemoved = removedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN2), tokensRemoved.stream().toList());
	}
	
	private static StringData runStep(WorkflowState state, StringData inputData) {
		return inputData;
	}
}
