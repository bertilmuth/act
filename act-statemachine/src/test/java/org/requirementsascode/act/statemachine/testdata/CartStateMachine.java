package org.requirementsascode.act.statemachine.testdata;

import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;
import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.StatemachineApi.entryFlow;
import static org.requirementsascode.act.statemachine.StatemachineApi.init;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;

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
					whenInCase(RemoveItem.class, d -> cartSize(d) > 1 && itemIsInCart(d), 
						consumeWith(Cart::removeItem))),
								
				transition(nonEmpty, empty, 
					whenInCase(RemoveItem.class, d -> cartSize(d) == 1 && itemIsInCart(d), 
						consumeWith(Cart::removeItem))),
				
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
	
	private boolean itemIsInCart(Data<Cart, RemoveItem> data) {
		return cartItems(data).contains(item(data));
	}
	
	private String item(Data<Cart, RemoveItem> d) {
		RemoveItem value = d.value().get();
		return value.item();
	}
}






