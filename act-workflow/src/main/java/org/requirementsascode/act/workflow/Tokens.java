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
		List<Token> listWithTokenBeforeRemoved = removeTokenFromList(tokens, tokenBefore);
		List<Token> listWithTokenAfterAdded = addTokenToList(listWithTokenBeforeRemoved, tokenAfter);
		return new Tokens(listWithTokenAfterAdded);
	}

	Tokens addToken(Token tokenToAdd) {
		List<Token> listWithTokenAdded = addTokenToList(tokens, tokenToAdd);
		return new Tokens(listWithTokenAdded);
	}
	
	private List<Token> removeTokenFromList(List<Token> tokens, Token tokenToBeRemoved) {
		List<Token> newTokensList = new ArrayList<>(tokens);
		newTokensList.remove(tokenToBeRemoved);
		return newTokensList;
	}
	
	private List<Token> addTokenToList(List<Token> tokens, Token tokenToAdd) {
		List<Token> newTokensList = new ArrayList<>(tokens);
		newTokensList.add(tokenToAdd);
		return newTokensList;
	}

	@Override
	public String toString() {
		return "Tokens[" + tokens + "]";
	}
}
