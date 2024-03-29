package org.requirementsascode.act.statemachine;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S0;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S1;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S2;
import static org.requirementsascode.act.statemachine.RegularLanguageTest.NonTerminal.S3;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.entryFlow;
import static org.requirementsascode.act.statemachine.StatemachineApi.flow;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;

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
		Data<NonTerminal, String> d = data(S0, "bbb");
		assertTrue(handle(d).isAccepting());
	}
	
	@Test
	void acceptsAnotherThreeLetterStringWithLastOneBeingB() {
		Data<NonTerminal, String> d = data(S0, "aab");
		assertTrue(handle(d).isAccepting());
	}
	
	@Test
	void acceptsFourLetterStringWithLastOneBeingB_bRemains() {
		Data<NonTerminal, String> d = data(S0, "bbbb");
		assertTrue(handle(d).isAccepting());
	}
	
	@Test
	void rejectsThreeLetterStringWithLastOneBeingB_ifFirstLetterIsNeitherANorB() {
		Data<NonTerminal, String> d = data(S0, "cab");
		assertFalse(handle(d).isAccepting());
	}
	
	@Test
	void rejectsEmptyString() {
		Data<NonTerminal, String> d = data(S0, "");
		assertFalse(handle(d).isAccepting());
	}
	
	private NonTerminal handle(Data<NonTerminal, String> before) {
		return statemachine.actOn(before).state();
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
		  	i -> data(targetState, tail(i.value())));
	}

	private boolean isFirstLetterTheSame(char expectedTerminal, Data<NonTerminal, String> before) {
		return !before.value().isEmpty() && expectedTerminal == firstLetter(before.value());
	}

	private char firstLetter(String s) {
		return s.charAt(0);
	}
	
	private String tail(String before) {
		return before.substring(1);
	}
}
