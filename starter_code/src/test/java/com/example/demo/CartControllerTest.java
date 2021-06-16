package com.example.demo;

import com.example.demo.controllers.CartController;
import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {
    private CartController cartController;

    private CartRepository cartRepository = mock(CartRepository.class);

    private UserRepository userRepository = mock(UserRepository.class);

    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void add_to_cart_test_happy_path() throws Exception {
        when(userRepository.findByUsername("user")).thenReturn(getUser());
        when(itemRepository.findById(1l)).thenReturn(Optional.of(getItem()));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart c = response.getBody();

        Assert.assertEquals(request.getUsername(), c.getUser().getUsername());
        Assert.assertEquals(request.getItemId(), c.getItems().get(0).getId());
    }

    @Test
    public void add_to_cart_test_user_not_found() throws Exception {
        when(itemRepository.findById(1l)).thenReturn(Optional.of(getItem()));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void add_to_cart_test_item_not_found() throws Exception {
        when(userRepository.findByUsername("user")).thenReturn(getUser());
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_cart_test_happy_path() {
        when(userRepository.findByUsername("user")).thenReturn(getUser());
        when(itemRepository.findById(1l)).thenReturn(Optional.of(getItem()));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        Cart c = response.getBody();

        Assert.assertEquals(0, c.getItems().size());
    }

    @Test
    public void remove_cart_test_user_not_found() throws Exception {
        when(itemRepository.findById(1l)).thenReturn(Optional.of(getItem()));
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void remove_cart_test_item_not_found() throws Exception {
        when(userRepository.findByUsername("user")).thenReturn(getUser());
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("user");
        request.setItemId(1);
        request.setQuantity(1);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        Assert.assertNotNull(response);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private User getUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("1234567");

        Cart cart = new Cart();
        cart.setUser(user);

        user.setCart(cart);

        return user;
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
