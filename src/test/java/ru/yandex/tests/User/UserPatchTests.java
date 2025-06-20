package ru.yandex.tests.User;

import com.google.gson.Gson;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import ru.yandex.models.User.User;
import ru.yandex.models.User.UserResponse;
import ru.yandex.steps.UserSteps;

import java.util.Objects;

@DisplayName("Тесты на изменение пользователя")
public class UserPatchTests {
    private final User uniqueUser = new User("13autotestrandomemael123@at.com", "3passwordVEryHard123", "3Alex1337555");
    private final User patchedUser = new User(uniqueUser.getEmail() + "123", uniqueUser.getPassword() + "123", uniqueUser.getName() + "123");
    private final Gson gson = new Gson();
    private final UserSteps userSteps = new UserSteps();
    private UserResponse userResponse;
    private UserResponse userPatchedResponse;

    @Test
    @DisplayName("Изменение пользователя с авторизацией")
    public void successfulPatchTest() {
        Response response = userSteps.createUser(uniqueUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        response = userSteps.patchUserInfoWithAuth(userResponse.getAccessToken(), patchedUser);
        userPatchedResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        userSteps.checkResponseSuccessfulUserPatch(response, patchedUser, userPatchedResponse);
        // Дополнительная проверка авторизации для проверки изменения пароля
        Response authResponse=userSteps.authUser(patchedUser);
        userPatchedResponse = gson.fromJson(authResponse.getBody().asString(), UserResponse.class);
        userSteps.checkResponseSuccessfulAuthUser(authResponse, userPatchedResponse, patchedUser);

    }

    @Test
    @DisplayName("Изменение пользователя без авторизации")
    public void failurePatchTest(){
        Response response = userSteps.createUser(uniqueUser);
        userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        response = userSteps.patchUserInfoWithoutAuth(patchedUser);
        userPatchedResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        userSteps.checkResponseFailureWithoutAuthUserPatch(response, userPatchedResponse);
    }
    @After
    public void tearDown() {
        if (userResponse.getAccessToken() != null) {
            userSteps.deleteUser(userResponse.getAccessToken());
        }
        if (userPatchedResponse.getAccessToken() != null && !Objects.equals(userPatchedResponse.getAccessToken(), userResponse.getAccessToken())) {
            userSteps.deleteUser(userPatchedResponse.getAccessToken());
        }
    }
}
