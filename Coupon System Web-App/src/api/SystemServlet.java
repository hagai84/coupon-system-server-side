package api;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import core.CouponSystem;

/**
 * Servlet implementation class SystemServlet
 */

public class SystemServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static CouponSystem couonSystem = CouponSystem.getInstance();   
       
	private static String driverName = "com.mysql.jdbc.Driver";
	private static String databaseUrl = "jdbc:mysql://db4free.net:3306/coupon_system";
	private static String userName = "coupon_group";
	private static String password = "12345678";
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SystemServlet() {
        super();		
        // TODO Auto-generated constructor stub
    }

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
		couonSystem.shutdown();
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
		couonSystem.setServer(driverName, databaseUrl, userName, password);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
