package com.mindstormsoftware.vethackday.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class NetworkUtil {
	public static String makeNetworkCall(String networkURL) throws Exception {
		//String strWordNikService = "https://nrdbeta-static.devis.com/equivalents.json";
		URL url = new URL(networkURL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer response = new StringBuffer();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String strCallResult = response.toString();
        return strCallResult;

	}

	public static String fetchMOCFeed() throws Exception {
		//String strMOCFeedURL = "https://nrdbeta-static.devis.com/equivalents.json";
		String strMOCFeedURL = "http://veteranshelperbot.appspot.com/moc.json";
		URL url = new URL(strMOCFeedURL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer response = new StringBuffer();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String strCallResult = response.toString();
        return strCallResult;

	}
	
	public static String fetchJobs(String query, String location) throws Exception {
		String strJobsFeedURL = "https://beta.nrd.devis.com/jobSearch/raw/jobSearch?q="+query + "&location=" + location + "&includeNearbyCities=on&datePosted=60&sort=jobposting-dateposted&order=desc";
		URL url = new URL(strJobsFeedURL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuffer response = new StringBuffer();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String strCallResult = response.toString();
        return strCallResult;

	}
}
