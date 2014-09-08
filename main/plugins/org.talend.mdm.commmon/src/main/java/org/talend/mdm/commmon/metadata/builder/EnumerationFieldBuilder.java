package org.talend.mdm.commmon.metadata.builder;

import org.talend.mdm.commmon.metadata.EnumerationFieldMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class EnumerationFieldBuilder extends FieldBuilder {

    public EnumerationFieldBuilder(FieldBuilder builder) {
        this.name = builder.name;
    }

    @Override
    protected FieldMetadata build() {
        EnumerationFieldMetadata field = new EnumerationFieldMetadata(null, isKey, isMany, isMandatory, name, fieldType.build(),
                allowWriteUsers, hideUsers, workflowAccessRights);
        for (Map.Entry<Locale, String> entry : fieldNameLocale.entrySet()) {
            field.registerName(entry.getKey(), entry.getValue());
        }
        return field;
    }
}
