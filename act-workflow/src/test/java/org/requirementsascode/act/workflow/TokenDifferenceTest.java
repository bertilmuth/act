package org.requirementsascode.act.workflow;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.workflow.TokensDifference.*;
import static org.requirementsascode.act.workflow.WorkflowApi.*;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.workflow.testdata.StringData;

class TokenDifferenceTest {
	private static final Node ACTION = action("Action1", StringData.class, TokenDifferenceTest::runStep);
	private static final Token TOKEN1 = token(ACTION, new StringData("Action1"));
	private static final Token TOKEN2 = token(ACTION, new StringData("Action2"));

	@Test
	void differenceBetweenEmptyLists() {
		Tokens emptyTokens = new Tokens(emptyList());
		Tokens difference = TokensDifference.addedTokens(emptyTokens, emptyTokens);
		assertEquals(emptyList(), tokensList(difference));
	}

	@Test
	void oneTokenAddedToEmptyList() {
		Tokens tokensBefore = new Tokens(emptyList());
		Tokens tokensAfter = new Tokens(asList(TOKEN1));

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN1), tokensList(tokensAdded));
	}
	
	@Test
	void oneDifferentTokenAddedToOneElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1));
		Tokens tokensAfter = new Tokens(asList(TOKEN1, TOKEN2));

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN2), tokensList(tokensAdded));
	}
	
	@Test
	void sameTokenAddedToOneElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1));
		Tokens tokensAfter = new Tokens(asList(TOKEN1, TOKEN1));

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN1), tokensList(tokensAdded));
	}
	
	@Test
	void oneTokenRemovedFromOneElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1));
		Tokens tokensAfter = new Tokens(emptyList());

		Tokens tokensRemoved = removedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN1), tokensList(tokensRemoved));
	}
	
	@Test
	void tokenRemovedFromTwoElementList() {
		Tokens tokensBefore = new Tokens(asList(TOKEN1, TOKEN2));
		Tokens tokensAfter = new Tokens(asList(TOKEN1));

		Tokens tokensRemoved = removedTokens(tokensBefore, tokensAfter);
		assertEquals(asList(TOKEN2), tokensList(tokensRemoved));
	}
	
	private static StringData runStep(WorkflowState state, StringData inputData) {
		return inputData;
	}
	
	private List<Token> tokensList(Tokens difference) {
		return difference.stream().collect(Collectors.toList());
	}
}
