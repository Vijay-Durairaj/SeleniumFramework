package api.endpoints;

import api.base.ApiClient;
import api.models.request.LoginRequest;
import api.models.response.LoginResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Auth API endpoint methods — analogous to LoginPage in the UI layer.
 */
public class AuthApi {

    private static final String LOGIN_ENDPOINT = "/auth/login";

    /**
     * Posts login credentials and returns the raw Response.
     * Use this when you need to assert on status code or headers.
     */
    public Response login(String email, String password) {
        LoginRequest body = new LoginRequest(email, password);
        return given()
                .spec(ApiClient.getRequestSpec())
                .body(body)
                .when()
                .post(LOGIN_ENDPOINT)
                .then()
                .extract()
                .response();
    }

    /**
     * Posts login and returns the deserialized LoginResponse.
     * Use this when you just need the token for downstream calls.
     */
    public LoginResponse loginAndGetToken(String email, String password) {
        return login(email, password).as(LoginResponse.class);
    }
}
