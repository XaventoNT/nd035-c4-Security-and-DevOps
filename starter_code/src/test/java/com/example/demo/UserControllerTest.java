package com.example.demo;

import com.example.demo.controllers.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", bCryptPasswordEncoder);
    }

    @Test
    public void create_user_test_happy_path() throws Exception{
        when(bCryptPasswordEncoder.encode("1234567")).thenReturn("1234567");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("user");
        r.setPassword("1234567");
        r.setConfirmPassword("1234567");

        final ResponseEntity<User> response = userController.createUser(r);

        Assert.assertNotNull(response);

        User u = response.getBody();
        Assert.assertEquals(0, u.getId());
        Assert.assertEquals("user", u.getUsername());
        Assert.assertEquals("1234567", u.getPassword());
    }

    @Test
    public void create_user_test_bad_request() throws Exception{
        when(bCryptPasswordEncoder.encode("1234567")).thenReturn("1234567");
        CreateUserRequest r = new CreateUserRequest();
        r.setUsername("user");
        r.setPassword("123");
        r.setConfirmPassword("123");

        ResponseEntity<User> response = userController.createUser(r);

        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        r.setPassword("1234567");
        r.setConfirmPassword("7654321");
        Assert.assertNotNull(response);
        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void find_user_by_id() throws Exception {
        when(userRepository.findById(0l)).thenReturn(Optional.of(new_user()));
        User user = new_user();
        final ResponseEntity<User> responseFind = userController.findById(0l);

        Assert.assertNotNull(responseFind);
        Assert.assertEquals(HttpStatus.OK, responseFind.getStatusCode());

        User u = responseFind.getBody();
        Assert.assertEquals(user.getId(), u.getId());
        Assert.assertEquals(user.getUsername(), u.getUsername());
        Assert.assertEquals(user.getPassword(), u.getPassword());
    }

    @Test
    public void find_user_by_username() {
        when(userRepository.findByUsername("userFind")).thenReturn(new_user());
        User user = new_user();
        final ResponseEntity<User> responseFind = userController.findByUserName("userFind");

        Assert.assertNotNull(responseFind);
        Assert.assertEquals(HttpStatus.OK, responseFind.getStatusCode());

        User u = responseFind.getBody();
        Assert.assertEquals(user.getId(), u.getId());
        Assert.assertEquals(user.getUsername(), u.getUsername());
        Assert.assertEquals(user.getPassword(), u.getPassword());
    }

    @Test
    public void find_user_by_username_not_found() {
        User user = new_user();
        final ResponseEntity<User> responseFind = userController.findByUserName("userFind");

        Assert.assertNotNull(responseFind);
        Assert.assertEquals(HttpStatus.NOT_FOUND, responseFind.getStatusCode());
    }

    private User new_user() {
        User user = new User();
        user.setUsername("userFind");
        user.setPassword("1234567");
        return user;
    }
}
