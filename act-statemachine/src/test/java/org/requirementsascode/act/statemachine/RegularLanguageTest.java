package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S0;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S1;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S2;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S3;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.entryFlow;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.*;

import java.util.function.Predicate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
	@Disabled
	void acceptsThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> before = data(S0, "bbb");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertTrue(after.state().isAccepting());
		assertEquals("", after.value().get());
	}
	
	@Test
	@Disabled
	void acceptsAnotherThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> before = data(S0, "aab");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertTrue(after.state().isAccepting());
		assertEquals("", after.value().get());
	}
	
	@Test
	@Disabled
	void acceptsFourLetterStringWithLastOneBeingB_bRemains() {
		Data<NonTerminal, String> before = data(S0, "bbbb");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertTrue(after.state().isAccepting());
		assertEquals("b", after.value().get());
	}
	
	@Test
	@Disabled
	void rejectsThreeLetterStringWithLastOneBeingB_ifFirstLetterIsNeitherANorB() {
		Data<NonTerminal, String> before = data(S0, "cab");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertFalse(after.state().isAccepting());
		assertEquals("cab", after.value().get());
	}
	
	@Test
	@Disabled
	void rejectsEmptyString() {
		Data<NonTerminal, String> before = data(S0, "");
		Data<NonTerminal, String> after = statemachine.actOn(before);
		assertFalse(after.state().isAccepting());
		assertEquals("", after.value().get());
	}
	
	////////////////////////////////////////
	// Test state machine data starts here /
	////////////////////////////////////////

	private Statemachine<NonTerminal, String> createStatemachine() {
		State<NonTerminal, String> s1 = s(S1);
		State<NonTerminal, String> s2 = s(S2);
		State<NonTerminal, String> s3 = s(S3);

		Statemachine<NonTerminal, String> statemachine = Statemachine.builder()
			.states(s1, s2, s3)
			.transitions(
				flow(s1, s2, accept('a', S2)),
				flow(s1, s2, accept('b', S2)),
				flow(s2, s3, accept('b', S3)),
				entryFlow(s1, accept('a', S1)),
				entryFlow(s1, accept('b', S1))
			)
			.isRecursive(true)
			.build();
		
		return statemachine;
	}
	
	enum NonTerminal{
		S0(false), S1(false), S2(false), S3(true);

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
		return state(nonTerminal.toString(), stateInvariantOf(nonTerminal));
	}
	
	private Predicate<NonTerminal> stateInvariantOf(NonTerminal nonTerminal) {
		return nonTerminal::equals;
	}
	
	private Behavior<NonTerminal, String, String> accept(char expectedTerminal, NonTerminal targetState) {
		return inCase(i -> 
		  isFirstLetterTheSame(expectedTerminal, i), 
		  	i -> data(targetState, tail(i.value().get())));
	}

	private boolean isFirstLetterTheSame(char expectedTerminal, Data<NonTerminal, String> i) {
		String beforeValue = i.value().get();
		return !beforeValue.isEmpty() && expectedTerminal == firstLetter(beforeValue);
	}

	private char firstLetter(String s) {
		return s.charAt(0);
	}
	
	private String tail(String before) {
		return before.substring(1);
	}
}
