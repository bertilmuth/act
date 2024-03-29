package org.requirementsascode.act.statemachine.pbt.cart;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.requirementsascode.act.statemachine.StatemachineApi.data;
import static org.requirementsascode.act.statemachine.pbt.cart.Cart.cart;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.AddItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.CreateCart;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.RemoveItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.Value;

class CartStateMachineExampleBasedTest {
	private CartStateMachine statemachine;

	@BeforeEach
	void setup() {
		statemachine = new CartStateMachine();
	}

	@Test
	void createsEmptyCart() {
		Data<Cart, Value> createFromScratch = data(null, new CreateCart());
		Data<Cart, Value> after = statemachine.actOn(createFromScratch);
		assertThat(cartSize(after)).isEqualTo(0);
	}

	@Test
	void addsOneItem() {
		final String testString = "AAAAA";
		Data<Cart, Value> add = data(cart(emptyList()), new AddItem(testString));
		Data<Cart, Value> after = statemachine.actOn(add);
		assertThat(items(after)).containsExactly(testString);
		assertThat(quantityOf(after, testString)).isEqualTo(1);
	}

	@Test
	void addsTheSameItemTwice() {
		final String testString = "AAAAA";
		Data<Cart, Value> add = data(cart(asList(testString)), new AddItem(testString));
		Data<Cart, Value> after = statemachine.actOn(add);
		assertThat(items(after)).containsExactly(testString);
		assertThat(quantityOf(after, testString)).isEqualTo(2);
	}
	
	@Test
	void removesOneItem() {
		final String testString = "AAAAA";
		Data<Cart, Value> remove = data(cart(asList(testString)), new RemoveItem(testString));
		Data<Cart, Value> after = statemachine.actOn(remove);
		assertThat(cartSize(after)).isEqualTo(0);
	}
	
	@Test
	void removesNonExistingItem() {
		final String testString = "AAAAA";
		Data<Cart, Value> remove = data(cart(asList()), new RemoveItem(testString));
		Data<Cart, Value> after = statemachine.actOn(remove);
		assertThat(cartSize(after)).isEqualTo(0);
	}
	
	@Test
	void removesItemThatDoesntExist() {
		final String testString = "AAAAA";
		Data<Cart, Value> add = data(cart(asList(testString)), new RemoveItem(testString));
		Data<Cart, Value> after = statemachine.actOn(add);
		assertThat(cartSize(after)).isEqualTo(0);
	}

	private List<String> items(Data<Cart, Value> after) {
		return after.state().items();
	}
	
	private long quantityOf(Data<Cart, Value> after, String item) {
		return after.state().quantityOf(item);
	}

	private int cartSize(Data<Cart, Value> data) {
		return items(data).size();
	}
}