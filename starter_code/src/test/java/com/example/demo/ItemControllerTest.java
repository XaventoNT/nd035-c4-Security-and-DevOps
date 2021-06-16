package com.example.demo;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void get_items_test() throws Exception {
        when(itemRepository.findAll()).thenReturn(getItems());

        ResponseEntity<List<Item>> response = itemController.getItems();

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> items = response.getBody();

        Assert.assertArrayEquals(getItems().toArray(), items.toArray());
    }

    @Test
    public void get_items_by_id_test() throws Exception {
        when(itemRepository.findById(1l)).thenReturn(Optional.of(getItem()));

        ResponseEntity<Item> response = itemController.getItemById(1l);

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        Item item = response.getBody();
        Item expected = getItem();

        Assert.assertEquals(expected.getId(), item.getId());
        Assert.assertEquals(expected.getName(), item.getName());
        Assert.assertEquals(expected.getPrice(), item.getPrice());
        Assert.assertEquals(expected.getDescription(), item.getDescription());
    }

    @Test
    public void get_items_by_name_test() throws Exception {
        when(itemRepository.findByName("Round Widget")).thenReturn(getItems());

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<Item> items = response.getBody();

        Assert.assertArrayEquals(getItems().toArray(), items.toArray());
    }

    @Test
    public void get_items_by_name_test_not_found() throws Exception {
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Round Widget");

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private List<Item> getItems() {
        List<Item> items = new ArrayList<>();
        items.add(getItem());
        return items;
    }

    private Item getItem() {
        Item item = new Item();
        item.setId(1l);
        item.setName("Round Widget");
        item.setPrice(BigDecimal.valueOf(2.99));
        item.setDescription("A widget that is round");
        return item;
    }
}
