package org.requirementsascode.act.workflow;

import static java.util.Collections.*;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toMap;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
	
	Tokens union(Tokens tokensToMerge) {
		Map<Port<?>, Set<Token>> mergedTokenMaps = 
			Stream.of(asMap(), tokensToMerge.asMap())
		    	.flatMap(m -> m.entrySet().stream())
		    	.collect(toMap(
			        Map.Entry::getKey,
			        Map.Entry::getValue,
			        (v1, v2) -> { 
			        	return Stream.concat(v1.stream(), v2.stream())
			        		.collect(toCollection(LinkedHashSet::new)); 
			        }));
		
		return new Tokens(mergedTokenMaps);
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
}
