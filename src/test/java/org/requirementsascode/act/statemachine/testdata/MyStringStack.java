package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.requirementsascode.act.core.Data;

/**
 * This is a statemachine based variation of the jqwik property based testing class:
 * https://github.com/jlink/jqwik/blob/main/documentation/src/test/java/net/jqwik/docs/state/mystack/MyStringStack.java
 * 
 * @author BertilMuth
 *
 */
public class MyStringStack {
	public interface Value{};
	
	public static class Push implements Value {
		private String text;
				
		public Push(String text) {
			this.text = text;
		}
		
		public String text() {
			return text;
		}
		
		@Override
		public String toString() {
			return "Push [text=" + text + "]";
		}
	};
	
	public static class Pop implements Value {		
		public Pop() { }
		
		@Override
		public String toString() {
			return "Pop []";
		}
	};
	
	public static class PoppedElement implements Value {
		private String text;
		
		public PoppedElement(String text) {
			this.text = text;
		}
		
		public String text() {
			return text;
		}
		
		@Override
		public String toString() {
			return "PoppedElement [text=" + text + "]";
		}
	};
	
	public static class Clear implements Value {};
		
	private final List<String> elements = new ArrayList<>();
	
	public MyStringStack(){
		this(new ArrayList<>());
	}
	
	private MyStringStack(List<String> elements){
		this.elements.addAll(elements);
	}

	MyStringStack push(Push push) {
		List<String> newElements = new ArrayList<>(elements);
		newElements.add(0, push.text());
		return new MyStringStack(newElements);
	}

	Data<MyStringStack, PoppedElement> pop() {		
		List<String> newElements = new ArrayList<>(elements);
		String element = newElements.remove(0);
		return data(new MyStringStack(newElements), new PoppedElement(element));
	}

	MyStringStack clear(Clear clear) {
		return new MyStringStack();
	}

	public boolean isEmpty() {
		return elements.isEmpty();
	}

	public int size() {
		return elements.size();
	}

	public String top() {
		return elements.get(0);
	}
	
	public List<String> elements() {
		return Collections.unmodifiableList(elements);
	}

	@Override
	public String toString() {
		return elements.toString();
	}
}
