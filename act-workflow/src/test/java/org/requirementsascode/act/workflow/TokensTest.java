package org.requirementsascode.act.workflow;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.workflow.WorkflowApi.port;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.workflow.testdata.StringData;

class TokensTest {
	private static final String PORT_1 = "Port_1";
	private static final String PORT_2 = "Port_2";
	private static final String PORT_3 = "Port_3";
	
	private static final Token TOKEN_1 = token(new StringData("Token_1"));
	private static final Token TOKEN_2 = token(new StringData("Token_2"));
	private static final Token TOKEN_3 = token(new StringData("Token_3"));

	
	@Test
	void unionOfEmptyTokensIsEmpty() {
		Tokens unifiedTokens = emptyTokens().union(emptyTokens());
		assertEquals(emptyTokens(), unifiedTokens);
	}
	
	@Test
	void unionOfEmptyTokensWithOneToken() {
		Tokens unifiedTokens = emptyTokens().union(token1());
		assertEquals(token1(), unifiedTokens);
	}
	
	@Test
	void unionOfOneTokenWithEmptyTokens() {
		Tokens unifiedTokens = token1().union(emptyTokens());
		assertEquals(token1(), unifiedTokens);
	}
	
	@Test
	void unionOfOneTokenWithItselfIsTheToken() {
		Tokens unifiedTokens = token1().union(token1());
		assertEquals(token1(), unifiedTokens);
	}
	
	@Test
	void unionOfTwoTokens() {
		Tokens unifiedTokens = token1().union(token2());
		
		List<Token> unifiedTokensList = asTokenList(unifiedTokens);
		List<Token> expectedTokenList = asList(TOKEN_1, TOKEN_2);
		assertEquals(expectedTokenList.size(), unifiedTokensList.size());
		assertTrue(unifiedTokensList.containsAll(expectedTokenList));
	}
	
	@Test
	void unionOfTwoTokensWithAnotherToken() {
		Tokens unifiedTokens = (token1().union(token2())).union(token3());
		
		List<Token> unifiedTokensList = asTokenList(unifiedTokens);
		List<Token> expectedTokenList = asList(TOKEN_1, TOKEN_2, TOKEN_3);
		assertEquals(expectedTokenList.size(), unifiedTokensList.size());
		assertTrue(unifiedTokensList.containsAll(expectedTokenList));
	}
	
	@Test
	void differenceOfEmptyTokensIsEmpty() {
		Tokens diff = emptyTokens().minus(emptyTokens());
		assertEquals(emptyTokens(), diff);
	}
	
	@Test
	void oneTokenMinusOneTokenIsEmpty() {
		Tokens diff = token1().minus(token1());
		assertEquals(emptyTokens(), diff);
	}
	
	private Tokens emptyTokens() {
		return new Tokens(emptyMap());
	}

	private Tokens token1() {
		return createToken(PORT_1, TOKEN_1);
	}
	
	private Tokens token2() {
		return createToken(PORT_2, TOKEN_2);
	}
	
	private Tokens token3() {
		return createToken(PORT_3, TOKEN_3);
	}
	
	private Tokens createToken(String portName, Token token) {
		Port<?> port1 = port(portName, ActionData.class);
		HashMap<Port<?>, Set<Token>> oneTokenMap = new HashMap<>();
		oneTokenMap.put(port1, singleton(token));
		return new Tokens(oneTokenMap);
	}
	
	private List<Token> asTokenList(Tokens union) {
		return union.asMap().values().stream().flatMap(Set::stream).collect(Collectors.toList());
	}
}
