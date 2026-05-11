package com.Job.applybot.bot;

import com.Job.applybot.Service.AnswerEngine;
import org.openqa.selenium.*;

public class PopupHandler {

    public static void handle(WebDriver driver) {

        for (int i = 0; i < 10; i++) {
            try {
                String q = driver.findElement(
                        By.xpath("//*[contains(@class,'question')]")
                ).getText();

                String ans = AnswerEngine.getAnswer(q.toLowerCase());

                driver.findElement(By.xpath("//label[contains(.,'" + ans + "')]")).click();

                driver.findElement(By.xpath(
                        "//button[contains(.,'Next') or contains(.,'Submit')]"
                )).click();

                Thread.sleep(1500);

            } catch (Exception e) {
                break;
            }
        }
    }
}