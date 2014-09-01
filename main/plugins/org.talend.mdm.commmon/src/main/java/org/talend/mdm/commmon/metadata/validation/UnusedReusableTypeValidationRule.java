/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.validation;

import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

/**
 * This rules warns user when a reusable type is never used.
 */
class UnusedReusableTypeValidationRule implements ValidationRule {

    private final ComplexTypeMetadata type;

    UnusedReusableTypeValidationRule(ComplexTypeMetadata type) {
        this.type = type;
    }

    @Override
    public boolean perform(ValidationHandler handler) {
        if (MetadataUtils.countEntityUsageCount(type) == 0) {
            handler.warning(type, "Type '" + type.getName() + "' is never used in an entity type.",
                    type.<Element> getData(MetadataRepository.XSD_DOM_ELEMENT),
                    type.<Integer> getData(MetadataRepository.XSD_LINE_NUMBER),
                    type.<Integer> getData(MetadataRepository.XSD_COLUMN_NUMBER), ValidationError.UNUSED_REUSABLE_TYPE);
            return false;
        }
        return true;
    }

    @Override
    public boolean continueOnFail() {
        return true;
    }
}
