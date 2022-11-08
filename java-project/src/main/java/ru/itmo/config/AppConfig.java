package ru.itmo.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.annotation.SessionScope;
import ru.itmo.dto.ResponseDto;

import static java.time.Duration.ofSeconds;

@Configuration
public class AppConfig {
    @Bean
    @SessionScope
    public WebDriver webDriver() {
        System.setProperty("webdriver.chrome.driver","/Users/dinar/chromedriver");
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(ofSeconds(2));
        return driver;
    }

    @Bean
    @SessionScope
    public ResponseDto responseDto() {
        return ResponseDto.builder().build();
    }
}
