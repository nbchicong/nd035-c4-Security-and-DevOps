package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
  @InjectMocks
  private UserController userController;
  @Mock
  private final UserRepository userRepository = mock(UserRepository.class);
  @Mock
  private final CartRepository cartRepository = mock(CartRepository.class);
  @Mock
  private final BCryptPasswordEncoder passwordEncoder = mock(BCryptPasswordEncoder.class);

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void createUser_ok() {
    User newUser = getNewUser();
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername(newUser.getUsername());
    userRequest.setPassword(newUser.getPassword());
    userRequest.setConfirmPassword(newUser.getPassword());

    when(cartRepository.save(any(Cart.class))).thenReturn(getNewCart());
    when(userRepository.save(any(User.class))).thenReturn(newUser);
    when(passwordEncoder.encode(userRequest.getPassword())).thenReturn(newUser.getPassword());

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(newUser.getId(), response.getBody().getId());
  }

  @Test
  public void createUser_usernameIsNull() {
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername(null);
    userRequest.setPassword("12345678");
    userRequest.setConfirmPassword("12345678");

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void createUser_usernameIsExisted() {
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("new_user");
    userRequest.setPassword("12345678");
    userRequest.setConfirmPassword("12345678");

    when(userRepository.existsByUsername(anyString())).thenReturn(true);

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void createUser_shortPassword() {
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("user_01");
    userRequest.setPassword("1234567");
    userRequest.setConfirmPassword("1234567");

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void createUser_passwordNotMatch() {
    CreateUserRequest userRequest = new CreateUserRequest();
    userRequest.setUsername("user_01");
    userRequest.setPassword("12345678");
    userRequest.setConfirmPassword("12345687");

    when(userRepository.existsByUsername(anyString())).thenReturn(false);

    ResponseEntity<User> response = userController.createUser(userRequest);

    assertNotNull(response);
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  public void getUserById_ok() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.of(getNewUser()));

    ResponseEntity<User> response = userController.findById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
  }

  @Test
  public void getUserById_notFound() {
    when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

    ResponseEntity<User> response = userController.findById(1L);

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getUserByUsername_ok() {
    when(userRepository.findByUsername(anyString())).thenReturn(getNewUser());

    ResponseEntity<User> response = userController.findByUserName("new_user");

    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(1L, response.getBody().getId());
  }

  @Test
  public void getUserByUsername_notFound() {
    when(userRepository.findByUsername(anyString())).thenReturn(null);

    ResponseEntity<User> response = userController.findByUserName("new_user");

    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private User getNewUser() {
    User user = new User();
    user.setId(1L);
    user.setUsername("new_user");
    user.setPassword("12345678");
    user.setCart(getNewCart());
    return user;
  }

  private Cart getNewCart() {
    return new Cart();
  }
}
