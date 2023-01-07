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
	
	Tokens moveToken(Token tokenToMove, Node fromNode, Node toNode) {
		Token movedToken = tokenToMove.moveTo(toNode);
		return replaceToken(fromNode, tokenToMove, toNode, movedToken);
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
		Map<Node, List<Token>> newTokensMap = new HashMap<>(tokens);
		newTokensMap.remove(node);
		return newTokensMap;
	}
	
	private Map<Node, List<Token>> addTokenToMap(Node node, Map<Node, List<Token>> tokens, Token tokenToAdd) {
		LinkedHashMap<Node, List<Token>> newTokensMap = new LinkedHashMap<>(tokens);
		newTokensMap.merge(node, singletonList(tokenToAdd), (oldValue, value) -> {
			List<Token> newList = new ArrayList<>(oldValue);
			newList.addAll(value);
			return newList;
		});
		return newTokensMap;
	}
	
	@Override
	public String toString() {
		return "Tokens[" + tokens + "]";
	}
}
