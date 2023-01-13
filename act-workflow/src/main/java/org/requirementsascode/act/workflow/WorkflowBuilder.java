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
		private List<Flow<?,?>> builderFlows = Collections.emptyList();
		private List<Port<?>> builderStartPorts = Collections.emptyList();

		private NodesBuilder(Node[] nodes) {
			requireNonNull(nodes, "nodes must be non-null!");
			this.builderNodes = asList(nodes);
		}
		
		@SafeVarargs
		public final StartPortsBuilder startPorts(Port<?>... portsArray) {
			requireNonNull(portsArray, "portsArray must be non-null!");
			builderStartPorts = asList(portsArray);
			return new StartPortsBuilder();
		}

		
		public class StartPortsBuilder {
			private StartPortsBuilder(){}
			
			@SafeVarargs
			public final FlowsBuilder flows(Flow<?,?>... flowsArray) {
				requireNonNull(flowsArray, "flowsArray must be non-null!");
				builderFlows = asList(flowsArray);
				return new FlowsBuilder();
			}
			
			public class FlowsBuilder {
				private FlowsBuilder(){}
				
				public final Workflow build() {
					return Workflow.create(nodes(), flows(), startFlows());
				}

				private StartFlows startFlows() {
					return new StartFlows(builderStartPorts);
				}

				private Flows flows() {
					return new Flows(builderFlows);
				}

				private Nodes nodes() {
					return new Nodes(builderNodes);
				}
			}
		}
	}
}