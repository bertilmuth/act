package org.requirementsascode.act.statemachine.merge;

public class MoreThanOneBehaviorActed extends RuntimeException {
	private static final long serialVersionUID = 1L;

	MoreThanOneBehaviorActed(String message) {
		super(message);
	}
}
