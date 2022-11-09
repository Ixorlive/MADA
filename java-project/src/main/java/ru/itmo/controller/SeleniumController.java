package ru.itmo.controller;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.itmo.config.AppProperty;
import ru.itmo.dto.PersonalDataDto;
import ru.itmo.dto.ResponseDto;
import ru.itmo.dto.ResponseMeterDataDto;


// для теста
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.List;

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

            byte[] captcha = driver.findElement(By.id("m-login-meter-captcha-image"))
                    .getScreenshotAs(OutputType.BYTES);
            //для теста
//            BufferedImage img = ImageIO.read(new ByteArrayInputStream(captcha));
            return ResponseDto.builder().success(true).captcha(captcha).build();
        } catch (Exception e) {
            return ResponseDto.builder().build();
        }
    }

    @GetMapping("/districts")
    public List<String> getDistricts() {
        try {
            driver.get(property.getPage());
            Thread.sleep(300);

            return new Select(driver.findElement(By.id("id_select_region")))
                    .getOptions()
                    .stream()
                    .map(WebElement::getText)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/captcha")
    public ResponseMeterDataDto submitCaptcha(@RequestParam String value) {
        try {
            WebElement idInputCaptcha = driver.findElement(By.id("id_input_captcha"));
            idInputCaptcha.click();
            idInputCaptcha.clear();
            idInputCaptcha.sendKeys(value);

            driver.findElement(By.id("m-button-submit")).click();
            Thread.sleep(500);
            List<String> allMeters = driver.findElements(By.tagName("td"))
                    .stream()
                    .filter(el -> el.getText().contains("Счетчик"))
                    .map(el -> el.getText().replace("Счетчик ", ""))
                    .toList();
            List<String> textWithDate = Arrays.stream(driver.findElements(By.tagName("i"))
                    .get(5)
                    .getText()
                    .split("период с | по | число"))
                    .toList();
            return ResponseMeterDataDto.builder()
                    .success(true)
                    .meters(allMeters)
                    .dateFrom(Integer.parseInt(textWithDate.get(1)))
                    .dateTo(Integer.parseInt(textWithDate.get(2)))
                    .build();
        } catch (Exception e) {
            return ResponseMeterDataDto.builder().build();
        }
    }
}
