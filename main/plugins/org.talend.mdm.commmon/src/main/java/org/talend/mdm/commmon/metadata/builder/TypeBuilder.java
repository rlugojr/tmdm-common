package org.talend.mdm.commmon.metadata.builder;

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.*;
import org.talend.mdm.commmon.util.core.ICoreConstants;

import javax.xml.XMLConstants;
import java.util.*;

/**
 *
 */
public class TypeBuilder extends Loop<ComplexTypeMetadata> implements MetadataExtensible {

    private static final MetadataRepository SIMPLE_TYPE_REPOSITORY = new MetadataRepository();

    private static int anonymousCounter = 0;

    private final String namespace;

    private final String name;

    private ComplexTypeMetadata type;

    private final Map<String, Object> data = new HashMap<String, Object>();

    // If write is not allowed for everyone, at least add "administration".
    private List<String> writeRoles = Collections.singletonList(ICoreConstants.ADMIN_PERMISSION);

    private TypeBuilder(String namespace, String name) {
        this.namespace = namespace;
        this.name = name;
    }

    public static TypeBuilder type(String namespace, String name) {
        return new TypeBuilder(namespace, name);
    }

    public static TypeBuilder type(String name) {
        return type(StringUtils.EMPTY, name);
    }

    public static TypeBuilder anonymous() {
        return type(StringUtils.EMPTY, MetadataRepository.ANONYMOUS_PREFIX + anonymousCounter++);
    }

    public TypeBuilder write(String role) {
        writeRoles = new LinkedList<String>();
        if(!writeRoles.contains(role)) {
            writeRoles.add(role);
        }
        return this;
    }

    public TypeBuilder with(FieldBuilder... fieldBuilders) {
        Collections.addAll(predicates, fieldBuilders);
        return this;
    }

    public <T extends TypeMetadata> T build() {
        if (XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(namespace)) {
            return (T) SIMPLE_TYPE_REPOSITORY.getNonInstantiableType(namespace, name);
        } else {
            type = new ComplexTypeMetadataImpl(namespace, name, true);
            apply(type);
            return (T) type;
        }
    }

    public TypeBuilder name(Locale locale, String name) {
        // TODO
        return this;
    }

    @Override
    protected ComplexTypeMetadata getInput() {
        return type;
    }

    @Override
    public void setData(String key, Object data) {
        this.data.put(key, data);
    }

    @Override
    public <X> X getData(String key) {
        return (X) data.get(key);
    }

    public TypeBuilder inherits(TypeBuilder type) {
        return this;
    }

    public TypeBuilder maxLength(String maxLength) {
        return null;
    }
}
