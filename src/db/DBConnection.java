package db;
import java.util.List;
import java.util.Set;
import java.sql.*;

import entity.Item;


public interface DBConnection {
    /**
     * Close the connection.
     */
    public void close();
    
    /**
     * Return whether the user is existed
     * 
     * @param username
     */
    public boolean existUser(String username);
    
    /**
     * Return the result of creating a user
     * 
     * @param username
     * @param email
     * @param password
     * @return
     */
    public boolean createUser(String username, String password, String email);

    /**
     * Return whether the credential is correct. (This is not needed for main
     * course, just for demo and extension)
     * 
     * @param username
     * @param password
     * @return boolean
     */

    /**
     * Insert the favorite items for a user.
     * 
     * @param userId
     * @param itemIds
     */
    public void setFavoriteItems(String userId, List<String> itemIds);

    /**
     * Delete the favorite items for a user.
     * 
     * @param userId
     * @param itemIds
     */
    public void unsetFavoriteItems(String userId, List<String> itemIds);

    /**
     * Get the favorite item id for a user.
     * 
     * @param userId
     * @return itemIds
     */
    public Set<String> getFavoriteItemIds(String userId);

    /**
     * Get the favorite items for a user.
     * 
     * @param userId
     * @return items
     */
    public Set<Item> getFavoriteItems(String userId);

    /**
     * Gets categories based on item id
     * 
     * @param itemId
     * @return set of categories
     */
    public Set<String> getCategories(String itemId);

    /**
     * Search items near a geolocation and a term (optional).
     * 
     * @param userId
     * @param lat
     * @param lon
     * @param term
     *            (Nullable)
     * @return list of items
     */
    public List<Item> searchItems(double lat, double lon, String term);

    /**
     * Save item into db.
     * 
     * @param item
     */
    public void saveItem(Item item);


    /**
     * Return whether the credential is correct. (This is not needed for main
     * course, just for demo and extension)
     * 
     * @param userId
     * @param password
     * @return boolean
     */
    public boolean verifyLogin(String userId, String password);

}
