package ru.yandex.tests;

import com.google.gson.Gson;
import org.junit.After;
import ru.yandex.models.User.User;
import ru.yandex.models.User.UserResponse;
import ru.yandex.steps.OrderSteps;
import ru.yandex.steps.UserSteps;
import java.util.Objects;

public class BaseTest {
    protected final User uniqueUser = new User("13autotestrandomemael123@at.com", "3passwordVEryHard123", "3Alex1337555");
    protected final User patchedUser = new User(uniqueUser.getEmail() + "123", uniqueUser.getPassword() + "123", uniqueUser.getName() + "123");
    protected final User noPasswordUser = new User("autotestrandom123emael@at.com","Alexxx1337197", true);
    protected final User wrongCredentialsUser = new User("superrandomemailnoonetakes@for.sure","superRandomPaswordDoesntExist", false);
    protected final Gson gson = new Gson();
    protected final UserSteps userSteps = new UserSteps();
    protected final OrderSteps orderSteps = new OrderSteps();
    protected UserResponse userResponse;
    protected UserResponse userPatchedResponse;

    @After
    public void tearDown() {
        if (userResponse != null && userResponse.getAccessToken() != null) {
            userSteps.deleteUser(userResponse.getAccessToken());
        }
        if (userPatchedResponse != null && userPatchedResponse.getAccessToken() != null && !Objects.equals(userPatchedResponse.getAccessToken(), userResponse.getAccessToken())) {
            userSteps.deleteUser(userPatchedResponse.getAccessToken());
        }
    }

}
