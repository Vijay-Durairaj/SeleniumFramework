package api.base;

import helper.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Base API client — analogous to BaseTest for UI.
 * Configures REST Assured with the base URL and common headers.
 */
public class ApiClient {

    private static RequestSpecification requestSpec;

    public static RequestSpecification getRequestSpec() {
        if (requestSpec == null) {
            String baseUrl = ConfigReader.get("api.base.url");
            requestSpec = new RequestSpecBuilder()
                    .setBaseUri(baseUrl)
                    .setContentType(ContentType.JSON)
                    .log(LogDetail.ALL)
                    .build();
        }
        return requestSpec;
    }

    /**
     * Returns a spec with Bearer token attached — use after login.
     */
    public static RequestSpecification getAuthRequestSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(getRequestSpec())
                .addHeader("Authorization", token)
                .build();
    }
}
