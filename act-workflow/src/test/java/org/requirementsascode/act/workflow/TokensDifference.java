package org.requirementsascode.act.workflow;

import java.util.ArrayList;
import java.util.List;

class TokensDifference {
	public static Tokens tokensAdded(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokensAdded = minus(asList(tokensAfter), asList(tokensBefore));
		return new Tokens(tokensAdded);
	}
	
	public static Tokens tokensRemoved(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokensRemoved = minus(asList(tokensBefore), asList(tokensAfter));
		return new Tokens(tokensRemoved);
	}
	
	private static List<Token> minus(List<Token> tokens, List<Token> toBeRemoved) {
		List<Token> tokensList = new ArrayList<>();
		tokensList.addAll(tokens);
		toBeRemoved.stream().forEach(tokensList::remove);
		return tokensList;
	}

	private static List<Token> asList(Tokens tokensBefore) {
		return tokensBefore.stream().toList();
	}

}
