// ============================================================================
//
// Copyright (C) 2006-2015 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.commmon.util.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Handles the mdm.conf file
 */
public final class MDMConfiguration {

    private static final Logger logger = Logger.getLogger(MDMConfiguration.class);

    private static MDMConfiguration instance;

    private String location;

    private Properties properties = null;

    private MDMConfiguration(String location) {
        this.location = location;
    }

    public static synchronized MDMConfiguration createConfiguration(String location, boolean ignoreIfNotFound) {
        if (instance != null) {
            throw new IllegalStateException();
        }
        instance = new MDMConfiguration(location);
        instance.getProperties(true, ignoreIfNotFound);
        return instance;
    }

    public static synchronized Properties getConfiguration() {
        return getConfiguration(false);
    }

    public static synchronized Properties getConfiguration(boolean reload) {
        if (instance == null) {
            throw new IllegalStateException();
        }
        return instance.getProperties(reload, false);
    }

    public static synchronized void save() {
        if (instance == null) {
            throw new IllegalStateException();
        }
        instance.saveProperties();
    }

    private Properties getProperties(boolean reload, boolean ignoreIfNotFound) {
        if (reload) {
            properties = null;
        }
        if (properties != null) {
            return properties;
        }
        properties = new Properties();

        File file = new File(location);
        if (file.exists()) {
            logger.info("MDM Configuration: found in '" + file.getAbsolutePath() + "'."); //$NON-NLS-1$ //$NON-NLS-2$
            FileInputStream in = null;
            try {
                in = new FileInputStream(file);
                properties.load(in);
            } catch (Exception e) {
                if (!ignoreIfNotFound) {
                    throw new IllegalStateException("Unable to load MDM configuration from '" //$NON-NLS-1$
                            + file.getAbsolutePath() + "'", e); //$NON-NLS-1$
                }
                logger.warn("Unable to load MDM configuration from '" + file.getAbsolutePath() + "'", e); //$NON-NLS-1$ //$NON-NLS-2$
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (Exception e) {
                        if (logger.isDebugEnabled()) {
                            logger.error(e.getMessage(), e);
                        }
                    }
                }
            }
        } else {
            if (!ignoreIfNotFound) {
                throw new IllegalStateException("Unable to load MDM configuration from '" + file.getAbsolutePath() //$NON-NLS-1$
                        + "'"); //$NON-NLS-1$
            }
            logger.warn("Unable to load MDM configuration from '" + file.getAbsolutePath() + "'"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return properties;
    }

    /**
     * save configure file
     */
    private void saveProperties() {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(location);
            properties.store(out, "MDM configuration file"); //$NON-NLS-1$
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        }
    }

    public static EDBType getDBType() {
        Object dbType = getConfiguration().get("xmldb.type"); //$NON-NLS-1$
        if (dbType != null && dbType.toString().equals(EDBType.QIZX.getName())) {
            return EDBType.QIZX;
        }
        return EDBType.EXIST;
    }

    public static boolean isExistDb() {
        Object dbType = getConfiguration().get("xmldb.type"); //$NON-NLS-1$
        return !(dbType != null && !dbType.toString().equals(EDBType.EXIST.getName()));
    }

    public static String getEndpointUrl() {
        String endpointUrl = getConfiguration().getProperty("mdm.endpoint.url"); //$NON-NLS-1$
        endpointUrl = endpointUrl == null ? "http://localhost:8080/talendmdm/services/soap" : endpointUrl; //$NON-NLS-1$
        return endpointUrl;
    }

    public static String getAdminPassword() {
        String password = getConfiguration().getProperty("admin.password"); //$NON-NLS-1$
        password = password == null ? "talend" : password; //$NON-NLS-1$
        return password;
    }

    public static String getAdminUser() {
        String user = getConfiguration().getProperty("admin.user"); //$NON-NLS-1$
        user = user == null ? "admin" : user; //$NON-NLS-1$
        return user;
    }

    public static int getAutoEntityFindThreshold() {
        String value = getConfiguration().getProperty("autoentityfind.item.max"); //$NON-NLS-1$
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return Integer.MAX_VALUE;
        }
    }

    /**
     * It can be only called when MDM applies on user data containers. <li>DispatchWrapper is Hybrid configuration,</li>
     * <li>SQLWrapper is Full SQL</li>
     */
    public static boolean isSqlDataBase() {
        String xmlServerClass = getConfiguration().getProperty("xmlserver.class"); //$NON-NLS-1$
        return "com.amalto.core.storage.DispatchWrapper".equals(xmlServerClass) //$NON-NLS-1$
                || "com.amalto.core.storage.SQLWrapper".equals(xmlServerClass); //$NON-NLS-1$
    }
}
