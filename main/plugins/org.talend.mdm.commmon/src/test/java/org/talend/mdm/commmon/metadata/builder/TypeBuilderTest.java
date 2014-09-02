package org.talend.mdm.commmon.metadata.builder;

import static org.talend.mdm.commmon.metadata.builder.FieldBuilder.field;
import static org.talend.mdm.commmon.metadata.builder.TypeBuilder.anonymous;
import static org.talend.mdm.commmon.metadata.builder.TypeBuilder.type;

import java.util.Iterator;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.*;

public class TypeBuilderTest extends TestCase {

    public void test1() throws Exception {
        ComplexTypeMetadata type = type("Product")
            .with(
                    field("id", Types.STRING).pk(),
                    field("name", Types.STRING),
                    field("features", anonymous().with(
                            field("colors", anonymous().with(
                                    field("color", Types.STRING).many()
                            )),
                            field("sizes", anonymous().with(
                                    field("size", Types.STRING).many()
                            ))
                    ))
            )
            .build();
        assertEquals("Product", type.getName());
        assertEquals(3, type.getFields().size());
        // Field assertions
        Iterator<FieldMetadata> iterator = type.getFields().iterator();
        FieldMetadata idField = iterator.next();
        assertEquals("id", idField.getName());
        assertTrue(idField.isKey());
        FieldMetadata testField = iterator.next();
        assertEquals("name", testField.getName());
        FieldMetadata featureField = iterator.next();
        assertEquals("features", featureField.getName());
        assertTrue(featureField instanceof ContainedTypeFieldMetadata);
        assertTrue(type.hasField("features/colors/color"));
        assertTrue(type.getField("features/colors/color").isMany());
        assertTrue(type.hasField("features/sizes/size"));
        assertTrue(type.getField("features/sizes/size").isMany());
    }

    public void testType() throws Exception {
        ComplexTypeMetadata type = type("Product").build();
        assertEquals("Product", type.getName());
    }

    public void testAnonymous() throws Exception {
        ComplexTypeMetadata type = anonymous().build();
        assertTrue(type.getName().startsWith(MetadataRepository.ANONYMOUS_PREFIX));
    }

    public void testWith() throws Exception {
        ComplexTypeMetadata type = type("Product")
                    .with(
                            field("id", Types.STRING)
                    ).build();
        assertEquals(1, type.getFields().size());
        assertEquals("id", type.getFields().iterator().next().getName());
    }
}
