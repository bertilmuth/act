package org.requirementsascode.act.core;

import static java.util.Objects.requireNonNull;

public class Change<S, V> {
	private final Data<S, V> before;
	private final Data<S, V> after;

	private Change(Data<S,V> before, Data<S,V> after) {
		this.before = requireNonNull(before, "before must be non-null");
		this.after = requireNonNull(after, "after must be non-null");
	}
	
	public static <S,V> Change<S,V> change(Data<S,V> before, Data<S,V> after) {
		return new Change<>(before,after);
	}
	
	public Data<S,V> before() {
		return before;
	}
	
	public Data<S,V> after() {
		return after;
	}

	@Override
	public String toString() {
		return "Change [before=" + before + ", after=" + after + "]";
	}
}
