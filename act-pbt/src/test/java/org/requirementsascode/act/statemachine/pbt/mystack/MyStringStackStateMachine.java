package org.requirementsascode.act.statemachine.pbt.mystack;

import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.core.HandleChange;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Clear;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Pop;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.PoppedElement;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Push;
import org.requirementsascode.act.statemachine.pbt.mystack.MyStringStack.Value;

public class MyStringStackStateMachine implements Behavior<MyStringStack, Value, Value>{
	private static int MAX_STACK_SIZE = 50000;
	
	private HandleChange<MyStringStack, Push, Push> pushValidator;
	private HandleChange<MyStringStack, Pop, PoppedElement> popValidator;
	private Statemachine<MyStringStack, Value> statemachine;

	public MyStringStackStateMachine(HandleChange<MyStringStack, Push, Push> pushValidator, HandleChange<MyStringStack, Pop, PoppedElement> popValidator) {
		this.pushValidator = pushValidator;
		this.popValidator = popValidator;
		this.statemachine = createStateMachine();
	}
	
	@Override
	public Data<MyStringStack, Value> actOn(Data<MyStringStack, Value> before) {
		return statemachine.actOn(before);
	}
	
	private Statemachine<MyStringStack, Value> createStateMachine() {
		State<MyStringStack, Value> stackEmpty = state("Stack Empty", MyStringStack::isEmpty);
		State<MyStringStack, Value> stackNotEmpty = state("Stack Empty", s -> !s.isEmpty());
		
		State<MyStringStack, Value> stackSizeLessThanZero = state("Stack Size Less Than Zero", s -> s.size() < 0, this::throwStackSizeLessThanZero);
		State<MyStringStack, Value> stackToBig = state("Stack Too Big", s -> s.size() > MAX_STACK_SIZE, this::throwStackSizeTooBig);
		
		return Statemachine.builder()
				.states(stackEmpty, stackNotEmpty, stackSizeLessThanZero, stackToBig)
				.transitions(
						transition(anyState(), stackNotEmpty, 
							when(Push.class, consumeWith(MyStringStack::push)
								.andHandleChangeWith(pushValidator))),
						
						transition(stackNotEmpty, stackEmpty, 
							whenInCase(Pop.class, i -> i.state().size() == 1, mapWith(MyStringStack::pop)
								.andHandleChangeWith(popValidator))),
						
						transition(stackNotEmpty, stackNotEmpty, 
							whenInCase(Pop.class, i -> i.state().size() > 1, mapWith(MyStringStack::pop)
								.andHandleChangeWith(popValidator))),
						
						transition(anyState(), stackEmpty, 
							when(Clear.class, consumeWith(MyStringStack::clear)))
				)
				.build();
	}
	
	private Data<MyStringStack,Value> throwStackSizeLessThanZero(Data<MyStringStack,Value> data) {
		throw new IllegalStateException("Stack size must be greater or equal than zero, but is " + data.state().size());
	}
	
	private Data<MyStringStack,Value> throwStackSizeTooBig(Data<MyStringStack,Value> data) {
		throw new IllegalStateException("Stack size mus be less or equal than " + MAX_STACK_SIZE + ", but is " + data.state().size());
	}
}
