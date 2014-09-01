package org.talend.mdm.commmon.metadata.builder;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadataImpl;
import org.talend.mdm.commmon.metadata.FieldMetadata;

/**
 *
 */
public class TypeBuilder extends Builder<ComplexTypeMetadata> {

    private final String namespace;

    private final String name;

    private ComplexTypeMetadata type;

    private Builder<FieldMetadata>[] fields;

    public TypeBuilder(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    @Override
    public ComplexTypeMetadata build() {
        type = new ComplexTypeMetadataImpl(namespace, name, true);
        for (Builder<FieldMetadata> field : fields) {
            type.addField(field.build());
        }
        return type;
    }

    public TypeBuilder with(Builder<FieldMetadata>... fields) {
        this.fields = fields;
        return this;
    }

    public Builder<FieldMetadata> field(String name) {
        return new FieldBuilder(name);
    }
}
