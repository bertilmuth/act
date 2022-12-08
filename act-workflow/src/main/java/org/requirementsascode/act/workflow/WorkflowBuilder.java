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
		private InitialNodes builderInitialNodes = new InitialNodes(Collections.emptyList());

		private NodesBuilder(Node[] nodes) {
			requireNonNull(nodes, "nodes must be non-null!");
			this.builderNodes = new Nodes(asList(nodes));
		}
		
		@SafeVarargs
		public final InitialNodesBuilder initialNodes(Action... nodesArray) {
			requireNonNull(nodesArray, "nodesArray must be non-null!");
			builderInitialNodes = new InitialNodes(asList(nodesArray));
			return new InitialNodesBuilder();
		}

		
		public class InitialNodesBuilder {
			private InitialNodesBuilder(){}
			
			@SafeVarargs
			public final DataFlowsBuilder dataFlows(DataFlow<?>... dataFlowsArray) {
				requireNonNull(dataFlowsArray, "dataFlowsArray must be non-null!");
				builderDataFlows = new DataFlows(asList(dataFlowsArray));
				return new DataFlowsBuilder();
			}
			
			public class DataFlowsBuilder {
				private DataFlowsBuilder(){}
				
				public final Workflow build() {
					return Workflow.createInitialWorkflow(builderNodes, builderDataFlows, builderInitialNodes);
				}
			}
		}
	}
}