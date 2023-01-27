package org.requirementsascode.act.workflow;

public interface Part<T extends ActionData, U extends ActionData>{
	Ports inPorts();
	Ports outPorts();
	Flow asFlow();
}
