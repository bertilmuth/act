package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Tokens {
	private final Map<Node, List<Token>> tokens;
	
	Tokens(Map<Node, List<Token>> tokensMap) {
		this.tokens = tokensMap;
	}
	
	public Map<Node, List<Token>> asMap() {
		return Collections.unmodifiableMap(tokens);
	}
	
	public Stream<Token> tokensIn(Node node) {
		return tokens.getOrDefault(node, emptyList()).stream();
	}

	public Tokens addToken(Node node, Token token) {
		Map<Node, List<Token>> newTokensMap = new HashMap<>(tokens);
		Map<Node, List<Token>> mapWithTokenAdded = addTokenToMap(node, newTokensMap, token);
		return new Tokens(mapWithTokenAdded);
	}
	
	public Tokens removeToken(Node node, Token token) {
		Map<Node, List<Token>> mapWithTokenRemoved = removeTokenFromMap(node, token);
		return new Tokens(mapWithTokenRemoved);
	}
	
	Tokens replaceToken(Node nodeBefore, Token tokenBefore, Token tokenAfter) {
		return replaceToken(nodeBefore, tokenBefore, nodeBefore, tokenAfter);
	}
	
	private Tokens replaceToken(Node nodeBefore, Token tokenBefore, Node nodeAfter, Token tokenAfter) {
		Map<Node, List<Token>> mapWithTokenRemoved = removeTokenFromMap(nodeBefore, tokenBefore);
		Map<Node, List<Token>> mapWithTokenAdded = addTokenToMap(nodeAfter, mapWithTokenRemoved, tokenAfter);
		return new Tokens(mapWithTokenAdded);
	}
	
	private Map<Node, List<Token>> removeTokenFromMap(Node node, Token tokenToBeRemoved) {
		List<Token> tokensWithTokenRemoved = tokensIn(node).filter(t -> !tokenToBeRemoved.equals(t)).collect(toList());
		Map<Node, List<Token>> newTokensMap = new HashMap<>(tokens);
		newTokensMap.put(node, tokensWithTokenRemoved);
		return newTokensMap;
	}
	
	private Map<Node, List<Token>> addTokenToMap(Node node, Map<Node, List<Token>> tokensMap, Token tokenToAdd) {
		tokensMap.merge(node, singletonList(tokenToAdd), (oldValue, value) -> {
			oldValue.addAll(value);
			return oldValue;
		});
		return tokensMap;
	}
	
	@Override
	public String toString() {
		return "Tokens[" + tokens + "]";
	}
}
