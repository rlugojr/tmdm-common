package org.talend.mdm.commmon.util.core;

public enum EDBType {
	EXIST("exist",""),
	BERKELEY("berkeley",""),
	QIZX("qizx",""),
	ORACLE("oracle","/talendmdm");	
	String name;
	String root;
	EDBType(String name,String root){
		this.name=name;
		this.root=root;
	}
	
	public String getName() {
		return name;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}
	
}
