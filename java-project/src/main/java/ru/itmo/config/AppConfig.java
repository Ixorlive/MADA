package ru.itmo.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;

import static java.time.Duration.ofSeconds;

@Configuration
public class AppConfig {
    @Bean
    @SessionScope
    public WebDriver webDriver() {
        // да-да ебашу хардкод 🤡
        //хардкор запрещен.
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "/java-project/src/main/resources/chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(ofSeconds(2));
        return driver;
    }
}
