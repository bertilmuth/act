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

	Tokens replaceToken(Token tokenBefore, Token tokenAfter) {
		List<Token> mapWithTokenRemoved = removeTokenFromMap(tokens, tokenBefore);
		List<Token> mapWithTokenAdded = addTokenToMap(mapWithTokenRemoved, tokenAfter);
		return new Tokens(mapWithTokenAdded);
	}

	Tokens addToken(Token tokenToAdd) {
		List<Token> mapWithTokenAdded = addTokenToMap(tokens, tokenToAdd);
		return new Tokens(mapWithTokenAdded);
	}
	
	private List<Token> removeTokenFromMap(List<Token> tokens, Token tokenToBeRemoved) {
		List<Token> newTokensList = new ArrayList<>(tokens);
		newTokensList.remove(tokenToBeRemoved);
		return newTokensList;
	}
	
	private List<Token> addTokenToMap(List<Token> tokens, Token tokenToAdd) {
		List<Token> newTokensList = new ArrayList<>(tokens);
		newTokensList.add(tokenToAdd);
		return newTokensList;
	}

	@Override
	public String toString() {
		return "Tokens[" + tokens + "]";
	}
}
