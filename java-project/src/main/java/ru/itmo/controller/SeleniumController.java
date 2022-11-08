package ru.itmo.controller;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.config.AppProperty;
import ru.itmo.dto.ResponseDto;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeleniumController {

    private final AppProperty property;
    private final WebDriver driver;
    private final ResponseDto responseDto;

    @SneakyThrows
    @PostMapping("/run")
    public ResponseDto run() {
        driver.get(property.getPage());
        Thread.sleep(300);
        driver.findElement(By.id("id_select_region")).click();

        new Select(driver.findElement(By.id("id_select_region"))).selectByVisibleText("Выксунский р-н");
        WebElement id_input_lc = driver.findElement(By.id("id_input_lc"));
        id_input_lc.click();
        id_input_lc.clear();
        id_input_lc.sendKeys(property.getAccount());


        // запрос к телефону. телефон возаращет ответ
        driver.findElement(By.id("id_input_captcha")).click();
        driver.findElement(By.id("id_input_captcha")).clear();
        driver.findElement(By.id("id_input_captcha")).sendKeys("96629"); // сюда пихаем

        return ResponseDto.builder().success(true).build();
    }

//    @GetMapping("/test")
//    public String test() {
//
//    }
}
