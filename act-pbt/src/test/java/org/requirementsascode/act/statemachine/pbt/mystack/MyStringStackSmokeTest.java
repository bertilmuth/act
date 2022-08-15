package org.requirementsascode.act.statemachine.pbt.mystack;

import static org.assertj.core.api.Assertions.assertThat;
import static org.requirementsascode.act.core.Data.data;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Clear;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Pop;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Push;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Value;

class MyStringStackSmokeTest {

	@Test
	void test() {
		MyStringStackStateMachine statemachine = new MyStringStackStateMachine(c -> c.after(),c -> c.after());
		
		String testString = "AAAAA";
		Push push = new Push(testString);
		Data<MyStringStack, Value> pushData = data(new MyStringStack(), push);
		
		Data<MyStringStack, Value> after = statemachine.actOn(pushData);
		assertThat(size(after)).isEqualTo(1);
		assertThat(elements(after).get(0)).isEqualTo(testString);
		
		pushData = data(after.state(), push);
		after = statemachine.actOn(pushData);
		assertThat(size(after)).isEqualTo(2);
		assertThat(elements(after).get(1)).isEqualTo(testString);
		
		pushData = data(after.state(), push);
		after = statemachine.actOn(pushData);
		assertThat(size(after)).isEqualTo(3);
		assertThat(elements(after).get(2)).isEqualTo(testString);
		
		pushData = data(after.state(), push);
		after = statemachine.actOn(pushData);
		assertThat(size(after)).isEqualTo(4);
		assertThat(elements(after).get(3)).isEqualTo(testString);
		
		Data<MyStringStack,Value> popData = data(after.state(), new Pop());
		after = statemachine.actOn(popData);
		assertThat(size(after)).isEqualTo(3);
		
		Clear clear = new Clear();
		Data<MyStringStack,Value> clearData = data(after.state(), clear);
		after = statemachine.actOn(clearData);
	}

	private List<String> elements(Data<MyStringStack, Value> data) {
		return data.state().elements();
	}

	private int size(Data<MyStringStack, Value> data) {
		return elements(data).size();
	}

}