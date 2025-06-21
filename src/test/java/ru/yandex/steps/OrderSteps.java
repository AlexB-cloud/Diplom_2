package ru.yandex.steps;

import com.google.gson.Gson;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import ru.yandex.models.Order.Order;
import ru.yandex.models.User.User;

import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderSteps {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    private final Gson gson = new Gson();

    @Step("Запрос на получение ингредиентов")
    public Response getIngredients() {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .when()
                .get("api/ingredients");
    }

    @Step("Выбор ингредиентов для оформления заказа")
    public ArrayList<String> getIngredientsIds(Response response) {
        return new ArrayList<>(List.of(response.jsonPath().getString("data[0]._id"),
                response.jsonPath().getString("data[1]._id")));
    }

    @Step("Запрос на оформление заказа без авторизации")
    public Response placeOrderNoAuth(Order order) {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(gson.toJson(order))
                .when()
                .post("api/orders");

    }


    @Step("Проверка ответа на оформление заказа без авторизации")
    public void checkPlaceOrderNoAuth(Response response) {
        addAttachments(response);
        response.then()
                .statusCode(200)
                .body("", hasKey("name"))
                .body("", hasKey("success"))
                .body("", hasKey("order"))
                .body("order", hasKey("number"))
                .body("success", equalTo(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", allOf(
                        notNullValue(),
                        instanceOf(Integer.class)));
    }

    @Step("Проверка ответа на оформление заказа без ингредиентов")
    public void checkPlaceOrderNoIngredients(Response response) {
        addAttachments(response);
        response.then()
                .statusCode(400)
                .body("", hasKey("message"))
                .body("", hasKey("success"))
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));


    }

    @Step("Проверка ответа на оформление заказа с некорректным hash ингредиентов")
    public void checkPlaceOrderWithWrongHash(Response response) {
        addAttachments(response);
        response.then()
                .statusCode(500)
                .body(containsString("Internal Server Error"));
    }

    @Step("Запрос на оформление заказа с авторизацией")
    public Response placeOrderWithAuth(Order order, String accessToken) {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(gson.toJson(order))
                .when()
                .post("api/orders");

    }

    @Step("Проверка ответа на оформление заказа с авторизацией")
    public void checkPlaceOrderWithAuth(Response response, Order order, User user) {
        addAttachments(response);
        response.then()
                .statusCode(200)
                .body("", hasKey("success"))
                .body("", hasKey("name"))
                .body("", hasKey("order"))
                .body("order", hasKey("ingredients"))
                .body("order", hasKey("number"))
                .body("order", hasKey("price"))
                .body("order", hasKey("_id"))
                .body("order.owner", hasKey("name"))
                .body("order.owner", hasKey("email"))
                .body("success", equalTo(true))
                .body("name", not(emptyOrNullString()))
                .body("order.number", allOf(
                        notNullValue(),
                        instanceOf(Integer.class)))
                .body("order.price", allOf(
                        notNullValue(),
                        instanceOf(Integer.class)))
                .body("order.ingredients[0]._id", equalTo(order.getIngredients().get(0)))
                .body("order.ingredients[1]._id", equalTo(order.getIngredients().get(1)))
                .body("order.owner.name", equalTo(user.getName()))
                .body("order.owner.email", equalTo(user.getEmail()));

    }

    @Step("Запрос на получение заказа c авторизацией")
    public Response getOrderWithAuth(String accessToken) {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get("api/orders");
    }

    @Step("Запрос на получение заказа без авторизации")
    public Response getOrderNoAuth() {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-Type", "application/json")
                .when()
                .get("api/orders");
    }

    @Step("Проверка ответа на получение заказа без авторизации")
    public void checkGetOrderNoAuth(Response response) {
        addAttachments(response);
        response.then()
                .statusCode(401)
                .body("", hasKey("message"))
                .body("", hasKey("success"))
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }
    @Step("Проверка ответа на получение заказа без авторизации")
    public void checkGetOrderWithAuth(Response response,Order order,String _id,int number) {
        addAttachments(response);
        response.then()
                .statusCode(200)
                .body("", hasKey("success"))
                .body("", hasKey("orders"))
                .body("", hasKey("total"))
                .body("", hasKey("totalToday"))
                .body("orders[0]", hasKey("name"))
                .body("orders[0]", hasKey("_id"))
                .body("orders[0]", hasKey("ingredients"))
                .body("orders[0]", hasKey("number"))
                .body("success", equalTo(true))
                .body("orders[0].name", not(emptyOrNullString()))
                .body("orders[0]._id", equalTo(_id))
                .body("orders[0].number", equalTo(number))
                .body("orders[0].ingredients", hasItems(
                        order.getIngredients().get(0),
                        order.getIngredients().get(1)));

    }
    void addAttachments (Response response){
        Allure.addAttachment("Полученный ответ", "text/plain", response.getBody().asString());
        Allure.addAttachment("Статус-код", "text/plain", String.valueOf(response.statusCode()));
    }
}
