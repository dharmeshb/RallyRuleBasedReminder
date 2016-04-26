package com.dharmeshborad.rallyreminder.core.framework;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.dharmeshborad.rallyreminder.core.Developer;

public class RallyDefect extends RallyObject {
	
	private String name;
	
	private String resDesc;
	private String targetBuild;
	private String fixedInBuild;
	private String verifiedBuild;
	private String whatChanged;
	private String scheduled;
	private String URL;
	private String ID;
	private String webURL;
	private String tasksURL;
	private String taskURL;
	private String state;
	private int uTaskIndicator=0; //0=allgood, 1=missing, 2=unittest task description is missing
	private String blocked;
	private String blockedReason;
	private String planEstimate;
	private String scriptReference;
	private String foundInIteration;
	private String foundInRelease;
	
	private String owner;
	private String submittedBy;
	private String assignedSQE;
	private String severity;
	private String iteration;
	private String release;
	
	
	public String getGroupByKey(String groupByKeyField){
		String val = getValueOfFieldStr(groupByKeyField);
		val = (val==null || val.isEmpty())?"No "+groupByKeyField:val;
		return val;
	}
	
	public RallyDefect(Map<String, String> data) {
		this.owner = data.get("OWNER");
		this.URL = data.get("URL");
		this.name = data.get("NAME");
		this.ID = data.get("ID");
		this.state = data.get("STATE");
		this.webURL = "https://rally1.rallydev.com/#/2540974475ud/detail/defect"+this.URL.substring(this.URL.lastIndexOf("/"));
	}
	
	public String getMissingInfoForSQEReport(){
		
		String validation = (notOwnerSet()?"Owner is missing<br/>":"")
				+(isBuildMissingForFixedMade(targetBuild)?"Target In Build is missing<br/>":"")
				+(isBuildMissingForFixedMade(verifiedBuild)?"Verified In Build is missing<br/>":"")
				+(notScheduleCompletedWhenFixedMade()?"Schedule should be Completed when fix made<br/>":"")
				+(notScheduleDoneWhenFixedVerified()?"Schedule should be Done when fix verified<br/>":"")
				+(isResOrResDesMissingForFixedMade()?"Resolution or Resolution Description is missing<br/>":"")
				+(isBlockedAndReasonMissing()?"Blocked Reason is missing<br/>":"")
				+(isEmpty(planEstimate)?"Plan Estimate is missing<br/>":"")
				+(isEmpty(scriptReference)?"Script Reference is missing<br/>":"")
				+(isEmpty(foundInIteration)?"Found In Iteration is missing<br/>":"")
				+(isEmpty(foundInRelease)?"Found In Release is missing<br/>":"")
				+"<br/>";
		
		if(validation.trim().length()>0){
			
			return //"Rally Validator found problems<br/><br/>"+
					defectWithHyperLink(ID,getWebURL())+validation;
		}
		return "";
	}
	
	public String getOpenNewDefectReport(){
		return isDefectOpenOrNew()?defectWithHyperLink(ID,getWebURL())+name+"<br/><br/>"
				:"";
	}
	
	public String getBlockedDefectsReport(){
		return isDefectBlocked()?defectWithHyperLink(ID,getWebURL())+"<br/>"+name+"<br/><br/>"
				:"";
	}
	
	private boolean isDefectBlocked(){
		return blocked!=null && blocked.toLowerCase().equals("true");
	}
	
	private boolean isDefectOpenOrNew(){
		return state==null || state.toLowerCase().equals("new") || state.toLowerCase().equals("open");
	}
	
	private boolean notOwnerSet(){
		if(state!=null && !state.toLowerCase().equals("closed")){
			return isEmpty(owner);
		}
		return false;
	}
	
	private boolean isBlockedAndReasonMissing(){
		if(state!=null && state.toLowerCase().equals("done") && blocked!=null && blocked.toLowerCase().equals("true")){
			return isEmpty(blockedReason);
		}
		return false;
	}
	
	private boolean notScheduleCompletedWhenFixedMade(){
		if(state!=null && state.toLowerCase().equals("fixmade")){
			return isEmpty(scheduled) || !scheduled.toLowerCase().equals("completed");
		}
		return false;
	}
	
	private boolean notScheduleDoneWhenFixedVerified(){
		if(state!=null && state.toLowerCase().equals("fixverified")){
			return isEmpty(scheduled) || !scheduled.toLowerCase().equals("done");
		}
		return false;
	}
	
	private boolean isBuildMissingForFixedMade(String data){
		if(state!=null && state.toLowerCase().equals("fixmade")){
			return isEmpty(data);
		}
		return false;
	}
	private boolean isResOrResDesMissingForFixedMade(){
		if(state!=null && state.toLowerCase().equals("fixmade")){
			return isEmpty(whatChanged) 
					|| (state.toLowerCase().equals("closed") ? false : isEmpty(resDesc));
		}
		return false;
	}
	
	private boolean isEmpty(String data){
		return data==null || data.trim().isEmpty() || data.trim().toLowerCase().equals("null");
	}
	public String[] getMissingInfoForSQEReport(List<RuleBean> lst) {
		
		String validation = "";
		for(RuleBean rb : lst){
			String rule = rb.getRule();
			String message = rb.getMessage();
			if(performCheck(rule)){
				validation += ( replaceMessageWithTemplate(message) +"<br/>");
			}
		}
		
		if(validation.trim().length()>0){
			return new String[]{defectWithHyperLink(ID,getWebURL()), validation};
		}
		
		return new String[]{"",""};
	}
	
	private String defectWithHyperLink(String id, String link){
		return "<a href='"+getWebURL()+"'>"+ID+"</a> - <b>"+getName()+"</b><br/>";
	}
	
	private String replaceMessageWithTemplate(String message){
		if(message.toLowerCase().contains("{OWNER}".toLowerCase())){
			message = message.replace("{OWNER}", getOwner()!=null?getOwner():"No OWNER");
		}
		if(message.toLowerCase().contains("{SUBMITTED BY}".toLowerCase())){
			message = message.replace("{SUBMITTED BY}", getSubmittedBy()!=null?getSubmittedBy():"No SUBMITTED BY");
		}
		if(message.toLowerCase().contains("{ASSIGNEDSQE}".toLowerCase())){
			message = message.replace("{ASSIGNEDSQE}", getAssignedSQE()!=null?getAssignedSQE():"No ASSIGNEDSQE");
		}
		if(message.toLowerCase().contains("{FIXED IN BUILD}".toLowerCase())){
			message = message.replace("{FIXED IN BUILD}", getFixedInBuild()!=null?getFixedInBuild():"No FIXED IN BUILD");
		}
		if(message.toLowerCase().contains("{DEFECT STATE}".toLowerCase())){
			message = message.replace("{DEFECT STATE}", getState()!=null?getState():"No DEFECT STATE");
		}
		if(message.toLowerCase().contains("{VERIFIED IN BUILD}".toLowerCase())){
			message = message.replace("{VERIFIED IN BUILD}", getVerifiedBuild()!=null?getVerifiedBuild():"No VERIFIED IN BUILD");
		}
		if(message.toLowerCase().contains("{TARGET BUILD}".toLowerCase())){
			message = message.replace("{TARGET BUILD}", getTargetBuild()!=null?getTargetBuild():"No TARGET BUILD");
		}
		if(message.toLowerCase().contains("{ID}".toLowerCase())){
			message = message.replace("{ID}", getID()!=null?getID():"No ID");
		}
		if(message.toLowerCase().contains("{PLAN EST}".toLowerCase())){
			message = message.replace("{PLAN EST}", getPlanEstimate()!=null?getPlanEstimate():"No PLAN EST");
		}
		if(message.toLowerCase().contains("{FOUND IN ITERATION}".toLowerCase())){
			message = message.replace("{FOUND IN ITERATION}", getFoundInIteration()!=null?getFoundInIteration():"No FOUND IN ITERATION");
		}
		if(message.toLowerCase().contains("{FOUND IN RELEASE}".toLowerCase())){
			message = message.replace("{FOUND IN RELEASE}", getFoundInRelease()!=null?getFoundInRelease():"No FOUND IN RELEASE");
		}
		if(message.toLowerCase().contains("{RESOLUTION}".toLowerCase())){
			message = message.replace("{RESOLUTION}", getWhatChanged()!=null?getWhatChanged():"No RESOLUTION");
		}
		if(message.toLowerCase().contains("{RESOLUTION DESCRIPTION}".toLowerCase())){
			message = message.replace("{RESOLUTION DESCRIPTION}", getResDesc()!=null?getResDesc():"No RESOLUTION DESCRIPTION");
		}
		if(message.toLowerCase().contains("{SCHEDULE STATE}".toLowerCase())){
			message = message.replace("{SCHEDULE STATE}", getScheduled()!=null?getScheduled():"No SCHEDULE STATE");
		}
		if(message.toLowerCase().contains("{SCRIPT REFERENCE}".toLowerCase())){
			message = message.replace("{SCRIPT REFERENCE}", getScriptReference()!=null?getScriptReference():"No SCRIPT REFERENCE");
		}
		if(message.toLowerCase().contains("{BLOCKED}".toLowerCase())){
			message = message.replace("{BLOCKED}", getBlocked()!=null?getBlocked():"No BLOCKED");
		}
		if(message.toLowerCase().contains("{BLOCKED REASON}".toLowerCase())){
			message = message.replace("{BLOCKED REASON}", getBlockedReason()!=null?getBlockedReason():"No BLOCKED REASON");
		}
		if(message.toLowerCase().contains("{SEVERITY}".toLowerCase())){
			message = message.replace("{SEVERITY}", getSeverity()!=null?getSeverity():"No SEVERITY");
		}
		if(message.toLowerCase().contains("{DESCRIPTION}".toLowerCase())){
			message = message.replace("{DESCRIPTION}", getName()!=null?getName():"No DESCRIPTION");
		}
		if(message.toLowerCase().contains("{ITERATION}".toLowerCase())){
			message = message.replace("{ITERATION}", getIteration()!=null?getIteration():"No ITERATION");
		}
		if(message.toLowerCase().contains("{RELEASE}".toLowerCase())){
			message = message.replace("{RELEASE}", getRelease()!=null?getRelease():"No RELEASE");
		}
		
		return message;
	}
	
	private boolean check(String expression){
		if(expression.contains("==")){
			String lhs = expression.split("==")[0].trim().replaceAll("_", " ");
			String rhs = expression.split("==")[1].trim().replaceAll("_", " ");
			
			String value = getValueOfField(lhs);
			return (value.equalsIgnoreCase(filterRHS(rhs)));
		}
		else if(expression.contains("!=")){
			String lhs = expression.split("!=")[0].trim().replaceAll("_", " ");
			String rhs = expression.split("!=")[1].trim().replaceAll("_", " ");
			
			String value = getValueOfField(lhs);
			return (!value.equalsIgnoreCase(filterRHS(rhs)));
		}
//		else if(expression.contains(">=")){
//			String lhs = expression.split("!=")[0];
//			String rhs = expression.split("!=")[1];
//			
//			String value = getValueOfField(lhs);
//			float valueF=0;
//			if(!isEmpty(value)){
//				valueF=Float.parseFloat(value);
//			}
//		}
		else if(expression.equals("true")){
			return true;
		}
		else if(expression.equals("false")){
			return false;
		}
		
		return false;
	}
	
	public static void main(String[] args) {
		RallyDefect rd = new RallyDefect();
		rd.setWhatChanged("ONE");
		boolean value = rd.performCheck("(RESOLUTION != BLANK AND RESOLUTION == ONE AND ((RESOLUTION == ONE)))");
		System.out.println(value);
		System.out.println("----------");
		rd = new RallyDefect();
		rd.setWhatChanged("ONE2");
		value = rd.performCheck("(RESOLUTION != BLANK AND RESOLUTION == ONE) OR (RESOLUTION == ONE1)");
		System.out.println(value);
		System.out.println("----------");
		rd = new RallyDefect();
		rd.setWhatChanged("ONE TWO");
		value = rd.performCheck("(RESOLUTION != BLANK AND RESOLUTION == ONE_TWO) OR (RESOLUTION == ONE1)");
		System.out.println(value);
	}
	
	private boolean performCheck(String condition){
		if(validateSyntax(condition)){
			return evaluateCondition(new StringBuilder(condition));
		}
		return false;
	}
	
	private boolean evaluateCondition(StringBuilder condition){
		
//		System.out.println(condition);
		StringBuilder conditionSB=new StringBuilder(condition);
		
		int start=-1,end=-1;
		boolean recordIt = false;
		StringBuilder innerMostCondition=new StringBuilder();
		for(int i=0;i<condition.length();i++){
			
			if(recordIt){
				innerMostCondition.append(condition.charAt(i));
			}
			
			if(end!=-1){//We found first inner most
				break; 
			}
			
			if(condition.charAt(i) == '('){
				start=i;
				recordIt = true;
				innerMostCondition=new StringBuilder("(");
			}
			else if(condition.charAt(i) == ')'){
				end=i;
				recordIt = false;
			}
			
		}
		
		if(end!=-1 && start!=-1){ //Only do this if ( or ) are there
			conditionSB = conditionSB.replace(start, end+1, evaluation(innerMostCondition)+""); //End is exclusive hence +1
		}
		
		return conditionSB.indexOf("(")!=-1 && conditionSB.indexOf(")")!=-1
				?	
					evaluateCondition(conditionSB)
				:
					evaluation(conditionSB);
	}
	
	private boolean evaluation(StringBuilder condition){
		String con=condition.toString();
		if(con.contains("(")){
			con = con.replace("(", "")
					 .replace(")", "");
		}
		
		if(con.toLowerCase().contains(" or ")){
			String ors[] = con.toLowerCase().split(" or ");
			for(String exp:ors){
				if(check(exp)){
					return true;
				}
			}
		}
		else if(con.toLowerCase().contains(" and ")){
			String ors[] = con.toLowerCase().split(" and ");
			int count = 0;
			for(String exp:ors){
				if(check(exp)){
					count++;
				}
			}
			if(ors.length==count){
				return true;
			}
		}
		else{
			return check(con);
		}
		
		return false;
	}
	
	/*
	 * This code checks if condition format is right or not, including syntax of condition and supported operators.
	 * 
	 * (RESOLUTION != BLANK AND RESOLUTION == ONE AND ((RESOLUTION == TRUE)))
	 * 
	 */
	private boolean validateSyntax(String condition){
		int count=0;
		
		for(int i=0;i<condition.length();i++){
			if(condition.charAt(i) == '('){
				count++;
			}
			else if(condition.charAt(i) == ')'){
				count--;
			}
			if(count<0){ //If any point of time this goes in negative, that means ')' is not followed by '('
				System.out.println("Syntax issue:"+condition+" is wrong at index "+i+".");
				return false;
			}
		}
		
		if(count!=0){
			System.out.println("Syntax issue:"+condition+" is wrong.");
			return false;
		}
		
		String[] checks = condition
								.replaceAll("\\(", "")
								.replaceAll("\\)", "")
								.split(" ");
		String[] supportedSyntax = {"==","!="};
		List<String> supportedSyntaxLst = Arrays.asList(supportedSyntax);
		
		String[] supportedKeyWords = {"or","and"};
		List<String> supportedKeyWordsLst = Arrays.asList(supportedKeyWords);
		
		boolean toggle=true;
		boolean rhs=false;
		
		for(int i=0;i<checks.length;i++, toggle= !toggle){
			String toTest = checks[i];
			if(toggle){
				
				//Dont check value
				if(rhs){
					rhs=false;
					continue;
				}
				//This must be a valid Rally field 
				getValueOfField(toTest.replaceAll("_", " "));
			}
			else{
				int cnt=-2;
				if(supportedSyntaxLst.contains(toTest.toLowerCase()))
					rhs=true;
				else
					cnt--;
				
				if(!supportedKeyWordsLst.contains(toTest.toLowerCase()))
					cnt--;
				if(cnt==0){
					System.out.println(toTest+" is not valid syntax for this application. Please check "+condition+".");
					return false;
				}
			}
		}
		return true;
	}
	
	private int getRankOfState(String state){
		if(state.equalsIgnoreCase("New"))
			return 1;
		else if(state.equalsIgnoreCase("Open"))
			return 2;
		else if(state.equalsIgnoreCase("Assigned"))
			return 3;
		else if(state.equalsIgnoreCase("Review"))
			return 4;
		else if(state.equalsIgnoreCase("FixMade"))
			return 5;
		else if(state.equalsIgnoreCase("FixVerified"))
			return 6;
		else if(state.equalsIgnoreCase("Closed"))
			return 7;
		
		return -1;
	}
	
	private String filterRHS(String rhs){
		return rhs.equalsIgnoreCase("blank")?"": rhs;
	}
	
	private String getValueOfFieldStr(String field){
		
		if(field.equalsIgnoreCase("DEFECT STATE"))
			return ifEmptyMakeEmpty(state);
		else if(field.equalsIgnoreCase("FIXED IN BUILD"))
			return ifEmptyMakeEmpty(fixedInBuild);
		else if(field.equalsIgnoreCase("VERIFIED IN BUILD"))
			return ifEmptyMakeEmpty(verifiedBuild);
		else if(field.equalsIgnoreCase("TARGET BUILD"))
			return ifEmptyMakeEmpty(targetBuild);
		else if(field.equalsIgnoreCase("ID"))
			return ifEmptyMakeEmpty(ID);
		else if(field.equalsIgnoreCase("PLAN EST"))
			return ifEmptyMakeEmpty(planEstimate);
		else if(field.equalsIgnoreCase("FOUND IN ITERATION"))
			return ifEmptyMakeEmpty(foundInIteration);
		else if(field.equalsIgnoreCase("FOUND IN RELEASE"))
			return ifEmptyMakeEmpty(foundInRelease);
		else if(field.equalsIgnoreCase("OWNER"))
			return ifEmptyMakeEmpty(owner);
		else if(field.equalsIgnoreCase("RESOLUTION"))
			return ifEmptyMakeEmpty(whatChanged);
		else if(field.equalsIgnoreCase("RESOLUTION DESCRIPTION"))
			return ifEmptyMakeEmpty(resDesc);
		else if(field.equalsIgnoreCase("SCHEDULE STATE"))
			return ifEmptyMakeEmpty(scheduled);
		else if(field.equalsIgnoreCase("SCRIPT REFERENCE"))
			return ifEmptyMakeEmpty(scriptReference);
		else if(field.equalsIgnoreCase("BLOCKED"))
			return ifEmptyMakeEmpty(blocked);
		else if(field.equalsIgnoreCase("BLOCKED REASON"))
			return ifEmptyMakeEmpty(blockedReason);
		else if(field.equalsIgnoreCase("ASSIGNEDSQE"))
			return ifEmptyMakeEmpty(assignedSQE);
		else if(field.equalsIgnoreCase("SUBMITTED BY"))
			return ifEmptyMakeEmpty(submittedBy);
		else if(field.equalsIgnoreCase("SEVERITY"))
			return ifEmptyMakeEmpty(severity);
		else if(field.equalsIgnoreCase("ITERATION"))
			return ifEmptyMakeEmpty(iteration);
		else if(field.equalsIgnoreCase("RELEASE"))
			return ifEmptyMakeEmpty(release);
		
		return "-1";
	}
	
	private String getValueOfField(String field){
		
		String result = getValueOfFieldStr(field);
		if("-1".equals(result))
			throw new RuntimeException("Wrong Rally Field "+field+", please check rally.rules file for this rule or contact Dharmesh.");
		else
			return result;
	}
	
	private String ifEmptyMakeEmpty(String data){
		return (data==null || data.trim().isEmpty() || data.trim().toLowerCase().equals("null")) ?""  :data;
	}
	
	
	
	
	public String getFoundInIteration() {
		return foundInIteration;
	}
	public void setFoundInIteration(String foundInIteration) {
		this.foundInIteration = foundInIteration;
	}
	public String getFoundInRelease() {
		return foundInRelease;
	}
	public void setFoundInRelease(String foundInRelease) {
		this.foundInRelease = foundInRelease;
	}
	public String getScriptReference() {
		return scriptReference;
	}
	public void setScriptReference(String scriptReference) {
		this.scriptReference = scriptReference;
	}
	public String getPlanEstimate() {
		return planEstimate;
	}
	public void setPlanEstimate(String planEstimate) {
		this.planEstimate = planEstimate;
	}
	public String getBlocked() {
		return blocked;
	}
	public void setBlocked(String blocked) {
		this.blocked = blocked;
	}
	public String getBlockedReason() {
		return blockedReason;
	}
	public void setBlockedReason(String blockedReason) {
		this.blockedReason = blockedReason;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getTasksURL() {
		return tasksURL;
	}
	public void setTasksURL(String tasksURL) {
		this.tasksURL = tasksURL;
	}
	public String getFixedInBuild() {
		return fixedInBuild;
	}
	public void setFixedInBuild(String fixedInBuild) {
		this.fixedInBuild = fixedInBuild;
	}
	public int getuTaskIndicator() {
		return uTaskIndicator;
	}
	public void setuTaskIndicator(int uTaskIndicator) {
		this.uTaskIndicator = uTaskIndicator;
	}
	public String getTaskURL() {
		return taskURL;
	}
	public void setTaskURL(String taskURL) {
		this.taskURL = taskURL;
	}
	public String getWebURL() {
		return webURL;
	}
	public void setWebURL(String webURL) {
		this.webURL = webURL;
	}
	public String getID() {
		return ID;
	}
	public void setID(String iD) {
		ID = iD;
	}
	public String getURL() {
		return URL;
	}
	public void setURL(String uRL) {
		URL = uRL;
	}
	public RallyDefect(Developer dev, Map<String, String> data) {
		this(data);
		this.owner = dev.getName();
	}
	private RallyDefect(){
		
	}

	public String getSubmittedBy() {
		return submittedBy;
	}

	public void setSubmittedBy(String submittedBy) {
		this.submittedBy = submittedBy;
	}

	public String getAssignedSQE() {
		return assignedSQE;
	}

	public void setAssignedSQE(String assignedSQE) {
		this.assignedSQE = assignedSQE;
	}
	
	public String getVerifiedBuild() {
		return verifiedBuild;
	}
	public void setVerifiedBuild(String verifiedBuild) {
		this.verifiedBuild = verifiedBuild;
	}
	public String getScheduled() {
		return scheduled;
	}
	public void setScheduled(String scheduled) {
		this.scheduled = scheduled;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getResDesc() {
		return resDesc;
	}
	public void setResDesc(String resDesc) {
		this.resDesc = resDesc;
	}
	public String getWhatChanged() {
		return whatChanged;
	}
	public void setWhatChanged(String whatChanged) {
		this.whatChanged = whatChanged;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof RallyDefect){
			return this.ID.equals( ((RallyDefect)obj).ID );
		}
		return false;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public String getTargetBuild() {
		return targetBuild;
	}

	public void setTargetBuild(String targetBuild) {
		this.targetBuild = targetBuild;
	}

	public String getIteration() {
		return iteration;
	}

	public void setIteration(String iteration) {
		this.iteration = iteration;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}
	
}
