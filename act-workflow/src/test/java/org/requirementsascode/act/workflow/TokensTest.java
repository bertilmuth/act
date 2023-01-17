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
	private static final String PORT_1 = "TestingPort1";
	private static final String PORT_2 = "TestingPort2";
	
	private static final Token TOKEN_1 = token(new StringData("Token1"));
	private static final Token TOKEN_2 = token(new StringData("Token2"));
	
	@Test
	void unionOfEmptyTokensIsEmpty() {
		Tokens unifiedTokens = emptyTokens().union(emptyTokens());
		assertTrue(unifiedTokens.asMap().entrySet().isEmpty());
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
		assertEquals(asList(TOKEN_1, TOKEN_2), unifiedTokensList);
	}
	
	private Tokens emptyTokens() {
		return new Tokens(emptyMap());
	}

	private Tokens token1() {
		Port<?> port1 = port(PORT_1, ActionData.class);
		HashMap<Port<?>, Set<Token>> oneTokenMap = new HashMap<>();
		oneTokenMap.put(port1, singleton(TOKEN_1));
		return new Tokens(oneTokenMap);
	}
	
	private Tokens token2() {
		Port<?> port1 = port(PORT_2, ActionData.class);
		HashMap<Port<?>, Set<Token>> oneTokenMap = new HashMap<>();
		oneTokenMap.put(port1, singleton(TOKEN_2));
		return new Tokens(oneTokenMap);
	}
	
	private List<Token> asTokenList(Tokens union) {
		return union.asMap().values().stream().flatMap(Set::stream).collect(Collectors.toList());
	}
}
