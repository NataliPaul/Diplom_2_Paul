import Client.StellarBurgersClient;
import Client.Users;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.Before;

import static org.hamcrest.CoreMatchers.is;

public class UserAuthTest {

    private final StellarBurgersClient client = new StellarBurgersClient();
    private Users user;

    @Before
    @Step("Подготовка данных пользователя перед тестом")
    public void setUp() {
        user = new Users(ConstantsDate.EMAIL, ConstantsDate.PASSWORD);   //валидные данные
    }

    @Test
    @Step("Проверка логина под существующим пользователем")
    public void loginUser() {
        ValidatableResponse response = client.loginUsers(user);
        response.assertThat()
                .statusCode(200) // Проверяем, что код ответа 200
                .body("success", is(true)); // Проверяем, что success: true
    }

}
