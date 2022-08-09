package org.requirementsascode.act.core;

import static java.util.Objects.requireNonNull;

public class Change<S, V> {
	private final Data<S, V> input;
	private final Data<S, V> output;

	private Change(Data<S,V> input, Data<S,V> output) {
		this.input = requireNonNull(input, "input must be non-null");
		this.output = requireNonNull(output, "output must be non-null");
	}
	
	public static <S,V> Change<S,V> change(Data<S,V> input, Data<S,V> output) {
		return new Change<>(input,output);
	}
	
	public Data<S,V> input() {
		return input;
	}
	
	public Data<S,V> output() {
		return output;
	}

	@Override
	public String toString() {
		return "Change [input=" + input + ", output=" + output + "]";
	}
}
