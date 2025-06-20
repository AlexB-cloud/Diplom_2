package ru.yandex.tests.User;

import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.yandex.models.User.UserResponse;
import ru.yandex.models.User.User;
import ru.yandex.steps.UserSteps;
@DisplayName("Тесты на авторизацию пользователя")
public class UserLoginTests {
    private final User uniqueUser = new User("13autotestrandomemael123@at.com","3passwordVEryHard123","3Alex1337555");
    private final User wrongCredentialsUser = new User("superrandomemailnoonetakes@for.sure","superRandomPaswordDoesntExist", false);
    private final Gson gson = new Gson();
    private final UserSteps userSteps = new UserSteps();
    private UserResponse userResponse;

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

    @After
    public void tearDown() {
        if (userResponse.getAccessToken() != null){
            userSteps.deleteUser(userResponse.getAccessToken());
        }
    }
}
