package com.mindstormsoftware.vethackday.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.mindstormsoftware.vethackday.entity.Job;
import com.mindstormsoftware.vethackday.entity.MOC;
import com.mindstormsoftware.vethackday.util.NetworkUtil;

public class MOCService {
	public static final Logger _logger = Logger.getLogger(MOCService.class.toString());

	private static MOCService _self = null;

	private MOCService() {
	}

	public static MOCService getInstance() {
		if (_self == null) {
			_self = new MOCService();
			ObjectifyService.register(MOC.class);
		}
		return _self;
	}
	
	public ArrayList<Job> findJobs(String query, String location) throws Exception {
		ArrayList<Job> _Jobs = new ArrayList<Job>();
		String strJobsXML = NetworkUtil.fetchJobs(query, location);
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(new StringReader(strJobsXML));
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
					Job _Job = new Job();
					_Job.setSnippet(jobElement.getChildText("snippet"));
					_Job.setTitle(jobElement.getChildText("title"));
					_Job.setUrl(jobElement.getChildText("url"));
					_Jobs.add(_Job);
					//System.out.println("Snippet : " + jobElement.getChildText("snippet"));
					//System.out.println("Title : " + jobElement.getChildText("title"));
					//System.out.println("URL : " + jobElement.getChildText("url"));
				}
				break;
			}
		}
		return _Jobs;
	}
	
	public String addMOC(String mocCode, String mocBranch, String mocTitle, String mocCivilianEquivalent) throws Exception {
		Objectify obj = ObjectifyService.begin();
		MOC _record = new MOC();
		_record.setBranch(mocBranch);
		_record.setCivilianEquivalent(mocCivilianEquivalent);
		_record.setCode(mocCode);
		_record.setTitle(mocTitle);
		obj.put(_record);
		return "success";
	}

	public MOC findMOCByCode(String code) {
		try {
			Objectify obj = ObjectifyService.begin();
			MOC r = obj.query(MOC.class).filter("code",code).get();
			if (r != null)
				return r;
			return null;
		} catch (Exception ex) {
			return null;
		}
	}
	
	public List<MOC> getAllRemindersByBranch(String branch) throws Exception {
		List<MOC> _results = new ArrayList<MOC>();
		Objectify obj = ObjectifyService.begin();
		_results = obj.query(MOC.class).filter("branch",branch).list();
		return _results;
	}	
	
	public List<MOC> getAllMOC() throws Exception {
		List<MOC> _results = new ArrayList<MOC>();
		Objectify obj = ObjectifyService.begin();
		_results = obj.query(MOC.class).list();
		return _results;
	}
	
	private void sendIM(String JabberId, String msg) throws Exception {
		XMPPService xmpp = null;
		JID fromJid = new JID(JabberId);
		xmpp = XMPPServiceFactory.getXMPPService();
		Message replyMessage = new MessageBuilder()
        .withRecipientJids(fromJid)
        .withBody(msg)
        .build();
        boolean messageSent = false;
        //The condition is commented out so that it can work over non Google Talk XMPP providers also.
        //if (xmpp.getPresence(fromJid).isAvailable()) {  
        SendResponse status = xmpp.sendMessage(replyMessage);
        messageSent = (status.getStatusMap().get(fromJid) == SendResponse.Status.SUCCESS);
        //}
        if (messageSent) {
        	_logger.info("Message has been sent successfully");
        }
        else {
        	_logger.info("Message could not be sent");
        }
	}
}
