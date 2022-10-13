package org.requirementsascode.act.places;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

class TokensTest {

	@Test
	void createsEmptyTokens() {
		Tokens<String,String> tokens = Tokens.of();
		
		List<Token<String,String>> tokenList = 
			tokens.stream().collect(Collectors.toList());
		assertTrue(tokenList.isEmpty());
	}

}
