package org.requirementsascode.act.workflow;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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

	Tokens replaceToken(Token tokenBefore, Token tokenAfter) {
		Map<Node, List<Token>> mapWithTokenRemoved = removeTokenFromMap(tokens, tokenBefore);
		Map<Node, List<Token>> mapWithTokenAdded = addTokenToMap(mapWithTokenRemoved, tokenAfter);
		return new Tokens(mapWithTokenAdded);
	}
	
	private Map<Node, List<Token>> addTokenToMap(Map<Node, List<Token>> tokens, Token tokenToAdd) {
		LinkedHashMap<Node, List<Token>> newTokensMap = new LinkedHashMap<>(tokens);
		newTokensMap.merge(tokenToAdd.node(), singletonList(tokenToAdd), (oldValue, value) -> {
			List<Token> newList = new ArrayList<>(oldValue);
			newList.addAll(value);
			return newList;
		});
		return newTokensMap;
	}
	
	private Map<Node, List<Token>> removeTokenFromMap(Map<Node, List<Token>> tokens, Token tokenToBeRemoved) {
		Map<Node, List<Token>> newTokensMap = new HashMap<>(tokens);
		newTokensMap.remove(tokenToBeRemoved.node());
		return newTokensMap;
	}

	@Override
	public String toString() {
		return "Tokens[" + tokens + "]";
	}
}
