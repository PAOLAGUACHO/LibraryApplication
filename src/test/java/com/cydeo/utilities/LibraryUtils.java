package com.cydeo.utilities;

import com.github.javafaker.Faker;
import io.cucumber.java.en.Given;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lombok.Getter;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import static io.restassured.RestAssured.given;

public class LibraryUtils {


    public static void librarianLogin() {

        String token = RoleLoginAuthorization("librarian");

        given()
                .header("Authorization", token)
                .then()
                .statusCode(200);
    }

    // This is giving me the LIBRARIAN token.
    public static String getToken(String email, String password) {
        JsonPath jsonPath = given().accept(ContentType.JSON)
                .contentType(ContentType.URLENC)
                .formParam("email", email)
                .formParam("password", password)
                .when().post(ConfigurationReader.getProperty("base_url") + "/login")
                .then()
                .statusCode(200)
                .extract().jsonPath();

        String token = jsonPath.getString("token");

        return token;

    }

    // This is giving me the email and password according to role
    public static String RoleLoginAuthorization(String role) {

        //String email = System.getenv("email");
        //String password = System.getenv("password");

        Map<String, String> roleCredentials = returnCredentials(role);
        String email = roleCredentials.get("email");
        String password = roleCredentials.get("password");

        return getToken(email, password);

    }

    public static Map<String, String> returnCredentials(String role) {
        String email = "";
        String password = "";

        switch (role) {
            case "librarian":
                email = ConfigurationReader.getProperty("librarian_username");
                password = ConfigurationReader.getProperty("password");
                break;
        }
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);
        return credentials;
    }

    public static Map<String, Object> creatRandomBook() {
        Faker faker = new Faker();
        Map<String, Object> randomBook = new LinkedHashMap<>();
        randomBook.put("name", faker.book().title());
        randomBook.put("isbn", faker.number().randomNumber());
        randomBook.put("year", faker.number().numberBetween(1900, 2000));
        randomBook.put("author", faker.book().author());
        randomBook.put("book_category_id", faker.number().numberBetween(1, 20));
        randomBook.put("description", faker.color().name());

        return randomBook;

    }

//    public static Map<String,Object> creatRandomUser() {
//
//        Faker faker = new Faker();
//
//        Map<String,Object> randomUser = new LinkedHashMap<>();
//        randomUser.put("full_name",faker.name().fullName());
//        randomUser.put("email",faker.internet().emailAddress());
//        randomUser.put("password","libraryUser");
//        randomUser.put("user_group_id","2");
//        randomUser.put("status","ACTIVE");
//        randomUser.put("start_date","1217-13-12");
//        randomUser.put("end_date","1314-15-11");
//        randomUser.put("address",faker.address().fullAddress());
//
//        return randomUser;

    public static Map<String, Object> createRandomUser() {

        Faker faker = new Faker();
        Map<String, Object> userMap = new LinkedHashMap<>();
        String fullName = faker.name().fullName();
        String email = fullName.substring(0, fullName.indexOf(" ")) + faker.number().numberBetween(1, 100) + "@library";
        System.out.println(email);
        userMap.put("full_name", fullName);
        userMap.put("email", email);
        userMap.put("password", "libraryUser");
        // 2 is librarian as role
        userMap.put("user_group_id", "2");
        userMap.put("status", "ACTIVE");
        userMap.put("start_date", "2023-03-11");
        userMap.put("end_date", "2024-03-11");
        userMap.put("address", faker.address().cityName());

        return userMap;
    }

    public static Response testUser(String email, String password) {

        Response response = given().accept(ContentType.JSON)
                .contentType(ContentType.URLENC)
                .formParam("email", email)
                .formParam("password", password)
                .when().post(ConfigurationReader.getProperty("base_url") + "/login")
                .then()
                .statusCode(200)
                .extract().response();

        JsonPath jsonPath = response.jsonPath();

       return response;
    }

    public static void postRequestWithToken(String token) {






    }


}






