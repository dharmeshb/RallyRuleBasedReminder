package com.dharmeshborad.rallyreminder.core.framework;

import java.util.ArrayList;
import java.util.List;

import com.dharmeshborad.rallyreminder.core.Product;

public class Input {
	private String emaild;
	private List<RuleBean> lst = new ArrayList<>();
	private String releaseOrProjectString;
	private String[] releasesOrProjects;
	private Product product;
	private boolean fetchReleases=true;
	private boolean fetchChild=false;
	private boolean showNewDefectSection=false;
	private boolean showBlockedDefectSection=false;
	private String groupBy;
	private String customMessage;
	
	public String getCustomMessage() {
		return customMessage;
	}
	public void setCustomMessage(String customMessage) {
		this.customMessage = customMessage;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public boolean isFetchChild() {
		return fetchChild;
	}
	public void setFetchChild(boolean fetchChild) {
		this.fetchChild = fetchChild;
	}
	public boolean isFetchReleases() {
		return fetchReleases;
	}
	public void setFetchReleases(boolean fetchReleases) {
		this.fetchReleases = fetchReleases;
	}
	public boolean isShowNewDefectSection() {
		return showNewDefectSection;
	}
	public void setShowNewDefectSection(boolean showNewDefectSection) {
		this.showNewDefectSection = showNewDefectSection;
	}
	public boolean isShowBlockedDefectSection() {
		return showBlockedDefectSection;
	}
	public void setShowBlockedDefectSection(boolean showBlockedDefectSection) {
		this.showBlockedDefectSection = showBlockedDefectSection;
	}
	public Product getProduct() {
		return product;
	}
	public void setProduct(Product product) {
		this.product = product;
	}
	public String getEmaild() {
		return emaild;
	}
	public void setEmaild(String emaild) {
		this.emaild = emaild;
	}
	public List<RuleBean> getLst() {
		return lst;
	}
	public void setLst(List<RuleBean> lst) {
		this.lst = lst;
	}
	
	public void add(RuleBean rb){
		lst.add(rb);
	}
	public String getReleaseOrProjectString() {
		return releaseOrProjectString;
	}
	public void setReleaseOrProjectString(String releaseOrProjectString) {
		this.releaseOrProjectString = releaseOrProjectString;
	}
	public String[] getReleasesOrProjects() {
		return releasesOrProjects;
	}
	public void setReleasesOrProjects(String[] releasesOrProjects) {
		this.releasesOrProjects = releasesOrProjects;
	}
	
}
