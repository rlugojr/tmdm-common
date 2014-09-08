package org.talend.mdm.commmon.metadata.builder;

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ReferenceFieldBuilder extends FieldBuilder {

    private final List<SimpleFieldBuilder> foreignKeyInfo = new LinkedList<SimpleFieldBuilder>();

    private TypeBuilder referencedType;

    private SimpleFieldBuilder referencedField;

    private boolean fkIntegrity = false;

    private boolean allowFkIntegrityOverride = false;

    private String foreignKeyFilter = StringUtils.EMPTY;

    public ReferenceFieldBuilder(SimpleFieldBuilder fieldBuilder) {
        isKey = fieldBuilder.isKey;
        isMany = fieldBuilder.isMany;
        isMandatory = fieldBuilder.isMandatory;
        fieldType = fieldBuilder.fieldType;
        allowWriteUsers = fieldBuilder.allowWriteUsers;
        hideUsers = fieldBuilder.hideUsers;
        workflowAccessRights = fieldBuilder.workflowAccessRights;
    }

    public ReferenceFieldBuilder referencedType(TypeBuilder referencedType) {
        this.referencedType = referencedType;
        return this;
    }

    public ReferenceFieldBuilder referencedField(SimpleFieldBuilder referencedField) {
        this.referencedField = referencedField;
        return this;
    }

    public ReferenceFieldBuilder foreignKeyInfo(SimpleFieldBuilder foreignKeyInfo) {
        this.foreignKeyInfo.add(foreignKeyInfo);
        return this;
    }

    public ReferenceFieldBuilder fkIntegrity(boolean enable) {
        this.fkIntegrity = true;
        return this;
    }

    public ReferenceFieldBuilder allowIntegrityOverride(boolean enable) {
        this.allowFkIntegrityOverride = true;
        return this;
    }

    public ReferenceFieldBuilder filter(String foreignKeyFilter) {
        this.foreignKeyFilter = foreignKeyFilter;
        return this;
    }

    @Override
    protected FieldMetadata build() {
        if (referencedType == null) {
            throw new IllegalStateException("No referenced type for FK.");
        }
        if (referencedField == null) {
            throw new IllegalStateException("No referenced field for FK.");
        }
        ComplexTypeMetadata referencedType = this.referencedType.build();
        FieldMetadata referencedField = this.referencedField.build();
        List<FieldMetadata> foreignKeyInfoFields = new ArrayList<FieldMetadata>(foreignKeyInfo.size());
        for (SimpleFieldBuilder builder : foreignKeyInfo) {
            foreignKeyInfoFields.add(builder.build());
        }
        return new ReferenceFieldMetadata(null, isKey, isMany, isMandatory, name, referencedType, referencedField,
                foreignKeyInfoFields, fkIntegrity, allowFkIntegrityOverride, fieldType.build(), allowWriteUsers, hideUsers,
                workflowAccessRights, foreignKeyFilter);
    }

    @Override
    public ReferenceFieldBuilder fk() {
        return null;
    }

    @Override
    public EnumerationFieldBuilder enumeration() {
        return null;
    }
}
