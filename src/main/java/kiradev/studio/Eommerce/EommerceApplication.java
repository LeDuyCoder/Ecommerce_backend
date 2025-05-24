package kiradev.studio.Eommerce;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.Enum.UserStatus;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.UserRepository;
import kiradev.studio.Eommerce.service.ProductService;
import kiradev.studio.Eommerce.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

@SpringBootApplication
public class EommerceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EommerceApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(ProductService productService){
		return args -> {
			//addProducts(productService);
		};
	}


	public void registerUsers(UserService userService) {
		for (int i = 1; i <= 50; i++) {
			User user = new User();
			user.setEmail("user" + i + "@example.com");
			user.setPassword("password" + i); // In a real application, ensure passwords are hashed
			userService.registerUser(user.getPassword(), user.getEmail());
		}
	}

	public void addProducts(ProductService productService) {
		for (int i = 1; i <= 50; i++) {
			Products product = new Products();
			product.setName("Product " + i);
			product.setDescription("Description for Product " + i);
			product.setPrice(10.0 * i); // Example price
			product.setStock(100 - i); // Example stock
			product.setImage(new byte[0]); // Placeholder for image
			product.setShopID(UUID.fromString("8af18c6c-22f2-4834-9b86-b70308aee473")); // Assuming shop ID is set later
			productService.createProduct(product.getName(), product.getDescription(), product.getPrice(), product.getStock(), product.getImage(), product.getShopID());
		}
	}
}
