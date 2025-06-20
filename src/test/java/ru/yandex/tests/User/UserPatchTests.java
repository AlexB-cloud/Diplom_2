package ru.yandex.tests.User;

import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import ru.yandex.models.User.UserResponse;
import ru.yandex.tests.BaseTest;

@DisplayName("Тесты на изменение пользователя")
public class UserPatchTests extends BaseTest {

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
}
