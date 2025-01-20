package com.cydeo.steps;

import com.cydeo.pages.BasePage;
import com.cydeo.pages.BookPage;
import com.cydeo.pages.LoginPage;
import com.cydeo.utilities.BookUtils;
import com.cydeo.utilities.BrowserUtils;
import com.cydeo.utilities.DataBaseUtil;
import com.cydeo.utilities.LibraryUtils;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Librarian_Steps extends LibraryUtils {

    LoginPage loginPage = new LoginPage();
    BookPage bookPage = new BookPage();
    BasePage basePage = new BasePage();

    Map<String, Object> randomBook;
    Map<String,Object> randomUser;

    RequestSpecification authRequest1 = RestAssured.given();
    Response response;
    ValidatableResponse then;
    JsonPath jsonPath;
    String expectedId;

    RequestSpecification authRequest2;
    Response testResponse;


    @Given("I logged Library api as a {string}")
    public void i_logged_library_api_as_a(String role) {
        authRequest1.header("x-library-token", LibraryUtils.RoleLoginAuthorization(role));
    }

    @Given("Accept header is {string}")
    public void accept_header_is(String acceptHeader) {
        authRequest1.accept(acceptHeader);
    }

    @When("I send GET request to {string} endpoint")
    public void iSendGETRequestToEndpoint(String endpoint) {

       response = authRequest1.when().get(endpoint).prettyPeek();
       then = response.then();
       jsonPath = response.jsonPath();

    }

    @Then("status code should be {int}")
    public void status_code_should_be(Integer expectedStatusCode) {

        assertEquals(expectedStatusCode, (Integer) response.statusCode());
    }

    @Then("Response Content type is {string}")
    public void response_content_type_is(String expectedContentType) {
        assertTrue(expectedContentType.equalsIgnoreCase(response.contentType()));

    }


    @And("{string} field should not be null")
    public void fieldShouldNotBeNull(String path) {
        //then.body(path, Matchers.everyItem(Matchers.notNullValue()));
        String expectedPath = response.jsonPath().getString(path);
        Assert.assertNotNull(expectedPath);

    }

// Scenario 2
    @And("Path param is {string}")
    public void pathParamIs(String id) {
        expectedId = id;
        authRequest1.pathParam("id", id);

    }

    @And("{string} field should be same with path param")
    public void fieldShouldBeSameWithPathParam(String path) {
        assertEquals(expectedId, response.jsonPath().getString(path));
    }

    @And("following fields should not be null")
    public void followingFieldsShouldNotBeNull(List<String>paths) {
        for(String path : paths) {
            Assert.assertNotNull(jsonPath.getString(path));
        }
    }

    //Scenario 3 API

    @And("Request Content Type header is {string}")
    public void requestContentTypeHeaderIs(String contentType) {
        authRequest1.contentType(contentType);
    }

    @And("I create a random {string} as request body")
    public void i_Create_A_Random_AsRequestBody(String dataType) {

        if(dataType.equalsIgnoreCase("book")) {
            randomBook = LibraryUtils.creatRandomBook();
            authRequest1.formParams(randomBook);
        } else if(dataType.equalsIgnoreCase("user")) {
            randomUser=LibraryUtils.createRandomUser();
            authRequest1.formParams(randomUser);
        }

    }

    @When("I send POST request to {string} endpoint")
    public void iSendPOSTRequestToEndpoint(String postEndpoint) {
        response = authRequest1.when().post(postEndpoint).prettyPeek();
        then = response.then();
        jsonPath = response.jsonPath();

    }

    @And("the field value for {string} path should be equal to {string}")
    public void theFieldValueForPathShouldBeEqualTo(String pathKey, String expectedValue) {
        String actualValue = response.jsonPath().getString(pathKey);
        System.out.println("actualValue = " + actualValue);
        assertEquals(expectedValue, actualValue);
        System.out.println("actualVal: " + actualValue);

}

//scenario 3 All Layers

    @And("I logged in Library UI as {string}")
    public void iLoggedInLibraryUIAs(String role) {
        loginPage.singInWithRole(role);
    }

    @And("I navigate to {string} page")
    public void iNavigateToPage(String page)  {
        BrowserUtils.navigateToPage(page);
    }

    @And("UI, Database and API created book information must match")
    public void ui_Database_And_API_CreatedBook_Information_Must_Match() {

        // POST create request id book
        String book_id = response.jsonPath().getString("book_id");

        // API
        Map<String, String> apiBookInfo = BookUtils.apiBookInfo(book_id, authRequest1);

        // DataBase
        Map<String, String> dataBaseBookInfo = BookUtils.dataBaseBookInfo(book_id);


        // API VS UI
        assertEquals(apiBookInfo.get("name"),randomBook.get("name"));
        assertEquals(apiBookInfo.get("author"),randomBook.get("author"));
        assertEquals(Integer.parseInt(apiBookInfo.get("year")),(int)randomBook.get("year"));
        assertEquals(Long.valueOf((apiBookInfo.get("isbn"))),randomBook.get("isbn"));


       // DB VS API
        assertEquals(dataBaseBookInfo.get("name"),randomBook.get("name"));
        assertEquals(dataBaseBookInfo.get("author"),randomBook.get("author"));
        assertEquals(Integer.parseInt(dataBaseBookInfo.get("year")),(int)randomBook.get("year"));
        assertEquals(Long.valueOf((dataBaseBookInfo.get("isbn"))),randomBook.get("isbn"));

        //UI VS BD
        assertEquals(randomBook.get("isbn"),Long.valueOf((dataBaseBookInfo.get("isbn"))));
        assertEquals(randomBook.get("name"),dataBaseBookInfo.get("name"));
        assertEquals(randomBook.get("author"),dataBaseBookInfo.get("author"));
        assertEquals((int)randomBook.get("year"),Integer.parseInt(dataBaseBookInfo.get("year")));

    }

//scenario 4 All Layers

    @And("created user information should match with Database")
    public void createdUserInformationShouldMatchWithDatabase() {

        String userId = response.jsonPath().getString("user_id");

        ResultSet resultSet = DataBaseUtil.executeQuery("select * from users where id = '" + userId + "'");

        Map<String, String> dataTable = DataBaseUtil.getDataTable(resultSet);

        assertEquals(randomUser.get("full_name"),dataTable.get("full_name"));
        assertEquals(randomUser.get("email"),dataTable.get("email"));
        assertEquals(randomUser.get("user_group_id"),dataTable.get("user_group_id"));
        assertEquals(randomUser.get("status"),dataTable.get("status"));
        assertEquals(randomUser.get("start_date"),dataTable.get("start_date"));
        assertEquals(randomUser.get("end_date"),dataTable.get("end_date"));
        assertEquals(randomUser.get("address"),dataTable.get("address"));

    }

    @And ("created user should be able to login Library UI")
    public void createdUserShouldBeAbleToLoginLibraryUI(){

        loginPage.signIn(randomUser.get("email").toString(),randomUser.get("password").toString());

    }

    @And("created user name should appear in Dashboard Page")
    public void createdUserNameShouldAppearInDashboardPage() {

        BrowserUtils.pause(5);
        assertTrue("User Name Element is does not appear",basePage.accountHolderName.isDisplayed());
    }

    //scenario 5

    @Given("I logged Library api with credentials {string} and {string}")
    public void i_Logged_Library_Api_With_Credentials(String email, String password) {

        response = LibraryUtils.testUser(email, password);

    }

    @And("I send token information as request body")
    public void iSendTokenInformationAsRequestBody() {

        String token = response.jsonPath().getString("token");

        authRequest1.formParam("token", token);

    }
}
