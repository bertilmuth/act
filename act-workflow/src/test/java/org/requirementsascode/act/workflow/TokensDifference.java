package org.requirementsascode.act.workflow;

import java.util.ArrayList;
import java.util.List;

class TokensDifference {

	public static Tokens tokensAdded(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokens = plus(tokensBefore, tokensAfter);
		minus(tokensBefore, tokens);
		return new Tokens(tokens);
	}

	private static List<Token> plus(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokensList = new ArrayList<>();
		tokensList.addAll(asList(tokensBefore));
		tokensList.addAll(asList(tokensAfter));
		return tokensList;
	}
	
	private static boolean minus(Tokens tokensBefore, List<Token> tokens) {
		return tokens.removeAll(asList(tokensBefore));
	}

	private static List<Token> asList(Tokens tokensBefore) {
		return tokensBefore.stream().toList();
	}

}
