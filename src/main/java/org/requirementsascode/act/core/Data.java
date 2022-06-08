package org.requirementsascode.act.core;

import java.util.Objects;

public class Data<S,V> {
	private final S state;
	private final V value;

	private Data(S state, V value) {
		this.state = state;
		this.value = value;
	}
	
	public static <S, V> Data<S, V> data(S state) {
		return new Data<S, V>(state, null);
	}
	
	public static <S, V> Data<S, V> data(S state, V value) {
		return new Data<S, V>(state, value);
	}

	public S getState() {
		return state;
	}

	public V getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		return Objects.hash(value, state);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Data<?, ?> other = (Data<?, ?>) obj;
		return Objects.equals(value, other.value) && Objects.equals(state, other.state);
	}

	@Override
	public String toString() {
		return "Data [state=" + state + ", value=" + value + "]";
	}
}