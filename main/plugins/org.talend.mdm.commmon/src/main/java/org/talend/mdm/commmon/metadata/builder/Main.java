package org.talend.mdm.commmon.metadata.builder;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

import static org.talend.mdm.commmon.metadata.builder.FieldBuilder.field;

/**
 *
 */
public class Main {

    public static void main(String[] args) {
        TypeBuilder b1 = Builder.type("Test").with(field("id").pk(), field("test2"));
        ComplexTypeMetadata type = b1.build();

        Builder<ComplexTypeMetadata> b2 = Builder.type("Test", "Test");
    }
}
