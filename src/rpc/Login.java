package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnection;
import db.DBConnectionFactory;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        DBConnection conn = DBConnectionFactory.getConnection();
        try {
            JSONObject obj = new JSONObject();
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.setStatus(403);
                obj.put("status", "Session invalid");
            } else {
                String username = (String) session.getAttribute("username");
                obj.put("status", "OK");
                obj.put("username", username);
            }
            
            RpcHelper.writeJsonObject(response, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnectionFactory.getConnection();
		try {
            JSONObject input = RpcHelper.readJSONObject(request);
            String username = input.getString("username");
            String password = input.getString("password");
            
            JSONObject obj = new JSONObject();
            
            if (conn.verifyLogin(username, password)) {
                HttpSession session = request.getSession();
                session.setAttribute("username", username);
                // set session to expire in 10 minutes
                session.setMaxInactiveInterval(10 * 60);

                obj.put("status", "OK");
                obj.put("username", username);

            } else {
                response.setStatus(401);
            }
            RpcHelper.writeJsonObject(response, obj);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
	}

}
