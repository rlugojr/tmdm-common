package org.talend.mdm.commmon.util.webapp;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.commmon.util.core.ICoreConstants;

public enum XSystemObjects {
    // Data Clusters
    DC_JCAADAPTERS(XObjectType.DATA_CLUSTER, "JCAAdapters", true), //$NON-NLS-1$
    DC_INBOX(XObjectType.DATA_CLUSTER, "Inbox", false), //$NON-NLS-1$
    DC_CONF(XObjectType.DATA_CLUSTER, "CONF", false), //$NON-NLS-1$
    DC_PROVISIONING(XObjectType.DATA_CLUSTER, "PROVISIONING", true), //$NON-NLS-1$
    DC_UPDATE_PREPORT(XObjectType.DATA_CLUSTER, "UpdateReport", false), //$NON-NLS-1$
    DC_XTENTIS_COMMON_REPORTING(XObjectType.DATA_CLUSTER, "Reporting", false), //$NON-NLS-1$
    DC_MDMITEMSTRASH(XObjectType.DATA_CLUSTER, "MDMItemsTrash", false), //$NON-NLS-1$
    DC_SEARCHTEMPLATE(XObjectType.DATA_CLUSTER, "SearchTemplate", true), //$NON-NLS-1$
    DC_CROSSREFERENCING(XObjectType.DATA_CLUSTER, ICoreConstants.CrossReferencing_datacluster, true),
    // Revision
    DC_REVISION(XObjectType.DATA_CLUSTER, "Revision", true), //$NON-NLS-1$
    DC_MDMItemImages(XObjectType.DATA_CLUSTER, "MDMItemImages", false), //$NON-NLS-1$
    DC_MDMMigration(XObjectType.DATA_CLUSTER, "MDMMigration", false), //$NON-NLS-1$
    // Role
    ROLE_DEFAULT_ADMIN(XObjectType.ROLE, ICoreConstants.SYSTEM_ADMIN_ROLE, false),
    ROLE_DEFAULT_USER(XObjectType.ROLE, ICoreConstants.SYSTEM_INTERACTIVE_ROLE, false),
    ROLE_DEFAULT_WEB(XObjectType.ROLE, ICoreConstants.SYSTEM_WEB_ROLE, false),
    ROLE_DEFAULT_VIEWER(XObjectType.ROLE, ICoreConstants.SYSTEM_VIEW_ROLE, true), // readonly for everything, can't be
                                                                                  // modified
    // Data Models
    DM_CONF(XObjectType.DATA_MODEL, "CONF", false), //$NON-NLS-1$
    DM_PROVISIONING(XObjectType.DATA_MODEL, "PROVISIONING", true), //$NON-NLS-1$
    DM_UPDATEREPORT(XObjectType.DATA_MODEL, "UpdateReport", false), //$NON-NLS-1$
    DM_XTENTIS_COMMON_REPORTING(XObjectType.DATA_MODEL, "Reporting", false), //$NON-NLS-1$
    DM_SEARCHTEMPLATE(XObjectType.DATA_MODEL, "SearchTemplate", true), //$NON-NLS-1$
    Dm_CROSSREFERENCING(XObjectType.DATA_MODEL, ICoreConstants.CrossReferencing_datamodel, true),
    // Menus
    M_BROWSE_ITEMS2(XObjectType.MENU, "Browse items2", false), //$NON-NLS-1$
    // GXT refact menu
    M_BrowseRecords(XObjectType.MENU, "BrowseRecords", false), //$NON-NLS-1$
    M_Hierarchy(XObjectType.MENU, "Hierarchy", true), //$NON-NLS-1$
    M_WelcomePortal(XObjectType.MENU, "WelcomePortal", false), //$NON-NLS-1$
    M_LicenseManager(XObjectType.MENU, "LicenseManager", true), //$NON-NLS-1$
    M_UserManager(XObjectType.MENU, "UserManager", true), //$NON-NLS-1$
    M_RecycleBin(XObjectType.MENU, "RecycleBin", false), //$NON-NLS-1$
    M_CrossReference(XObjectType.MENU, "CrossReference", true), //$NON-NLS-1$
    M_SEARCH(XObjectType.MENU, "Search", true), //$NON-NLS-1$
    M_BROWSE_VIEWS(XObjectType.MENU, "Browse views", false), //$NON-NLS-1$
    M_MANAGER_USERS(XObjectType.MENU, "Manage users", true), //$NON-NLS-1$
    M_LICENSE(XObjectType.MENU, "License", true), //$NON-NLS-1$
    M_REPORTING(XObjectType.MENU, "Reporting", false), //$NON-NLS-1$
    M_SYNCHRONIZATIONACTION(XObjectType.MENU, "SynchronizationAction", true), //$NON-NLS-1$
    M_SYNCHRONIZATIONPLAN(XObjectType.MENU, "SynchronizationItem", true), //$NON-NLS-1$
    M_UPDATE_REPORT(XObjectType.MENU, "UpdateReport", false), //$NON-NLS-1$
    M_SERVICE_SCHEDULE(XObjectType.MENU, "Service Schedule", true), //$NON-NLS-1$
    M_LOGGING(XObjectType.MENU, "logging", false), //$NON-NLS-1$
    M_ITEMSTRASH(XObjectType.MENU, "ItemsTrash", false), //$NON-NLS-1$
    M_SMTP(XObjectType.MENU, "smtp", false), //$NON-NLS-1$
    M_HIERARCHICAL_VIEW_DERIVED(XObjectType.MENU, "Derived Hierarchy", true), //$NON-NLS-1$
    M_UNIVERSEMANAGER(XObjectType.MENU, "Universe Manager", true), //$NON-NLS-1$
    M_CROSSREFERENCING(XObjectType.MENU, "Cross Referencing", true), //$NON-NLS-1$
    M_WORKFLOWTASKS(XObjectType.MENU, "WorkflowTasks", true), //$NON-NLS-1$
    M_DATASTEWARDSHIP(XObjectType.MENU, "Datastewardship", true), //$NON-NLS-1$
    M_WELCOME(XObjectType.MENU, "Welcome", false), //$NON-NLS-1$
    M_STAGINGAREA(XObjectType.MENU, "StagingArea", false), //$NON-NLS-1$
    M_BROWSE_RECORDS_STAGING(XObjectType.MENU, "BrowseRecordsInStaging", false), //$NON-NLS-1$
    M_BONITAWORKFLOWTASKS(XObjectType.MENU, "BonitaWorkflowTasks", true); //$NON-NLS-1$

    private String name;

    private int type;

    private boolean tem;

    XSystemObjects(int type, String name, boolean tem) {
        this.name = name;
        this.type = type;
        this.tem = tem;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isTem() {
        return tem;
    }

    /**
     * get all the elements
     * 
     * @return map
     */
    public static Map<String, XSystemObjects> getXSystemObjects() {
        Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
        for (int i = 0; i < values().length; i++) {
            map.put(values()[i].getType() + "_" + String.valueOf(values()[i].getName()), values()[i]); //$NON-NLS-1$
        }
        return map;
    }

    /**
     * get the "type" elements
     */
    public static Map<String, XSystemObjects> getXSystemObjects(int type) {
        Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
        for (int i = 0; i < values().length; i++) {
            if (type == values()[i].getType()) {
                map.put(String.valueOf(values()[i].getName()), values()[i]);
            }
        }
        return map;
    }

    public static boolean isXSystemObject(int type, String objectPK) {
        Map<String, XSystemObjects> map = getXSystemObjects(type);
        return isXSystemObject(map, objectPK);
    }

    public static boolean isXSystemObject(Map<String, XSystemObjects> map, String objectPK) {
        if (map.get(objectPK) != null) {
            return true;
        }
        // if objectPK is like MDMMigration/completed
        if (objectPK.indexOf('/') != -1) {
            String key = objectPK.split("/")[0]; //$NON-NLS-1$
            return map.get(key) != null;
        }
        return false;
    }

    /**
     * check if the element is exist
     */
    public static boolean isExist(int type, String name) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].type == type && values()[i].name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * check if the element is exist
     */
    public static boolean isExist(String name) {
        for (int i = 0; i < values().length; i++) {
            if (values()[i].name.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static Map<String, XSystemObjects> getXSystemObjectsTOM(int type) {
        Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
        for (int i = 0; i < values().length; i++) {
            if (type == values()[i].getType() && !values()[i].isTem()) {
                map.put(String.valueOf(values()[i].getName()), values()[i]);
            }
        }
        return map;
    }
}
