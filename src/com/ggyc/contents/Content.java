package com.ggyc.contents;

public class Content {
	public int id;
	public int Lid;
	public String content;
	// weight of a content
	public int value;
	// start time
	public int start;
	// end time
	public int end;
	
	public Content(int id) {
		this.id = id;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public void setLocationID(int id) {
		Lid = id;
	}
	
	public void setValue(int v) {
		value = v;
	}
	
	public void setStartTime(int s) {
		start = s;
	}
	
	public void setEndTime(int e) {
		end  = e;
	}
}
