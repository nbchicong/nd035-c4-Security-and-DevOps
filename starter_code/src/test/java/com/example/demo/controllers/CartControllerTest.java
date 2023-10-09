package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
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
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class CartControllerTest {
  @InjectMocks
  private CartController cartController;
  @Mock
  private UserRepository userRepository;
  @Mock
  private CartRepository cartRepository;
  @Mock
  private ItemRepository itemRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void addCartItem_ok() {
    Cart cart = getNewCart();
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(cart.getItems().get(0).getId());
    modifyCartRequest.setUsername(cart.getUser().getUsername());
    modifyCartRequest.setQuantity(1);

    when(userRepository.findByUsername(anyString())).thenReturn(getUser());
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(getItem()));
    when(cartRepository.save(any(Cart.class))).thenReturn(cart);

    ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(cart.getId(), response.getBody().getId());
    assertEquals(cart.getUser().getId(), response.getBody().getUser().getId());
    assertEquals(1, response.getBody().getItems().size());
  }

  @Test
  public void addCartItem_usernameNotFound() {
    Cart cart = getNewCart();
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(cart.getItems().get(0).getId());
    modifyCartRequest.setUsername(cart.getUser().getUsername());
    modifyCartRequest.setQuantity(1);

    when(userRepository.findByUsername(anyString())).thenReturn(null);

    ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void addCartItem_itemNotFound() {
    Cart cart = getNewCart();
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(cart.getItems().get(0).getId());
    modifyCartRequest.setUsername(cart.getUser().getUsername());
    modifyCartRequest.setQuantity(1);

    when(userRepository.findByUsername(anyString())).thenReturn(getUser());
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void removeCartItem_ok() {
    User user = getUser();
    Cart cart = getNewCart();
    user.setCart(cart);
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(cart.getItems().get(0).getId());
    modifyCartRequest.setUsername(cart.getUser().getUsername());
    modifyCartRequest.setQuantity(1);

    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(getItem()));
    Cart result = user.getCart();
    result.setItems(Collections.emptyList());
    when(cartRepository.save(any(Cart.class))).thenReturn(result);

    ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(cart.getId(), response.getBody().getId());
    assertEquals(cart.getUser().getId(), response.getBody().getUser().getId());
    assertEquals(0, response.getBody().getItems().size());
  }

  @Test
  public void removeCartItem_usernameNotFound() {
    User user = getUser();
    Cart cart = getNewCart();
    user.setCart(cart);
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(cart.getItems().get(0).getId());
    modifyCartRequest.setUsername(cart.getUser().getUsername());
    modifyCartRequest.setQuantity(1);

    when(userRepository.findByUsername(anyString())).thenReturn(null);

    ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void removeCartItem_itemNotFound() {
    User user = getUser();
    Cart cart = getNewCart();
    user.setCart(cart);
    ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
    modifyCartRequest.setItemId(cart.getItems().get(0).getId());
    modifyCartRequest.setUsername(cart.getUser().getUsername());
    modifyCartRequest.setQuantity(1);

    when(userRepository.findByUsername(anyString())).thenReturn(user);
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private Cart getNewCart() {
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
