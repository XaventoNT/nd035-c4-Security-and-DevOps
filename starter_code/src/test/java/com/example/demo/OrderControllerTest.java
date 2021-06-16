package com.example.demo;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {
    private OrderController orderController;

    private UserRepository userRepository = mock(UserRepository.class);

    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void submit_username_test() throws Exception {
        when(userRepository.findByUsername("user")).thenReturn(getUser());

        ResponseEntity<UserOrder> response = orderController.submit("user");

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        UserOrder order = response.getBody();
        User user = getUser();

        Assert.assertEquals(0l, order.getId());
        Assert.assertArrayEquals(user.getCart().getItems().toArray(), order.getItems().toArray());
        Assert.assertEquals(user.getCart().getTotal(), order.getTotal());

    }

    @Test
    public void submit_username_test_user_not_found() throws Exception {
        ResponseEntity<UserOrder> response = orderController.submit("user");

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void get_orders_for_user_test() throws Exception {
        User user = getUser();
        when(userRepository.findByUsername("user")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(getOrders());
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user");

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

        List<UserOrder> orders = response.getBody();

        Assert.assertEquals(1, orders.size());

        UserOrder order = orders.get(0);

        Assert.assertArrayEquals(user.getCart().getItems().toArray(), order.getItems().toArray());
        Assert.assertEquals(user.getCart().getTotal(), order.getTotal());
    }

    @Test
    public void get_orders_for_user_test_user_not_found() throws Exception {
        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("user");

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private User getUser() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("1234567");

        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(getItem());

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

    private List<UserOrder> getOrders() {
        List<UserOrder> orders =new ArrayList<>();
        orders.add(UserOrder.createFromCart(getUser().getCart()));
        return orders;
    }
}
