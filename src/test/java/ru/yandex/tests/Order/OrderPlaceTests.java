package ru.yandex.tests.Order;

import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.yandex.models.Order.Order;
import ru.yandex.models.User.User;
import ru.yandex.models.User.UserResponse;
import ru.yandex.steps.OrderSteps;
import ru.yandex.steps.UserSteps;

import java.util.ArrayList;

@DisplayName("Тесты на оформление заказа")
public class OrderPlaceTests {
    private final User uniqueUser = new User("13autotestrandomemael123@at.com","3passwordVEryHard123","3Alex1337555");
    private final Gson gson = new Gson();
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private UserResponse userResponse;

    @Test
    @DisplayName("Оформление заказа без авторизации")
    public void placeOrderNoAuthTest(){
        Response response = orderSteps.getIngredients();
        ArrayList<String> IngList = orderSteps.getIngredientsIds(response);
        Order order = new Order(IngList);
        response = orderSteps.placeOrderNoAuth(order);
        orderSteps.checkPlaceOrderNoAuth(response);
    }

    @Test
    @DisplayName("Оформление заказа без ингредиентов")
    public void placeOrderNoIngredientsTest(){
        Order order = new Order(new ArrayList<String>());
        Response response = orderSteps.placeOrderNoAuth(order);
        orderSteps.checkPlaceOrderNoIngredients(response);
    }

    @Test
    @DisplayName("Оформление заказа с некорректным hash ингредиентов")
    public void placeOrderWithWrongHashTest(){
        Response response = orderSteps.getIngredients();
        ArrayList<String> IngList = orderSteps.getIngredientsIds(response);
        IngList.set(0,IngList.get(0)+"adasdad");
        Order order = new Order(IngList);
        response = orderSteps.placeOrderNoAuth(order);
        orderSteps.checkPlaceOrderWithWrongHash(response);
    }

    @Test
    @DisplayName("Оформление заказа с авторизацией и ингредиентами")
    public void placeOrderWithAuthTest(){
        Response response = userSteps.createUser(uniqueUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        response = orderSteps.getIngredients();
        ArrayList<String> IngList = orderSteps.getIngredientsIds(response);
        Order order = new Order(IngList);
        response = orderSteps.placeOrderWithAuth(order,userResponse.getAccessToken());
        orderSteps.checkPlaceOrderWithAuth(response,order,uniqueUser);

        }
    @After
    public void tearDown() {
        if (userResponse!= null && userResponse.getAccessToken() != null) {
            userSteps.deleteUser(userResponse.getAccessToken());
        }
    }

    }

