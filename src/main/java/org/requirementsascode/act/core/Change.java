package org.requirementsascode.act.core;

import static java.util.Objects.requireNonNull;

public class Change<S, V1,V2> {
	private final Data<S, V1> before;
	private final Data<S, V2> after;

	private Change(Data<S,V1> before, Data<S,V2> after) {
		this.before = requireNonNull(before, "before must be non-null");
		this.after = requireNonNull(after, "after must be non-null");
	}
	
	public static <S,V1,V2> Change<S,V1,V2> change(Data<S,V1> before, Data<S,V2> after) {
		return new Change<>(before,after);
	}
	
	public Data<S,V1> before() {
		return before;
	}
	
	public Data<S,V2> after() {
		return after;
	}

	@Override
	public String toString() {
		return "Change [before=" + before + ", after=" + after + "]";
	}
}
