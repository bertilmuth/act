package org.requirementsascode.act.core;

import static org.requirementsascode.act.core.Data.data;

public class DoNothing<S,V> implements Behavior<S, V>{
	@Override
	public Data<S,V> actOn(Data<S,V> before) {
		return data(before.state());
	}
}