package ru.yandex.tests.User;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.models.User.UserResponse;
import ru.yandex.tests.BaseTest;

@DisplayName("Тесты на авторизацию пользователя")
public class UserLoginTests extends BaseTest {

    @Test
    @DisplayName("Тест успешной авторизации")
    public void successfulLoginTest() {
        userSteps.createUser(uniqueUser);
        Response response = userSteps.authUser(uniqueUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        userSteps.checkResponseSuccessfulAuthUser(response, userResponse, uniqueUser);
    }
    @Test
    @DisplayName("Тест авторизации с некорректными данными")
    public void failureWrongCredentialsLoginTest(){
            Response response = userSteps.authUser(wrongCredentialsUser);
            userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
            userSteps.checkResponseFailureUserLogin(response, userResponse);
        }

}
