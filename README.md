Act
===
[![Gitter](https://badges.gitter.im/requirementsascode/community.svg)](https://gitter.im/requirementsascode/community?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
# Getting started
If you are using Maven, include the following in your POM:

``` xml
  <dependency>
    <groupId>org.requirementsascode.act</groupId>
    <artifactId>act</artifactId>
    <version>0.1.1</version>
  </dependency>
```

If you are using Gradle, include the following in your build.gradle dependencies:

```
implementation 'org.requirementsascode.act:act:0.1.1'
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

![The code of the statemachine of the shopping cart](https://github.com/bertilmuth/act/blob/main/doc/flat_statemachine_code.png)

To learn more, see [this test class](https://github.com/bertilmuth/act/blob/main/src/test/java/org/requirementsascode/act/statemachine/StateMachineTest.java)
and the [Cart class](https://github.com/bertilmuth/act/blob/main/src/test/java/org/requirementsascode/act/statemachine/testdata/Cart.java).
