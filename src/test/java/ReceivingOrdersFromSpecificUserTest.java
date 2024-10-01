import Client.StellarBurgersClient;
import Client.Users;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class ReceivingOrdersFromSpecificUserTest {

    private StellarBurgersClient client;
    private String accessToken;
    private final String email;  // Поле для email
    private final String password;  // Поле для пароля

    // Конструктор, который принимает параметры
    public ReceivingOrdersFromSpecificUserTest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Parameterized.Parameters(name = "{index}: получение заказов пользователя с email={0}, password={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {ConstantsDate.EMAIL, ConstantsDate.PASSWORD},
                {ConstantsDate.INVALID_EMAIL, ConstantsDate.PASSWORD},
        });
    }

    @Before
    @Step("Подготовка данных пользователя перед тестом")
    public void setUp() {
        client = new StellarBurgersClient(); // Инициализируем клиент
        Users user = new Users(email, password);
        ValidatableResponse response = client.loginUsers(user);
        accessToken = response.extract().statusCode() == 200 ? response.extract().jsonPath().getString("accessToken") : null;
    }

    @Test
    @Step("Проверка получения заказов пользователя")
    public void receivingOrders () {
        ValidatableResponse responseOrder = client.receivingOrder(accessToken);
        //ожидаем ответ 200, если пользователь авторизован
        if (accessToken != null) {
            responseOrder.assertThat()
                    .statusCode(200)
                    .body("success", is(true));
        } else {
            //ожидаем ответ 401, если пользователь не авторизован
            responseOrder.assertThat()
                    .statusCode(401)
                    .body("message", is("You should be authorised"));
        }
    }
}
