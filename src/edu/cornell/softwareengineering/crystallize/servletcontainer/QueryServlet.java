package edu.cornell.softwareengineering.crystallize.servletcontainer;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.cornell.softwareengineering.crystallize.util.Query;
import edu.cornell.softwareengineering.crystallize.util.common.ParameterParser;

/**
 * Servlet implementation class Query
 */
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public QueryServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		PrintWriter out = response.getWriter();
		JSONObject parameters;
		try {
			parameters = ParameterParser.getParameterObject(request);
			out.append(parameters.toString() + "\n");

			JSONObject refinedParams = refineParameters(parameters);
			out.append(refinedParams.toString() + "\n");
			
			String result = Query.query(refinedParams);	
			out.append(result);
			
		} catch (Exception e) {
			out.append(e.getMessage());
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}


	private JSONObject refineParameters(JSONObject parameters) throws Exception {
		JSONObject refined = new JSONObject();
		if(parameters.length() == 0) throw new Exception("No parameters found");
		
		// check table parameter
		if(parameters.has("table")) {
			JSONArray tableArray = parameters.getJSONArray("table");
			if(tableArray.length() == 1) {
				refined.put("table", tableArray.get(0));
			}
			else if(tableArray.length() > 1) { throw new Exception("Parameters 'table' has more than one value"); }
			else { throw new Exception("Parameters 'table' is empty"); }
		}
		else { throw new Exception("Parameter 'table' missing"); }
		
		// check query parameter
		if(parameters.has("query")) {
			JSONArray queryArray = parameters.getJSONArray("query");
			if(queryArray.length() >= 1) {
				refined.put("query", queryArray);
			}
			else { throw new Exception("Parameter 'query' is empty"); }		}
		else { throw new Exception("Parameter 'query' missing"); }
		
		// check filters parameter
		if(parameters.has("filters")) {
			JSONArray filterArray = parameters.getJSONArray("filters");
			if(filterArray.length() >= 1) {
				refined.put("filters", filterArray);
			}
			else { refined.put("filters", new JSONArray()); }
		}
		else { refined.put("filters", new JSONArray()); }
		
		return refined;
	}
}
