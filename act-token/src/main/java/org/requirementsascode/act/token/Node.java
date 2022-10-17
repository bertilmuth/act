package org.requirementsascode.act.token;

import org.requirementsascode.act.statemachine.State;

public interface Node {
	String name();
	State<Workflow, Token> asState();
}
