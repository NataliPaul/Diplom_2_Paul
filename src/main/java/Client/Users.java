package Client;

public class Users {

    private final String email;
    private final String password;
    private final String name;

    public Users(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Конструктор без имени, имя по умолчанию будет null
    public Users(String email, String password) {
        this(email, password, null); // Вызываем другой конструктор
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

}
