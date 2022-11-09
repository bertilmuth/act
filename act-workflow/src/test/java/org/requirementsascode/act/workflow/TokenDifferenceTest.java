package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyList;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class TokenDifferenceTest {

	@Test
	void test() {
		Tokens emptyTokens = new Tokens(emptyList());
		Tokens difference = TokensDifference.between(emptyTokens, emptyTokens);
		assertEquals(emptyList(), difference.stream().toList());
	}

}
