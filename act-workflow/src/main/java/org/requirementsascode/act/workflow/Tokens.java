package org.requirementsascode.act.workflow;

import static java.util.stream.Collectors.toMap;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Tokens {
	private final Map<Port<?>, List<Token>> tokensMap;
	
	Tokens(Map<Port<?>, List<Token>> tokensMap) {
		this.tokensMap = new LinkedHashMap<>(tokensMap);
	}
	
	public Map<Port<?>, List<Token>> asMap() {
		return Collections.unmodifiableMap(tokensMap);
	}
	
	public Stream<Token> tokensIn(Port<?> port) {
		return tokensMap.getOrDefault(port, emptyList()).stream();
	}
	
	Tokens union(Tokens tokensToMerge) {
		Map<Port<?>, List<Token>> mergedTokenMaps = 
			Stream.of(asMap(), tokensToMerge.asMap())
		    	.flatMap(m -> m.entrySet().stream())
		    	.collect(toMap(
			        Map.Entry::getKey,
			        Map.Entry::getValue,
			        (v1, v2) -> { 
			        	return Stream.concat(v1.stream(), v2.stream())
			        		.collect(Collectors.toList()); 
			        }));
		
		return new Tokens(mergedTokenMaps);
	}
	
	Tokens removeDirtyTokens() {
		Map<Port<?>, List<Token>> resultMap = new LinkedHashMap<>(asMap());
		resultMap.replaceAll((key, value) -> value.stream()
			.filter(t -> t.actionData().isPresent())
		    .collect(Collectors.toList()));
		return new Tokens(resultMap);
	}

	Tokens addToken(Port<?> port, Token token) {
		Map<Port<?>, List<Token>> newTokensMap = new LinkedHashMap<>(tokensMap);
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
		Map<Port<?>, List<Token>> newTokensMap = new LinkedHashMap<>(tokensMap);
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
		return "Tokens[" + tokensMap + "]";
	}
}
