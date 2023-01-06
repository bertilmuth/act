package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.requirementsascode.act.workflow.TokensDifference.*;
import static org.requirementsascode.act.workflow.WorkflowApi.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.workflow.testdata.StringData;

class TokenDifferenceTest {
	private static final Node ACTION = action("Action1", StringData.class, TokenDifferenceTest::runStep);
	private static final Token TOKEN1 = token(ACTION, new StringData("Action1"));
	private static final Token TOKEN2 = token(ACTION, new StringData("Action2"));

	@Test
	void differenceBetweenEmptyTokens() {
		Tokens emptyTokens = new Tokens(emptyMap());
		Tokens difference = TokensDifference.addedTokens(emptyTokens, emptyTokens);
		assertEquals(emptyList(), tokensList(difference));
	}

	@Test
	void oneTokenAdded() {
		Tokens tokensBefore = new Tokens(emptyMap());
		Tokens tokensAfter = new Tokens(token1inAction());

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(token1inAction(), tokensList(tokensAdded));
	}
	
	@Test
	void oneDifferentTokenAdded() {
		Tokens tokensBefore = new Tokens(token1inAction());
		Tokens tokensAfter = new Tokens(token1_2inAction());

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(token2inAction(), tokensList(tokensAdded));
	}
	
	@Test
	void sameTokenAddedToOneElementList() {
		Tokens tokensBefore = new Tokens(token1inAction());
		Tokens tokensAfter = new Tokens(token1_1inAction());

		Tokens tokensAdded = addedTokens(tokensBefore, tokensAfter);
		assertEquals(token1inAction(), tokensList(tokensAdded));
	}
	
	@Test
	void oneTokenRemovedFromOneElementList() {
		Tokens tokensBefore = new Tokens(token1inAction());
		Tokens tokensAfter = new Tokens(emptyList());

		Tokens tokensRemoved = removedTokens(tokensBefore, tokensAfter);
		assertEquals(token1inAction(), tokensList(tokensRemoved));
	}
	
	@Test
	void tokenRemovedFromTwoElementList() {
		Tokens tokensBefore = new Tokens(token1_2inAction());
		Tokens tokensAfter = new Tokens(token1inAction());

		Tokens tokensRemoved = removedTokens(tokensBefore, tokensAfter);
		assertEquals(token2inAction(), tokensList(tokensRemoved));
	}
	
	private static StringData runStep(WorkflowState state, StringData inputData) {
		return inputData;
	}
	
	private List<Token> tokensList(Tokens tokens) {
		return tokens.stream().collect(Collectors.toList());
	}
	
	private List<Token> asList(Token...tokens){
		return Arrays.asList(tokens);
	}
	
	private List<Token> token1inAction() {
		return asList(TOKEN1);
	}
	
	private List<Token> token2inAction() {
		return asList(TOKEN2);
	}
	
	private List<Token> token1_1inAction() {
		return asList(TOKEN1, TOKEN1);
	}
	
	private List<Token> token1_2inAction() {
		return asList(TOKEN1, TOKEN2);
	}
}
