package org.requirementsascode.act.workflow;

import static java.util.Collections.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.workflow.WorkflowApi.port;
import static org.requirementsascode.act.workflow.WorkflowApi.token;

import java.util.HashMap;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.workflow.testdata.StringData;

class TokensTest {
	private static final String PORT1 = "TestingPort";
	
	private static final Token TOKEN_1 = token(new StringData("Token1"));
	
	@Test
	void unionOfEmptyTokensIsEmpty() {
		Tokens union = emptyTokens().union(emptyTokens());
		assertTrue(union.asMap().entrySet().isEmpty());
	}
	
	@Test
	void unionOfEmptyTokensWithOneToken() {
		Tokens union = emptyTokens().union(token1());
		assertEquals(token1(), union);
	}
	
	@Test
	void unionOfOneTokenWithEmptyTokens() {
		Tokens union = token1().union(emptyTokens());
		assertEquals(token1(), union);
	}
	
	private Tokens emptyTokens() {
		return new Tokens(emptyMap());
	}

	private Tokens token1() {
		Port<?> port1 = port(PORT1, ActionData.class);
		HashMap<Port<?>, Set<Token>> oneTokenMap = new HashMap<>();
		oneTokenMap.put(port1, singleton(TOKEN_1));
		return new Tokens(oneTokenMap);
	}
}
