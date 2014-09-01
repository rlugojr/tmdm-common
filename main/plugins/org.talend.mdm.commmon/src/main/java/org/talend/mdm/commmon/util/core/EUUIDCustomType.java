package org.talend.mdm.commmon.util.core;

/**
 * all uuid type
 */
public enum EUUIDCustomType {
	//two custom simple type (only used for concept id)
	UUID("UUID"), //$NON-NLS-1$
	PICTURE("PICTURE"), //$NON-NLS-1$
	URL("URL"), //$NON-NLS-1$
	AUTO_INCREMENT("AUTO_INCREMENT"), //$NON-NLS-1$
	MULTI_LINGUAL("MULTI_LINGUAL"); //$NON-NLS-1$

    String name;

	EUUIDCustomType(String name){
		this.name=name;
	}
	
	public String getName() {
		return name;
	}
}
