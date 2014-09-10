package org.talend.mdm.commmon.metadata.validation;

import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

import java.util.Collection;

/**
 * This rule checks if a {@link org.talend.mdm.commmon.metadata.FieldMetadata field} is not overriding a inherited
 * field from the super type hierarchy. It consists in a simple check of field's declaring type (it must be same declaring type
 * has super type's - if present in super type).
 */
public class FieldInheritanceOverrideRule implements ValidationRule {

    private final FieldMetadata field;

    public FieldInheritanceOverrideRule(FieldMetadata field) {
        this.field = field;
    }

    @Override
    public boolean perform(ValidationHandler handler) {
        ComplexTypeMetadata declaringType = (ComplexTypeMetadata) field.getDeclaringType();
        if (declaringType != null) {
            Collection<TypeMetadata> superTypes = declaringType.getSuperTypes();
            for (TypeMetadata superType : superTypes) {
                if (superType instanceof ComplexTypeMetadata) {
                    ComplexTypeMetadata type = (ComplexTypeMetadata) superType;
                    String path = field.getPath();
                    if (type.hasField(path)) {
                        FieldMetadata superField = type.getField(path);
                        if (!declaringType.equals(superField.getDeclaringType())) {
                            handler.error(field,
                                    "Field '" + field.getName() + "' can not override inherited element.",
                                    field.<Element> getData(MetadataRepository.XSD_DOM_ELEMENT),
                                    field.<Integer> getData(MetadataRepository.XSD_LINE_NUMBER),
                                    field.<Integer> getData(MetadataRepository.XSD_COLUMN_NUMBER),
                                    ValidationError.FIELD_CANNOT_OVERRIDE_INHERITED_ELEMENT);
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean continueOnFail() {
        return false;
    }
}
