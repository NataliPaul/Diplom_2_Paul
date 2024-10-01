import Client.StellarBurgersClient;
import Client.Users;
import io.qameta.allure.Step;
import io.qameta.allure.Description;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class UserUpdateDataTest {

    private final String email;
    private final String name;
    private final boolean isAuthenticated;

    public UserUpdateDataTest(String email, String name, boolean isAuthenticated) {
        this.email = email;
        this.name = name;
        this.isAuthenticated = isAuthenticated;
    }

    @Parameterized.Parameters(name = "{index}: изменение данных пользователя: EMAIL={0}, NAME={1}, статус авторизации={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {ConstantsDate.NEW_EMAIL, null, true},                     // Изменение только email с авторизацией
                {null, ConstantsDate.NEW_NAME, true},                      // Изменение только имени с авторизацией
                {ConstantsDate.NEW_EMAIL, ConstantsDate.NEW_NAME, true},   // Изменение email и имени с авторизацией
                {ConstantsDate.NEW_EMAIL, null, false},                    // Изменение только email без авторизации
                {null, ConstantsDate.NEW_NAME, false},                     // Изменение только имени без авторизации
                {ConstantsDate.NEW_EMAIL, ConstantsDate.NEW_NAME, false},  // Изменение email и имени без авторизации
                {ConstantsDate.EMAIL, null, true},                         // Изменение email на существующий с авторизацией
                {ConstantsDate.EMAIL, null, false}                         // Изменение email на существующий без авторизации
        });
    }

    private StellarBurgersClient client;
    private Users user;
    private String accessToken;

    @Before
    @Step("Подготовка данных перед тестом: создание объекта пользователя")
    public void before() {
        client = new StellarBurgersClient();
        user = new Users(ConstantsDate.VALID_EMAIL, ConstantsDate.VALID_PASSWORD, ConstantsDate.VALID_NAME);

        // Регистрируем пользователя
        ValidatableResponse createResponse = client.registerUsers(user);
        accessToken = createResponse.extract().jsonPath().getString("accessToken");

        if (isAuthenticated) {
            // Авторизуем пользователя
            ValidatableResponse loginResponse = client.loginUsers(user);
            accessToken = loginResponse.extract().jsonPath().getString("accessToken");
        }
    }

    @Test
    @Step("Проверка изменения полей пользователя")
    @Description("Тест проверяет возможность изменения полей пользователя (email и имя) в зависимости от авторизации.")
    public void updateUserData() {
        // Создаем объект пользователя с новыми данными, сохраняя пароль
        Users updatedUser = new Users(
                email != null ? email : user.getEmail(),
                user.getPassword(),
                name != null ? name : user.getName()
        );

        // Отправляем PATCH запрос для изменения данных
        ValidatableResponse response = client.updateUsers(updatedUser, accessToken);


        // Проверяем ответ в зависимости от авторизации
        if (accessToken != null && !ConstantsDate.EMAIL.equals(email)) {
            // Проверяем успешный ответ для авторизованного пользователя
            response.assertThat()
                    .statusCode(200)  // Проверяем, что запрос успешен
                    .body("success", is(true)); // Проверяем, что операция прошла успешно
            // Проверяем изменение только тех полей, которые не null
            if (email != null) {
                response.body("user.email", equalTo(email));  // Проверяем, что email изменился
            }
            if (name != null) {
                response.body("user.name", equalTo(name));  // Проверяем, что имя изменилось
            }

        } else {
            if (ConstantsDate.EMAIL.equals(email)) {
                response.assertThat()
                        .statusCode(403)
                        .body("message", is("User with such email already exists"));
            } else {
                // Проверяем ошибку для неавторизованного пользователя
                response.assertThat()
                        .statusCode(401) // Проверяем, что код ответа 401
                        .body("message", is("You should be authorised")); // Проверяем сообщение об ошибке (измените на актуальное сообщение от вашего API)
            }
        }

    }

    @After
    @Step("Удаление пользователя после теста по token: {token}")
    public void after() {
        ValidatableResponse deleteResponse = client.deleteUser(accessToken);
        deleteResponse.assertThat()
                .statusCode(202);// Проверяем, что статус код 202 OK

    }
}
