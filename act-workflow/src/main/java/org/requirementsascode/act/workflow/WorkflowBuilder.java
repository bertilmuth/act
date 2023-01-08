package org.requirementsascode.act.workflow;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;

public class WorkflowBuilder {
	WorkflowBuilder() {
	}

	@SafeVarargs
	public final NodesBuilder nodes(Node... nodesArray) {
		return new NodesBuilder(nodesArray);
	}

	public class NodesBuilder {
		private List<Node> builderNodes = Collections.emptyList();		
		private DataFlows builderDataFlows = new DataFlows(Collections.emptyList());
		private StartFlows builderStartFlows = new StartFlows(Collections.emptyList());

		private NodesBuilder(Node[] nodes) {
			requireNonNull(nodes, "nodes must be non-null!");
			this.builderNodes = asList(nodes);
		}
		
		@SafeVarargs
		public final StartNodesBuilder startNodes(Node... nodesArray) {
			requireNonNull(nodesArray, "nodesArray must be non-null!");
			builderStartFlows = new StartFlows(asList(nodesArray));
			return new StartNodesBuilder();
		}

		
		public class StartNodesBuilder {
			private StartNodesBuilder(){}
			
			@SafeVarargs
			public final DataFlowsBuilder dataFlows(DataFlow<?>... dataFlowsArray) {
				requireNonNull(dataFlowsArray, "dataFlowsArray must be non-null!");
				builderDataFlows = new DataFlows(asList(dataFlowsArray));
				return new DataFlowsBuilder();
			}
			
			public class DataFlowsBuilder {
				private DataFlowsBuilder(){}
				
				public final Workflow build() {
					return Workflow.create(nodes(), dataFlows(), startFlows());
				}

				private StartFlows startFlows() {
					return builderStartFlows;
				}

				private DataFlows dataFlows() {
					return builderDataFlows;
				}

				private Nodes nodes() {
					return new Nodes(builderNodes);
				}
			}
		}
	}
}