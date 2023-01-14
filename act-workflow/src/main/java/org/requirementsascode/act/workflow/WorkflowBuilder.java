package org.requirementsascode.act.workflow;

import static java.util.Arrays.asList;
import static java.util.Objects.requireNonNull;

import java.util.Collections;
import java.util.List;

public class WorkflowBuilder {
	WorkflowBuilder() {
	}

	@SafeVarargs
	public final ActionsBuilder actions(Action<?,?>... actionsArray) {
		return new ActionsBuilder(actionsArray);
	}

	public class ActionsBuilder {
		private List<Action<?,?>> builderActions = Collections.emptyList();		
		private List<Flow<?,?>> builderFlows = Collections.emptyList();
		private List<Port<?>> builderPorts = Collections.emptyList();
		private List<Port<?>> builderInPorts = Collections.emptyList();

		private ActionsBuilder(Action<?,?>[] actions) {
			requireNonNull(actions, "actions must be non-null!");
			this.builderActions = asList(actions);
		}
		
		@SafeVarargs
		public final PortsBuilder ports(Port<?>... portsArray) {
			requireNonNull(portsArray, "portsArray must be non-null!");
			builderPorts = asList(portsArray);
			return new PortsBuilder();
		}
		
		public class PortsBuilder {
			private PortsBuilder(){}
			
			@SafeVarargs
			public final InPortsBuilder inPorts(Port<?>... portsArray) {
				requireNonNull(portsArray, "portsArray must be non-null!");
				builderInPorts = asList(portsArray);
				return new InPortsBuilder();
			}
		}

		
		public class InPortsBuilder {
			private InPortsBuilder(){}
			
			@SafeVarargs
			public final FlowsBuilder flows(Flow<?,?>... flowsArray) {
				requireNonNull(flowsArray, "flowsArray must be non-null!");
				builderFlows = asList(flowsArray);
				return new FlowsBuilder();
			}
			
			public class FlowsBuilder {
				private FlowsBuilder(){}
				
				public final Workflow build() {
					return Workflow.create(actions(), ports(), flows(), inFlows());
				}

				private InFlows inFlows() {
					return new InFlows(builderInPorts);
				}
				
				private Ports ports() {
					return new Ports(builderPorts);
				}

				private Flows flows() {
					return new Flows(builderFlows);
				}

				private Actions actions() {
					return new Actions(builderActions);
				}
			}
		}
	}
}