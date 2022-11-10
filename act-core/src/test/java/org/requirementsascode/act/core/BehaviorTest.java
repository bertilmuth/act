package org.requirementsascode.act.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.core.InCase.inCase;
import static org.requirementsascode.act.core.UnitedBehavior.unitedBehavior;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.testdata.LastOneWhoActsWins;
import org.requirementsascode.act.core.testdata.State;
import org.requirementsascode.act.core.testdata.trigger.ConditionalTrigger_B1;
import org.requirementsascode.act.core.testdata.trigger.Trigger;
import org.requirementsascode.act.core.testdata.trigger.Trigger_B1;
import org.requirementsascode.act.core.testdata.trigger.Trigger_B2;
import org.requirementsascode.act.core.testdata.trigger.UnknownTrigger;
import static org.requirementsascode.act.core.testdata.On.on;

class BehaviorTest {
	/**
	 * States
	 */
	private static final State STATE_BEFORE_B1 = new State("State before B1");
	private static final State STATE_AFTER_B1 = new State("State after B1");
	private static final State STATE_BEFORE_B2 = new State("State before B2");
	private static final State STATE_AFTER_B2 = new State("State after B2");
	
	/**
	 * Triggers
	 */
	private static final Trigger_B1 TRIGGER_B1 = new Trigger_B1(0);
	private static final Trigger_B2 TRIGGER_B2 = new Trigger_B2(0);
	private static final ConditionalTrigger_B1 TRUE_TRIGGER_B1 = new ConditionalTrigger_B1(true);
	private static final ConditionalTrigger_B1 FALSE_TRIGGER_B1 = new ConditionalTrigger_B1(false);
	
	/**
	 * Inputs
	 */
	private static final Data<State, Trigger_B1> DATA_BEFORE_B1 = data(STATE_BEFORE_B1, TRIGGER_B1);
	private static final Data<State, ConditionalTrigger_B1> TRUE_BEFORE_B1 = data(STATE_BEFORE_B1, TRUE_TRIGGER_B1);
	private static final Data<State, ConditionalTrigger_B1> FALSE_BEFORE_B1 = data(STATE_BEFORE_B1, FALSE_TRIGGER_B1);
	private static final Data<State, Trigger_B2> DATA_BEFORE_B2 = data(STATE_BEFORE_B2, TRIGGER_B2);
	
	/**
	 * Results
	 */
	private static final Trigger_B1 RESULT_B1 = new Trigger_B1(4.56d);
	private static final Trigger_B2 RESULT_B2 = new Trigger_B2(456);
	
	/**
	 * Outputs
	 */
	private static final Data<State, Trigger_B1> DATA_AFTER_B1 = data(STATE_AFTER_B1, RESULT_B1);
	private static final Data<State, Trigger_B2> DATA_AFTER_B2 = data(STATE_AFTER_B2, RESULT_B2);
	private static final Data<State, ConditionalTrigger_B1> CONDITIONAL_AFTER_B1 = data(STATE_AFTER_B1, TRUE_TRIGGER_B1);


	@Test
	void performsB1() {
		assertEquals(DATA_AFTER_B1, b1().actOn(DATA_BEFORE_B1));
	}

	@Test
	void performsB2() {
		assertEquals(DATA_AFTER_B2, b2().actOn(DATA_BEFORE_B2));
	}

	@Test
	void performsUnitedBehavior() {
		UnitedBehavior<State, Trigger> behavior = 
			unitedBehavior(
				new LastOneWhoActsWins<>(),
				on(Trigger_B1.class.getSimpleName(), b1()), 
				on(Trigger_B2.class.getSimpleName(), b2())
			);
		
		assertEquals(DATA_AFTER_B2, behavior.actOn(data(STATE_BEFORE_B2, TRIGGER_B2)));

		assertEquals(DATA_AFTER_B1, behavior.actOn(data(STATE_BEFORE_B1, TRIGGER_B1)));
	}

	@Test
	void unitedBehaviorHasNoResultForUnknownTrigger() {
		UnitedBehavior<State, Trigger> behavior = 
			unitedBehavior(
				new LastOneWhoActsWins<>(),
				on(Trigger_B1.class.getSimpleName(), b1()), 
				on(Trigger_B2.class.getSimpleName(), b2())
			);
		Data<State, Trigger> before = data(STATE_BEFORE_B1, new UnknownTrigger());
		Data<State, Trigger> after = behavior.actOn(before);
		assertEquals(new NoOp<State, Trigger, Trigger>().actOn(before), after);
	}

	@Test
	void unitedBehaviorExecutesLatestBehaviorIfSeveralBehaviorsCanAct() {
		UnitedBehavior<State, Trigger_B1> behavior = 
			unitedBehavior(
				new LastOneWhoActsWins<>(),
				on(Trigger_B1.class.getSimpleName(), new NoOp<>()), 
				on(Trigger_B1.class.getSimpleName(), b1())
			);		
		assertEquals(DATA_AFTER_B1, behavior.actOn(data(STATE_BEFORE_B1, TRIGGER_B1)));
	}

	@Test
	void actsOnTrueTrigger() {
		assertEquals(CONDITIONAL_AFTER_B1, conditionalB1().actOn(TRUE_BEFORE_B1));
	}

	@Test
	void doesntActOnFalseTrigger() {
		assertFalse(conditionalB1().actOn(FALSE_BEFORE_B1).value().isPresent());
	}
	
	@Test
	void doesntPerformsFalseConditionalBehavior() {
		UnitedBehavior<State, ConditionalTrigger_B1> unitedBehavior = 
			unitedBehavior(new LastOneWhoActsWins<>(),
				inCase(i -> false, conditionalB1())
			);

		Data<State, ConditionalTrigger_B1> after = unitedBehavior.actOn(TRUE_BEFORE_B1);
		assertNull(after.value());
	}

	@Test
	void performsTrueConditionalBehavior() {
		UnitedBehavior<State, ConditionalTrigger_B1> unitedBehavior = 
			unitedBehavior(new LastOneWhoActsWins<>(),
				inCase(i -> false, conditionalB1()), 
				inCase(i -> true, conditionalB1())
			);

		assertEquals(CONDITIONAL_AFTER_B1, unitedBehavior.actOn(TRUE_BEFORE_B1));
	}

	private Behavior<State, Trigger_B1, Trigger_B1> b1() {
		return before -> DATA_AFTER_B1;
	}

	private Behavior<State, ConditionalTrigger_B1, ConditionalTrigger_B1> conditionalB1() {
		return inCase(before -> before.value().get().isTriggering(), before -> CONDITIONAL_AFTER_B1);
	}

	private Behavior<State, Trigger_B2, Trigger_B2> b2() {
		return before -> DATA_AFTER_B2;
	}
}