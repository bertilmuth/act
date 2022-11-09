package org.requirementsascode.act.workflow;

import java.util.List;

class TokensDifference {

	public static Tokens between(Tokens tokensBefore, Tokens tokensAfter) {
		List<Token> tokenList = tokensAfter.stream().toList();
		return new Tokens(tokenList);
	}

}
