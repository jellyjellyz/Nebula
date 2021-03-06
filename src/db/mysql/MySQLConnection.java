package db.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import db.DBConnection;
import entity.Item;
import entity.Item.ItemBuilder;
import external.TicketMasterAPI;


public class MySQLConnection implements DBConnection {
    private Connection conn;
    public MySQLConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").getConstructor().newInstance();
            conn = DriverManager.getConnection(MySQLDBUtil.URL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void close() {
        if (conn != null) {
            try {
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    

    @Override
    public void setFavoriteItems(String username, List<String> itemIds) {
        // TODO Auto-generated method stub
        if (conn == null) {
            System.out.println("DB connection failed");
            return;
        }
        try {
            String sql = "INSERT IGNORE INTO history(username, item_id) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            for (String itemId : itemIds) {
                ps.setString(2, itemId);
                ps.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unsetFavoriteItems(String username, List<String> itemIds) {
        if (conn == null) {
            System.out.println("DB connection failed");
            return;
        }
        try {
            String sql = "DELETE FROM history WHERE username = ? AND item_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            for (String itemId : itemIds) {
                ps.setString(2, itemId);
                ps.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public Set<String> getFavoriteItemIds(String username) {
        if (conn == null) {
            return new HashSet<>();
        }
        
        Set<String> favoriteItems = new HashSet<>();
        try {
            String sql = "SELECT item_id FROM history WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                favoriteItems.add(itemId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }

    @Override
    public Set<Item> getFavoriteItems(String username) {
        if (conn == null) {
            return new HashSet<>();
        }
        Set<Item> favoriteItems = new HashSet<>();
        Set<String> itemIds = getFavoriteItemIds(username);
        try {
            String sql = "SELECT * FROM items WHERE item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            for (String itemId : itemIds) {
                stmt.setString(1,  itemId);
                
                ResultSet rs = stmt.executeQuery();  // like map id java
                
                ItemBuilder builder = new ItemBuilder();
                
                while (rs.next()) {
                    builder.setItemId(rs.getString("item_id")); // json: snake_case
                    builder.setName(rs.getString("name"));
                    builder.setAddress(rs.getString("address"));
                    builder.setImageUrl(rs.getString("image_url"));
                    builder.setUrl(rs.getString("url"));
                    builder.setCategories(getCategories(itemId));
                    builder.setDistance(rs.getDouble("distance"));
                    builder.setRating(rs.getDouble("rating"));
                    
                    favoriteItems.add(builder.build());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return favoriteItems;
    }

    @Override
    public Set<String> getCategories(String itemId) {
        if (conn == null) {
            return new HashSet<String>();
        }
        
        Set<String> categories = new HashSet<>();
        
        try {
            String sql = "SELECT category FROM categories WHERE item_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, itemId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String category = rs.getString("category");
                categories.add(category);
            }
            
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return categories;
    }

    @Override
    public List<Item> searchItems(double lat, double lon, String term) {
        TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
        List<Item> items = ticketMasterAPI.search(lat, lon, term);
        
        for (Item item : items) {
            saveItem(item);
        }
        
        return items;
    }

    @Override
    public void saveItem(Item item) {
        // TODO Auto-generated method stub
        if (conn == null) {
            System.err.println("DB connection failed");
            return;
        }
        
        try {// sql injection issue
            String sql = "INSERT IGNORE INTO items VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, item.getItemId());
            ps.setString(2, item.getName());
            ps.setDouble(3, item.getRating());
            ps.setString(4, item.getAddress());
            ps.setString(5, item.getImageUrl());
            ps.setString(6, item.getUrl());
            ps.setDouble(7, item.getDistance());
            ps.execute();
            
            sql = "INSERT INTO categories VALUES(?, ?)";
            ps = conn.prepareStatement(sql);
            // 优化，第一个string都是相同的
            ps.setString(1, item.getItemId());
            for(String category : item.getCategories()) {
                ps.setString(2, category);// 多行
                ps.execute();
            }
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public boolean verifyLogin(String username, String password) {
        if (conn == null) {
            return false;
        }
        
        try {
            String sql = "SELECT username FROM users WHERE username = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    @Override
    public boolean existUser(String username) {
        if (conn == null) {
            return false;
        } 
        
        try {
            String sql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    @Override
    public boolean createUser(String username, String password, String email) {
        if (conn == null) {
            return false;
        }
        
        try {           
            String sql = "INSERT INTO users "
                            + "(username, password, email) "
                            + "VALUES "
                            + "(?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, email);
            stmt.executeUpdate();
                
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    
        return false;
    }

}
