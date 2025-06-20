package ru.yandex.tests.Order;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.models.Order.Order;
import ru.yandex.models.User.UserResponse;
import ru.yandex.tests.BaseTest;

import java.util.ArrayList;

@DisplayName("Тесты на оформление заказа")
public class OrderPlaceTests extends BaseTest {

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

    }

