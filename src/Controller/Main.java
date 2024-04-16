package Controller;

public class Main {
    public static void main(String[] args) {
        new CallerController(System.getenv("AllowedHostsConfig"),
                System.getenv("DbUrl"),
                System.getenv("DbUser"),
                System.getenv("DbUserPassword"));
    }
}
