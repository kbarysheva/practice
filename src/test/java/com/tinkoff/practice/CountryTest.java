package com.tinkoff.practice;

import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.sql.SQLException;

import static com.tinkoff.practice.EndPoint.BASE_PATH;
import static com.tinkoff.practice.EndPoint.BASE_URL;
import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.Matchers.is;

public class CountryTest {
    private RequestSpecification givenRequest;
    private static EntityManagerFactory entityManagerFactory;

    @BeforeAll
    public static void setUpJpa() {
        entityManagerFactory = Persistence.createEntityManagerFactory("dbo");
    }

    @AfterAll
    public static void tearDownJpa() {
        entityManagerFactory.close();
    }

    @BeforeEach
    public void setUp() {
        givenRequest = given()
                .baseUri(BASE_URL)
                .basePath(BASE_PATH)
                .port(8080);
    }

    @Test
    public void shouldGetAllCountries() throws SQLException {
        final Region region = new Region("Браавос");

        final EntityManager em =  entityManagerFactory.createEntityManager();
        em.getTransaction().begin();
        em.persist(region);
        em.getTransaction().commit();

        em.getTransaction().begin();
        final Region newRegion = em.find(Region.class, region.getId());
        final Country newCountry = new Country("USA", newRegion.getId());
        em.persist(newCountry);
        em.getTransaction().commit();

        givenRequest.when()
                .get("/api/countries")
                .then()
                .statusCode(is(SC_OK));

        em.getTransaction().begin();
        em.remove(newCountry);
        em.remove(newRegion);
        em.getTransaction().commit();
    }
}
