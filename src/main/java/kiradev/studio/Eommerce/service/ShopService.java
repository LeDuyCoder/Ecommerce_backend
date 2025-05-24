package kiradev.studio.Eommerce.service;

import kiradev.studio.Eommerce.entity.Shop;
import kiradev.studio.Eommerce.repository.ShopRepository;
import kiradev.studio.Eommerce.repository.UserRepository;
import kiradev.studio.Eommerce.service.Interface.IShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ShopService implements IShopService {
    private final ShopRepository shopRepository;
    private final UserRepository userRepository;

    @Autowired
    public ShopService(ShopRepository shopRepository, UserRepository userRepository) {
        this.shopRepository = shopRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates a new shop with the given name, description, and user ID.
     *
     * @param name        the name of the shop
     * @param description the description of the shop
     * @param userID      the ID of the user who owns the shop
     */
    @Override
    public void createShop(String name, String description, UUID userID) {
        Shop shop = new Shop();
        shop.setName(name);
        shop.setDescription(description);
        shop.setUserID(userID);
        shopRepository.save(shop);
    }

    /**
     * Updates an existing shop with the given ID, name, description, and image.
     *
     * @param shopid      the ID of the shop to update
     * @param name        the new name of the shop (can be null)
     * @param description the new description of the shop (can be null)
     * @param image       the new image of the shop (can be null)
     */
    @Override
    public void updateShop(UUID shopid, String name, String description, byte[] image) {
        Shop shop = shopRepository.findByid(shopid);

        if(name != null) {
            shop.setName(name);
        }
        if(description != null) {
            shop.setDescription(description);
        }
        if(image != null) {
            shop.setLogo(image);
        }

        shopRepository.save(shop);


    }

    /**
     * Deletes a shop with the given UUID.
     *
     * @param uuid the UUID of the shop to delete
     */
    @Override
    public void deleteShop(UUID uuid) {
        shopRepository.deleteByid(uuid);
    }

    /**
     * Retrieves a shop by its UUID.
     *
     * @param uuid the UUID of the shop to retrieve
     * @return the shop with the specified UUID, or null if not found
     */
    @Override
    public Shop getShopById(UUID uuid) {
        Shop shop = shopRepository.findByid(uuid);
        if(shop == null) {
            return null;
        }
        return shop;
    }

    /**
     * Retrieves all shops from the database.
     *
     * @return a list of all shops
     */
    @Override
    public List<Shop> getAllShops() {
        return shopRepository.findAll();
    }

    /**
     * Retrieves a shop by the owner's user ID.
     *
     * @param userID the ID of the user who owns the shop
     * @return the shop owned by the specified user, or null if not found
     */
    @Override
    public Shop getShopsByOwner(UUID userID) {
        Shop shop = shopRepository.findByUserID(userID);
        if(shop == null) {
            return null;
        }
        return shop;
    }

    /**
     * Checks if a shop exists with the given UUID.
     *
     * @param uuid the UUID of the shop to check
     * @return true if the shop exists, false otherwise
     */
    public boolean isShopExist(UUID uuid) {
        Shop shop = shopRepository.findByid(uuid);
        return shop != null;
    }
}
