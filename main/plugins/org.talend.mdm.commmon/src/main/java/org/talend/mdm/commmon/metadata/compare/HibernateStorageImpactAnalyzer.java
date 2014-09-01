/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.compare;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.ObjectUtils;
import org.talend.mdm.commmon.metadata.*;

public class HibernateStorageImpactAnalyzer implements ImpactAnalyzer {

    public Map<Impact, List<Change>> analyzeImpacts(Compare.DiffResults diffResult) {
        Map<Impact, List<Change>> impactSort = new EnumMap<Impact, List<Change>>(Impact.class);
        for (Impact impact : Impact.values()) {
            impactSort.put(impact, new LinkedList<Change>());
        }
        // Add actions
        for (AddChange addAction : diffResult.getAddChanges()) {
            MetadataVisitable element = addAction.getElement();
            if (element instanceof ComplexTypeMetadata) {
                impactSort.get(Impact.LOW).add(addAction);
            } else if (element instanceof FieldMetadata) {
                if (element instanceof ContainedTypeFieldMetadata) {
                    // Contained field may change mapping strategy
                    impactSort.get(Impact.HIGH).add(addAction);
                } else {
                    impactSort.get(Impact.LOW).add(addAction);
                }
            }
        }
        // Remove actions
        for (RemoveChange removeAction : diffResult.getRemoveChanges()) {
            MetadataVisitable element = removeAction.getElement();
            if (element instanceof ComplexTypeMetadata) {
                impactSort.get(Impact.MEDIUM).add(removeAction);
            } else if (element instanceof FieldMetadata) {
                FieldMetadata field = (FieldMetadata) removeAction.getElement();
                if (field.isMandatory()) {
                    impactSort.get(Impact.HIGH).add(removeAction);
                } else {
                    impactSort.get(Impact.MEDIUM).add(removeAction);
                }
            } else {
                throw new NotImplementedException();
            }
        }
        // Modify actions
        for (ModifyChange modifyAction : diffResult.getModifyChanges()) {
            MetadataVisitable element = modifyAction.getElement();
            if (element instanceof ComplexTypeMetadata) {
                // Type modifications may include many things (inheritance changes for instance).
                impactSort.get(Impact.HIGH).add(modifyAction);
            } else if (element instanceof FieldMetadata) {
                FieldMetadata previous = (FieldMetadata) modifyAction.getPrevious();
                FieldMetadata current = (FieldMetadata) modifyAction.getCurrent();
                Object previousLength = previous.getType().getData(MetadataRepository.DATA_MAX_LENGTH);
                Object currentLength = current.getType().getData(MetadataRepository.DATA_MAX_LENGTH);
                /*
                 * HIGH IMPACT CHANGES
                 */
                if (!previous.getType().equals(current.getType())) {
                    // Type modification has high impact (values might not be following correct format).
                    impactSort.get(Impact.HIGH).add(modifyAction);
                } else if (previous.isMany() != current.isMany()) {
                    // Collection mapping undo (or creation) has a high impact on schema
                    impactSort.get(Impact.HIGH).add(modifyAction);
                } else if (previous.isKey() != current.isKey()) {
                    // Key creation might have high impact (in case of duplicated values).
                    impactSort.get(Impact.HIGH).add(modifyAction);
                } else if (previous.isMandatory() != current.isMandatory()) {
                    // Won't be able to change constraint
                    impactSort.get(Impact.HIGH).add(modifyAction);
                } else if (!ObjectUtils.equals(previousLength, currentLength)) {
                    // Won't be able to change constraint for max length
                    impactSort.get(Impact.HIGH).add(modifyAction);
                }
            }
        }
        return impactSort;
    }

}
