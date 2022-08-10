package org.requirementsascode.act.core;

import java.util.function.Predicate;

public class NothingGotDone<S,V> implements Predicate<Data<S, V>> {
	private final Data<S, V> nothingDone;
	
	private NothingGotDone(Data<S, V> data) {
		nothingDone = new DoNothing<S,V>().actOn(data);
	}
	
	public static <S,V> NothingGotDone<S,V> nothingGotDone(Data<S, V> before) {
		return new NothingGotDone<>(before);
	}
	
	@Override
	public boolean test(Data<S, V> before) {
		return nothingDone.equals(before);
	}

}
