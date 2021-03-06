package rpc;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;
import entity.Item;
import external.TicketMasterAPI;

/**
 * Servlet implementation class SearchItem
 */
@WebServlet("/search")      // end point  // 自动map /search到SearchItem class
public class SearchItem extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchItem() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    //http://localhost:8080/Jupiter/search?lat=37.38&lon=-122.08&term=sport
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    // allow access only if session exists
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.setStatus(403);
            return;
        }

        // optional
        String username = session.getAttribute("username").toString(); 
	    
	    double lat = Double.parseDouble(request.getParameter("lat"));
		double lon = Double.parseDouble(request.getParameter("lon"));
		
		String term = request.getParameter("term");

//		TicketMasterAPI ticketMasterAPI = new TicketMasterAPI();
//		List<Item> items = ticketMasterAPI.search(lat, lon, keyWord);
//		
//		JSONArray array = new JSONArray();
//		
//		try {
//		    for(Item item : items) {
//		        JSONObject object = item.toJSONObject();
//		        array.put(object);
//		    }
//		} catch (Exception e) {
//		    e.printStackTrace();
//		}
//		RpcHelper.writeJsonArray(response, array);
		DBConnection connection = DBConnectionFactory.getConnection();
		try {
            List<Item> items = connection.searchItems(lat, lon, term);
            Set<String> favoriteItems = connection.getFavoriteItemIds(username);
            
            
            JSONArray array = new JSONArray();
            for (Item item : items) {
                JSONObject obj = item.toJSONObject();
                obj.put("favorite", favoriteItems.contains(item.getItemId()));
                array.put(obj);
            }
            RpcHelper.writeJsonArray(response, array);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
