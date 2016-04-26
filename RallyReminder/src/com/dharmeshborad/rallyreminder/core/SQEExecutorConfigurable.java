package com.dharmeshborad.rallyreminder.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import com.dharmeshborad.rallyreminder.core.framework.Input;
import com.dharmeshborad.rallyreminder.core.framework.RallyDefect;
import com.dharmeshborad.rallyreminder.core.framework.RestCaller;
import com.dharmeshborad.rallyreminder.core.framework.RuleBean;

public class SQEExecutorConfigurable {

	private static EmailSender not = new EmailSender();
	private static String admin = "abc@xyz.com";//Put your admin email here.
	private static String rallyUsd="";
	private static String rallyPsw="";
	private static String fileName="rally.rules";
	
	private static boolean printInProcess=false;

	private static long lastModified=-1l;
	private static List<Input> ruleListG = new ArrayList();
	private static Map<String, Product> mapOfRelease=new HashMap();
	
	private static List<Input> parseAllRules() throws Exception{
		File f = new File(SQEExecutorConfigurable.class.getClassLoader().getResource(fileName).getPath());
		if(f.lastModified()!=lastModified && lastModified != -1l){
			System.out.println("Looks like rule file is changed after last run. Going to parse it again.\n");
			mapOfRelease=new HashMap();
		}
		else if(lastModified != -1l){
			System.out.println("No rule file change since last run.\n");
			return ruleListG;
		}
		lastModified=f.lastModified();
		
		FileReader fr = new FileReader(f);
		BufferedReader br = new BufferedReader(fr);
		String readLine = br.readLine();
		boolean firstLine=true;
		Input res = new Input();
		List<Input> ruleList = new ArrayList();
		
		while(readLine!=null){
			System.out.println(readLine);
			
			if(readLine.isEmpty() || readLine.startsWith("//")){
				readLine = br.readLine();
				continue;
			}
			
			if(readLine.startsWith("----")){
				firstLine=true;
				ruleList.add(res);
				res = new Input();
				readLine = br.readLine();
				continue;
			}
			
			if(firstLine){
				String firstline[] = readLine.split("-");
				res.setReleasesOrProjects(firstline[0].split(","));
				res.setReleaseOrProjectString(firstline[0]);
				
				res.setEmaild(firstline[1]);
				res.setFetchReleases(firstline.length>2 && firstline[2].startsWith("0")?false:true); //Default true-1 if not defined, means fetch releases
				res.setFetchChild(firstline.length>2 && firstline[2].equalsIgnoreCase("0C")?true:false); //Default false if 0C - fetch all childs of this project as well
				res.setShowNewDefectSection(firstline.length>3 && firstline[3].equalsIgnoreCase("1")?true:false);
				res.setShowBlockedDefectSection(firstline.length>4 && firstline[4].equalsIgnoreCase("1")?true:false);
				res.setGroupBy(firstline.length>5?firstline[5]:null);
				System.out.println("Info>>"+(res.isFetchReleases()?"Fetch objects through releases is turned ON.":
															"Fetch objects through projects is turned ON"
															+(res.isFetchChild()?" including children projects":"")
															+ "."));
				System.out.println("Info>>Show new defect section is turned "+(res.isShowNewDefectSection()?"ON.":"OFF."));
				System.out.println("Info>>Show blocked defect section is turned "+(res.isShowBlockedDefectSection()?"ON.":"OFF."));
				System.out.println();
				firstLine=false;
				readLine = br.readLine();
				if(readLine.contains("[")){
					res.setCustomMessage(readLine.substring(1, readLine.indexOf("]")-1));
					readLine = br.readLine();
				}
				continue;
			}
			
			String[] firstSplit = readLine.split(",",2);
			
			RuleBean rb = new RuleBean(firstSplit[0], firstSplit[1]);
			res.add(rb);
			
			readLine = br.readLine(); 
		}
		
		br.close();
		
		ruleListG = ruleList;
		return ruleListG;
	}
	
	/**
	 * Just to print ..... in command line where there is some process going on.
	 * Whole logic is driven through printInProcess boolean variable.
	 */
	public static void startAInProgressThread(){
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				int i=0;
				while(true){
					if(printInProcess){
						System.out.print(".");
					}
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(i++ > 100){
						System.out.println();
						i=0;
					}
				}
			}
		});
		
		t.start();
	}
	
	public static void main1(String[] args) throws Exception {
		checkWhenToStart("11pM");
	}
	
	public static int checkWhenToStart(String time) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("hha");
		Date now = new Date();
		SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss");
		now = sdf2.parse(sdf2.format(now));
		int diff = (int) (sdf.parse(time).getTime()/(1000*60) - now.getTime()/(1000*60));
		if(diff<0){
			diff = (24*60) + diff;
		}
		System.out.println(diff+" minutes to start.");
		return diff;
	}
	
	public static void main(String[] args) throws Exception {
		startAInProgressThread();
		
		SimpleDateFormat sdf = new SimpleDateFormat("EEE");
		SimpleDateFormat sdfLog = new SimpleDateFormat("dd/MMM/YYYY hh:mm:ss");
		int indicator = Integer.parseInt(args[0]);
		rallyUsd=args[1];
		rallyPsw=args[2];
		String thirdArg = args[3];
		int hours=24;
		if(thirdArg.toLowerCase().contains("m")){//If AM/PM is used instead interval in batch file (main parameters)
			int firstSleepInMins=checkWhenToStart(thirdArg);
			System.out.println("First execution is scheduled for "+thirdArg+" and it will run every "+hours+" hours after it.\nMake sure rule file is valid and all rules are tested prior without AM/PM option.");
			Thread.sleep(1000 * 60 * firstSleepInMins);
		}
		else{
			hours=Integer.parseInt(args[3]);
		}
		if(args.length>4)
			fileName=args[4];
		
		while(true){
			try{
				String day = sdf.format(new Date());
				if(!day.equals("Sat") && !day.equals("Sun") ){
					loop(indicator);
					System.out.println();
				}
				if(indicator==0){
					printInProcess=false;
					break;
				}
			}
			catch(RuntimeException e){
				e.printStackTrace();
			}
			catch(Exception e){
				e.printStackTrace();
				not.sendMail("ERROR",e.getMessage(), admin);
			}
			printInProcess=false;
			System.out.println("Execution time:"+sdfLog.format(new Date()));
			System.out.println("Next will be in "+hours+" hours.\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>\n");
			Thread.sleep(1000 * 60 * 60 * hours);
		}
		
	}
	
	public static void loop(int indicator) throws Exception{
		List<Input> ruleListInput = parseAllRules();
		printInProcess=true;
		
		RestCaller rest = new RestCaller();
		rest.doAuth(rallyUsd, rallyPsw, new HashMap<String, String>());
		
		for(Input input : ruleListInput){//Per rule group this will rotate
			
			Product product = mapOfRelease.get(input.getReleaseOrProjectString());
			if(product == null){
				product = new Product();
				
				String releaseOrProject = input.isFetchReleases()?"releases":"projects";
				String urlMain = "https://rally1.rallydev.com/slm/webservice/v2.0";
				String urlToGetAllRelease = urlMain+"/"+releaseOrProject+"?fetch&pagesize=200";
				
				product.setReleasesOrProjects(rest.getAllReleases(urlToGetAllRelease));
				List<ReleaseOrProject> releases = new ArrayList<>();
				
				List<String> releasesName = new ArrayList<>();
				for(ReleaseOrProject rel:product.getReleasesOrProjects()){
					second: for(String relTofind : input.getReleasesOrProjects()){
						if(rel.getReleaseOrProjectName().equalsIgnoreCase(relTofind)){
							releases.add(rel); // Rally returns multiple release for same name but with different refURL
							if(input.isFetchChild())//Only fetch child if it is true.
								findChildOfRelease(rel, releases, rest);
							if(!releasesName.contains(relTofind)) 
								releasesName.add(relTofind);
							break second;
						}
					}
				}
				
				if(releases.size()<=0)
					throw new RuntimeException("Couldnt find release or product "+input.getReleaseOrProjectString());
				if(releasesName.size() != input.getReleasesOrProjects().length)
					System.out.println(">>>>>NOT ALL RELEASES ARE FOUND "+input.getReleaseOrProjectString()+"<<<<<");

				product.setReleasesOrProjects(releases);
				mapOfRelease.put(input.getReleaseOrProjectString(),product);
			}
			
			Map<ReleaseOrProject,List<RallyDefect>> resultMap = rest.getAllDefects(product, input.isFetchReleases()?"Release":"Project");
			sendEmailOut(resultMap, input);
		}
	}
	
	private static void findChildOfRelease(ReleaseOrProject rel,List<ReleaseOrProject> releases, RestCaller rest) throws Exception {

		List<ReleaseOrProject> childOfRelease = rest.getChildReleases(rel.getRefUrl());
		
		for(ReleaseOrProject r:childOfRelease){
			if(!releases.contains(r)){
				releases.add(r);
			}
		}
	}
	
	private static void sendEmailOut(Map<ReleaseOrProject,List<RallyDefect>> resultMap, Input input){
		
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm");
		Set<ReleaseOrProject> keys = resultMap.keySet();
		Iterator<ReleaseOrProject> it = keys.iterator();
		List<String> sbS1Lst = new ArrayList<>();
		SortedMap<String, StringBuilder> groupByMain = new TreeMap<>();
		StringBuilder sbMainList = new StringBuilder("RallyProgram Rule Results:<br/><br/>");
		
		if(input.getCustomMessage()!=null && !input.getCustomMessage().isEmpty()){
			sbMainList.append("\"");
			sbMainList.append(input.getCustomMessage());
			sbMainList.append("\"");
			sbMainList.append("<br/><br/>");
		}
		
		List<String> sbS2Lst = new ArrayList<>();
		StringBuilder sbS2NewDefects = new StringBuilder();
		if(input.isShowNewDefectSection()){
			sbS2NewDefects = new StringBuilder("<br/>----------<br/>Defects in the notifications category New Defects were found:<br/><br/>");
		}
		
		List<String> sbS3Lst = new ArrayList<>();
		StringBuilder sbS3BlockedDefects = new StringBuilder();
		if(input.isShowBlockedDefectSection()){
			sbS3BlockedDefects = new StringBuilder("<br/>----------<br/>Defects in the notifications category Blocked Defects were found:<br/><br/>");
		}
		
		StringBuilder subjectRelease = new StringBuilder();
		List<String> subjectReleaseLst = new ArrayList<>();
		
		Map<String, String> grouping = new HashMap<String, String>();
		
		while(it.hasNext()){
			ReleaseOrProject release = it.next();
			List<RallyDefect> rdL = resultMap.get(release);
			if(!subjectReleaseLst.contains(release.getReleaseOrProjectName())){
				subjectRelease.append(release.getReleaseOrProjectName()+" ");
				subjectReleaseLst.add(release.getReleaseOrProjectName());
			}
			
			for(RallyDefect rDefect : rdL){

				if(!sbS1Lst.contains(rDefect.getID())){ //Rally does return multiple releases URL but with same release name, this may lead to get same Defect again.
					
					String[] data = rDefect.getMissingInfoForSQEReport(input.getLst());
					String dataL = data[0]+data[1];
					if(!dataL.isEmpty()){//Nothing to report
						
						if(input.getGroupBy()!=null){
							StringBuilder localSB = new StringBuilder();
							boolean firstMessage = true;
							String valueOfGroupByField = rDefect.getGroupByKey(input.getGroupBy());
							if(groupByMain.containsKey(valueOfGroupByField)){
								localSB = groupByMain.get(valueOfGroupByField);
								firstMessage = false;
							}
							if(firstMessage){
								localSB.append("<b><font color=\"#00AAFF\">"+valueOfGroupByField+"</font></b><br/>");
							}
							localSB.append(dataL);
							groupByMain.put(valueOfGroupByField, localSB);
						}
						else{
							sbMainList.append(dataL);
						}
					}
				}
				sbS1Lst.add(rDefect.getID());
				
			}
			
			if(input.isShowNewDefectSection()){
				for(RallyDefect rDefect : rdL){
					if(!sbS2Lst.contains(rDefect.getID()))  //Rally does return multiple releases URL but with same release name, this may lead to get same Defect again.
						sbS2NewDefects.append(rDefect.getOpenNewDefectReport());	
					sbS2Lst.add(rDefect.getID());
				}
			}
			
			if(input.isShowBlockedDefectSection()){
				for(RallyDefect rDefect : rdL){
					if(!sbS3Lst.contains(rDefect.getID()))  //Rally does return multiple releases URL but with same release name, this may lead to get same Defect again.
						sbS3BlockedDefects.append(rDefect.getBlockedDefectsReport());
					sbS3Lst.add(rDefect.getID());
				}
			}
		}
		
		//Group by is applied for main list. i.e. sbS1
		if(input.getGroupBy()!=null){
			Set<String> keysSet = groupByMain.keySet();
			Iterator<String> i = keysSet.iterator();
			while(i.hasNext()){
				StringBuilder sb = groupByMain.get(i.next());
				sbMainList.append(sb.toString()+"<br/>");
			}
		}
		
		not.sendMail(subjectRelease.toString()+": "+sdf.format(new Date()), sbMainList.toString()+sbS2NewDefects.toString()+sbS3BlockedDefects.toString(), input.getEmaild());
	}
	
}

