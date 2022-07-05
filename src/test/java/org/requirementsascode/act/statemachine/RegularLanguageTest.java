package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transition.transition;

import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

class RegularLanguageTest {
	private Statemachine<NonTerminal, String> statemachine;

	@BeforeEach
	void setup() {
		this.statemachine = createStatemachine();
	}

	@Test
	void acceptsThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> input = Data.data(NonTerminal.S0, "bbb");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertTrue(output.getState().isAccepting());
	}
	
	@Test
	void acceptsAnotherThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> input = Data.data(NonTerminal.S0, "aab");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertTrue(output.getState().isAccepting());
	}
	
	@Test
	void rejectsFourLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> input = Data.data(NonTerminal.S0, "bbbb");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertFalse(output.getState().isAccepting());
	}
	
	@Test
	void rejectsEmptyString() {
		Data<NonTerminal, String> input = Data.data(NonTerminal.S0, "");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertFalse(output.getState().isAccepting());
	}

	private Statemachine<NonTerminal, String> createStatemachine() {
		State<NonTerminal, String> s0 = s("s0", NonTerminal.S0);
		State<NonTerminal, String> s1 = s("s1", NonTerminal.S1);
		State<NonTerminal, String> s2 = s("s2", NonTerminal.S2);
		State<NonTerminal, String> s3 = s("s3", NonTerminal.S3);

		Statemachine<NonTerminal, String> statemachine = Statemachine.builder()
			.states(s0,s1, s2, s3)
			.transitions(
				transition(s0, s1, accept("a", NonTerminal.S1)),
				transition(s0, s1, accept("b", NonTerminal.S1)),
				transition(s1, s2, accept("a", NonTerminal.S2)),
				transition(s1, s2, accept("b", NonTerminal.S2)),
				transition(s2, s3, accept("b", NonTerminal.S3))
			)
			.flows(
				ExitFlow.exitFlow(s3, inCase(i -> !i.getValue().isEmpty(), Transit.transit((s,t) -> NonTerminal.Rejected)))
			)
			.build();
		
		return statemachine;
	}
	
	enum NonTerminal{
		S0(false), S1(false), S2(false), S3(true), Rejected(false);

		private final boolean isAccepting;
		
		NonTerminal(boolean isAccepting){
			this.isAccepting = isAccepting;
		}

		public boolean isAccepting() {
			return isAccepting;
		}
	};
	
	private State<NonTerminal, String> s(String stateName, NonTerminal nonTerminal){
		// The Behavior.identity() part is the state's behavior.
		// The purpose is to pass on the output value of each transition that fires 
		// to another transition, unchanged.
		return state(stateName, stateEquals(nonTerminal), Behavior.identity());
	}
	
	private Predicate<NonTerminal> stateEquals(NonTerminal nonTerminal) {
		return nonTerminal::equals;
	}
	
	private Behavior<NonTerminal, String> accept(String terminal, NonTerminal target) {
		return inCase(i -> 
		  !i.getValue().isEmpty() && firstLetter(terminal) == firstLetter(i.getValue()), 
		  	i -> Data.data(target, tail(i.getValue())));
	}

	private char firstLetter(String s) {
		return s.charAt(0);
	}
	
	private String tail(String input) {
		return input.substring(1);
	}
}
