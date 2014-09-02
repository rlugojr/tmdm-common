package org.talend.mdm.commmon.metadata.builder;

import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;

import java.util.Locale;
import java.util.Map;

class SimpleFieldBuilder extends FieldBuilder {

    protected SimpleFieldBuilder() {
    }

    public static SimpleFieldBuilder field(String name) {
        SimpleFieldBuilder builder = new SimpleFieldBuilder();
        builder.name = name;
        return builder;
    }

    @Override
    protected FieldMetadata build() {
        SimpleTypeFieldMetadata field = new SimpleTypeFieldMetadata(null, isKey, isMany, isMandatory, name, fieldType.build(),
                allowWriteUsers, hideUsers, workflowAccessRights);
        for (Map.Entry<Locale, String> entry : fieldNameLocale.entrySet()) {
            field.registerName(entry.getKey(), entry.getValue());
        }
        return field;
    }

    public ReferenceFieldBuilder fk() {
        return new ReferenceFieldBuilder(this);
    }

}
