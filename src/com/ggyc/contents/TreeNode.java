package com.ggyc.contents;

import java.util.ArrayList;

public class TreeNode {
	Content content;
	
	public TreeNode(Content content) {
		this.content  = content;
	}
	// Child Nodes
	public ArrayList<TreeNode> contents = new ArrayList<TreeNode>();
}