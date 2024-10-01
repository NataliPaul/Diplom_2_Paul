import Client.StellarBurgersClient;
import Client.Order;
import Client.Users;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class CreateOrderTest {

    private final List<String> ingredients; // Список ингредиентов
    private final boolean isAuthenticated;

    public CreateOrderTest(List<String> ingredients, boolean isAuthenticated) {
        this.ingredients = ingredients;
        this.isAuthenticated = isAuthenticated;
    }

    @Parameterized.Parameters(name = "{index}: создание заказа с ингредиентами={0}, статус авторизации={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {Arrays.asList(ConstantsDate.MAIN, ConstantsDate.SAUCE), true}, //
                {Arrays.asList(null, ConstantsDate.SAUCE), true},  //
                {Arrays.asList(ConstantsDate.MAIN, null), true},  //
                {Arrays.asList(ConstantsDate.MAIN, ConstantsDate.SAUCE), false},  //
                {Arrays.asList(null, ConstantsDate.SAUCE), false},  //
                {Arrays.asList(ConstantsDate.MAIN, null), false},  //
                {Arrays.asList(ConstantsDate.MAIN, ""), false},  //
                {Arrays.asList("", ""), false},  //
                {Arrays.asList("", ConstantsDate.SAUCE), false},  //
                {Arrays.asList(null, ConstantsDate.INVALID_SAUCE), true},  // Только невалидный соус
                {List.of(), true}, // Без ингредиентов
        });
    }

    private StellarBurgersClient client;
    private String accessToken;


    @Before
    @Step("Инициализация и авторизация пользователя (если требуется)")
    public void setUp() {
        client = new StellarBurgersClient();
        if (isAuthenticated) {
            authUser();         // Авторизация пользователя
        } else {
            accessToken = null; // Не авторизованный пользователь
        }
    }

    @Step("Авторизация пользователя")
    private void authUser() {
        Users user = new Users(ConstantsDate.EMAIL, ConstantsDate.PASSWORD);
        ValidatableResponse response = client.loginUsers(user);
        response.assertThat()
                .statusCode(200) // Проверяем, что код ответа 200
                .body("success", is(true)); // Проверяем, что success: true
        accessToken = response.extract().jsonPath().getString("accessToken");  //сохраняем токен
    }

    @Test
    @Step("Создание заказа")
    public void createOrder() {
        // Создаем объект заказа с переданным списком ингредиентов
        Order order = new Order(ingredients);
        // Отправляем запрос на создание заказа через клиент с указанием accessToken (если он есть)
        ValidatableResponse responseOrder = client.createOrder(order, accessToken);
        // Вызываем метод, проверяющий корректность ответа сервера на запрос создания заказа
        validateOrderResponse(responseOrder, ingredients);
    }

    @Step("Проверка ответа при создании заказа")
    private void validateOrderResponse(ValidatableResponse response, List<String> ingredients) {
        // Если ингредиенты отсутствуют или список пуст, ожидаем ошибку 400
        if (ingredients == null || ingredients.isEmpty()) {
            response.assertThat()
                    .statusCode(400)
                    .body("message", is("Ingredient ids must be provided"));
            // Если среди ингредиентов есть невалидные данные (например, пустые строки или некорректный соус), ожидаем ошибку 500
        } else if (ingredients.contains(ConstantsDate.INVALID_SAUCE) || ingredients.contains("")) {
            response.assertThat()
                    .statusCode(500);
        } else {
            // Если ингредиенты валидны, ожидаем успешный ответ 200 и success = true
            response.assertThat()
                    .statusCode(200)
                    .body("success", is(true));
//                    .body("orders[0]._id", notNullValue())  // Проверяем, что поле _id присутствует и не null
//                    .body("orders[0].ingredients", notNullValue())  // Проверяем наличие ingredients
//                    .body("orders[0].status", notNullValue())  // Проверяем статус заказа
//                    .body("orders[0].name", notNullValue())  // Проверяем имя заказа
//                    .body("orders[0].createdAt", notNullValue())  // Проверяем наличие даты создания
//                    .body("orders[0].updatedAt", notNullValue())  // Проверяем наличие даты обновления
//                    .body("orders[0].number", notNullValue());  // Проверяем номер заказа
        }
    }
}


