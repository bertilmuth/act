package org.requirementsascode.act.places;

import java.util.stream.Stream;

public class Tokens<S, V> {

	public static <S,V> Tokens<S, V> of() {
		return new Tokens<>();
	}

	public Stream<Token<S,V>> stream() {
		return Stream.of();
	}
}
