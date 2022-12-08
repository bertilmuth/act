package org.requirementsascode.act.workflow;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collections;

public class WorkflowBuilder {
	WorkflowBuilder() {
	}

	@SafeVarargs
	public final NodesBuilder nodes(Node... nodesArray) {
		return new NodesBuilder(nodesArray);
	}

	public class NodesBuilder {
		private Nodes builderNodes = new Nodes(Collections.emptyList());		
		private DataFlows builderDataFlows = new DataFlows(Collections.emptyList());
		private InitialActions builderInitialActions = new InitialActions(Collections.emptyList());

		private NodesBuilder(Node[] nodes) {
			requireNonNull(nodes, "nodes must be non-null!");
			this.builderNodes = new Nodes(asList(nodes));
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
					return Workflow.createInitialWorkflow(builderNodes, builderDataFlows, builderInitialActions);
				}
			}
		}
	}
}