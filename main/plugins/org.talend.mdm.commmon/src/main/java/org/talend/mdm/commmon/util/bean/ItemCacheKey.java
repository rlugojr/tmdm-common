package org.talend.mdm.commmon.util.bean;

public class ItemCacheKey {

    private final String revisionID;

    private final String uniqueID;

    private final String dataClusterID;

    private int cachedHashCode = -1;

    public ItemCacheKey(String revisionID, String uniqueID, String dataClusterID) {
        this.uniqueID = uniqueID;
        this.dataClusterID = dataClusterID == null ? "__ROOT__" : dataClusterID; //$NON-NLS-1$
        this.revisionID = (revisionID == null) || "".equals(revisionID) ? "__HEAD__" : revisionID; //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String getRevisionID() {
        return revisionID;
    }

    public String getUniqueID() {
        return uniqueID;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ItemCacheKey) {
            ItemCacheKey key = (ItemCacheKey) obj;
            return key.uniqueID.equals(uniqueID)
                    && key.revisionID.equals(revisionID)
                    && key.dataClusterID.equals(dataClusterID);
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (cachedHashCode == -1) {
            cachedHashCode = revisionID != null ? revisionID.hashCode() : 0;
            cachedHashCode = 31 * cachedHashCode + (uniqueID != null ? uniqueID.hashCode() : 0);
            cachedHashCode = 31 * cachedHashCode + (dataClusterID != null ? dataClusterID.hashCode() : 0);
        }
        return cachedHashCode;
    }

    @Override
    public String toString() {
        return revisionID + "." + dataClusterID + "." + uniqueID; //$NON-NLS-1$ //$NON-NLS-2$
    }
}
