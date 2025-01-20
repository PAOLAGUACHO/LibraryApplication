package com.cydeo.utilities;

import com.cydeo.pages.BookPage;
import org.openqa.selenium.WebElement;

public class BrowserUtils {

    public static void pause(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        }catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void navigateToPage(String page){

        if (page.equalsIgnoreCase("books")) {
            BookPage bookPage = new BookPage();
            bookPage.booksPage.click();
        }

    }
}