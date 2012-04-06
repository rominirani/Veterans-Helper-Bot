package com.mindstormsoftware.vethackday;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.xmpp.JID;
import com.google.appengine.api.xmpp.Message;
import com.google.appengine.api.xmpp.MessageBuilder;
import com.google.appengine.api.xmpp.SendResponse;
import com.google.appengine.api.xmpp.XMPPService;
import com.google.appengine.api.xmpp.XMPPServiceFactory;
import com.mindstormsoftware.vethackday.entity.Job;
import com.mindstormsoftware.vethackday.entity.MOC;
import com.mindstormsoftware.vethackday.service.MOCService;

/**
 * This is the Chat Interface to the Application. All interactions between the Google Talk User and the application is routed through this.
 * 
 * It is easy to follow the code. 
 * 1. The main message pump is the doGet method below from which we first use some XMPP semantics to retrieve out who is sending us the message
 * and the text of the message.
 * 
 * 2. Once we have the message, we need to interpret it and compare it against the commands that we understand. If we understand the command i.e. help,
 * about, remove then we can process them otherwise we need to send back a message saying that we do not understand the command. 
 * 
 * @author irani_r
 * @version 1.0
 * 
 */


@SuppressWarnings("serial")
public class VeteransHelperBotServlet extends HttpServlet {
	public static final Logger _log = Logger.getLogger(VeteransHelperBotServlet.class.getName());
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String strCallResult="";
		String strStatus="";
		resp.setContentType("text/plain");
		XMPPService xmpp = null;
		JID fromJid = null;
		try {
	
			//STEP 1 - Extract out the message and the Jabber Id of the user sending us the message via the Google Talk client
			xmpp = XMPPServiceFactory.getXMPPService();
			Message msg = xmpp.parseMessage(req);

			fromJid = msg.getFromJid();
			String body = msg.getBody();
			
			//_log.info("Received a message from " + fromJid.getId() + " and body = " + body);
			//String emailId = fromJid.getId().substring(0,fromJid.getId().indexOf("/"));
			//String emailId = fromJid.getId();
			String userId = fromJid.getId();
			//String userId = fromJid.getId();
			//_log.info("Email Id : " + userId);
			
			//String strWord = req.getParameter("command");
			String strCommand = body;
			
			//Do validations here. Only basic ones i.e. cannot be null/empty
			if (strCommand == null) throw new Exception("You must give a command.");
			
			//Trim the stuff
			strCommand = strCommand.trim();
			if (strCommand.length() == 0) throw new Exception("You must give a command.");
			
			/**
			 * STEP 2 : Now that we have something, compare it against the commands that understand and process them accordingly.
			 * 
			 * We currently support only 2 commands that are not single word commands. 
			 * 1. remove [ID] : This removes a particular specified ACTIVE Reminder in the system. ACTIVE Reminders are those reminders that have not yet 
			 * got triggered. The reminder is specified by an ID, which are retrieved by the list command
			 * 2. 
			 */
			
			String[] words = strCommand.split(" ");
			if (words.length >= 2) {
				try {
					if (words.length == 2) {
						String command = words[0];
						String command_data = words[1];
						//Parse the stuff over here
						if (command.equalsIgnoreCase("MOC")) {
							if (command_data != null) {
								command_data = command_data.trim();
							}
							MOC _MOC = MOCService.getInstance().findMOCByCode(command_data);
							//if user present, sent reply about Status
							if (_MOC != null) {
								StringBuffer SB = new StringBuffer();
								SB.append("Branch = " + _MOC.getBranch() + "\r\n");
								SB.append("Title = " + _MOC.getTitle() + "\r\n");
								SB.append("Civilian Equivalent = " + _MOC.getCivilianEquivalent() + "\r\n");
								strCallResult = SB.toString();
							}
							else {
								strCallResult = "Sorry! This does not seem to be a valid MOC record in my system.";
								throw new Exception(strCallResult);
							}
						}
						else if (command.equalsIgnoreCase("Jobs")) {
							String query = "";
							String location = "";
							if (command_data != null) {
								command_data = command_data.trim();
								if (command_data.length() > 0) {
									String[] params = command_data.split(":");
									if (params.length == 2) {
										query = params[0];
										location = params[1];
									}
								}
							}
							if (query.equalsIgnoreCase("") || (location.equalsIgnoreCase(""))) {
								strCallResult = "Sorry! You must specify the query and the location.";
								throw new Exception(strCallResult);
							}
							ArrayList<Job> Jobs = MOCService.getInstance().findJobs(query, location);
							if (Jobs.size() == 0) {
								strCallResult = "Sorry! The Search did not return any results.";
								throw new Exception(strCallResult);
							}
							StringBuffer SB = new StringBuffer();
							SB.append("Job results powered by NRD Veterans Job Search API : https://www.nationalresourcedirectory.gov/home/api/veterans_job_search" + "\r\n");
							SB.append("\r\n");
							SB.append("The following jobs were found" + "\r\n");
							for (Job job : Jobs) {
								SB.append("Job Title : " + job.getTitle() + "\r\n");
								SB.append("Job URL : " + job.getUrl() + "\r\n");
								SB.append("Job Snippet : " + job.getSnippet() + "\r\n");
								SB.append("--------------------" + "\r\n");
							}
							strCallResult = SB.toString();
						}
					}
				}
				catch (Exception ex) {
					strCallResult = ex.getMessage();
				}
			}			
			else if (words.length == 1) {
				if (words[0].equalsIgnoreCase("help")) {
					//Print out help
					//strCallResult = "Help Text Over Here";
					StringBuffer SB = new StringBuffer();
					SB.append("***** Welcome to Veterans Helper Bot *****");
					SB.append("\r\nI understand the following commands:");
					SB.append("\r\n1. Type help to get the list of commands.");
					SB.append("\r\n2. Type helplines to get a list of Help Lines for Veterans");
					SB.append("\r\n3. Type MOC<space>[Military Operation Code] to get more information on the MOC. E.g. MOC 00Z");
					SB.append("\r\n4. Type jobs<space><keywords>:<location> to get the current list of jobs. For e.g. <location> can be a city or state or zipcode. Example of a keyword is managers. For e.g. jobs manager:VA");
					SB.append("\r\n5. Type about to get more information about this Agent.");
					strCallResult = SB.toString();
				}
				else if (words[0].equalsIgnoreCase("about")) {
					strCallResult = "Hello! I am the Veterans Helper Bot version 1.0"+"\r\n"+"Developer: Romin Irani"+"\r\n"+"(http://veteranshelperbot.appspot.com)" + "\r\n" + "Proud to be part of the LinkedIn Veterans HackDay 2011 (http://veterans2011.linkedin.com/)" + "\r\n" + "All Data powered by National Resource Directory APIs (https://www.nationalresourcedirectory.gov/home/api)" + "\r\n"; 
				}
				else if (words[0].equalsIgnoreCase("helplines")) {
					StringBuffer SB = new StringBuffer();
					SB.append("1. Veterans Crisis Line 1.800.273.TALK (8255)" + "\r\n");
					SB.append("http://www.veteranscrisisline.net/" + "\r\n");
					SB.append("2. National Call Center for Homeless Veterans 1.877.4AID.VET (424.3838)" + "\r\n");
					SB.append("http://www1.va.gov/HOMELESS/NationalCallCenter.asp" + "\r\n");
					SB.append("3. VA Caregiver Support Line 1.855.260.3274" + "\r\n");
					SB.append("http://www.caregiver.va.gov/" + "\r\n");
					SB.append("4. Wounded Warrior Resource Center 1.800.342.9647" + "\r\n");
					strCallResult = SB.toString();
				}
				
				else if (words[0].equalsIgnoreCase("jobs")) {
					strCallResult = "You must specify the query and the location. E.g. jobs manager:VA";
				}
			}
			else {
				strCallResult = "Sorry! Could not understand your command.";
			}
			
			//Send out the Response message on the same XMPP channel. This will be delivered to the user via the Google Talk client.
	        Message replyMessage = new MessageBuilder().withRecipientJids(fromJid).withBody(strCallResult).build();
                
	        boolean messageSent = false;
	        //if (xmpp.getPresence(fromJid).isAvailable()) {
	        SendResponse status = xmpp.sendMessage(replyMessage);
	        messageSent = (status.getStatusMap().get(fromJid) == SendResponse.Status.SUCCESS);
	        //}
	        if (messageSent) {
	        	strStatus = "Message has been sent successfully";
	        }
	        else {
	        	strStatus = "Message could not be sent";
	        }
	        _log.info(strStatus);
		}
		catch (Exception ex) {
			
			//If there is an exception then we send back a generic message to the client i.e. MyReminderBot could not understand your command. Please
			//try again. We log the exception internally.
			_log.info("Something went wrong. Please try again!" + ex.getMessage());
	        Message replyMessage = new MessageBuilder()
            .withRecipientJids(fromJid)
            .withBody("Veterans Helper Bot could not understand your command. Please try again.")
            .build();
                
	        boolean messageSent = false;
	        //The condition is commented out so that it can work over non Google Talk XMPP providers also.
	        //if (xmpp.getPresence(fromJid).isAvailable()) {
	        SendResponse status = xmpp.sendMessage(replyMessage);
	        messageSent = (status.getStatusMap().get(fromJid) == SendResponse.Status.SUCCESS);
	        //}
	        if (messageSent) {
	        	strStatus = "Message has been sent successfully";
	        }
	        else {
	        	strStatus = "Message could not be sent";
	        }
	        _log.info(strStatus);
		}
	}
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		 doGet(req, resp);
	}
}
