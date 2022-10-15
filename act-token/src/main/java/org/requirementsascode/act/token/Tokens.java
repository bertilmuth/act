package org.requirementsascode.act.token;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Tokens<V> {
	private final List<Token<V>> tokens;

	public Tokens(List<Token<V>> tokens) {
		this.tokens = requireNonNull(tokens);
	}

	@SafeVarargs
	public static <S,V> Tokens<V> tokens(Token<V>... tokens) {
		return new Tokens<>(asList(tokens));
	}

	public Stream<Token<V>> stream() {
		return tokens.stream();
	}

	public Stream<Token<V>> inState(String stateName) {
		return this.stream()
			.filter(token -> token.state().name().equals(stateName));
	}
	
	public boolean isAnyTokenInState(String stateName) {
		return inState(stateName).count() != 0;
	}
	
	public Optional<Token<V>> firstTokenInState(String stateName) {
		return inState(stateName).findFirst();
	}

	public Tokens<V> moveToken(Token<V> token, State<?, V> state) {
		Token<V> movedToken = token.moveTo(state);
		
		List<Token<V>> newTokens = new ArrayList<>(tokens);
		if(newTokens.remove(token)) {
			newTokens.add(movedToken);
		}
		
		return new Tokens<>(newTokens);
	}

	@Override
	public String toString() {
		return "Tokens [" + tokens + "]";
	}
}
