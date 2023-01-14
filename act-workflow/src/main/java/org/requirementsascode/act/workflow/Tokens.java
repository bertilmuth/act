package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Tokens {
	private final Map<Port<?>, List<Token>> tokens;
	
	Tokens(Map<Port<?>, List<Token>> tokensMap) {
		this.tokens = new LinkedHashMap<>(tokensMap);
	}
	
	public Map<Port<?>, List<Token>> asMap() {
		return Collections.unmodifiableMap(tokens);
	}
	
	public Stream<Token> tokensIn(Node node) {
		return tokens.getOrDefault(node, emptyList()).stream();
	}

	Tokens addToken(Port<?> port, Token token) {
		Map<Port<?>, List<Token>> newTokensMap = new LinkedHashMap<>(tokens);
		Map<Port<?>, List<Token>> mapWithTokenAdded = addTokenToMap(port, newTokensMap, token);
		return new Tokens(mapWithTokenAdded);
	}
	
	Tokens removeToken(Port<?> port, Token token) {
		Map<Port<?>, List<Token>> mapWithTokenRemoved = removeTokenFromMap(port, token);
		return new Tokens(mapWithTokenRemoved);
	}
	
	Tokens replaceToken(Port<?> port, Token tokenBefore, Token tokenAfter) {
		Map<Port<?>, List<Token>> mapWithTokenRemoved = removeTokenFromMap(port, tokenBefore);
		Map<Port<?>, List<Token>> mapWithTokenAdded = addTokenToMap(port, mapWithTokenRemoved, tokenAfter);
		return new Tokens(mapWithTokenAdded);
	}
	
	private Map<Port<?>, List<Token>> removeTokenFromMap(Port<?> port, Token tokenToBeRemoved) {
		List<Token> tokensWithTokenRemoved = tokensIn(port).filter(t -> !tokenToBeRemoved.equals(t)).collect(toList());
		Map<Port<?>, List<Token>> newTokensMap = new LinkedHashMap<>(tokens);
		newTokensMap.put(port, tokensWithTokenRemoved);
		return newTokensMap;
	}
	
	private Map<Port<?>, List<Token>> addTokenToMap(Port<?> port, Map<Port<?>, List<Token>> tokensMap, Token tokenToAdd) {
		tokensMap.merge(port, singletonList(tokenToAdd), (oldValue, newValue) -> {
			List<Token> newList = new ArrayList<>(oldValue);
			newList.addAll(newValue);
			return newList;
		});
		return tokensMap;
	}
	
	@Override
	public String toString() {
		return "Tokens[" + tokens + "]";
	}
}
