package com.cydeo.utilities;

import com.mysql.cj.protocol.Resultset;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.specification.RequestSpecification.*;

public class BookUtils {

 public static Map<String,String> apiBookInfo(String bookId, RequestSpecification request) {

     JsonPath jsonPath = request.pathParam("id", bookId)
             .accept(ContentType.JSON)
             .when()
             .get(ConfigurationReader.getProperty("base_url")+"/get_book_by_id/{id}")
             .then()
             .statusCode(200)
             .extract().jsonPath();

     Map<String,String> objectMap = new LinkedHashMap<>();
     objectMap.put("id", jsonPath.getString("id"));
     objectMap.put("name", jsonPath.getString("name"));
     objectMap.put("isbn", jsonPath.getString("isbn"));
     objectMap.put("year", jsonPath.getString("year"));
     objectMap.put("author", jsonPath.getString("author"));
     objectMap.put("book_category_id", jsonPath.getString("book_category_id"));
     objectMap.put("description", jsonPath.getString("description"));
     objectMap.put("added_date", jsonPath.getString("added_date"));


     return objectMap;

 }


    public static Map<String,String> dataBaseBookInfo(String bookID){

        ResultSet resultSet = DataBaseUtil.executeQuery("select * from books where id='"+bookID+"'");
        Map<String,String> bookMap = new LinkedHashMap<>();

        try {
        resultSet.next();

            bookMap.put("id", resultSet.getString(1));
            bookMap.put("name", resultSet.getString(2));
            bookMap.put("isbn", resultSet.getString(3));
            bookMap.put("year", resultSet.getString(4));
            bookMap.put("author", resultSet.getString(5));
            bookMap.put("book_category_id", resultSet.getString(6));
            bookMap.put("description", resultSet.getString(7));
            bookMap.put("added_date", resultSet.getString(8));

        } catch (SQLException e) {
            System.out.println("Unable to load bookMap "+e.getMessage());
        }

        return bookMap;
    }








}


