package org.talend.mdm.commmon.metadata.builder;

import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;
import org.talend.mdm.commmon.metadata.UnresolvedFieldMetadata;

import java.util.Locale;
import java.util.Map;

public class SimpleFieldBuilder extends FieldBuilder {

    protected SimpleFieldBuilder() {
    }

    public static SimpleFieldBuilder field(String name) {
        SimpleFieldBuilder builder = new SimpleFieldBuilder();
        builder.name = name;
        return builder;
    }

    @Override
    protected FieldMetadata build() {
        if (fieldType != null) { // Unresolved fields may have a null type.
            TypeMetadata type = fieldType.build();
            SimpleTypeFieldMetadata field = new SimpleTypeFieldMetadata(null, isKey, isMany, isMandatory, name, type,
                    allowWriteUsers, hideUsers, workflowAccessRights);
            for (Map.Entry<Locale, String> entry : fieldNameLocale.entrySet()) {
                field.registerName(entry.getKey(), entry.getValue());
            }
            return field;
        } else {
            return new UnresolvedFieldMetadata(name, isKey, null);
        }
    }

    @Override
    public ReferenceFieldBuilder fk() {
        return new ReferenceFieldBuilder(this);
    }

    @Override
    public EnumerationFieldBuilder enumeration() {
        return new EnumerationFieldBuilder(this);
    }

}
