package com.tinkoff.practice;

import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;

import static com.tinkoff.practice.EndPoint.BASE_PATH;
import static com.tinkoff.practice.EndPoint.BASE_URL;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.*;

public class CountryTest {
    private static RequestSpecification givenRequest;
    private static EntityManagerFactory entityManagerFactory;
    private static String token;

    @BeforeAll
    public static void setUpJpa() {
        entityManagerFactory = Persistence.createEntityManagerFactory("dbo");
        givenRequest = given()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .port(8080)
                .filters(new RequestLoggingFilter(), new ResponseLoggingFilter());

        token = givenRequest
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"password\": \"u7ljdajLNo7PsVw7\",\n" +
                        "  \"rememberMe\": true,\n" +
                        "  \"username\": \"admin\"\n" +
                        "}")
                .post("authenticate")
                .then().statusCode(HttpStatus.SC_OK)
                .extract().path("id_token");
    }

    @AfterAll
    public static void tearDownJpa() {
        entityManagerFactory.close();
    }

    @Test
    public void shouldGetAllCountries() throws SQLException {
        final Region region = new Region("Волантис");

        final EntityManager em = entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(region);
        em.getTransaction().commit();

        em.getTransaction().begin();
        final Region newRegion = em.find(Region.class, region.getId());
        final Country newCountry = new Country("USA", newRegion.getId());
        em.persist(newCountry);
        final Country newCountryDb = em.find(Country.class, newCountry.getId());
        em.getTransaction().commit();

        try {
            givenRequest.auth()
                    .oauth2(token)
                    .when()
                    .get("countries")
                    .then()
                    .statusCode(is(SC_OK))
                    .body("id", hasItem(newCountryDb.getId()));
        } finally {
            em.getTransaction().begin();
            em.remove(newCountry);
            em.remove(newRegion);
            em.getTransaction().commit();
        }
    }

    @Test
    public void shouldGetAuthorities() {
        givenRequest
                .auth()
                .oauth2(token)
                .when()
                .get("authorities")
                .then().statusCode(HttpStatus.SC_OK);
    }

    @Test
    public void shouldPostNewRegion() {
        String regionName = "Горноалтайск";
        Region newRegion = new Region(regionName);
        int regionId = 0;
        try {
            regionId = givenRequest
                    .auth()
                    .oauth2(token)
                    .when()
                    .body(newRegion)
                    .post("regions")
                    .then().statusCode(HttpStatus.SC_CREATED)
                    .extract().path("id");

            final EntityManager em = entityManagerFactory.createEntityManager();
            final Region newPostRegion = em.find(Region.class, regionId);
            Assertions.assertEquals(regionId, newPostRegion.getId());
            Assertions.assertEquals(regionName, newPostRegion.getRegionName());
        } finally {
            givenRequest
                    .auth()
                    .oauth2(token)
                    .when()
                    .delete("regions/" + regionId);
        }
    }
    @Test
    public void shouldPostNewCountry() {
        // Пока нерабочий, так как нет понимания, как правильно создать Country: 139
        String regionName = "R" + System.currentTimeMillis();
        Region newRegion = new Region(regionName);

        int regionId = givenRequest
                .auth()
                .oauth2(token)
                .when()
                .body(newRegion)
                .post("regions")
                .then().statusCode(HttpStatus.SC_CREATED)
                .extract().path("id");

        int countryId = 0;
        try {
            String countryName = "C" + System.currentTimeMillis();
            Region region = new Region();
            region.setId(regionId);
            region.setRegionName(regionName);
            CountryDto newCountry = new CountryDto();
            newCountry.setCountryName(countryName);
            newCountry.setRegion(region);
            countryId = givenRequest
                    .auth()
                    .oauth2(token)
                    .when()
                    .body(newCountry)
                    .post("countries")
                    .then().statusCode(HttpStatus.SC_CREATED)
                    .extract().path("id");

            final EntityManager em = entityManagerFactory.createEntityManager();
            final Country newPostCountry = em.find(Country.class, countryId);
            Assertions.assertEquals(countryId, newPostCountry.getId());
        } finally {
            givenRequest
                    .auth()
                    .oauth2(token)
                    .when()
                    .delete("countries/" + countryId);
        }
    }
}
