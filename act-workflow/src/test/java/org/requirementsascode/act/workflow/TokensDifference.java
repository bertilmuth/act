package org.requirementsascode.act.workflow;

import java.util.ArrayList;
import java.util.List;

class TokensDifference {

	public static Tokens tokensAdded(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> allTokens = allOf(tokensBefore, tokensAfter);
		List<Token> tokensAdded = minus(allTokens, asList(tokensBefore));
		return new Tokens(tokensAdded);
	}

	private static List<Token> allOf(Tokens tokensA, Tokens tokensB) {
		List<Token> tokensList = new ArrayList<>();
		tokensList.addAll(asList(tokensA));
		tokensList.addAll(asList(tokensB));
		return tokensList;
	}
	
	private static List<Token> minus(List<Token> tokens, List<Token> toBeRemoved) {
		List<Token> tokensList = new ArrayList<>();
		tokensList.addAll(tokens);
		tokensList.removeAll(toBeRemoved);
		return tokensList;
	}

	private static List<Token> asList(Tokens tokensBefore) {
		return tokensBefore.stream().toList();
	}

}
