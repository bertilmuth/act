package org.requirementsascode.act.statemachine.pbt.mystack;
import static org.assertj.core.api.Assertions.assertThat;
import static org.requirementsascode.act.statemachine.pbt.StateMachineEvent.stateMachineEvent;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Change;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Clear;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Pop;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.PoppedElement;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Push;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Value;

import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.arbitraries.TypeArbitrary;
import net.jqwik.api.lifecycle.BeforeProperty;
import net.jqwik.api.state.Action;
import net.jqwik.api.state.ActionChain;

/**
 * This is a statemachine based variation of the jqwik property based testing class:
 * https://github.com/jlink/jqwik/blob/main/documentation/src/test/java/net/jqwik/docs/state/mystack/MyStringStackExamples.java
 * 
 * @author BertilMuth
 *
 */
class MyStringStackTest {
	private Behavior<MyStringStack, Value, Value> statemachine;
	
	@BeforeProperty
	void setup() {
		this.statemachine = new MyStringStackStateMachine(this::validatePush, this::validatePop);
	}
	
	@Property
	void checkMyStack(@ForAll("myStackActions") ActionChain<MyStringStack> chain) {
		chain.run();
	}
	
	@Provide
	Arbitrary<ActionChain<MyStringStack>> myStackActions() {
		
		return ActionChain.startWith(MyStringStack::new)
		  .addAction(arbitraryActionOf(Push.class))
		  .addAction(arbitraryActionOf(Pop.class))
		  .addAction(arbitraryActionOf(Clear.class));
	}
	
	private Action<MyStringStack> arbitraryActionOf(Class<? extends Value> actionClass) {
		TypeArbitrary<? extends Value> triggers = Arbitraries.forType(actionClass);
		return stateMachineEvent(triggers, statemachine);
	}
	
	private Data<MyStringStack, Push> validatePush(Change<MyStringStack, Push, Push> c) {
		assertThat(afterStack(c).size()).isEqualTo(beforeStack(c).size() + 1);		
		return c.after();
	}
	
	private Data<MyStringStack, PoppedElement> validatePop(Change<MyStringStack, Pop, PoppedElement> c) {		
		assertThat(poppedElement(c)).isEqualTo(beforeStack(c).top());
		assertThat(afterStack(c).size()).isEqualTo(beforeStack(c).size() - 1);
		return c.after();
	}
	
	/*** Helper methods ***/
	private MyStringStack beforeStack(Change<MyStringStack, ?, ?> c) {
		return c.before().state();
	}
	
	private MyStringStack afterStack(Change<MyStringStack, ?, ?> c) {
		return c.after().state();
	}
	
	private String poppedElement(Change<MyStringStack, Pop, PoppedElement> c) {
		return c.after().value().text();
	}
}
