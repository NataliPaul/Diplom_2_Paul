import Client.StellarBurgersClient;
import Client.Users;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class UserUniqueCreateTest {

    private final StellarBurgersClient client = new StellarBurgersClient();
    private Users user;
    private String accessToken;

    @Before
    @Step("Подготовка данных перед тестом: создание объекта пользователя")
    public void before() {
        user = new Users(ConstantsDate.VALID_EMAIL, ConstantsDate.VALID_PASSWORD, ConstantsDate.VALID_NAME);
    }

    //Проверка создания пользователя и что запрос успешный (возвращает ok: true)
    @Test
    @Step("Проверка создания пользователя")
    public void createUserSuccessfully() {
        ValidatableResponse response = client.registerUsers(user);

        response.assertThat()
                .statusCode(200)
                .body("success", is(true));  //успешный запрос возвращает ok: true
        // Извлекаем accessToken только после успешной регистрации
        accessToken = response.extract().jsonPath().getString("accessToken");
    }

    // Проверка обработки случая, когда пользователь уже существует
    @Test
    @Step("Проверка попытки создания существующего пользователя")
    public void createExistingUserShouldReturnError() {

        ValidatableResponse response = client.registerUsers(user);
        accessToken = response.extract().jsonPath().getString("accessToken");
        // Пытаемся создать того же пользователя снова
        ValidatableResponse secondResponse = client.registerUsers(user);
        secondResponse.assertThat()
                .statusCode(403) // Проверяем код ответа
                .body("success", is(false)) // Проверяем, что success: false
                .body("message", is("User already exists")); // Проверяем сообщение об ошибке
    }

    @After
    @Step("Удаление пользователя после теста по token: {accessToken}")
    public void after() {
        // Проверяем, что accessToken не null перед удалением
        if (accessToken != null) {
            ValidatableResponse deleteResponse = client.deleteUser(accessToken);
            deleteResponse.assertThat()
                    .statusCode(202);// Проверяем, что статус код 202 OK
        }
    }
}
