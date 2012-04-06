package com.mindstormsoftware.vethackday;

import java.io.IOException;
import javax.servlet.http.*;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mindstormsoftware.vethackday.service.MOCService;
import com.mindstormsoftware.vethackday.util.NetworkUtil;

@SuppressWarnings("serial")
public class MOCImportServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		try {
			String JSONResult = NetworkUtil.fetchMOCFeed();
			//JSONObject JO = new JSONObject(JSONResult);
			JSONArray MOCCodes = new JSONArray(JSONResult);
			for (int i=0;i<MOCCodes.length();i++) {
				String mocCode = ((JSONObject)MOCCodes.get(i)).getString("MOC");
				JSONArray CE = ((JSONObject)MOCCodes.get(i)).getJSONArray("civilianEquivalent");
				String mocCivilianEquivalent = CE.toString();
				String mocBranch = ((JSONObject)MOCCodes.get(i)).getString("branch");
				String mocTitle = ((JSONObject)MOCCodes.get(i)).getString("title");
				MOCService.getInstance().addMOC(mocCode, mocBranch, mocTitle, mocCivilianEquivalent);
				System.out.print("Inserted MOC #" + (i+1));
			}
			//System.out.println(MOCCodes.length());
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}
		
		resp.getWriter().println("Done");
	}
}
