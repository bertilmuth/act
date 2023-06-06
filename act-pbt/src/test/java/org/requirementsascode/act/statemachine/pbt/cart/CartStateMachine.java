package org.requirementsascode.act.statemachine.pbt.cart;

import static org.requirementsascode.act.statemachine.StatemachineApi.anyState;
import static org.requirementsascode.act.statemachine.StatemachineApi.consumeWith;
import static org.requirementsascode.act.statemachine.StatemachineApi.entryFlow;
import static org.requirementsascode.act.statemachine.StatemachineApi.init;
import static org.requirementsascode.act.statemachine.StatemachineApi.state;
import static org.requirementsascode.act.statemachine.StatemachineApi.transition;
import static org.requirementsascode.act.statemachine.StatemachineApi.when;
import static org.requirementsascode.act.statemachine.StatemachineApi.whenInCase;
import static org.requirementsascode.act.statemachine.pbt.Property.property;
import static org.requirementsascode.act.statemachine.pbt.PropertyValidator.validate;

import java.util.Collections;
import java.util.List;

import org.requirementsascode.act.core.Behavior;
import org.requirementsascode.act.core.Change;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.State;
import org.requirementsascode.act.statemachine.Statemachine;
import org.requirementsascode.act.statemachine.pbt.PropertyValidator;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.AddItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.CreateCart;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.RemoveItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.Value;

public class CartStateMachine implements Behavior<Cart,Value,Value>{	
	private final Statemachine<Cart, Value> statemachine;
	
	public CartStateMachine() {
		this.statemachine = createStatemachine();
	}
	
	public Data<Cart, Value> actOn(Data<Cart,Value> before) {		
		return statemachine.actOn(before);		
	}
	
	private Statemachine<Cart, Value> createStatemachine() {
		State<Cart, Value> empty = state("Empty", cart -> cart != null && cart.items().size() == 0);
		State<Cart, Value> nonEmpty = state("Non-Empty", cart -> cart != null && cart.items().size() > 0);
		
		PropertyValidator<Cart, RemoveItem> itemRemoved = itemRemoved();
		PropertyValidator<Cart, RemoveItem> itemNotRemoved = itemNotRemoved();
		
		Statemachine<Cart, Value> statemachine = Statemachine.builder()
			.states(empty,nonEmpty)
			.transitions(
				transition(anyState(), nonEmpty, 
					when(AddItem.class, consumeWith(Cart::addItem))),	
				
				transition(nonEmpty, nonEmpty, 
					whenInCase(RemoveItem.class, i -> size(i) > 1 && itemIsInCart(i), 
						consumeWith(Cart::removeItem).andHandleChangeWith(itemRemoved))),
				
				transition(nonEmpty, empty, 
					whenInCase(RemoveItem.class, i -> size(i) == 1 && itemIsInCart(i), 
						consumeWith(Cart::removeItem).andHandleChangeWith(itemRemoved))),
				
				transition(anyState(), anyState(), 
					whenInCase(RemoveItem.class, this::itemIsNotInCart, 
						consumeWith(Cart::removeItem).andHandleChangeWith(itemNotRemoved))),
				
				entryFlow(when(CreateCart.class, init(Cart::create)))
			)
			.build();
		
		return statemachine;
	}
	
	private PropertyValidator<Cart, RemoveItem> itemRemoved() {
		return validate(
				property(c -> sizeAfter(c) == sizeBeforeMinus1(c),
						c -> "itemRemoved property: Expected cart size of " + sizeBeforeMinus1(c) + ", but was " + sizeAfter(c)),
				property(c -> itemOccurencesAfter(c) == itemOccurencesBeforeMinus1(c),
						c -> "itemRemoved property: Expected #items of " + itemOccurencesBeforeMinus1(c) + ", but was "
								+ itemOccurencesAfter(c)));
	}

	private PropertyValidator<Cart, RemoveItem> itemNotRemoved() {
		return validate(property(c -> sizeAfter(c) == sizeBefore(c),
				c -> "itemNotRemoved property: Expected cart size: " + sizeBefore(c) + ", but was: " + sizeAfter(c)));
	}
	
	/**************** Helper methods ****************************/
	
	private List<String> items(Data<Cart, ?> d) {
		return d.state().items();
	}
	
	private String item(Data<Cart, RemoveItem> d) {
		return d.value().item();
	}
	
	private int size(Data<Cart, ?> d) {
		return items(d).size();
	}

	private int sizeBefore(Change<Cart, ?, ?> c) {
		return size(c.before());
	}
	private int sizeBeforeMinus1(Change<Cart, ?, ?> c) {
		return sizeBefore(c)-1;
	}
	private int sizeAfter(Change<Cart, ?, ?> c) {
		return size(c.after());
	}
	
	private boolean itemIsInCart(Data<Cart, RemoveItem> d) {
		return items(d).contains(item(d));
	}

	private boolean itemIsNotInCart(Data<Cart, RemoveItem> d) {
		return !itemIsInCart(d);
	}
	
	private int occurences(Data<Cart, ?> d, String item) {
		return Collections.frequency(items(d), item);
	}
	
	private int itemOccurencesBefore(Change<Cart, RemoveItem, RemoveItem> c) {
		return occurences(c.before(), item(c.before()));
	}
	
	private int itemOccurencesBeforeMinus1(Change<Cart, RemoveItem, RemoveItem> c) {
		return itemOccurencesBefore(c)-1;
	}
	
	private int itemOccurencesAfter(Change<Cart, RemoveItem, RemoveItem> c) {
		return occurences(c.after(), item(c.before()));
	}
}






