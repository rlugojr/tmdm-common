package org.talend.mdm.commmon.metadata.builder;

import java.util.Locale;
import java.util.Map;

import org.talend.mdm.commmon.metadata.*;

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
            // Create field (depends on what type is the field).
            FieldMetadata field;
            if (type instanceof SimpleTypeMetadata) {
                field = new SimpleTypeFieldMetadata(null, isKey, isMany, isMandatory, name, type, allowWriteUsers, hideUsers,
                        workflowAccessRights);
            } else {
                ComplexTypeMetadata fieldType = (ComplexTypeMetadata) type;
                field = new ContainedTypeFieldMetadata(null, isMany, isMandatory, name, fieldType, allowWriteUsers, hideUsers,
                        workflowAccessRights);
            }
            // Sets locale for the field.
            for (Map.Entry<Locale, String> entry : fieldNameLocale.entrySet()) {
                field.registerName(entry.getKey(), entry.getValue());
            }
            return field;
        } else {
            // No field type means field wasn't resolved.
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
