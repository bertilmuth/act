package org.requirementsascode.act.token;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.stream.Stream;

public class TokenFlows{
	private final List<TokenFlow> tokenFlows;

	private TokenFlows(List<TokenFlow> tokenFlows) {
		this.tokenFlows = requireNonNull(tokenFlows, "tokenFlows must be non-null!");
	}

	static TokenFlows tokenFlows(List<TokenFlow> tokenFlows) {
		return new TokenFlows(tokenFlows);
	}

	public Stream<TokenFlow> stream() {
		return tokenFlows.stream();
	}
}
