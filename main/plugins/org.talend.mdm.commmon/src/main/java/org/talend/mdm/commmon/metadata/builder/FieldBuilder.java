package org.talend.mdm.commmon.metadata.builder;

import com.google.common.base.Predicate;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.MetadataExtensible;

import javax.xml.XMLConstants;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public abstract class FieldBuilder implements Predicate<ComplexTypeMetadata>, MetadataExtensible {

    protected String name;

    protected boolean isKey;

    protected boolean isMany;

    protected boolean isMandatory;

    protected TypeBuilder fieldType;

    protected List<String> allowWriteUsers;

    protected List<String> hideUsers;

    protected List<String> workflowAccessRights;

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

    public static ContainedFieldBuilder field(String name, TypeBuilder typeBuilder) {
        ContainedFieldBuilder builder = new ContainedFieldBuilder();
        builder.name = name;
        builder.fieldType = typeBuilder;
        return builder;
    }

    public FieldBuilder name(String name, Locale locale) {
        fieldNameLocale.put(locale, name);
        return this;
    }

    public FieldBuilder as(TypeBuilder type) {
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
}
