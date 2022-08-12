package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S0;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S1;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S2;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S3;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.WordTooLong;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transition.transition;

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
		Data<NonTerminal, String> before = data(S0, "bbb");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertTrue(after.state().isAccepting());
	}
	
	@Test
	void acceptsAnotherThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> before = data(S0, "aab");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertTrue(after.state().isAccepting());
	}
	
	@Test
	void rejectsThreeLetterStringWithLastOneBeingB_ifFirstLetterIsNeitherANorB() {
		Data<NonTerminal, String> before = data(S0, "cab");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertFalse(after.state().isAccepting());
	}
	
	@Test
	void rejectsFourLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> before = data(S0, "bbbb");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertFalse(after.state().isAccepting());
	}
	
	@Test
	void rejectsEmptyString() {
		Data<NonTerminal, String> before = data(S0, "");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertFalse(after.state().isAccepting());
	}
	
	////////////////////////////////////////
	// Test state machine data starts here /
	////////////////////////////////////////

	private Statemachine<NonTerminal, String> createStatemachine() {
		State<NonTerminal, String> s0 = s(S0);
		State<NonTerminal, String> s1 = s(S1);
		State<NonTerminal, String> s2 = s(S2);
		State<NonTerminal, String> s3 = s(S3);
		State<NonTerminal, String> wordTooLong = s(WordTooLong);

		Statemachine<NonTerminal, String> statemachine = Statemachine.builder()
			.states(s0,s1, s2, s3, wordTooLong)
			.transitions(
				transition(s0, s1, accept('a', S1)),
				transition(s0, s1, accept('b', S1)),
				transition(s1, s2, accept('a', S2)),
				transition(s1, s2, accept('b', S2)),
				transition(s2, s3, accept('b', S3)),
				transition(s3, wordTooLong, inCase(i -> !i.value().isEmpty(), i -> data(WordTooLong)))
			)
			.build();
		
		return statemachine;
	}
	
	enum NonTerminal{
		S0(false), S1(false), S2(false), S3(true), WordTooLong(false);

		private final boolean accepting;
		
		NonTerminal(boolean accepting){
			this.accepting = accepting;
		}

		public boolean isAccepting() {
			return accepting;
		}
	};
	
	///////////////////////////////////////////////////
	// The regular language specific code starts here /
	///////////////////////////////////////////////////
	
	private State<NonTerminal, String> s(NonTerminal nonTerminal){
		// The Behavior.identity() part is the state behavior.
		// The purpose of the state behavior is to pass on the after value of each transition that fires 
		// to another transition, unchanged.
		return state(nonTerminal.toString(), stateInvariantOf(nonTerminal), Behavior.identity());
	}
	
	private Predicate<NonTerminal> stateInvariantOf(NonTerminal nonTerminal) {
		return nonTerminal::equals;
	}
	
	private Behavior<NonTerminal, String, String> accept(char expectedTerminal, NonTerminal targetState) {
		return inCase(i -> 
		  isFirstLetterTheSame(expectedTerminal, i), 
		  	i -> data(targetState, tail(i.value())));
	}

	private boolean isFirstLetterTheSame(char expectedTerminal, Data<NonTerminal, String> i) {
		String beforeValue = i.value();
		return !beforeValue.isEmpty() && expectedTerminal == firstLetter(beforeValue);
	}

	private char firstLetter(String s) {
		return s.charAt(0);
	}
	
	private String tail(String before) {
		return before.substring(1);
	}
}