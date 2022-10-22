package org.requirementsascode.act.token.function;

import org.requirementsascode.act.token.ActionData;

public class AtomicSystemFunction implements ActionData{
	private static final AtomicSystemFunction atomicSystemFunction = new AtomicSystemFunction();
	public static AtomicSystemFunction atomicSystemFunction() {
		return atomicSystemFunction;
	}
}