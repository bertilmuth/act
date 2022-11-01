package org.requirementsascode.act.token;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collections;

public class WorkflowBuilder {
	WorkflowBuilder() {
	}

	@SafeVarargs
	public final ActionsBuilder actions(Action... actionsArray) {
		return new ActionsBuilder(actionsArray);
	}

	public class ActionsBuilder {
		private Actions builderActions = Actions.actions(Collections.emptyList());		
		private TokenFlows builderTokenFlows = TokenFlows.tokenFlows(Collections.emptyList());
		private InitialActions builderInitialActions = InitialActions.initialActions(Collections.emptyList());

		private ActionsBuilder(Action[] actions) {
			requireNonNull(actions, "actions must be non-null!");
			this.builderActions = Actions.actions(asList(actions));
		}

		@SafeVarargs
		public final TokenFlowsBuilder tokenFlows(TokenFlow... tokenFlowsArray) {
			requireNonNull(tokenFlowsArray, "tokenFlowsArray must be non-null!");
			builderTokenFlows = TokenFlows.tokenFlows(asList(tokenFlowsArray));
			return new TokenFlowsBuilder();
		}

		public class TokenFlowsBuilder {
			private TokenFlowsBuilder(){}
			
			@SafeVarargs
			public final InitialActionsBuilder initialActions(Action... actionsArray) {
				requireNonNull(actionsArray, "initialActionsArray must be non-null!");
				builderInitialActions = InitialActions.initialActions(asList(actionsArray));
				return new InitialActionsBuilder();
			}
		}
		
		public class InitialActionsBuilder {
			private InitialActionsBuilder(){}
			
			public final Workflow build() {
				return Workflow.initialWorkflow(builderActions, builderTokenFlows, builderInitialActions);
			}
		}
	}
}