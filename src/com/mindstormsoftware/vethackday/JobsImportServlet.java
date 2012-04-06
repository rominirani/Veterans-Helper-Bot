package com.mindstormsoftware.vethackday;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mindstormsoftware.vethackday.service.MOCService;
import com.mindstormsoftware.vethackday.util.NetworkUtil;

@SuppressWarnings("serial")
public class JobsImportServlet extends HttpServlet {
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		try {
			String strXMLResult = NetworkUtil.fetchJobs("q","l");
			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(new StringReader(strXMLResult));
			Element rootElement = doc.getRootElement();
			List<Element> entryElements = rootElement.getChildren("entry");
			Iterator<Element> it1 = entryElements.iterator();
			while (it1.hasNext()) {
				Element entryElement = (Element)it1.next();
				if (entryElement.getAttributeValue("key").equalsIgnoreCase("jobSearchResult")) {
					List<Element> jobs = entryElement.getChild("jobPostings").getChildren("jobSearchPosting");
					Iterator jobsIt = jobs.iterator();
					while (jobsIt.hasNext()) {
						Element jobElement = (Element)jobsIt.next();
						System.out.println("Snippet : " + jobElement.getChildText("snippet"));
						System.out.println("Title : " + jobElement.getChildText("title"));
						System.out.println("URL : " + jobElement.getChildText("url"));
					}
					break;
				}
			}
			System.out.println(strXMLResult);
		}
		catch (Exception ex) {
			System.out.println(ex.toString());
		}
		
		resp.getWriter().println("Done");
	}
}
