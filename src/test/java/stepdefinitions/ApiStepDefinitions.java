package stepdefinitions;

import api.endpoints.AuthApi;
import api.endpoints.ProductApi;
import api.models.response.LoginResponse;
import api.models.response.ProductResponse;
import helper.ConfigReader;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import org.testng.Assert;

/**
 * Cucumber step definitions for API tests.
 * Follows the same pattern as LoginPageSteps for UI tests.
 */
public class ApiStepDefinitions {

    private final AuthApi authApi = new AuthApi();
    private Response response;
    private String authToken;

    @Given("I have the API base URL configured")
    public void iHaveTheApiBaseUrlConfigured() {
        String baseUrl = ConfigReader.get("api.base.url");
        Assert.assertNotNull(baseUrl, "api.base.url must be set in config.properties");
    }

    @When("I send a POST request to login with valid credentials")
    public void iSendAPostRequestToLoginWithValidCredentials() {
        String email = ConfigReader.get("login.username");
        String password = ConfigReader.get("login.password");
        response = authApi.login(email, password);
    }

    @When("I send a POST request to login with invalid credentials")
    public void iSendAPostRequestToLoginWithInvalidCredentials() {
        response = authApi.login("invalid@test.com", "wrongpassword");
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(int expectedStatusCode) {
        Assert.assertEquals(response.getStatusCode(), expectedStatusCode,
                "Unexpected status code. Response: " + response.getBody().asString());
    }

    @And("the response body should contain a token")
    public void theResponseBodyShouldContainAToken() {
        LoginResponse loginResponse = response.as(LoginResponse.class);
        Assert.assertNotNull(loginResponse.getToken(), "Token should not be null");
        Assert.assertFalse(loginResponse.getToken().isEmpty(), "Token should not be empty");
    }

    @Given("I am logged in via the API")
    public void iAmLoggedInViaTheApi() {
        String email = ConfigReader.get("login.username");
        String password = ConfigReader.get("login.password");
        LoginResponse loginResponse = authApi.loginAndGetToken(email, password);
        authToken = loginResponse.getToken();
        Assert.assertNotNull(authToken, "Login failed — token is null");
    }

    @When("I send a GET request to fetch all products")
    public void iSendAGetRequestToFetchAllProducts() {
        ProductApi productApi = new ProductApi(authToken);
        response = productApi.getAllProducts();
    }

    @And("the product list should not be empty")
    public void theProductListShouldNotBeEmpty() {
        ProductResponse productResponse = response.as(ProductResponse.class);
        Assert.assertNotNull(productResponse.getData(), "Product data should not be null");
        Assert.assertFalse(productResponse.getData().isEmpty(), "Product list should not be empty");
    }
}
