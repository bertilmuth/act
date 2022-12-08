package org.requirementsascode.act.workflow;

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
		private Actions builderActions = new Actions(Collections.emptyList());		
		private DataFlows builderDataFlows = new DataFlows(Collections.emptyList());
		private InitialActions builderInitialActions = new InitialActions(Collections.emptyList());

		private ActionsBuilder(Action[] actions) {
			requireNonNull(actions, "actions must be non-null!");
			this.builderActions = new Actions(asList(actions));
		}
		
		@SafeVarargs
		public final InitialActionsBuilder initialActions(Action... actionsArray) {
			requireNonNull(actionsArray, "initialActionsArray must be non-null!");
			builderInitialActions = new InitialActions(asList(actionsArray));
			return new InitialActionsBuilder();
		}

		
		public class InitialActionsBuilder {
			private InitialActionsBuilder(){}
			
			@SafeVarargs
			public final DataFlowsBuilder dataFlows(DataFlow<?>... dataFlowsArray) {
				requireNonNull(dataFlowsArray, "dataFlowsArray must be non-null!");
				builderDataFlows = new DataFlows(asList(dataFlowsArray));
				return new DataFlowsBuilder();
			}
			
			public class DataFlowsBuilder {
				private DataFlowsBuilder(){}
				
				public final Workflow build() {
					return Workflow.createInitialWorkflow(builderActions, builderDataFlows, builderInitialActions);
				}
			}
		}
	}
}