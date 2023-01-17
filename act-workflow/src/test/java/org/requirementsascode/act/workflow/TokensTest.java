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
	private static final Token TOKEN_1 = token(new StringData("Token1"));
	
	@Test
	void unionOfEmptyTokensIsEmpty() {
		Tokens union = new Tokens(emptyMap()).union(new Tokens(emptyMap()));
		assertTrue(union.asMap().entrySet().isEmpty());
	}
	
	@Test
	void unionOfEmptyTokensWithOneToken() {
		Tokens union = new Tokens(emptyMap()).union(oneToken());
		assertEquals(oneToken(), union);
	}

	private Tokens oneToken() {
		Port<?> port = port("TestingPort", ActionData.class);
		HashMap<Port<?>, Set<Token>> oneTokenMap = new HashMap<>();
		oneTokenMap.put(port, singleton(TOKEN_1));
		Tokens oneToken = new Tokens(oneTokenMap);
		return oneToken;
	}
}
