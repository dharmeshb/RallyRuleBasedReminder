package com.dharmeshborad.rallyreminder.core.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;

import com.dharmeshborad.rallyreminder.core.Developer;
import com.dharmeshborad.rallyreminder.core.Product;
import com.dharmeshborad.rallyreminder.core.ReleaseOrProject;

public class RestCaller {
	
	private Executor executor;
	
	public String doAuth(String username, String password, HashMap<String, String> header) throws Exception{
		String URL = "https://rally1.rallydev.com/slm/webservice/v2.0/security/authorize";
//		header.put("Authorization", "Basic:ZGJvcmFkQHBodGNvcnAuY29tOnZpc2hhbF9t"); //Encrypted value of my Rally account
		return MyJSONParser.getTheAuthToken(callARestAuth_Get(username, password, URL, header, null));
	}
	
	public Map<Developer,List<RallyDefect>> devIssues(List<Developer> devs) throws Exception{
		Map<Developer,List<RallyDefect>> result = new HashMap<>();
		for(Developer dev:devs){
			String url="https://rally1.rallydev.com/slm/webservice/v2.0/defect"
					+ "?query="
					 + "((((((State = Closed) OR (state = FixVerified)) OR (State = FixMade)) AND (Owner = "+dev.getName()+")) "
					 + "AND (Release = https://rally1.rallydev.com/slm/webservice/v2.0/release/21624332824)) " 
					 + "AND (((Resolution = None) OR (c_ResolutionDescription = null)) OR (FixedInBuild = null)) )&pagesize=500";
//			System.out.println(url);
			url=url.replaceAll(" ", "%20");
//			url=url.replaceAll("\"", "%22");
//			System.out.println(url);
//			String url="https://rally1.rallydev.com/slm/webservice/v2.0/defect?tpsV="+dev.getRallyView();
			
			List<RallyDefect> lst = MyJSONParser.jsonToListForDefect(callARestGet(url), dev);
			List<RallyDefect> newlst = new ArrayList<>(); 
			for(RallyDefect rd : lst){
				RallyDefect temp = MyJSONParser.grabADefect(callARestGet(rd.getURL()),rd);
				temp = MyJSONParser.grabADefectTask(callARestGet(rd.getTaskURL()), temp);
				newlst.add(temp);
			}
			result.put(dev, newlst);
		}
		return result;
	}
	
	public Map<ReleaseOrProject,List<RallyDefect>> getAllDefects(Product product, String type) throws Exception{
		Map<ReleaseOrProject,List<RallyDefect>> result = new HashMap<>();
		
		for(ReleaseOrProject release : product.getReleasesOrProjects()){
			
			String url="https://rally1.rallydev.com/slm/webservice/v2.0/defect"
					+ "?query=("+type+" = "+release.getRefUrl()+")&pagesize=200"; 
			url=url.replaceAll(" ", "%20");
			url=url.replaceAll("\"", "%22");
			
			List<RallyDefect> lstMain = new ArrayList<>();
			int count = MyJSONParser.getCount(callARestGet(url+"&start=1"));
			for(int i=0;i<=(count+100);){
				List<RallyDefect> lst = MyJSONParser.jsonToListForDefect(callARestGet(url+"&start="+i));
				
				List<RallyDefect> newlst = new ArrayList<>(); 
				for(RallyDefect rd : lst){
					RallyDefect temp = MyJSONParser.grabADefect(callARestGet(rd.getURL()),rd);
					temp = MyJSONParser.grabADefectTask(callARestGet(rd.getTaskURL()), temp);
					newlst.add(temp);
				}
				
				for(RallyDefect r:newlst){
					if(!lstMain.contains(r)){
						lstMain.add(r);
					}
				}
				
				i=i+100;
			}
			
			result.put(release, lstMain);
		}
		return result;
	}
	
	public Map<ReleaseOrProject,List<RallyDefect>> defectIssues(Product product, String type) throws Exception{
		Map<ReleaseOrProject,List<RallyDefect>> result = new HashMap<>();
//		https://rally1.rallydev.com/slm/webservice/v2.0/release/21624332824
		for(ReleaseOrProject release : product.getReleasesOrProjects()){
			String url="https://rally1.rallydev.com/slm/webservice/v2.0/defect"
					+ "?query=("+type+" = "+release.getRefUrl()+")&pagesize=500"; 
			url=url.replaceAll(" ", "%20");
			url=url.replaceAll("\"", "%22");
			
			List<RallyDefect> lst = MyJSONParser.jsonToListForDefect(callARestGet(url));
			List<RallyDefect> newlst = new ArrayList<>(); 
			for(RallyDefect rd : lst){
				RallyDefect temp = MyJSONParser.grabADefect(callARestGet(rd.getURL()),rd);
				temp = MyJSONParser.grabADefectTask(callARestGet(rd.getTaskURL()), temp);
				newlst.add(temp);
			}
			result.put(release, newlst);
		}
		return result;
	}
	
	public Map<ReleaseOrProject,List<RallyDefect>> defectIssues(Product product) throws Exception{
		return defectIssues(product, "Release");
	}
	
	
	public List<ReleaseOrProject> getAllReleases(String url) throws Exception{
		
		int count = MyJSONParser.getCount(callARestGet(url+"&start=1"));
		List<ReleaseOrProject> lst = new ArrayList<>();
		int c = count>99? count+100 : count;
		for(int i=0;i<=c;){
			List<ReleaseOrProject> lstL = MyJSONParser.jsonToListForRelease(callARestGet(url+"&start="+i));
			for(ReleaseOrProject r:lstL){
				if(!lst.contains(r)){
					lst.add(r);
				}
			}
			i=i+100;
		}
		return lst;
	}
	
	public List<ReleaseOrProject> getChildReleases(String url) throws Exception{
		
		url=url+"/Children";
		int count = MyJSONParser.getCount(callARestGet(url+"?pagesize=1&start=1"));
		List<ReleaseOrProject> lst = new ArrayList<>();
		int c = count>99? count+100 : count; 
		for(int i=0;i<=c;){
			List<ReleaseOrProject> lstL = MyJSONParser.jsonToListForRelease(callARestGet(url+"?pagesize=100&start="+i));
			for(ReleaseOrProject r:lstL){
				if(!lst.contains(r)){
					lst.add(r);
				}
			}
			i=i+100;
		}
		return lst;
	}
	
	public String callARestGet(String url) throws Exception{
//		System.out.println(url);
        // Execute a GET with timeout settings and return response content as String.
        return executor.execute(Request.Get(url)
//        		.bodyForm(Form.form().add("query", "(((State = Closed) OR (state = FixVerified)) OR (State = FixMade))").build())
                .connectTimeout(10*60*1000)
                .socketTimeout(10*60*1000)
                ).returnContent().asString();
        
	}
	
	private String callARestAuth_Get(String username, String password, String url, HashMap<String, String> header, String body) throws Exception{
	
		executor = Executor.newInstance()
                .auth(new HttpHost("rally1.rallydev.com"), username, password);

        // Execute a GET with timeout settings and return response content as String.
        return executor.execute(Request.Get(url)
                .connectTimeout(10*60*1000)
                .socketTimeout(10*60*1000)
                ).returnContent().asString();
        
	}
}
