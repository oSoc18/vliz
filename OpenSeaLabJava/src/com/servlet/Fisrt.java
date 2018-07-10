package com.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.Bean.CoordinatesBean;

/**
 * Servlet implementation class Fisrt
 */
@WebServlet("/Fisrt")
public class Fisrt extends HttpServlet {
	private static final long serialVersionUID = 1L;
    private static CoordinatesBean coord = new CoordinatesBean();   
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Fisrt() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		coord.setCoordinates1(request.getParameter("coordinates1"));
		coord.setCoordinates2(request.getParameter("coordinates2"));
		coord.setCoordinates3(request.getParameter("coordinates3"));
		coord.setCoordinates4(request.getParameter("coordinates4"));
		
		String message = "Coordinates are : "+ coord.getCoordinates1()+ " "+
						coord.getCoordinates2()+ " "+ coord.getCoordinates3()+ " "+
						coord.getCoordinates4();
		
		request.setAttribute("test", message);
		// TODO Auto-generated method stub
		this.getServletContext().getRequestDispatcher( "/WEB-INF/first.jsp" ).forward( request, response );
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
