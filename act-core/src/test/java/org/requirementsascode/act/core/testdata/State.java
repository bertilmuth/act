package org.requirementsascode.act.core.testdata;

import java.util.Objects;

public class State {
	private final String name;

	public State(String name) {
		this.name = name;
	}

	public String name() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		return Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "State [name=" + name + "]";
	}
}