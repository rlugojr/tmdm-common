package org.talend.mdm.commmon.metadata.builder;

import static org.talend.mdm.commmon.metadata.builder.FieldBuilder.field;
import static org.talend.mdm.commmon.metadata.builder.TypeBuilder.anonymous;

import junit.framework.TestCase;

import org.talend.mdm.commmon.metadata.ContainedTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.Types;

import java.util.Locale;

public class FieldBuilderTest extends TestCase {

    public void testField() throws Exception {
        FieldMetadata fieldMetadata = field("test", Types.STRING).build();
        assertEquals("test", fieldMetadata.getName());
        assertFalse(fieldMetadata.isKey());
        assertFalse(fieldMetadata.isMany());
        assertFalse(fieldMetadata.isMandatory());
        assertTrue(fieldMetadata instanceof SimpleTypeFieldMetadata);
    }

    public void testLocalizedField() throws Exception {
        FieldMetadata fieldMetadata = field("test", Types.STRING).name("test_fr", Locale.FRANCE).build();
        assertEquals("test", fieldMetadata.getName());
        assertEquals("test_fr", fieldMetadata.getName(Locale.FRANCE));
        assertEquals("test", fieldMetadata.getName(Locale.ENGLISH));
        assertTrue(fieldMetadata instanceof SimpleTypeFieldMetadata);
    }

    public void testField1() throws Exception {
        FieldMetadata fieldMetadata = field("test", anonymous()).build();
        assertEquals("test", fieldMetadata.getName());
        assertFalse(fieldMetadata.isKey());
        assertFalse(fieldMetadata.isMany());
        assertFalse(fieldMetadata.isMandatory());
        assertTrue(fieldMetadata instanceof ContainedTypeFieldMetadata);
    }

    public void testLocalizedField1() throws Exception {
        FieldMetadata fieldMetadata = field("test", anonymous()).name("test_fr", Locale.FRANCE).build();
        assertEquals("test", fieldMetadata.getName());
        assertEquals("test_fr", fieldMetadata.getName(Locale.FRANCE));
        assertEquals("test", fieldMetadata.getName(Locale.ENGLISH));
        assertTrue(fieldMetadata instanceof ContainedTypeFieldMetadata);
    }

    public void testPk() throws Exception {
        FieldMetadata fieldMetadata = field("test", Types.STRING).pk().build();
        assertEquals("test", fieldMetadata.getName());
        assertTrue(fieldMetadata.isKey());
        assertFalse(fieldMetadata.isMany());
        assertFalse(fieldMetadata.isMandatory());
        assertTrue(fieldMetadata instanceof SimpleTypeFieldMetadata);
    }

    public void testMany() throws Exception {
        FieldMetadata fieldMetadata = field("test", Types.STRING).many().build();
        assertEquals("test", fieldMetadata.getName());
        assertFalse(fieldMetadata.isKey());
        assertTrue(fieldMetadata.isMany());
        assertFalse(fieldMetadata.isMandatory());
        assertTrue(fieldMetadata instanceof SimpleTypeFieldMetadata);
    }

    public void testMandatory() throws Exception {
        FieldMetadata fieldMetadata = field("test", Types.STRING).mandatory().build();
        assertEquals("test", fieldMetadata.getName());
        assertFalse(fieldMetadata.isKey());
        assertFalse(fieldMetadata.isMany());
        assertTrue(fieldMetadata.isMandatory());
        assertTrue(fieldMetadata instanceof SimpleTypeFieldMetadata);
    }
}
