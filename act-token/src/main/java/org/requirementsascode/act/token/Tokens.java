package org.requirementsascode.act.token;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class Tokens {
	private final List<Token> tokens;

	public Tokens(List<Token> tokens) {
		this.tokens = requireNonNull(tokens);
	}

	@SafeVarargs
	public static Tokens tokens(Token... tokens) {
		return new Tokens(asList(tokens));
	}

	public Stream<Token> stream() {
		return tokens.stream();
	}

	public Stream<Token> inNode(String nodeName) {
		return this.stream()
			.filter(token -> token.node().name().equals(nodeName));
	}
	
	public boolean isAnyTokenInNode(String nodeName) {
		return inNode(nodeName).count() >= 1;
	}
	
	public Optional<Token> firstTokenInNode(String nodeName) {
		return inNode(nodeName).findFirst();
	}

	public Tokens moveToken(Token token, Node toNode) {
		Token movedToken = token.moveTo(toNode);
		
		List<Token> newTokens = new ArrayList<>(tokens);
		if(newTokens.remove(token)) {
			newTokens.add(movedToken);
		}
		
		return new Tokens(newTokens);
	}

	@Override
	public String toString() {
		return "Tokens [" + tokens + "]";
	}
}
