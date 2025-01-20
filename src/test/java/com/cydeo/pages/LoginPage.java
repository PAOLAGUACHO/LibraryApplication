package com.cydeo.pages;

import com.cydeo.utilities.Driver;
import com.cydeo.utilities.LibraryUtils;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.security.PublicKey;
import java.util.Map;

public class LoginPage extends LibraryUtils {

 public LoginPage() {
     PageFactory.initElements(Driver.getDriver(),this);
 }

 @FindBy(id="inputEmail")
    public WebElement email;

 @FindBy(id="inputPassword")
    public WebElement password;

 @FindBy(xpath = "//button[@class='btn btn-lg btn-primary btn-block']")
    public WebElement SignInButton;


 public void signIn(String email, String password) {
     this.email.sendKeys(email);
     this.password.sendKeys(password);
     this.SignInButton.click();
 }

 public void singInWithRole (String role) {

     Map<String, String> credentials = returnCredentials(role);

     String email = credentials.get("email");
     String password = credentials.get("password");

     signIn(email, password);
 }


}
