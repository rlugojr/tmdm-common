package org.talend.mdm.commmon.metadata.builder;

import com.google.common.base.Predicate;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataExtensible;

import javax.xml.XMLConstants;
import java.util.*;

/**
 *
 */
public abstract class FieldBuilder implements Predicate<ComplexTypeMetadata>, MetadataExtensible {

    protected String name;

    protected boolean isKey;

    protected boolean isMany;

    protected boolean isMandatory;

    protected TypeBuilder fieldType;

    protected List<String> allowWriteUsers = Collections.emptyList();

    protected List<String> hideUsers = Collections.emptyList();

    protected List<String> workflowAccessRights= Collections.emptyList();

    protected final Map<Locale, String> fieldNameLocale = new HashMap<Locale, String>();

    public FieldBuilder() {
    }

    public static SimpleFieldBuilder field(String name) {
        SimpleFieldBuilder builder = new SimpleFieldBuilder();
        builder.name = name;
        return builder;
    }

    public static SimpleFieldBuilder field(String name, String simpleTypeName) {
        SimpleFieldBuilder builder = new SimpleFieldBuilder();
        builder.name = name;
        builder.fieldType = TypeBuilder.type(XMLConstants.W3C_XML_SCHEMA_NS_URI, simpleTypeName);
        return builder;
    }

    public static SimpleFieldBuilder field(String name, TypeBuilder typeBuilder) {
        SimpleFieldBuilder builder = new SimpleFieldBuilder();
        builder.name = name;
        builder.fieldType = typeBuilder;
        return builder;
    }

    public FieldBuilder name(Locale locale, String name) {
        fieldNameLocale.put(locale, name);
        return this;
    }

    public FieldBuilder of(TypeBuilder type) {
        fieldType = type;
        return this;
    }

    @Override
    public boolean apply(ComplexTypeMetadata complexTypeMetadata) {
        if (complexTypeMetadata == null) {
            throw new NullPointerException("Type cannot be null.");
        }
        FieldMetadata newField = build();
        newField.setContainingType(complexTypeMetadata);
        complexTypeMetadata.addField(newField);
        return false;
    }

    protected abstract FieldMetadata build();

    public FieldBuilder pk() {
        isKey = true;
        return this;
    }

    public FieldBuilder many() {
        isMany = true;
        return this;
    }

    public FieldBuilder mandatory() {
        isMandatory = true;
        return this;
    }

    @Override
    public void setData(String key, Object data) {

    }

    @Override
    public <X> X getData(String key) {
        return null;
    }

    public abstract ReferenceFieldBuilder fk();

    public abstract EnumerationFieldBuilder enumeration();

    public FieldBuilder hide(String role) {
        return this;
    }

    public FieldBuilder write(String role) {
        return this;
    }

    public FieldBuilder denyCreate(String role) {
        return this;
    }

    public FieldBuilder denyLogicalDelete(String role) {
        return this;
    }

    public FieldBuilder denyPhysicalDelete(String role) {
        return this;
    }

    public FieldBuilder workflow(String role) {
        return this;
    }
}
