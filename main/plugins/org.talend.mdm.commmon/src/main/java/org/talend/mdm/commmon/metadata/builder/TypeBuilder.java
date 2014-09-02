package org.talend.mdm.commmon.metadata.builder;

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadataImpl;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.TypeMetadata;

import javax.xml.XMLConstants;
import java.util.Collections;

/**
 *
 */
public class TypeBuilder extends Loop<ComplexTypeMetadata> {

    private static final MetadataRepository SIMPLE_TYPE_REPOSITORY = new MetadataRepository();

    private static int anonymousCounter = 0;

    private final String namespace;

    private final String name;

    private ComplexTypeMetadata type;

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

    @Override
    protected ComplexTypeMetadata getInput() {
        return type;
    }
}
