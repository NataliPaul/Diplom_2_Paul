package Client;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class StellarBurgersClient {
    private static final String BASE_URL = "https://stellarburgers.nomoreparties.site";

    //Регистрация пользователя
    public ValidatableResponse registerUsers(Users user) {
        return given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/register")
                .then()
                .log()
                .all();

    }

    //Авторизация пользователя
    public ValidatableResponse loginUsers(Users user) {
        return given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(user)
                .post("/api/auth/login")
                .then()
                .log()
                .all();
    }

    //Обновление данных пользователя
    public ValidatableResponse updateUsers(Users user, String accessToken) {
        return given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Authorization", accessToken)
                .header("Content-Type", "application/json")
                .body(user)
                .patch("/api/auth/user")
                .then()
                .log()
                .all();

    }

    public ValidatableResponse deleteUser(String token) {
        return given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .header("Authorization", token) // Добавляем токен в заголовок
                .delete("/api/auth/user")
                .then()
                .log()
                .all();
    }

    public ValidatableResponse createOrder(Order order, String token) {
        RequestSpecification request = given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json")
                .body(order); // Передаем объект order в теле запроса
        //если токен не равен null добавляем его заголовок Authorization
        if (token != null) {
            request.header("Authorization", token);
        }
        return request.post("/api/orders")
                .then()
                .log()
                .all();
    }

    public ValidatableResponse receivingOrder(String token) {
        RequestSpecification request = given()
                .log()
                .all()
                .baseUri(BASE_URL)
                .header("Content-Type", "application/json");
        if (token != null) {
            request.header("Authorization", token);
        }
        return request.get("/api/orders")
                .then()
                .log()
                .all();
    }
}
