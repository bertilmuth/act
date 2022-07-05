package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.Transit.transit;
import static org.requirementsascode.act.statemachine.ExitFlow.exitFlow;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.*;

import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Data;

/**
 * Test of a state machine for a regular language that accepts words of exactly three letters of a and b character,
 * where the last letter must be b.
 * 
 * @author b_muth
 *
 */
class RegularLanguageTest {
	private Statemachine<NonTerminal, String> statemachine;

	@BeforeEach
	void setup() {
		this.statemachine = createStatemachine();
	}

	@Test
	void acceptsThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> input = data(S0, "bbb");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertTrue(output.getState().isAccepting());
	}
	
	@Test
	void acceptsAnotherThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> input = data(S0, "aab");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertTrue(output.getState().isAccepting());
	}
	
	@Test
	void rejectsThreeLetterStringWithLastOneBeingB_ifFirstLetterIsNeitherANorB() {
		Data<NonTerminal, String> input = data(S0, "cab");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertFalse(output.getState().isAccepting());
	}
	
	@Test
	void rejectsFourLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> input = data(S0, "bbbb");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertFalse(output.getState().isAccepting());
	}
	
	@Test
	void rejectsEmptyString() {
		Data<NonTerminal, String> input = data(S0, "");
		Data<NonTerminal, String> output = statemachine.actOn(input);
		assertFalse(output.getState().isAccepting());
	}
	
	////////////////////////////////////////
	// Test state machine data starts here /
	////////////////////////////////////////

	private Statemachine<NonTerminal, String> createStatemachine() {
		State<NonTerminal, String> s0 = s(S0);
		State<NonTerminal, String> s1 = s(S1);
		State<NonTerminal, String> s2 = s(S2);
		State<NonTerminal, String> s3 = s(S3);

		Statemachine<NonTerminal, String> statemachine = Statemachine.builder()
			.states(s0,s1, s2, s3)
			.transitions(
				transition(s0, s1, accept("a", S1)),
				transition(s0, s1, accept("b", S1)),
				transition(s1, s2, accept("a", S2)),
				transition(s1, s2, accept("b", S2)),
				transition(s2, s3, accept("b", S3))
			)
			.flows(
				exitFlow(s3, inCase(i -> !i.getValue().isEmpty(), transit((s,t) -> Rejected)))
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
	
	///////////////////////////////////////////////////
	// The regular language specific code starts here /
	///////////////////////////////////////////////////
	
	private State<NonTerminal, String> s(NonTerminal nonTerminal){
		// The Behavior.identity() part is the state behavior.
		// The purpose of the state behavior is to pass on the output value of each transition that fires 
		// to another transition, unchanged.
		return state(nonTerminal.toString(), stateInvariantOf(nonTerminal), Behavior.identity());
	}
	
	private Predicate<NonTerminal> stateInvariantOf(NonTerminal nonTerminal) {
		return nonTerminal::equals;
	}
	
	private Behavior<NonTerminal, String> accept(String terminal, NonTerminal targetState) {
		return inCase(i -> 
		  isFirstLetterTheSame(terminal, i), 
		  	i -> data(targetState, tail(i.getValue())));
	}

	private boolean isFirstLetterTheSame(String expectedTerminal, Data<NonTerminal, String> i) {
		String inputValue = i.getValue();
		return !inputValue.isEmpty() && firstLetter(expectedTerminal) == firstLetter(inputValue);
	}

	private char firstLetter(String s) {
		return s.charAt(0);
	}
	
	private String tail(String input) {
		return input.substring(1);
	}
}
