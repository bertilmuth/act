package org.requirementsascode.act.workflow.testdata;

import java.util.Objects;

import org.requirementsascode.act.workflow.ActionData;

public class StringData implements ActionData {
	public final String string;

	public StringData(String string) {
		this.string = string;
	}

	@Override
	public int hashCode() {
		return Objects.hash(string);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StringData other = (StringData) obj;
		return Objects.equals(string, other.string);
	}

	@Override
	public String toString() {
		return "StringValue [" + string + "]";
	}
};