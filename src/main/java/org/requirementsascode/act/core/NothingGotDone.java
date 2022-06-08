package org.requirementsascode.act.core;

import java.util.function.Predicate;

public class NothingGotDone<S,V> implements Predicate<Data<S, V>> {
	private final Data<S, V> nothingDoneWithInput;
	
	private NothingGotDone(Data<S, V> referenceInput) {
		nothingDoneWithInput = new DoNothing<S,V>().actOn(referenceInput);
	}
	
	public static <S,V> NothingGotDone<S,V> nothingGotDone(Data<S, V> input) {
		return new NothingGotDone<>(input);
	}
	
	@Override
	public boolean test(Data<S, V> actualInput) {
		return nothingDoneWithInput.equals(actualInput);
	}

}
