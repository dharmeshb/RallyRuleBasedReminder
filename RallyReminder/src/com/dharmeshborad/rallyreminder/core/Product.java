package com.dharmeshborad.rallyreminder.core;

import java.util.ArrayList;
import java.util.List;

public class Product {
	
	public Product() {
	}
	
	private List<ReleaseOrProject> releasesOrProjects = new ArrayList<>();
	
	public List<ReleaseOrProject> getReleasesOrProjects() {
		return releasesOrProjects;
	}

	public void setReleasesOrProjects(List<ReleaseOrProject> releases) {
		this.releasesOrProjects = releases;
	}

}
