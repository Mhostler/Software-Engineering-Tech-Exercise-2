import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import javax.sql.DataSource;
import com.mysql.cj.jdbc.MysqlDataSource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Enumeration;
import java.util.Calendar;

public class Event {

	public static String displayEvents(HttpServletResponse response) {
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		PrintWriter out;
		
		try {
			conn = getMysqlDataSource().getConnection();
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select * from event;");
			
			out = response.getWriter();	
			
			String output = "";
			
			SimpleDateFormat dFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm");
			
			while (rs.next()) {
				output += "<div>";
				output += "EventName: " + rs.getString(2) + "<br>";
				output += "Street: " + rs.getString(3) + "<br>";
				output += "City: " + rs.getString(4) + "<br>";
				output += "State: " + rs.getString(5) + "<br>";
				output += "Zip: " + Integer.toString(rs.getInt(6)) + "<br>";
				
				java.sql.Timestamp eTime = rs.getTimestamp(7, Calendar.getInstance());
				
				output += "Date: " + dFormat.format(eTime.getTime()) + "<br>";
				output += "<form action=\"DelEvent\" method=\"post\"><input type=\"submit\" name=\"" 
						+ Integer.toString(rs.getInt(1)) + "\" value=\"Remove\"></form>";
				output += "</div><br>";
			}
			
			out.print(output);
			return output;
			
		} catch (SQLException e) { e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (rs != null) {
					rs.close();
				}
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return "Error";		
	}//end displayEvents
	
	public static void insertEvent(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement prestmt = null;
		
		try {
			
			conn = getMysqlDataSource().getConnection();
			prestmt = conn.prepareStatement("INSERT INTO event (name, street, city, state, zip, eventdate) VALUES (?,?,?,?,?,?);");
			
			prestmt.setString(1, request.getParameter("eName"));
			prestmt.setString(2, request.getParameter("sAddr"));
			prestmt.setString(3, request.getParameter("city"));
			prestmt.setString(4, request.getParameter("state"));
			prestmt.setInt(5, Integer.parseInt(request.getParameter("zip")));

			SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-MM-dd");
			java.sql.Date eDate = new java.sql.Date(System.currentTimeMillis());
			eDate.setTime(dFormat.parse(request.getParameter("edate")).getTime() );
			
			SimpleDateFormat tFormat = new SimpleDateFormat("HH:mm");
			java.sql.Time eTime = new java.sql.Time(System.currentTimeMillis());
			eTime.setTime(tFormat.parse(request.getParameter("etime")).getTime());
			
			java.sql.Timestamp edatetime = new java.sql.Timestamp(eDate.getTime() + eTime.getTime());
			
			prestmt.setTimestamp(6, edatetime);
			
			prestmt.executeUpdate();
			
			conn.commit();
		} catch (SQLException e) { e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} finally {
			try {
				if (prestmt != null) {
					prestmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}	
	}//end insertEvent
	
	public static void removeEvent(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		PreparedStatement stmt = null;
		
		try {
			conn = getMysqlDataSource().getConnection();
			stmt = conn.prepareStatement("DELETE FROM event WHERE ID=?;");
			
			Enumeration<String> parameterNames = request.getParameterNames();
			
			stmt.setInt(1, Integer.parseInt(parameterNames.nextElement()));
			stmt.executeUpdate();
			
		} catch (SQLException e) { e.printStackTrace();
		} finally {
			try {
				if (stmt != null) {
					stmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}//end removeEvent
	
	private static DataSource getMysqlDataSource() {
		MysqlDataSource dataSource = new MysqlDataSource();
		
		dataSource.setServerName("localhost");
		dataSource.setPortNumber(3306);
		dataSource.setDatabaseName("EventPlanner");
		dataSource.setUser("softeng");
		dataSource.setPassword("Penp2llio");
		return dataSource;
	}//end getMysqlDataSource
	
}
