package api.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Response POJO for GET /product/get-all-products
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductResponse {

    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private List<Product> data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Product {

        @JsonProperty("_id")
        private String id;

        @JsonProperty("productName")
        private String productName;

        @JsonProperty("productCategory")
        private String productCategory;

        @JsonProperty("productSubCategory")
        private String productSubCategory;

        @JsonProperty("productPrice")
        private int productPrice;

        @JsonProperty("productDescription")
        private String productDescription;

        @JsonProperty("productImage")
        private String productImage;
    }
}
