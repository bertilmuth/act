package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.core.Data.data;
import static org.requirementsascode.act.statemachine.ConsumeWith.consumeWith;
import static org.requirementsascode.act.statemachine.EntryFlow.entryFlow;
import static org.requirementsascode.act.statemachine.Init.init;
import static org.requirementsascode.act.statemachine.State.anyState;
import static org.requirementsascode.act.statemachine.State.state;
import static org.requirementsascode.act.statemachine.Transition.transition;
import static org.requirementsascode.act.statemachine.When.when;
import static org.requirementsascode.act.statemachine.WhenInCase.whenInCase;

import java.util.List;

import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.testdata.trigger.AddItem;
import org.requirementsascode.act.statemachine.testdata.trigger.CreateCart;
import org.requirementsascode.act.statemachine.testdata.trigger.RemoveItem;
import org.requirementsascode.act.statemachine.testdata.trigger.Trigger;

public class CartStateMachine {	
	private Cart cart;
	private final Statemachine<Cart, Trigger> statemachine;

	public CartStateMachine() {
		this.statemachine = createStatemachine();
	}
	
	public Data<Cart, Trigger> actOn(Trigger trigger) {
		Data<Cart, Trigger> before = data(cart, trigger);
		
		Data<Cart, Trigger> after = statemachine.actOn(before);
		setState(after.state());
		
		return after;
	}
	
	public List<String> items(){
		return cart.items();
	}
	
	private void setState(Cart cartState) {
		this.cart = cartState;
	}
	
	private Statemachine<Cart, Trigger> createStatemachine() {
		State<Cart, Trigger> empty = state("Empty", cart -> cart != null && cart.items().size() == 0);
		State<Cart, Trigger> nonEmpty = state("Non-Empty", cart -> cart != null && cart.items().size() > 0);
		
		Statemachine<Cart, Trigger> statemachine = Statemachine.builder()
			.states(empty,nonEmpty)
			.transitions(
				transition(anyState(), nonEmpty, 
					when(AddItem.class, 
						consumeWith(Cart::addItem))),	
				
				transition(nonEmpty, nonEmpty, 
					whenInCase(RemoveItem.class, i -> cartSize(i) > 1 && itemIsInCart(i), 
						consumeWith(Cart::removeItem))),
				
				transition(nonEmpty, empty, 
					whenInCase(RemoveItem.class, i -> cartSize(i) == 1 && itemIsInCart(i), 
						consumeWith(Cart::removeItem)))
			)
			.flows(
				entryFlow(when(CreateCart.class, init(Cart::createCart)))
			)
			.build();
		
		return statemachine;
	}
	
	
	/**************** Helper methods ****************************/
	
	private List<String> cartItems(Data<Cart, RemoveItem> data) {
		return data.state().items();
	}
	
	private int cartSize(Data<Cart, RemoveItem> data) {
		return cartItems(data).size();
	}
	
	private boolean itemIsInCart(Data<Cart, RemoveItem> i) {
		return cartItems(i).contains(i.value().item());
	}
	
	private boolean itemIsNotInCart(Data<Cart, RemoveItem> i) {
		return !itemIsInCart(i);
	}
}






