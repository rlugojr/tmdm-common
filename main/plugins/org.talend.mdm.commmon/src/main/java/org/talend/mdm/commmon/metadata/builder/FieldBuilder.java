package org.talend.mdm.commmon.metadata.builder;

import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.TypeMetadata;

import java.util.List;

/**
 *
 */
public class FieldBuilder extends Builder<FieldMetadata> {

    private Builder<? extends FieldMetadata> inner;

    private FieldBuilder(Builder<? extends FieldMetadata> inner) {
        this.inner = inner;
    }

    public static FieldBuilder field(String name) {
        return new FieldBuilder(new SimpleFieldBuilder(name));
    }

    public FieldBuilder fk()

    @Override
    public FieldMetadata build() {
        return inner.build();
    }

    public FieldBuilder pk() {
        inner.pk();
        return this;
    }

    private static class SimpleFieldBuilder extends Builder<SimpleTypeFieldMetadata> {

        private final String name;

        private boolean isKey;

        private boolean isMany;

        private boolean isMandatory;

        private TypeMetadata fieldType;

        private List<String> allowWriteUsers;

        private List<String> hideUsers;

        private List<String> workflowAccessRights;

        public SimpleFieldBuilder(String name) {
            this.name = name;
        }

        @Override
        public SimpleTypeFieldMetadata build() {
            return new SimpleTypeFieldMetadata(null, isKey, isMany, isMandatory, name, fieldType, allowWriteUsers,
                    hideUsers, workflowAccessRights);
        }
    }
}
