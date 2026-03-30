package api.endpoints;

import api.base.ApiClient;
import api.models.response.ProductResponse;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

/**
 * Product API endpoint methods — analogous to HomePage in the UI layer.
 */
public class ProductApi {

    private static final String GET_PRODUCTS_ENDPOINT = "/product/get-all-products";

    private final String authToken;

    public ProductApi(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Fetches all products and returns the raw Response.
     */
    public Response getAllProducts() {
        return given()
                .spec(ApiClient.getAuthRequestSpec(authToken))
                .when()
                .get(GET_PRODUCTS_ENDPOINT)
                .then()
                .log().all()
                .extract().response();
    }

    /**
     * Fetches all products and returns the deserialized response.
     */
    public ProductResponse getAllProductsDeserialized() {
        return getAllProducts().as(ProductResponse.class);
    }
}
