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

@DisplayName("Тесты на получение оформленного заказа")
public class OrderGetTests {
    private final User uniqueUser = new User("13autotestrandomemael123@at.com","3passwordVEryHard123","3Alex1337555");
    private final Gson gson = new Gson();
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private UserResponse userResponse;

    @Test
    @DisplayName("Получение заказа без авторизации")
    public void getOrderWithoutAuthTest(){
        Response response = orderSteps.getIngredients();
        ArrayList<String> IngList = orderSteps.getIngredientsIds(response);
        Order order = new Order(IngList);
        orderSteps.placeOrderNoAuth(order);
        response = orderSteps.getOrderNoAuth();
        orderSteps.checkGetOrderNoAuth(response);
    }

    @Test
    @DisplayName("Получение заказа c авторизацией")
    public void getOrderWithAuthTest(){
        Response response = userSteps.createUser(uniqueUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        response = orderSteps.getIngredients();
        ArrayList<String> IngList = orderSteps.getIngredientsIds(response);
        Order order = new Order(IngList);
        response = orderSteps.placeOrderWithAuth(order,userResponse.getAccessToken());
        String orderId = response.jsonPath().getString("order._id");
        int orderNumber = response.jsonPath().getInt("order.number");
        response = orderSteps.getOrderWithAuth(userResponse.getAccessToken());
        orderSteps.checkGetOrderWithAuth(response,order,orderId,orderNumber);

    }

    @After
    public void tearDown() {
        if (userResponse!= null && userResponse.getAccessToken() != null) {
            userSteps.deleteUser(userResponse.getAccessToken());
        }
    }
}
