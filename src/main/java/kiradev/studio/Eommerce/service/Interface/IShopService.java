package kiradev.studio.Eommerce.service.Interface;

import kiradev.studio.Eommerce.entity.Shop;

import java.util.List;
import java.util.UUID;

public interface IShopService {
    void createShop(String name, String description, UUID userID);
    void updateShop(UUID shopid, String name, String description, byte[] image);
    void deleteShop(UUID uuid);
    Shop getShopById(UUID uuid);
    List<Shop> getAllShops();
    Shop getShopsByOwner(UUID userID);
}
