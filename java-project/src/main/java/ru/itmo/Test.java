package ru.itmo;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import static java.time.Duration.ofSeconds;

public class Test {

    public void test() {
        WebDriver driver = new ChromeDriver();
        driver.manage().window().fullscreen();
        driver.manage().timeouts().implicitlyWait(ofSeconds(2));
        driver.get("https://kvc-nn.ru/meter/");
//3403454
    }
}
