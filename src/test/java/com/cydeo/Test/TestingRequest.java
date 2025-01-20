package com.cydeo.Test;

import com.cydeo.pages.BasePage;
import com.cydeo.pages.BookPage;
import com.cydeo.pages.LoginPage;
import com.cydeo.utilities.*;
import com.cydeo.utilities.Driver;
import io.cucumber.java.en.Given;
import io.cucumber.java.it.Ma;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import io.restassured.path.json.JsonPath;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.sql.*;
import java.time.Duration;
import java.util.*;

import static com.cydeo.utilities.DataBaseUtil.getColumnCount;
import static io.restassured.RestAssured.given;

public class TestingRequest {

    LoginPage singInWithRole = new LoginPage();

    RequestSpecification authRequest = given().header("x-library-token",LibraryUtils.RoleLoginAuthorization("librarian"));
    Response response;
    ValidatableResponse then;
    JsonPath jsonPath;
    @Test
    public void librarianLoginInAPI()  {
       LibraryUtils.librarianLogin();
    }

    @Test
    public void getAllUsers() {

        authRequest.accept(ContentType.JSON);

        response = authRequest.get(ConfigurationReader.getProperty("base_url") + "/get_all_users");

        then = response.then().statusCode(200);

        then.contentType("application/json; charset=utf-8");

        response.prettyPrint();
    }

    @Test
    public void singleUser() {
        String id = authRequest
                .and()
                .pathParam("id", 1)
                .log().everything().toString();


        response= authRequest
                  .when()
                .get(ConfigurationReader.getProperty("base_url") + "/get_user_by_id/{id}");





       /*given().header("x-library-token", LibraryUtils.RoleLoginAuthorization("librarian"))
                .accept(ContentType.JSON)
                .and()
                .pathParam("id", 1)
                .when()
                .get(ConfigurationReader.getProperty("base_url") + "/get_user_by_id/{id}");*/




    }

    Map<String, Object> bookMap;

    @Test
    public void postRequest() {

       bookMap = LibraryUtils.creatRandomBook();
        System.out.println("bookMap = " + bookMap);

//        Map<String,Object> bookMap = new LinkedHashMap<>();
//        bookMap.put("name","John Doe");
//        bookMap.put("author","John Doe");
//        bookMap.put("isbn","978-3-16-148410-0");
//        bookMap.put("year",1995);
//        bookMap.put("description","Action");
//        bookMap.put("book_category_id",1000);


        authRequest.accept(ContentType.JSON)
                .contentType("application/x-www-form-urlencoded")
                .formParams(bookMap)
                .when()
                .post(ConfigurationReader.getProperty("base_url") + "/add_book")
                .then()
                .statusCode(200);


    }

    @Test
    public void postUser(){
        Map<String,Object> userMap = LibraryUtils.createRandomUser();
        System.out.println("userMap = " + userMap);

//        Map<String,Object> randomUser = new LinkedHashMap<>();
//        randomUser.put("full_name","paola");
//        randomUser.put("email","paola@gmail.com");
//        randomUser.put("password","123456");
//        randomUser.put("user_group_id","2");
//        randomUser.put("status","ACTIVE");
//        randomUser.put("start_date","1212-13-12");
//        randomUser.put("end_date","1314-15-15");
//        randomUser.put("address","148-04Ave");

        given().header("x-library-token",LibraryUtils.RoleLoginAuthorization("librarian"))
                .accept(ContentType.JSON)
                .contentType(ContentType.URLENC)
                .formParams(userMap)
                .when()
                .post(ConfigurationReader.getProperty("base_url") + "/add_user")
                .then()
                .statusCode(200);


    }

    @Test
    public void dbConnectionWithGetDataMap() throws SQLException {

        String dburl = ConfigurationReader.getProperty("library2.db.url");
        String dbUser = ConfigurationReader.getProperty("library2.db.username");
        String dbPassword = ConfigurationReader.getProperty("library2.db.password");
        String query = "select * from users where id=20756";
        // Creating Connection
        Connection connection = DriverManager.getConnection(dburl, dbUser, dbPassword);
        //allows flexible navi.        allows only read
        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery(query);

        /*
        //get column size
        System.out.println("rs.getMetaData().getColumnCount() = " + rs.getMetaData().getColumnCount());

        rs.next();
        Map<String,String> map = new LinkedHashMap<>();

        for (int column = 1; column <= rs.getMetaData().getColumnCount(); column++) {
           for (int columnValue = 1; columnValue <= rs.getMetaData().getColumnCount(); columnValue++) {
               map.put(rs.getMetaData().getColumnName(columnValue), rs.getString(columnValue));
           }
        }
        System.out.println("map = " + map);

         */

        System.out.println("DataBaseUtil.getDataTable(rs) = " + DataBaseUtil.getDataTable(rs));


    }

    @Test
    public void tes()  {
        LoginPage loginPage = new LoginPage();

        Driver.getDriver().get(ConfigurationReader.getProperty("library_url"));

        Map<String, Object> randomUser = LibraryUtils.createRandomUser();
        System.out.println("randomUser = " + randomUser);

        loginPage.email.sendKeys(randomUser.get("email").toString());

        BrowserUtils.pause(3);
        loginPage.password.sendKeys(randomUser.get("password").toString());
        BrowserUtils.pause(3);
        loginPage.SignInButton.click();


    }

    @Test
    public void getbookById() {

//        JsonPath jsonPath1 = authRequest.accept(ContentType.JSON)
//                .pathParam("id", 34286)
//                .when()
//                .get(ConfigurationReader.getProperty("base_url") + "/get_book_by_id/{id}")
//                .then()
//                .statusCode(200)
//                .extract().jsonPath();
//
//        String id = jsonPath1.getString("id");
//        System.out.println("id = " + id);

        Map<String, Object> bookMap = LibraryUtils.creatRandomBook();

        JsonPath jsonPath1 = authRequest.accept(ContentType.JSON)
                .contentType("application/x-www-form-urlencoded")
                .formParams(bookMap)
                .when()
                .post(ConfigurationReader.getProperty("base_url") + "/add_book")
                .then()
                .statusCode(200)
                .extract().jsonPath().prettyPeek();

        int bookId = jsonPath1.getInt("book_id");

        //String bookById = BookUtils.getBookInfo(bookId, authRequest);
        //System.out.println("bookById = " + bookById);

    }

    @Test
    public void getBookById() {

        int id = 34489;

        JsonPath jsonPath1 = given().header("x-library-token", LibraryUtils.RoleLoginAuthorization("librarian"))
                .pathParam("id", id)
                .accept(ContentType.JSON)
                .when()
                .get(ConfigurationReader.getProperty("base_url") + "/get_book_by_id/{id}")
                .then()
                .statusCode(200)
                .extract().jsonPath().prettyPeek();


        String string = jsonPath1.getString("id");
        System.out.println("string = " + string);

    }

    @Test
    public void getAllBooks() throws SQLException {

        DataBaseUtil.connectToDatabase();
        ResultSet resultSet = DataBaseUtil.executeQuery("select * from books where id=34533");
        ResultSetMetaData rsmd = resultSet.getMetaData();


        //ALL COLUMN NAMES

        List<String> columnNameList = new ArrayList<>();

        for (int colIndex = 1; colIndex <= getColumnCount(); colIndex++) {
            String columnName = rsmd.getColumnName(colIndex);
            columnNameList.add(columnName);
        }

        System.out.println("columnNameList = " + columnNameList);

        int columnNum = 1;

        List<String> columnDataList = new ArrayList<>();

        while (resultSet.next()) {
            String cellValue = resultSet.getString(columnNum);
            columnDataList.add(cellValue);
        }

        System.out.println("columnDataList = " + columnDataList);

    }

    @Test
    public void test() throws SQLException {

        DataBaseUtil.connectToDatabase();
//        ResultSet resultSet = DataBaseUtil.executeQuery("select * from books where id=34533");
        //ResultSetMetaData rsmd = resultSet.getMetaData();

//        resultSet.next();
//
//        Map<String, Object> bookMap = new LinkedHashMap<>();
//
//        bookMap.put("id", resultSet.getString(1));
//        bookMap.put("name", resultSet.getString(2));
//        bookMap.put("isbn", resultSet.getString(3));
//        bookMap.put("year", resultSet.getString(4));
//        bookMap.put("author", resultSet.getString(5));
//        bookMap.put("book_category_id", resultSet.getString(6));
//        bookMap.put("description", resultSet.getString(7));
//        bookMap.put("added_date", resultSet.getString(8));
//
//        System.out.println("bookMap = " + bookMap);

        Map<String, String> stringStringMap = BookUtils.dataBaseBookInfo("34533");
        System.out.println("stringStringMap = " + stringStringMap);

        System.out.println("stringStringMap.get(\"name\") = " + stringStringMap.get("name"));


    }

    @Test
    public void test2() {

        Driver.getDriver().get(ConfigurationReader.getProperty("library_url"));
        Driver.getDriver().manage().window().maximize();
        Driver.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        LoginPage loginPage = new LoginPage();
        BookPage bookPage = new BookPage();
        BasePage basePage = new BasePage();

        loginPage.signIn(ConfigurationReader.getProperty("librarian_username"), ConfigurationReader.getProperty("password"));

        loginPage.SignInButton.click();

        bookPage.booksPage.click();

        bookPage.search.sendKeys("To Your Scattered Bodies Go");

        WebElement element = Driver.getDriver().findElement(By.xpath("//table[@id='tbl_books']//td[.='96']/following-sibling::td[1]"));
        System.out.println("element.getText() = " + element.getText());

        List<WebElement> isbn = Driver.getDriver().findElements(By.xpath("//table[@id='tbl_books']//td[2]"));
        System.out.println("isbn.size() = " + isbn.size());

        Map<String,String> isbnMap = new LinkedHashMap<>();

        for (int i = 0; i < isbn.size(); i++) {
            isbnMap.put("isbn", isbn.get(i).getText());

        }

        System.out.println("isbnMap = " + isbnMap);

//        for (WebElement webElement : isbn) {
//            webElement.getText();
//        }


//        List<String> bookInfo = new ArrayList<>();
//
//        for (WebElement book : bookPage.books) {
//            bookInfo.add(book.getText());
//
//        }
//
//        System.out.println("bookInfo = " + bookInfo);
//        bookInfo.size();System.out.println("bookInfo.size = " + bookInfo.size());


    }

    @Test
    public void test3()  {

//        String libraryUser = LibraryUtils.getToken("librarian41@library", "libraryUser");
//        System.out.println("libraryUser = " + libraryUser);

        Map<String, String> stringStringMap = new LinkedHashMap<>();
        stringStringMap.put("email", "librarian41@library");
        stringStringMap.put("password", "libraryUser");

        //LibraryUtils.apiLogin(stringStringMap.get("email"), stringStringMap.get("password"));


        String token = LibraryUtils.getToken(stringStringMap.get("email"), stringStringMap.get("password"));

        given().accept(ContentType.JSON)
                .contentType("application/x-www-form-urlencoded")
                .formParam("token", token)
                .when()
                .post(ConfigurationReader.getProperty("base_url") + "/decode")
                .then()
                .statusCode(200);
    }




}
