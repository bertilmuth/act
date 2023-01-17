package org.requirementsascode.act.workflow;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toCollection;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class Tokens {
	private final Map<Port<?>, Set<Token>> tokensMap;
	
	Tokens(Map<Port<?>, Set<Token>> tokensMap) {
		this.tokensMap = new HashMap<>(tokensMap);
	}
	
	public Map<Port<?>, Set<Token>> asMap() {
		return Collections.unmodifiableMap(tokensMap);
	}
	
	public Stream<Token> tokensIn(Port<?> port) {
		return tokensMap.getOrDefault(port, emptySet()).stream();
	}
	
	Tokens union(Tokens tokens) {
		Map<Port<?>, Set<Token>> thisTokens = this.asMap();
		Map<Port<?>, Set<Token>> unifiedTokens = new HashMap<>(tokens.asMap());
		
		thisTokens.forEach((key, value) -> 
			unifiedTokens.merge(key, value, (addedTkns, tkns) -> {
				Set<Token> tknsPlusTkns = new HashSet<>(tkns);
				tknsPlusTkns.addAll(addedTkns);
		        return tknsPlusTkns;
		    })
		);
		
		return new Tokens(unifiedTokens);
	}
	
	public Tokens minus(Tokens tokens) {
		Map<Port<?>, Set<Token>> thisTokens = this.asMap();
		Map<Port<?>, Set<Token>> minusTokens = new HashMap<>(tokens.asMap());
	
		thisTokens.forEach((key, value) -> 
			minusTokens.merge(key, value, (minusTkns, tkns) -> {
				Set<Token> tknsMinusTkns = new HashSet<>(tkns);
				tknsMinusTkns.removeAll(minusTkns);
		        return tknsMinusTkns;
		    })
		);
		return new Tokens(minusTokens);
	}
	
	Tokens removeDirtyTokens() {
		Map<Port<?>, Set<Token>> resultMap = new HashMap<>(asMap());
		resultMap.replaceAll((key, value) -> value.stream()
			.filter(t -> t.actionData().isPresent())
		    .collect(toCollection(LinkedHashSet::new)));
		return new Tokens(resultMap);
	}

	Tokens addToken(Port<?> port, Token token) {
		Map<Port<?>, Set<Token>> newTokensMap = new HashMap<>(tokensMap);
		Map<Port<?>, Set<Token>> mapWithTokenAdded = addTokenToMap(port, newTokensMap, token);
		return new Tokens(mapWithTokenAdded);
	}
	
	Tokens removeToken(Port<?> port, Token token) {
		Map<Port<?>, Set<Token>> mapWithTokenRemoved = removeTokenFromMap(port, token);
		return new Tokens(mapWithTokenRemoved);
	}
	
	Tokens replaceToken(Port<?> port, Token tokenBefore, Token tokenAfter) {
		Map<Port<?>, Set<Token>> mapWithTokenRemoved = removeTokenFromMap(port, tokenBefore);
		Map<Port<?>, Set<Token>> mapWithTokenAdded = addTokenToMap(port, mapWithTokenRemoved, tokenAfter);
		return new Tokens(mapWithTokenAdded);
	}
	
	private Map<Port<?>, Set<Token>> removeTokenFromMap(Port<?> port, Token tokenToBeRemoved) {
		Set<Token> tokensWithTokenRemoved = tokensIn(port).filter(t -> !tokenToBeRemoved.equals(t)).collect(toCollection(LinkedHashSet::new));
		Map<Port<?>, Set<Token>> newTokensMap = new HashMap<>(tokensMap);
		newTokensMap.put(port, tokensWithTokenRemoved);
		return newTokensMap;
	}
	
	private Map<Port<?>, Set<Token>> addTokenToMap(Port<?> port, Map<Port<?>, Set<Token>> tokensMap, Token tokenToAdd) {
		tokensMap.merge(port, 
			new LinkedHashSet<>(singleton(tokenToAdd)), 
			(oldValue, newValue) -> {
				oldValue.addAll(newValue);
				return oldValue;
				});
		return tokensMap;
	}
	
	@Override
	public String toString() {
		return "Tokens[" + tokensMap + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(tokensMap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Tokens other = (Tokens) obj;
		return Objects.equals(tokensMap, other.tokensMap);
	}
}
