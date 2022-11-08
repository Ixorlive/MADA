package ru.itmo.controller;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.config.AppProperty;
import ru.itmo.dto.PersonalDataDto;
import ru.itmo.dto.ResponseDto;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class SeleniumController {
    private final AppProperty property;
    private final WebDriver driver;

    @PostMapping("/run")
    public ResponseDto run(@RequestBody PersonalDataDto data) {
        try {
            driver.get(property.getPage());
            Thread.sleep(300);

            WebElement idSelectRegion = driver.findElement(By.id("id_select_region"));
            idSelectRegion.click();

            new Select(idSelectRegion).selectByVisibleText(data.getDistrict());

            WebElement idInputLc = driver.findElement(By.id("id_input_lc"));
            idInputLc.click();
            idInputLc.clear();
            idInputLc.sendKeys(data.getAccount());

            String src = driver.findElement(By.id("m-login-meter-captcha-image"))
                    .getDomProperty("src");

            return ResponseDto.builder().success(true).captchaUrl(src).build();
        } catch (Exception e) {
            return ResponseDto.builder().build();
        }
    }

    @PostMapping("/captcha")
    public ResponseDto submitCaptcha(@RequestParam String value) {
        try {
            WebElement idInputCaptcha = driver.findElement(By.id("id_input_captcha"));
            idInputCaptcha.click();
            idInputCaptcha.clear();
            idInputCaptcha.sendKeys(value);

            driver.findElement(By.id("m-button-submit")).click();
            Thread.sleep(500);

            return ResponseDto.builder().success(true).build();
        } catch (Exception e) {
            return ResponseDto.builder().build();
        }
    }
}
