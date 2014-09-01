package org.talend.mdm.commmon.util.workbench;

public class Version {
	private int major;
	private int minor;
	private int rev;
	private String build;
	
	public Version(int major, int minor, int rev, String build) {
		super();
		this.major = major;
		this.minor = minor;
		this.rev = rev;
		this.build = build;
	}
	
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public int getRev() {
		return rev;
	}
	public void setRev(int rev) {
		this.rev = rev;
	}
	
	
}
