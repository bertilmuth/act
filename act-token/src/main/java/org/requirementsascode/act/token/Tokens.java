package org.requirementsascode.act.token;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.requirementsascode.act.statemachine.State;

public class Tokens {
	private final List<Token> tokens;

	public Tokens(List<Token> tokens) {
		this.tokens = requireNonNull(tokens);
	}

	@SafeVarargs
	public static Tokens tokens(Token... tokens) {
		return new Tokens(asList(tokens));
	}

	public Stream<Token> stream() {
		return tokens.stream();
	}

	public Stream<Token> inState(String stateName) {
		return this.stream()
			.filter(token -> token.state().name().equals(stateName));
	}
	
	public boolean isAnyTokenInState(String stateName) {
		return inState(stateName).count() != 0;
	}
	
	public Optional<Token> firstTokenInState(String stateName) {
		return inState(stateName).findFirst();
	}

	public Tokens moveToken(Token token, State<?, ActionData> state) {
		Token movedToken = token.moveTo(state);
		
		List<Token> newTokens = new ArrayList<>(tokens);
		if(newTokens.remove(token)) {
			newTokens.add(movedToken);
		}
		
		return new Tokens(newTokens);
	}

	@Override
	public String toString() {
		return "Tokens [" + tokens + "]";
	}
}
