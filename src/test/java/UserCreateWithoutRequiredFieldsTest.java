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
public class UserCreateWithoutRequiredFieldsTest {

    private final StellarBurgersClient client = new StellarBurgersClient();
    // Поля для параметров теста
    private final String email;
    private final String password;
    private final String name;

    // Конструктор тестового класса, который принимает параметры
    public UserCreateWithoutRequiredFieldsTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Parameterized.Parameters(name = "{index}: регистрация с пропущенными полями: EMAIL={0}, PASSWORD={1}, NAME={2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {null, ConstantsDate.VALID_PASSWORD, ConstantsDate.VALID_NAME},  // Пропущен email
                {ConstantsDate.VALID_EMAIL, null, ConstantsDate.VALID_NAME},     // Пропущен пароль
                {ConstantsDate.VALID_EMAIL, ConstantsDate.VALID_PASSWORD, null}, // Пропущен пароль
                {null, null, null},                                              // Ничего не введено
        });
    }

    @Test
    @Step("Проверка регистрации при заполнении не всех полей")
    public void createUserWithInvalidFields() {
        Users user = new Users(email, password, name);
        ValidatableResponse response = client.registerUsers(user);
        response.assertThat()
                .statusCode(403)  // Код ответа 403 - если не все обязательные поля переданы
                .body("message", equalTo("Email, password and name are required fields")); // Сообщение об ошибке
    }
}

