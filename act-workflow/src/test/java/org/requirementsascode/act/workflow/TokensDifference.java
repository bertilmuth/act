package org.requirementsascode.act.workflow;

import java.util.List;

class TokensDifference {

	public static Tokens tokensAdded(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokenList = tokensAfter.stream().toList();
		return new Tokens(tokenList);
	}

}
