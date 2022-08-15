package org.requirementsascode.act.statemachine.pbt.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.requirementsascode.act.core.Data.data;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.requirementsascode.act.core.Data;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.AddItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.CreateCart;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.RemoveItem;
import org.requirementsascode.act.statemachine.pbt.cart.Cart.Value;

class CartStateMachineSmokeTest {

	@Test
	void test() {
		CartStateMachine statemachine = new CartStateMachine();
		Data<Cart,Value> create = data(null, new CreateCart());
		var after = statemachine.actOn(create);
		assertThat(cartSize(after)).isEqualTo(0);
		
		String testString = "AAAAA";
		
		Data<Cart,Value> add = data(after.state(), new AddItem(testString));
		after = statemachine.actOn(add);
		assertThat(cartSize(after)).isEqualTo(1);
		assertThat(items(after).get(0)).isEqualTo(testString);
		
		add = data(after.state(), new AddItem(testString));
		after = statemachine.actOn(add);
		assertThat(cartSize(after)).isEqualTo(2);
		assertThat(items(after).get(1)).isEqualTo(testString);
		
		add = data(after.state(), new AddItem(testString));
		after = statemachine.actOn(add);
		assertThat(cartSize(after)).isEqualTo(3);
		assertThat(items(after).get(2)).isEqualTo(testString);
		
		Data<Cart,Value> remove = data(after.state(), new RemoveItem(testString));
		after = statemachine.actOn(remove);
		assertThat(cartSize(after)).isEqualTo(2);
		
		remove = data(after.state(), new RemoveItem(testString));
		after = statemachine.actOn(remove);
		assertThat(cartSize(after)).isEqualTo(1);
		
		remove = data(after.state(), new RemoveItem(testString));
		after = statemachine.actOn(remove);
		assertThat(cartSize(after)).isEqualTo(0);
	}

	private List<String> items(Data<Cart, Value> after) {
		return after.state().items();
	}

	private int cartSize(Data<Cart, Value> data) {
		return items(data).size();
	}

}