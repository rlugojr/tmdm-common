package org.talend.mdm.commmon.util.core;

import java.util.HashMap;

public interface ICoreConstants {
	//schematron_rule
	//static final String SCHEMATRON_RULE="schematron_rule";
	//static final String SCHEMATRON_TAG="<schema xmlns=\"http://www.ascc.net/xml/schematron\" ns=\"http://xml.apache.cocoon/xmlform\">";
	static final String X_Schematron="X_Schematron";
	static final String X_Workflow="X_Workflow";
	/**
	 * workflow access
	 */
	static final String[] WORKFLOW_ACCESSES={"Read-only","Hidden","Writable"};
	/**
	 * the default svn name in VersionSystem
	 */
	static final String DEFAULT_SVN="DEFAULT_SVN";
	
	static final String DEFAULT_CATEGORY_ROOT = "category";
	/**
	 * cross referencing init datacluster & datamodel
	 */
	public static final String CrossReferencing_datacluster="crossreferencing";
	public static final String CrossReferencing_datamodel="crossreferencing";	
	
	/**
	 * enum user roles
	 */
	public static final String AUTHENTICATED_PERMISSION = "authenticated";
	public static final String ADMIN_PERMISSION = "administration";
	
	public static final String SYSTEM_ADMIN_ROLE = "System_Admin";
	public static final String SYSTEM_INTERACTIVE_ROLE = "System_Interactive";
	public static final String SYSTEM_WEB_ROLE = "System_Web";
	public static final String SYSTEM_VIEW_ROLE = "System_View";
	
    /**
     * Item pk split
     */
    public static final String ITEM_PK_SPLIT = "src/main"; //$NON-NLS-1$

    public static final String TALEND_NAMESPACE = "http://www.talend.com/mdm"; //$NON-NLS-1$

    public static class rolesConvert {

        public static final HashMap<String, String> oldRoleToNewRoleMap = new HashMap<String, String>();

        static {
            oldRoleToNewRoleMap.put("Default_Admin", ICoreConstants.SYSTEM_ADMIN_ROLE);
            oldRoleToNewRoleMap.put("Default_User", ICoreConstants.SYSTEM_INTERACTIVE_ROLE);
            oldRoleToNewRoleMap.put("Default_Viewer", ICoreConstants.SYSTEM_VIEW_ROLE);
        }

        ;
    }

}
