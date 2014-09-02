package org.talend.mdm.commmon.metadata.builder;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ContainedTypeFieldMetadata;

import java.util.Locale;
import java.util.Map;

public class ContainedFieldBuilder extends FieldBuilder {

    protected ContainedTypeFieldMetadata build() {
        ComplexTypeMetadata build = fieldType.build();
        ContainedTypeFieldMetadata field = new ContainedTypeFieldMetadata(null, isMany, isMandatory, name, build, allowWriteUsers, hideUsers,
                workflowAccessRights);
        for (Map.Entry<Locale, String> entry : fieldNameLocale.entrySet()) {
            field.registerName(entry.getKey(), entry.getValue());
        }
        return field;
    }
}
