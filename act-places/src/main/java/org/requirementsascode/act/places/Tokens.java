package org.requirementsascode.act.places;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class Tokens<S, V> {
	private final List<Token<S,V>> tokens;

	public Tokens(List<Token<S, V>> tokens) {
		this.tokens = requireNonNull(tokens);
	}

	@SafeVarargs
	public static <S,V> Tokens<S, V> of(Token<S,V>... tokens) {
		return new Tokens<>(asList(tokens));
	}

	public Stream<Token<S,V>> stream() {
		return tokens.stream();
	}
}
