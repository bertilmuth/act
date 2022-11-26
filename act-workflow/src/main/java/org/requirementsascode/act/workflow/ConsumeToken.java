package org.requirementsascode.act.workflow;

class ConsumeToken implements ActionData {
	ConsumeToken() {
	};
	
	static boolean isConsumeToken(Token token) {
		return token.actionData().map(ad -> ad instanceof ConsumeToken).orElse(false);
	}
}