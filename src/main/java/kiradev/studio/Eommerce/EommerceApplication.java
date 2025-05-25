package kiradev.studio.Eommerce;

import kiradev.studio.Eommerce.Enum.UserRole;
import kiradev.studio.Eommerce.Enum.UserStatus;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.UserRepository;
import kiradev.studio.Eommerce.service.CategoryService;
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


}
