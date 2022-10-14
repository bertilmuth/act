package org.requirementsascode.act.token;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Tokens<V> {
	private final List<Token<V>> tokens;

	public Tokens(List<Token<V>> tokens) {
		this.tokens = requireNonNull(tokens);
	}

	@SafeVarargs
	public static <S,V> Tokens<V> of(Token<V>... tokens) {
		return new Tokens<>(asList(tokens));
	}

	public Stream<Token<V>> stream() {
		return tokens.stream();
	}

	public Stream<Token<V>> inState(State<?, V> state) {
		return this.stream()
			.filter(token -> token.state().equals(state));
	}

	public Tokens<V> moveToken(Token<V> token, State<?, V> state) {
		Token<V> movedToken = token.moveTo(state);
		
		List<Token<V>> newTokens = new ArrayList<>(tokens);
		if(newTokens.remove(token)) {
			newTokens.add(movedToken);
		}
		
		return new Tokens<>(newTokens);
	}
}
