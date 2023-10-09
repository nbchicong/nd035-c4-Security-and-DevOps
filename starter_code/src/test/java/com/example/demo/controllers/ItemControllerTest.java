package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
  @InjectMocks
  private ItemController itemController;
  @Mock
  private ItemRepository itemRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void getListItem_ok() {
    List<Item> itemList = Collections.singletonList(getNewItem());
    when(itemRepository.findAll()).thenReturn(itemList);
    ResponseEntity<List<Item>> response = itemController.getItems();
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
  }

  @Test
  public void getItemById_ok() {
    Item item = getNewItem();
    when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
    ResponseEntity<Item> response = itemController.getItemById(1L);
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(item.getId(), response.getBody().getId());
    assertEquals(item.getName(), response.getBody().getName());
  }

  @Test
  public void getItemById_notFound() {
    when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
    ResponseEntity<Item> response = itemController.getItemById(1L);
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  @Test
  public void getItemByName_ok() {
    List<Item> itemList = Collections.singletonList(getNewItem());
    when(itemRepository.findByName(anyString())).thenReturn(itemList);

    ResponseEntity<List<Item>> response = itemController.getItemsByName("Java Core");
    assertNotNull(response);
    assertEquals(HttpStatus.OK, response.getStatusCode());

    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
  }

  @Test
  public void getItemByName_notFound() {
    when(itemRepository.findByName(anyString())).thenReturn(Collections.emptyList());

    ResponseEntity<List<Item>> response = itemController.getItemsByName("Java Core");
    assertNotNull(response);
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
  }

  private Item getNewItem() {
    Item item = new Item();
    item.setId(1L);
    item.setName("Java Core");
    item.setPrice(new BigDecimal(1));
    item.setDescription("Java Core book");
    return item;
  }
}
