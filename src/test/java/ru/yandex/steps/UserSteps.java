package ru.yandex.steps;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import org.junit.Assert;
import ru.yandex.models.User.UserResponse;
import ru.yandex.models.User.User;

import static io.restassured.RestAssured.given;

public class UserSteps {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site/";
    private final Gson gson = new Gson();
    private final Gson gsonWithoutName = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .create();

    @Step("Запрос на создание пользователя")
    public Response createUser(User user) {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(gson.toJson(user))
                .when()
                .post("api/auth/register");
    }

    @Step("Проверка ответа при успешном создании")
    public void checkResponseSuccessfulUserCreate(Response response, UserResponse userResponse, User user) {
        response.then()
                .statusCode(200);
        addAttachments(response);
        Assert.assertTrue(userResponse.isSuccess());
        Assert.assertEquals(user.getName(), userResponse.getUser().getName());
        Assert.assertEquals(user.getEmail(), userResponse.getUser().getEmail());
        Assert.assertNotNull(userResponse.getAccessToken());
        Assert.assertNotNull(userResponse.getRefreshToken());
    }

    @Step("Проверка ответа при создании уже зарегистрированного пользователя")
    public void checkResponseAlreadyExistUserCreate(Response response, UserResponse userResponse) {
        response.then()
                .statusCode(403);
        addAttachments(response);
        Assert.assertFalse(userResponse.isSuccess());
        Assert.assertEquals("User already exists", userResponse.getMessage());

    }

    @Step("Проверка ответа при создании пользователя без password")
    public void checkResponseFailureUserCreate(Response response, UserResponse userResponse) {
        response.then()
                .statusCode(403);
        addAttachments(response);
        Assert.assertFalse(userResponse.isSuccess());
        Assert.assertEquals("Email, password and name are required fields", userResponse.getMessage());
    }

    @Step("Удаление пользователя")
    public Response deleteUser(String accessToken) {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .delete("api/auth/user");
    }

    @Step("Запрос на авторизацию пользователя")
    public Response authUser(User user) {

        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(gsonWithoutName.toJson(user))
                .when()
                .post("/api/auth/login");

    }

    @Step("Проверка ответа при успешной авторизации")
    public void checkResponseSuccessfulAuthUser(Response response, UserResponse userResponse, User user) {
        response.then()
                .statusCode(200);
        addAttachments(response);
        Assert.assertTrue(userResponse.isSuccess());
        Assert.assertNotNull(userResponse.getUser().getName());
        Assert.assertEquals(user.getEmail(), userResponse.getUser().getEmail());
        Assert.assertNotNull(userResponse.getAccessToken());
        Assert.assertNotNull(userResponse.getRefreshToken());
    }

    @Step("Проверка ответа при некорректной авторизации")
    public void checkResponseFailureUserLogin(Response response, UserResponse userResponse) {
        response.then()
                .statusCode(401);
        addAttachments(response);
        Assert.assertFalse(userResponse.isSuccess());
        Assert.assertEquals("email or password are incorrect", userResponse.getMessage());

    }

    @Step("Запрос на получение данных о пользователе")
    public Response getUserInfo(String accessToken){
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .when()
                .get("api/auth/user");
    }

    @Step("Запрос на изменение данных о пользователе с авторизацией")
    public Response patchUserInfoWithAuth(String accessToken,User user) {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .header("Authorization", accessToken)
                .body(gson.toJson(user))
                .when()
                .patch("api/auth/user");

    }

    @Step("Запрос на изменение данных о пользователе без авторизации")
    public Response patchUserInfoWithoutAuth(User user) {
        return given()
                .baseUri(BASE_URI)
                .filter(new AllureRestAssured())
                .header("Content-type", "application/json")
                .body(gson.toJson(user))
                .when()
                .patch("api/auth/user");

    }

    @Step("Проверка изменения данных пользователя")
    public void checkResponseSuccessfulUserPatch(Response response, User patchedUser, UserResponse userResponse){
        response.then()
                .statusCode(200);
        addAttachments(response);
        Assert.assertEquals(patchedUser.getName(),userResponse.getUser().getName());
        Assert.assertEquals(patchedUser.getEmail(),userResponse.getUser().getEmail());
        Assert.assertTrue(userResponse.isSuccess());

    }

    @Step("Проверка ошибки при изменении пользователя без авторизации")
    public void checkResponseFailureWithoutAuthUserPatch(Response response, UserResponse userResponse){
        response.then()
                .statusCode(401);
        addAttachments(response);
        Assert.assertFalse(userResponse.isSuccess());
        Assert.assertEquals("You should be authorised", userResponse.getMessage());
    }
    @Step("Проверка что данные пользователя не изменились при изменении пользователя без авторизации")
    public void checkUserCredentialsNotChangeAfterFailurePatch(String accessToken,User patchedUser){
        Response response = getUserInfo(accessToken);
        addAttachments(response);
        UserResponse userResponse = gson.fromJson(response.getBody().asString(), UserResponse.class);
        Assert.assertNotEquals(patchedUser.getName(),userResponse.getUser().getName());
        Assert.assertNotEquals(patchedUser.getEmail(),userResponse.getUser().getEmail());

    }
        void addAttachments (Response response){
            Allure.addAttachment("Полученный ответ", "text/plain", response.getBody().asString());
            Allure.addAttachment("Статус-код", "text/plain", String.valueOf(response.statusCode()));
        }

}
