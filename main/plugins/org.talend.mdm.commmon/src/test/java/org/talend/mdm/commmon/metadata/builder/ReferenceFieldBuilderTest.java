package org.talend.mdm.commmon.metadata.builder;

import static org.talend.mdm.commmon.metadata.builder.FieldBuilder.field;
import static org.talend.mdm.commmon.metadata.builder.TypeBuilder.type;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import org.talend.mdm.commmon.metadata.Types;

public class ReferenceFieldBuilderTest extends TestCase {

    public void testReferencedType() throws Exception {
        FieldMetadata fk = field("fk", Types.STRING).fk()
                .referencedType(type("Reference"))
                .referencedField(field("id", Types.STRING))
                .build();
        assertTrue(fk instanceof ReferenceFieldMetadata);
        assertEquals("Reference", ((ReferenceFieldMetadata) fk).getReferencedType().getName());
    }

    public void testReferencedField() throws Exception {
        FieldMetadata fk = field("fk", Types.STRING).fk()
                .referencedType(type("Reference"))
                .referencedField(field("id", Types.STRING))
                .build();
        assertTrue(fk instanceof ReferenceFieldMetadata);
        assertEquals("id", ((ReferenceFieldMetadata) fk).getReferencedField().getName());
    }

    public void testForeignKeyInfo() throws Exception {
        ReferenceFieldBuilder builder = field("fk", Types.STRING).fk()
                .referencedType(type("Reference"))
                .referencedField(field("id", Types.STRING))
                .foreignKeyInfo(field("name", Types.STRING));
        FieldMetadata fk = builder.build();
        assertTrue(fk instanceof ReferenceFieldMetadata);
        assertEquals(1, ((ReferenceFieldMetadata) fk).getForeignKeyInfoFields().size());
        assertEquals("name", ((ReferenceFieldMetadata) fk).getForeignKeyInfoFields().get(0).getName());
        // Test append
        fk = builder.foreignKeyInfo(field("name2", Types.STRING)).build();
        assertTrue(fk instanceof ReferenceFieldMetadata);
        assertEquals(2, ((ReferenceFieldMetadata) fk).getForeignKeyInfoFields().size());
        assertEquals("name", ((ReferenceFieldMetadata) fk).getForeignKeyInfoFields().get(0).getName());
        assertEquals("name2", ((ReferenceFieldMetadata) fk).getForeignKeyInfoFields().get(1).getName());
    }

    public void testFkIntegrity() throws Exception {
        FieldMetadata fk = field("fk", Types.STRING).fk()
                .referencedType(type("Reference"))
                .referencedField(field("id", Types.STRING))
                .fkIntegrity(true).build();
        assertTrue(fk instanceof ReferenceFieldMetadata);
        assertTrue(((ReferenceFieldMetadata) fk).isFKIntegrity());
        assertFalse(((ReferenceFieldMetadata) fk).allowFKIntegrityOverride());
    }

    public void testAllowIntegrityOverride() throws Exception {
        FieldMetadata fk = field("fk", Types.STRING).fk()
                .referencedType(type("Reference"))
                .referencedField(field("id", Types.STRING))
                .allowIntegrityOverride(true).build();
        assertTrue(fk instanceof ReferenceFieldMetadata);
        assertFalse(((ReferenceFieldMetadata) fk).isFKIntegrity());
        assertTrue(((ReferenceFieldMetadata) fk).allowFKIntegrityOverride());
    }

    public void testFilter() throws Exception {
        FieldMetadata fk = field("fk", Types.STRING).fk()
                .referencedType(type("Reference"))
                .referencedField(field("id", Types.STRING))
                .filter("value/value").build();
        assertTrue(fk instanceof ReferenceFieldMetadata);
        assertEquals("value/value", ((ReferenceFieldMetadata) fk).getForeignKeyFilter());
    }
}
