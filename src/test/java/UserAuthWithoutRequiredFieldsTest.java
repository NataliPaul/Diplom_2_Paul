import Client.StellarBurgersClient;
import Client.Users;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class UserAuthWithoutRequiredFieldsTest {

    private final StellarBurgersClient client = new StellarBurgersClient();
    // Поля для параметров теста
    private final String email;
    private final String password;

    // Конструктор тестового класса, который принимает параметры
    public UserAuthWithoutRequiredFieldsTest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters(name = "{index}: авторизация с невалидными данными: EMAIL={0}, PASSWORD={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, ConstantsDate.PASSWORD},                        // Пропущен email
                {ConstantsDate.EMAIL, null},                           // Пропущен пароль
                {null, null},                                          // ничего не введено
                {ConstantsDate.EMAIL, ConstantsDate.INVALID_PASSWORD}, // Неверный email
                {ConstantsDate.INVALID_EMAIL, ConstantsDate.PASSWORD}, // Неверный пароль
                {" ", " "}                                             // Пробелы
        });
    }

    @Test
    @Step("Проверка ошибки авторизации на отсутствие обязательных полей или ввод невалидных данных")
    public void createDuplicateCourier_shouldReturnError() {

        Users userWithMissingFields = new Users(email, password);
        ValidatableResponse response = client.loginUsers(userWithMissingFields);
        response.assertThat().statusCode(401);  // Код ответа 401 - если не все обязательные поля переданы
        response.assertThat().body("message", equalTo("email or password are incorrect")); // Сообщение об ошибке
    }
}
