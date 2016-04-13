package com.dharmeshborad.rallyreminder.core;

public class ReleaseOrProject {
	private String releaseOrProjectName;
	private String refUrl;
	
	public String getReleaseOrProjectName() {
		return releaseOrProjectName;
	}
	public void setReleaseOrProjectName(String releaseOrProjectName) {
		this.releaseOrProjectName = releaseOrProjectName;
	}
	public String getRefUrl() {
		return refUrl;
	}
	public void setRefUrl(String refUrl) {
		this.refUrl = refUrl;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.refUrl.equalsIgnoreCase(((ReleaseOrProject)obj).getRefUrl());
	}
}
