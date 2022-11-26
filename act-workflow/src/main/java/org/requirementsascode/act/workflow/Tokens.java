package org.requirementsascode.act.workflow;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Tokens {
	private final List<Token> tokens;

	Tokens(List<Token> tokens) {
		this.tokens = requireNonNull(tokens);
	}

	public Stream<Token> stream() {
		return tokens.stream();
	}

	Tokens moveToken(Token token, Node toNode) {
		requireNonNull(token, "token must be non-null!");
		requireNonNull(toNode, "toNode must be non-null!");
		
		Token movedToken = token.moveTo(toNode);
		Tokens newTokens = replaceToken(token, movedToken);

		return newTokens;
	}

	Tokens replaceToken(Token tokenToBeReplaced, Token tokenToReplace) {
		List<Token> newTokensList = removeTokenFromList(tokenToBeReplaced);
		newTokensList.add(tokenToReplace);
		Tokens newTokens = new Tokens(newTokensList);
		return newTokens;
	}
	
	Tokens removeToken(Token tokenToBeRemoved) {
		List<Token> newTokensList = removeTokenFromList(tokenToBeRemoved);
		Tokens newTokens = new Tokens(newTokensList);
		return newTokens;
	}

	private List<Token> removeTokenFromList(Token tokenToBeReplaced) {
		List<Token> newTokensList = new ArrayList<>(tokens);
		newTokensList.remove(tokenToBeReplaced);
		return newTokensList;
	}

	@Override
	public String toString() {
		return "Tokens[" + tokens + "]";
	}
}
