package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class LoadLog4jResourceTest {

    @Test
    public void testLoadEndpoint() {
        given()
                .when().get("/load-jars")
                .then()
                .statusCode(200)
                .body(is(LoadLog4jResource.DEFAULT_RESPONSE_STRING));
    }
}
