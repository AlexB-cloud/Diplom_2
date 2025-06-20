package ru.yandex.tests.User;

import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.yandex.models.User.UserResponse;
import ru.yandex.models.User.User;
import ru.yandex.steps.UserSteps;

@DisplayName("Тесты на создание пользователя")
public class UserCreateTests {
    private final User uniqueUser = new User("13autotestrandomemael123@at.com","3passwordVEryHard123","3Alex1337555");
    private final User noPasswordUser = new User("autotestrandom123emael@at.com","Alexxx1337197", true);
    private final Gson gson = new Gson();
    private final UserSteps userSteps = new UserSteps();
    private UserResponse userResponse;

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
    @After
    public void tearDown() {
        if (userResponse.getAccessToken() != null){
        userSteps.deleteUser(userResponse.getAccessToken());
        }
    }
}
