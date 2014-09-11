package org.talend.mdm.commmon.metadata.builder;

import java.util.*;

import javax.xml.XMLConstants;

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.metadata.*;
import org.talend.mdm.commmon.util.core.ICoreConstants;

/**
 *
 */
public class TypeBuilder extends Loop<ComplexTypeMetadata> implements MetadataExtensible {

    private static int anonymousCounter = 0;

    private final String name;

    private final Map<String, Object> data = new HashMap<String, Object>();

    private final List<TypeBuilder> superTypes = new LinkedList<TypeBuilder>();

    private final List<FieldBuilder> pkInfoFields = new LinkedList<FieldBuilder>();

    private final Map<Locale, String> localizedNames = new HashMap<Locale, String>();

    private String namespace;

    private ComplexTypeMetadata type;

    // If write is not allowed for everyone, at least add "administration".
    private List<String> writeRoles = Collections.singletonList(ICoreConstants.ADMIN_PERMISSION);

    private List<String> denyCreate = Collections.emptyList();

    private List<String> hideUsers = Collections.emptyList();

    private List<String> physicalDelete = Collections.emptyList();

    private List<String> logicalDelete = Collections.emptyList();

    private String schematron = StringUtils.EMPTY;

    private List<FieldMetadata> lookupFields = Collections.emptyList();

    private List<String> workflowAccessRights = Collections.emptyList();

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
        return type(StringUtils.EMPTY, MetadataRepository.ANONYMOUS_PREFIX + anonymousCounter++).reusable();
    }

    public TypeBuilder write(String role) {
        writeRoles = new LinkedList<String>();
        if (!writeRoles.contains(role)) {
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
            SimpleTypeMetadata simpleTypeMetadata = new SimpleTypeMetadata(namespace, name);
            for (TypeBuilder superType : superTypes) {
                simpleTypeMetadata.addSuperType(superType.build());
            }
            return (T) simpleTypeMetadata;
        } else {
            List<FieldMetadata> primaryKeyInfo = new ArrayList<FieldMetadata>(pkInfoFields.size());
            for (FieldBuilder field : pkInfoFields) {
                primaryKeyInfo.add(field.build());
            }
            type = new ComplexTypeMetadataImpl(namespace, name, writeRoles, denyCreate, hideUsers, physicalDelete, logicalDelete,
                    schematron, primaryKeyInfo, lookupFields, true, workflowAccessRights);
            apply(type);
            for (TypeBuilder superType : superTypes) {
                type.addSuperType(superType.build());
            }
            for (Map.Entry<Locale, String> entry : localizedNames.entrySet()) {
                type.registerName(entry.getKey(), entry.getValue());
            }
            return (T) type;
        }
    }

    public TypeBuilder name(Locale locale, String name) {
        localizedNames.put(locale, name);
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
        superTypes.add(type);
        return this;
    }

    public TypeBuilder length(int maxLength) {
        return this;
    }

    public TypeBuilder reusable() {
        return this;
    }

    public TypeBuilder simple() {
        namespace = XMLConstants.W3C_XML_SCHEMA_NS_URI;
        return this;
    }

    public TypeBuilder pkInfo(FieldBuilder fieldBuilder) {
        pkInfoFields.add(fieldBuilder);
        return this;
    }

    public TypeBuilder validationRule(String validationRule) {
        return this;
    }

    public TypeBuilder lookupField(SimpleFieldBuilder field) {
        return this;
    }

    public TypeBuilder hide(String role) {
        return this;
    }

    public TypeBuilder denyCreate(String role) {
        return this;
    }

    public TypeBuilder denyLogicalDelete(String role) {
        return this;
    }

    public TypeBuilder denyPhysicalDelete(String role) {
        return this;
    }

    public TypeBuilder workflow(String role) {
        return this;
    }
}
