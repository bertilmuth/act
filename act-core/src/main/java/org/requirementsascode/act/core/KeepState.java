package org.requirementsascode.act.core;

import static org.requirementsascode.act.core.Data.data;

public class KeepState<S,V1,V2> implements Behavior<S, V1, V2>{
	@Override
	public Data<S,V2> actOn(Data<S,V1> before) {
		return data(before.state());
	}
}