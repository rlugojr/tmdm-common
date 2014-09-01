package org.talend.mdm.commmon.util.core;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.talend.mdm.commmon.util.core.EDBType.*;


public class CommonUtil {

	public static String getConceptRevisionID(LinkedHashMap<String, String> itemsRevisionIDs,String defaultRevisionID,String conceptName) {
		List<String> patterns = new ArrayList<String>(itemsRevisionIDs.keySet());
        for (String pattern : patterns) {
            if (conceptName.matches(pattern)) return itemsRevisionIDs.get(pattern);
        }
		return defaultRevisionID;
	}

	public static String getErrMsgFromException(Throwable e){
        String msg = e.getLocalizedMessage();
        if (msg != null) {
            Pattern p=Pattern.compile("(.*?):(.*?)"); //$NON-NLS-1$
            Matcher m = p.matcher(msg);
            if (m.matches()) {
                msg = m.group(2);
            }
        }
        return msg;
	}

    /**
     * get the DB repository root path
     */
    private static String getDBRootPath() {
        EDBType dbType = MDMConfiguration.getDBType();
        if (dbType.getName().equals(EXIST.getName())) {
            return EXIST.getRoot();
        }
        return StringUtils.EMPTY;
    }

    public static String getPath(String revisionID, String clusterName) {
        if (revisionID != null) {
            revisionID = revisionID.replaceAll("\\[HEAD\\]|HEAD", ""); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String rootPath = getDBRootPath();
        return (revisionID == null || "".equals(revisionID) ? rootPath + "/" : rootPath + "/R-" + revisionID + "/")//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                + (clusterName == null ? "" : clusterName);
    }
}
