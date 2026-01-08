package lk.ase.kavinda.islandlink.controller;

import lk.ase.kavinda.islandlink.dto.CartItemDTO;
import lk.ase.kavinda.islandlink.entity.Cart;
import lk.ase.kavinda.islandlink.entity.Product;
import lk.ase.kavinda.islandlink.entity.User;
import lk.ase.kavinda.islandlink.repository.CartRepository;
import lk.ase.kavinda.islandlink.repository.ProductRepository;
import lk.ase.kavinda.islandlink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:4200")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<CartItemDTO> getCartItems(Authentication authentication) {
        try {
            System.out.println("Getting cart items for user: " + authentication.getName());
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user == null) {
                System.out.println("User not found");
                return List.of();
            }
            
            List<Cart> cartItems = cartRepository.findByUser(user);
            System.out.println("Found " + cartItems.size() + " cart items");
            return cartItems.stream()
                    .map(this::convertToDTO)
                    .filter(dto -> dto != null)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting cart items: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    private CartItemDTO convertToDTO(Cart cart) {
        if (cart == null || cart.getProduct() == null) {
            return null;
        }
        
        Product product = cart.getProduct();
        Double price = product.getPrice() != null ? product.getPrice().doubleValue() : 0.0;
        
        CartItemDTO.ProductDTO productDTO = new CartItemDTO.ProductDTO(
            product.getId(),
            product.getName(),
            price,
            product.getImageUrl(),
            1 // Default unit value since unit is a string like "kg", "bottle", etc.
        );
        
        return new CartItemDTO(
            cart.getId(),
            productDTO,
            cart.getQuantity(),
            cart.getCreatedAt() != null ? cart.getCreatedAt().toString() : ""
        );
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody AddToCartRequest request, Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found"));
            }

            Product product = productRepository.findById(request.getProductId()).orElse(null);
            if (product == null) {
                return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Product not found"));
            }

            Optional<Cart> existingCart = cartRepository.findByUserAndProductId(user, request.getProductId());
            
            if (existingCart.isPresent()) {
                Cart cart = existingCart.get();
                cart.setQuantity(cart.getQuantity() + request.getQuantity());
                cartRepository.save(cart);
            } else {
                Cart cart = new Cart(user, product, request.getQuantity());
                cartRepository.save(cart);
            }

            return ResponseEntity.ok(Map.of("success", true, "message", "Item added to cart"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Error adding to cart"));
        }
    }

    @DeleteMapping("/clear")
    public ResponseEntity<String> clearCart(Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user != null) {
                cartRepository.deleteByUser(user);
            }
            return ResponseEntity.ok("Cart cleared");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error clearing cart");
        }
    }

    @PutMapping("/update/{productId}")
    public ResponseEntity<String> updateCartItem(@PathVariable Long productId, @RequestBody UpdateQuantityRequest request, Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            if (request.getQuantity() <= 0) {
                cartRepository.deleteByUserAndProductId(user, productId);
                return ResponseEntity.ok("Cart item removed");
            }

            Optional<Cart> cartItem = cartRepository.findByUserAndProductId(user, productId);
            if (cartItem.isPresent()) {
                Cart cart = cartItem.get();
                cart.setQuantity(request.getQuantity());
                cartRepository.save(cart);
                return ResponseEntity.ok("Cart item updated");
            } else {
                return ResponseEntity.badRequest().body("Cart item not found");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating cart item");
        }
    }

    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeFromCart(@PathVariable Long productId, Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName()).orElse(null);
            if (user == null) {
                return ResponseEntity.badRequest().body("User not found");
            }

            cartRepository.deleteByUserAndProductId(user, productId);
            return ResponseEntity.ok("Item removed from cart");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing from cart");
        }
    }

    @GetMapping("/user-address")
    public ResponseEntity<String> getUserAddress(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body("User not found");
        }
        return ResponseEntity.ok(user.getAddress() != null ? user.getAddress() : "No address on file");
    }

    public static class UpdateQuantityRequest {
        private Integer quantity;

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }

    public static class AddToCartRequest {
        private Long productId;
        private Integer quantity;

        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}
