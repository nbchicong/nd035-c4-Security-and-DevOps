package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
  @InjectMocks
  private OrderController orderController;
  @Mock
  private UserRepository userRepository;
  @Mock
  private OrderRepository orderRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void submitOrder_ok() {
    User user = getUser();
    user.setCart(getCart());
    UserOrder order = UserOrder.createFromCart(user.getCart());
    order.setId(1L);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(orderRepository.save(any(UserOrder.class))).thenReturn(order);

    ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(order.getId(), response.getBody().getId());
    assertEquals(order.getUser().getId(), response.getBody().getUser().getId());
  }

  @Test
  public void submitOrder_usernameNotFound() {
    User user = getUser();
    user.setCart(getCart());
    UserOrder order = UserOrder.createFromCart(user.getCart());
    order.setId(1L);
    when(userRepository.findByUsername(anyString())).thenReturn(null);

    ResponseEntity<UserOrder> response = orderController.submit(user.getUsername());

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void historyOrder_ok() {
    User user = getUser();
    user.setCart(getCart());
    UserOrder order = UserOrder.createFromCart(user.getCart());
    order.setId(1L);
    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(orderRepository.findByUser(any(User.class))).thenReturn(Collections.singletonList(order));

    ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
  }

  @Test
  public void historyOrder_usernameNotFound() {
    User user = getUser();
    user.setCart(getCart());
    UserOrder order = UserOrder.createFromCart(user.getCart());
    order.setId(1L);
    when(userRepository.findByUsername(anyString())).thenReturn(null);

    ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser(user.getUsername());

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private Cart getCart() {
    Cart cart = new Cart();
    cart.setId(1L);
    cart.setUser(getUser());
    List<Item> items = new ArrayList<>();
    items.add(getItem());
    cart.setItems(items);
    cart.setTotal(new BigDecimal(1));
    return cart;
  }

  private User getUser() {
    User user = new User();
    user.setId(1L);
    user.setUsername("new_user");
    user.setPassword("12345678");
    user.setCart(new Cart());
    return user;
  }

  private Item getItem() {
    Item item = new Item();
    item.setId(1L);
    item.setName("Java Core");
    item.setPrice(new BigDecimal(1));
    item.setDescription("Java Core book");
    return item;
  }
}
