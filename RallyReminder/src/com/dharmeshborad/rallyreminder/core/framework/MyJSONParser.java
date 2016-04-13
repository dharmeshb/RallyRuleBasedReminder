package com.dharmeshborad.rallyreminder.core.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.dharmeshborad.rallyreminder.core.Developer;
import com.dharmeshborad.rallyreminder.core.ReleaseOrProject;

public class MyJSONParser {

	public static String getTheAuthToken(String json) throws Exception{
//		System.out.println(json);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json);

		JSONObject jsonObject = (JSONObject) obj;

		JSONObject operationResultJS = (JSONObject) jsonObject.get("OperationResult");

		return (String) operationResultJS.get("SecurityToken");
	}
	
	public static List<RallyDefect> jsonToListForDefect(String json, Developer dev) throws Exception{
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json);

		JSONObject jsonObject = (JSONObject) obj;

		JSONObject operationResultJS = (JSONObject) jsonObject.get("QueryResult");

//		Long resultL = (Long) operationResultJS.get("TotalResultCount");
		JSONArray result = (JSONArray) operationResultJS.get("Results");

		List<RallyDefect> lst = new ArrayList<>(); 
		for(int i=0;i<result.size();i++){
			JSONObject d = (JSONObject) result.get(i);
			Map<String, String> data = new HashMap<>();
			data.put("URL",(String) d.get("_ref"));
			data.put("NAME",(String) d.get("_refObjectName"));
			data.put("ID",(String) d.get("FormattedID"));
			data.put("OWNER",(String) d.get("Owner")); //URL
			data.put("STATE",(String) d.get("State"));
			if(dev==null){
				lst.add(new RallyDefect(data));
			}
			else{
				lst.add(new RallyDefect(dev,  data));
			}
		}
		return lst;
	}
	
	public static List<RallyDefect> jsonToListForDefect(String json) throws Exception{
		return jsonToListForDefect(json, null);
	}
	
	public static List<ReleaseOrProject> jsonToListForRelease(String json) throws Exception{
		List<ReleaseOrProject> lst = new ArrayList<>();
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject)parser.parse(json);
		
		JSONObject operationResultJS = (JSONObject)jsonObject.get("QueryResult");

		JSONArray result = (JSONArray) operationResultJS.get("Results");
		for(int i=0;i<result.size();i++){
			JSONObject d = (JSONObject) result.get(i);
			ReleaseOrProject r = new ReleaseOrProject();
			r.setRefUrl(d.get("_ref")+"");
			r.setReleaseOrProjectName(d.get("_refObjectName")+"");
			lst.add(r);
		}
		return lst;
	}
	
	public static int getCount(String json) throws Exception{
		JSONParser parser = new JSONParser();
		JSONObject jsonObject = (JSONObject)parser.parse(json);
		JSONObject operationResultJS = (JSONObject)jsonObject.get("QueryResult");
		return Integer.parseInt(""+operationResultJS.get("TotalResultCount")) ;
	}
	
	private static void prepareDefect(RallyDefect rd, JSONObject defect) throws Exception{
		rd.setID((String) defect.get("FormattedID"));
		rd.setTragetBuild((String) defect.get("TargetBuild"));
		rd.setFixedInBuild((String) defect.get("FixedInBuild"));
		rd.setWhatChanged((String) defect.get("Resolution"));
		rd.setResDesc((String) defect.get("c_ResolutionDescription"));
		rd.setTaskURL((String)((JSONObject) defect.get("Tasks")).get("_ref"));
		rd.setID((String) defect.get("FormattedID"));
		JSONObject ownerO = (JSONObject)defect.get("Owner");
		if(ownerO!=null)
			rd.setOwner((String) ownerO.get("_refObjectName"));
		rd.setState((String) defect.get("State"));
		rd.setScheduled((String) defect.get("ScheduleState"));
		rd.setVerifiedBuild((String) defect.get("VerifiedInBuild"));
		rd.setBlocked(defect.get("Blocked")+"");
		rd.setBlockedReason((String) defect.get("BlockedReason"));
		rd.setPlanEstimate(defect.get("PlanEstimate")+"");
		rd.setScriptReference(defect.get("c_ScriptReference")+"");
		rd.setFoundInIteration(defect.get("c_FoundInIteration")+"");
		rd.setFoundInRelease(defect.get("c_FoundInRelease")+"");
		
		JSONObject submittedBy = (JSONObject)defect.get("SubmittedBy");
		if(submittedBy!=null)
			rd.setSubmittedBy((String) submittedBy.get("_refObjectName"));
		
		rd.setFoundInIteration(defect.get("c_FoundInIteration")+"");
		rd.setAssignedSQE(defect.get("c_AssignedSQE")+"");
		rd.setSeverity(defect.get("Severity")+"");
	}
	
	
	public static RallyDefect grabADefect(String json, RallyDefect rd) throws Exception{
//		System.out.println(json);
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json);

		JSONObject jsonObject = (JSONObject) obj;
		JSONObject defect = (JSONObject) jsonObject.get("Defect");
		prepareDefect(rd, defect);
		return rd;
	}
	
	public static RallyDefect grabADefectTask(String json, RallyDefect rd) throws Exception{
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(json);

		JSONObject jsonObject = (JSONObject) obj;
		JSONObject defect = (JSONObject) jsonObject.get("QueryResult");
		long result = (Long) defect.get("TotalResultCount");
		if(result==0){
			rd.setuTaskIndicator(1);
		}
		else if(result>0){
			
			JSONArray results = (JSONArray) defect.get("Results");

			List<RallyDefect> lst = new ArrayList<>(); 
			boolean foundUnitTest = false;
			for(int i=0;i<results.size();i++){
				JSONObject d = (JSONObject) results.get(i);
				String nameOfTask = (String) d.get("_refObjectName");
				String description = (String) d.get("Description");
				String taskUrl = (String) d.get("_ref");
				if(nameOfTask.toLowerCase().contains("unit test")){
					foundUnitTest = true;
					if(description==null || description.trim().isEmpty()){
						rd.setTaskURL("https://rally1.rallydev.com/#/2540974475ud/detail/task"+taskUrl.substring(taskUrl.lastIndexOf("/")));
						rd.setuTaskIndicator(2);
					}
					break;
				}
			}
			
			if(foundUnitTest==false){
				rd.setuTaskIndicator(1);	
			}
		}
		return rd;
	}
}
