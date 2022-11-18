package org.requirementsascode.act.workflow.testdata;

import java.util.Objects;

import org.requirementsascode.act.workflow.ActionData;

public class IntegerData implements ActionData {
	public final Integer integer;

	public IntegerData(Integer integer) {
		this.integer = integer;
	}

	@Override
	public int hashCode() {
		return Objects.hash(integer);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		IntegerData other = (IntegerData) obj;
		return Objects.equals(integer, other.integer);
	}

	@Override
	public String toString() {
		return "IntegerValue [" + integer + "]";
	}
};