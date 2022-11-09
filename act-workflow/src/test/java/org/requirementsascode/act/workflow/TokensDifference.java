package org.requirementsascode.act.workflow;

import java.util.ArrayList;
import java.util.List;

class TokensDifference {

	public static Tokens tokensAdded(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokens = plus(tokensBefore, tokensAfter);
		tokens.removeAll(tokensBefore.stream().toList());

		return new Tokens(tokens);
	}

	private static List<Token> plus(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokensBeforeList = tokensBefore.stream().toList();
		List<Token> tokensAfterList = tokensAfter.stream().toList();
		List<Token> tokensList = new ArrayList<>();
		tokensList.addAll(tokensBeforeList);
		tokensList.addAll(tokensAfterList);
		return tokensList;
	}

}
