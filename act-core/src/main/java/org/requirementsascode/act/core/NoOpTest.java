package org.requirementsascode.act.core;

import java.util.function.Predicate;

public class NoOpTest<S,V> implements Predicate<Data<S, V>> {
	private final Data<S, V> nothingDone;
	
	private NoOpTest(Data<S, V> data) {
		nothingDone = new NoOp<S,V,V>().actOn(data);
	}
	
	public static <S,V> NoOpTest<S,V> noOpTest(Data<S, V> before) {
		return new NoOpTest<>(before);
	}
	
	@Override
	public boolean test(Data<S, V> before) {
		return nothingDone.equals(before);
	}

}
