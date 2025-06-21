package ru.yandex.tests.User;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.models.User.UserResponse;
import ru.yandex.tests.BaseTest;

@DisplayName("Тесты на создание пользователя")
public class UserCreateTests extends BaseTest {

    @Test
    @DisplayName("Успешное создание пользователя")
    public void successfulUserCreateTest(){
        Response response = userSteps.createUser(uniqueUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        userSteps.checkResponseSuccessfulUserCreate(response, userResponse,uniqueUser);

    }

    @Test
    @DisplayName("Создание пользователя без password")
    public void failureWithNoPasswordCreateTest(){
        Response response = userSteps.createUser(noPasswordUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        userSteps.checkResponseFailureUserCreate(response, userResponse);
    }

    @Test
    @DisplayName("Создание дубликата пользователя")
    public void failureAlreadyExistCreateTest(){
        Response response = userSteps.createUser(uniqueUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        userSteps.checkResponseSuccessfulUserCreate(response, userResponse,uniqueUser);

        response = userSteps.createUser(uniqueUser);
        UserResponse userResponseFailure = gson.fromJson(response.getBody().asString(), UserResponse.class);
        userSteps.checkResponseAlreadyExistUserCreate(response, userResponseFailure);
    }
}
