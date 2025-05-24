package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.Enum.ProductStatus;
import kiradev.studio.Eommerce.entity.Products;
import kiradev.studio.Eommerce.entity.User;
import kiradev.studio.Eommerce.repository.ProductRepository;
import kiradev.studio.Eommerce.service.Interface.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    private final UserService userService;

    @Autowired
    public ProductService(ProductRepository productRepository, UserService userService) {
        this.productRepository = productRepository;
        this.userService = userService;
    }


    @Override
    public void createProduct(String name, String description, double price, int stock, byte[] image, UUID shopId) {
        Products product = new Products();
        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setStock(stock);
        product.setImage(image);
        product.setShopID(shopId);
        product.setStatus(ProductStatus.AVAILABLE);
        product.setSold(0);
        product.setCreatedAt(Instant.now().toString());

        productRepository.save(product);
    }

    @Override
    public void updateProduct(UUID productId, String name, String description, double price, int stock, byte[] image) {
        Products product = productRepository.findByproductID(productId);

        if(product!=null){
            if(name != null) {
                product.setName(name);
            }
            if(description != null) {
                product.setDescription(description);
            }
            if(price >= 0) {
                product.setPrice(price);
            }
            if(stock >= 0) {
                product.setStock(stock);
            }
            if(image != null) {
                product.setImage(image);
            }

            productRepository.save(product);
        }
    }

    @Override
    public void deleteProduct(UUID productId) {
        Products product = productRepository.findByproductID(productId);
        if (product != null) {
            productRepository.delete(product);
        }
    }

    @Override
    public Products getProductById(UUID productId) {
        return productRepository.findByproductID(productId);
    }

    @Override
    public List<Products> getAllProductsByShopID(UUID shopId) {
        return productRepository.findByshopID(shopId);
    }

    public List<Products> getAllProducts() {
        return productRepository.findAll();
    }
}
