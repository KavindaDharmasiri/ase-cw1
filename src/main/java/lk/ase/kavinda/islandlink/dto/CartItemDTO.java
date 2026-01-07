package lk.ase.kavinda.islandlink.dto;

public class CartItemDTO {
    private Long id;
    private ProductDTO product;
    private Integer quantity;
    private String createdAt;

    public CartItemDTO() {}

    public CartItemDTO(Long id, ProductDTO product, Integer quantity, String createdAt) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductDTO getProduct() { return product; }
    public void setProduct(ProductDTO product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public static class ProductDTO {
        private Long id;
        private String name;
        private Double price;
        private String imageUrl;
        private Integer unit;

        public ProductDTO() {}

        public ProductDTO(Long id, String name, Double price, String imageUrl, Integer unit) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
            this.unit = unit;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public Double getPrice() { return price; }
        public void setPrice(Double price) { this.price = price; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

        public Integer getUnit() { return unit; }
        public void setUnit(Integer unit) { this.unit = unit; }
    }
}