Act
===
[![Gitter](https://badges.gitter.im/requirementsascode/community.svg)](https://gitter.im/requirementsascode/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)

# Getting started
If you are using Maven, include the following in your POM:

``` xml
  <dependency>
    <groupId>org.requirementsascode.act</groupId>
    <artifactId>act</artifactId>
    <version>0.1.3</version>
  </dependency>
```

If you are using Gradle, include the following in your build.gradle dependencies:

```
implementation 'org.requirementsascode.act:act:0.1.3'
```

# Example usage

As an example, look at the following state machine for a shopping cart.

It represents its two fundamental states of being either empty, or non-empty.

The AddItem trigger causes a change to the non-empty cart state.

The RemoveItem trigger causes a change to the empty cart state, if the cart only contains 1 item.

![Image of a statemachine of a shopping cart, with two states](https://github.com/bertilmuth/act/blob/main/doc/flat_statemachine_without_invariants_diagram.png)

Act uses invariant conditions to be able to check if the state machine is in a certain state.

Here's the state machine diagram with the states' invariants (yellow sticky notes).

![Image of a statemachine of a shopping cart, with two states and invariants](https://github.com/bertilmuth/act/blob/main/doc/flat_statemachine_diagram.png)

And here's how the state machine is presented in code:

``` java
State<CartState, Trigger> emptyCartState = state("Empty Cart", cart -> cart != null && cart.getItems().size() == 0);
State<CartState, Trigger> nonEmptyCartState = state("Non-Empty Cart", cart -> cart != null && cart.getItems().size() > 0);

Statemachine<CartState, Trigger> statemachine = Statemachine.builder()
  .states(emptyCartState,nonEmptyCartState)
  .transitions(
    transition(emptyCartState, nonEmptyCartState, 
      when(AddItem.class, transit(CartState::addItem))),

    transition(nonEmptyCartState, nonEmptyCartState, 
      when(AddItem.class, transit(CartState::addItem))),

    transition(nonEmptyCartState, nonEmptyCartState, 
      when(RemoveItem.class, inCase(i -> i.getState().getItems().size() > 1, transit(CartState::removeItem)))),

    transition(nonEmptyCartState, emptyCartState, 
      when(RemoveItem.class, inCase(i -> i.getState().getItems().size() == 1, transit(CartState::removeItem))))
  )
  .flows(
    entryFlow(when(CreateCart.class, init(CartState::createCart)))
  )
  .build();
```

To learn more, see [this test class](https://github.com/bertilmuth/act/blob/main/src/test/java/org/requirementsascode/act/statemachine/StateMachineTest.java)
and the [Cart class](https://github.com/bertilmuth/act/blob/main/src/test/java/org/requirementsascode/act/statemachine/testdata/Cart.java).

# Hierarchical state machines (a.k.a. state charts)
You can use Act to create hierarchical state machines as well.

That means: states can have a behavior that is a state machine itself.

Have a look at [this class](https://github.com/bertilmuth/act/blob/main/src/test/java/org/requirementsascode/act/statemachine/testdata/HierarchicalCart.java) for details on how to create one.

# Processing regular languages
Act can be used to decide whether a word is part of a regular language or not.

[This test class](https://github.com/bertilmuth/act/blob/main/src/test/java/org/requirementsascode/act/statemachine/RegularLanguageTest.java) shows how to do it. 

# Why another state machine library?
If you notice that you use complicated if-else checks throughout your code, or your application's behavior
changes over time depending on some application state, you might benefit from using a state machine in your code.

But why would you use Act to define an executable state machine model?
The main difference between Act and many other state machine libraries is the relationship between state machine and application state.

In most state machine libraries, the state of the state machine is only losely related to the "real" appliction state. 
You need to build logic to keep the two in sync. And that logic may be error-prone.

In contrast, Act directly operates on the application state. Act checks in which state the state machine is by checking invariant conditions. These conditions are based on the application state. Also, transitions directly transform the application state.

That has some interesting benefits, for example:

* Act can automatically check if the application really is in the intended target state of a transition. This is great for verification and can e.g. be used together with property based testing to verify the application behavior.

* When application state is persisted and reloaded, the state machine will automatically be reset to the latest state. 

In short, Act brings the state machine closer to what really happens.

Apart from that, Act is a light-weight implementation (single jar, <64 kBytes).
Ease of use is a key design goal. 
