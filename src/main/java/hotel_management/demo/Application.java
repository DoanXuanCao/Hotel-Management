package hotel_management.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAutoConfiguration
public class Application {

  public static void main(String[] args) {
    System.out.println("--- Starting Hotel Management Application Context... ---");
    SpringApplication.run(Application.class, args);
    System.out.println("--- Hotel Management Application Context Started Successfully. ---");
  }
}
