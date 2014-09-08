/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.*;
import org.eclipse.xsd.util.XSDParser;
import org.talend.mdm.commmon.metadata.annotation.*;
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.SimpleFieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;
import org.talend.mdm.commmon.metadata.validation.ValidationFactory;
import org.talend.mdm.commmon.metadata.xsd.XSDVisitor;
import org.talend.mdm.commmon.metadata.xsd.XmlSchemaWalker;

import javax.xml.XMLConstants;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

import static org.talend.mdm.commmon.metadata.builder.TypeBuilder.anonymous;
import static org.talend.mdm.commmon.metadata.builder.TypeBuilder.type;

/**
 *
 */
public class MetadataRepository implements MetadataVisitable, XSDVisitor, Serializable {

    private static final long serialVersionUID = 239525645052486072L;

    public static final String COMPLEX_TYPE_NAME = "metadata.complex.type.name"; //$NON-NLS-1$

    public static final String DATA_MAX_LENGTH = "metadata.data.length"; //$NON-NLS-1$

    public static final String DATA_ZIPPED = "metadata.zipped"; //$NON-NLS-1$

    public static final String XSD_LINE_NUMBER = "metadata.xsd.line"; //$NON-NLS-1$

    public static final String XSD_COLUMN_NUMBER = "metadata.xsd.column"; //$NON-NLS-1$

    public static final String XSD_DOM_ELEMENT = "metadata.xsd.dom.element"; //$NON-NLS-1$

    public static final String ANONYMOUS_PREFIX = "X_ANONYMOUS"; //$NON-NLS-1$

    private static final Logger LOGGER = Logger.getLogger(MetadataRepository.class);

    private final Map<XSDTypeDefinition, List<ComplexTypeMetadata>> entityTypeUsage = new HashMap<XSDTypeDefinition, List<ComplexTypeMetadata>>() {

        @Override
        public List<ComplexTypeMetadata> get(Object key) {
            List<ComplexTypeMetadata> types = super.get(key);
            if (types == null) {
                types = new LinkedList<ComplexTypeMetadata>();
                super.put((XSDTypeDefinition) key, types);
            }
            return types;
        }
    };

    private final static List<XmlSchemaAnnotationProcessor> XML_ANNOTATIONS_PROCESSORS = Arrays.asList(new ForeignKeyProcessor(),
            new UserAccessProcessor(), new SchematronProcessor(), new PrimaryKeyInfoProcessor(), new LookupFieldProcessor(),
            new LabelAnnotationProcessor());

    private final static String USER_NAMESPACE = StringUtils.EMPTY;

    // Keep a version of types that doesn't change from one model to another
    private final static MetadataRepository commonTypes = new MetadataRepository();

    private final Map<String, Map<String, TypeMetadata>> entityTypes = new HashMap<String, Map<String, TypeMetadata>>();

    private final Map<String, Map<String, TypeMetadata>> nonInstantiableTypes = new HashMap<String, Map<String, TypeMetadata>>();

    private final Stack<TypeBuilder> currentTypeStack = new Stack<TypeBuilder>();

    private String targetNamespace;

    static {
        // Load XML Schema types
        InputStream xmlSchemaDef = MetadataRepository.class.getResourceAsStream("XMLSchema.xsd"); //$NON-NLS-1$
        if (xmlSchemaDef == null) {
            throw new IllegalStateException("Could not find XML schema definition.");
        }
        commonTypes.load(xmlSchemaDef, NoOpValidationHandler.INSTANCE);
        // TMDM-4444: Adds standard Talend types such as UUID.
        InputStream internalTypes = MetadataRepository.class.getResourceAsStream("talend_types.xsd"); //$NON-NLS-1$
        if (internalTypes == null) {
            throw new IllegalStateException("Could not find internal type data model.");
        }
        commonTypes.load(internalTypes, NoOpValidationHandler.INSTANCE);
        // Prevent further modifications on common types
        for (Map.Entry<String, Map<String, TypeMetadata>> entry : commonTypes.nonInstantiableTypes.entrySet()) {
            commonTypes.nonInstantiableTypes.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
        }
        for (Map.Entry<String, Map<String, TypeMetadata>> entry : commonTypes.entityTypes.entrySet()) {
            if (entry.getValue() != null) {
                commonTypes.entityTypes.put(entry.getKey(), Collections.unmodifiableMap(entry.getValue()));
            }
        }
    }

    public MetadataRepository() {
        if (commonTypes != null) {
            for (Map.Entry<String, Map<String, TypeMetadata>> entry : commonTypes.nonInstantiableTypes.entrySet()) {
                this.nonInstantiableTypes.put(entry.getKey(), new TreeMap<String, TypeMetadata>(entry.getValue()));
            }
            for (Map.Entry<String, Map<String, TypeMetadata>> entry : commonTypes.entityTypes.entrySet()) {
                if (entry.getValue() != null) {
                    this.entityTypes.put(entry.getKey(), new TreeMap<String, TypeMetadata>(entry.getValue()));
                }
            }
        }
    }

    public TypeMetadata getType(String name) {
        return getType(USER_NAMESPACE, name);
    }

    public String getUserNamespace() {
        return USER_NAMESPACE;
    }

    public ComplexTypeMetadata getComplexType(String typeName) {
        if (typeName == null) {
            throw new IllegalArgumentException("Type cannot be null.");
        }
        try {
            return (ComplexTypeMetadata) getType(USER_NAMESPACE, typeName.trim());
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Type named '" + typeName + "' is not a complex type.");
        }
    }

    public TypeMetadata getType(String nameSpace, String name) {
        if (nameSpace == null) {
            nameSpace = StringUtils.EMPTY;
        }
        Map<String, TypeMetadata> nameSpaceTypes = entityTypes.get(nameSpace);
        if (nameSpaceTypes == null) {
            return null;
        }
        return nameSpaceTypes.get(name.trim());
    }

    /**
     * @return Returns only {@link ComplexTypeMetadata} types defined in the data model by the MDM user (no types
     * potentially defined in other name spaces such as the XML schema's one).
     */
    public Collection<ComplexTypeMetadata> getUserComplexTypes() {
        List<ComplexTypeMetadata> complexTypes = new LinkedList<ComplexTypeMetadata>();
        // User types are all located in the default (empty) name space.
        Map<String, TypeMetadata> userNamespace = entityTypes.get(USER_NAMESPACE);
        if (userNamespace == null) {
            return Collections.emptyList();
        }
        Collection<TypeMetadata> namespaceTypes = userNamespace.values();
        for (TypeMetadata namespaceType : namespaceTypes) {
            if (namespaceType instanceof ComplexTypeMetadata) {
                complexTypes.add((ComplexTypeMetadata) namespaceType);
            }
        }
        return complexTypes;
    }

    public Collection<TypeMetadata> getTypes() {
        List<TypeMetadata> allTypes = new LinkedList<TypeMetadata>();
        Collection<Map<String, TypeMetadata>> nameSpaces = entityTypes.values();
        for (Map<String, TypeMetadata> nameSpace : nameSpaces) {
            allTypes.addAll(nameSpace.values());
        }
        nameSpaces = nonInstantiableTypes.values();
        for (Map<String, TypeMetadata> nameSpace : nameSpaces) {
            allTypes.addAll(nameSpace.values());
        }
        return allTypes;
    }

    public TypeMetadata getNonInstantiableType(String namespace, String typeName) {
        if (namespace == null) {
            namespace = StringUtils.EMPTY;
        }
        Map<String, TypeMetadata> map = nonInstantiableTypes.get(namespace);
        if (map != null) {
            return map.get(typeName.trim());
        }
        return null;
    }

    public List<ComplexTypeMetadata> getNonInstantiableTypes() {
        Map<String, TypeMetadata> map = nonInstantiableTypes.get(USER_NAMESPACE);
        List<ComplexTypeMetadata> nonInstantiableTypes = new LinkedList<ComplexTypeMetadata>();
        if (map != null) {
            for (TypeMetadata typeMetadata : map.values()) {
                if (typeMetadata instanceof ComplexTypeMetadata) {
                    nonInstantiableTypes.add((ComplexTypeMetadata) typeMetadata);
                }
            }
        }
        return nonInstantiableTypes;
    }

    public void load(InputStream inputStream) {
        load(inputStream, getValidationHandler());
    }

    protected ValidationHandler getValidationHandler() {
        return new DefaultValidationHandler();
    }

    public void load(InputStream inputStream, ValidationHandler handler) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream can not be null.");
        }
        // Validates data model using shared studio / server classes
        // Load user defined data model now
        Map<String, Object> options = new HashMap<String, Object>();
        options.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
        XSDParser parse = new XSDParser(options);
        parse.parse(inputStream);
        XSDSchema schema = parse.getSchema();
        if (schema == null) {
            throw new IllegalStateException("No schema parsed from input (make sure stream contains a data model).");
        }
        schema.validate();
        EList<XSDDiagnostic> diagnostics = schema.getDiagnostics();
        for (XSDDiagnostic diagnostic : diagnostics) {
            XSDDiagnosticSeverity severity = diagnostic.getSeverity();
            if (XSDDiagnosticSeverity.ERROR_LITERAL.equals(severity)) {
                handler.error((TypeMetadata) null, "XSD validation error: " + diagnostic.getMessage(), null, -1, -1,
                        ValidationError.XML_SCHEMA);
            } else if (XSDDiagnosticSeverity.WARNING_LITERAL.equals(severity)) {
                handler.error((TypeMetadata) null, "XSD validation warning: " + diagnostic.getMessage(), null, -1, -1,
                        ValidationError.XML_SCHEMA);
            }
        }
        XmlSchemaWalker.walk(schema, this);
        // TMDM-4876 Additional processing for entity inheritance
        resolveAdditionalSuperTypes(this);
        // "Freeze" all types (ensure all soft references now point to actual types in the repository).
        nonInstantiableTypes.put(getUserNamespace(), freezeTypes(nonInstantiableTypes.get(getUserNamespace())));
        // "Freeze" all reusable type usages in the data model.
        freezeUsages();
        entityTypes.put(getUserNamespace(), freezeTypes(entityTypes.get(getUserNamespace())));
        // Validate types
        for (TypeMetadata type : getUserComplexTypes()) {
            if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(type.getNamespace())) {
                type.validate(handler);
            }
        }
        for (TypeMetadata type : getNonInstantiableTypes()) {
            if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(type.getNamespace())) {
                type.validate(handler);
            }
        }
        ValidationFactory.getRule(this).perform(handler); // Perform data model-scoped validation (e.g. cycles).
        handler.end();
        if (handler.getErrorCount() != 0) {
            LOGGER.error("Could not parse data model (" + handler.getErrorCount() + " error(s) found).");
        }
    }

    protected void freezeUsages() {
        for (List<ComplexTypeMetadata> entityTypes : entityTypeUsage.values()) {
            for (ComplexTypeMetadata entityType : entityTypes) {
                entityType.accept(new DefaultMetadataVisitor<Void>() {
                    @Override
                    public Void visit(ContainedComplexTypeMetadata containedType) {
                        containedType.getContainedType().declareUsage(containedType);
                        containedType.finalizeUsage();
                        for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
                            subType.accept(this);
                        }
                        return super.visit(containedType);
                    }
                });
            }
        }
    }

    private Map<String, TypeMetadata> freezeTypes(Map<String, TypeMetadata> typesToFreeze) {
        if (typesToFreeze == null) {
            return null;
        }
        Map<String, TypeMetadata> workingTypes = new TreeMap<String, TypeMetadata>(typesToFreeze);
        for (TypeMetadata type : typesToFreeze.values()) {
            workingTypes.put(type.getName(), type.freeze());
        }
        return workingTypes;
    }

    private static void resolveAdditionalSuperTypes(MetadataRepository repository) {
        Collection<ComplexTypeMetadata> types = repository.getUserComplexTypes();
        for (TypeMetadata current : types) {
            String complexTypeName = current.getData(COMPLEX_TYPE_NAME);
            if (complexTypeName != null) {
                TypeMetadata nonInstantiableType = repository.getNonInstantiableType(USER_NAMESPACE, complexTypeName);
                if (!nonInstantiableType.getSuperTypes().isEmpty() && !nonInstantiableType.isFrozen()) {
                    TypeMetadata superType = nonInstantiableType.getSuperTypes().iterator().next();
                    ComplexTypeMetadata entitySuperType = null;
                    int entitySuperTypeCandidateCount = 0;
                    for (TypeMetadata entity : types) {
                        if (superType.getName().equals(entity.getData(COMPLEX_TYPE_NAME))) {
                            entitySuperType = (ComplexTypeMetadata) entity;
                            entitySuperTypeCandidateCount++;
                        }
                    }
                    if (entitySuperTypeCandidateCount > 1) {
                        // TMDM-7583: Found multiple entity types as candidate for entity super types, consider type will only
                        // inherit from reusable and won't have entity super type.
                        LOGGER.warn("Type '" + current.getName()
                                + "' uses multiple inheritance (following reusable type usages), consider inheritance from '"
                                + superType.getName() + "' as a reuse.");
                    } else if (entitySuperType != null) {
                        current.addSuperType(entitySuperType);
                    }
                }
            }
        }
    }

    @Override
    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public void addTypeMetadata(TypeMetadata typeMetadata) {
        String namespace = typeMetadata.getNamespace();
        if (typeMetadata.isInstantiable()) {
            registerType(typeMetadata, namespace, entityTypes);
        } else {
            registerType(typeMetadata, namespace, nonInstantiableTypes);
        }
    }

    private static void registerType(TypeMetadata typeMetadata, String namespace, Map<String, Map<String, TypeMetadata>> typeMap) {
        if (namespace == null) {
            namespace = StringUtils.EMPTY;
        }
        Map<String, TypeMetadata> nameSpace = typeMap.get(namespace);
        if (nameSpace == null) {
            nameSpace = new TreeMap<String, TypeMetadata>();
            typeMap.put(namespace, nameSpace);
        }
        typeMap.get(namespace).put(typeMetadata.getName(), typeMetadata);
    }

    public void close() {
        entityTypes.clear();
        nonInstantiableTypes.clear();
    }

    public Collection<TypeMetadata> getInstantiableTypes() {
        return entityTypes.get(USER_NAMESPACE).values();
    }

    @Override
    public void visitSchema(XSDSchema xmlSchema) {
        targetNamespace = xmlSchema.getTargetNamespace() == null ? USER_NAMESPACE : xmlSchema.getTargetNamespace();
        if (!currentTypeStack.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                StringBuilder builder = new StringBuilder();
                for (ComplexTypeMetadata unprocessedType : currentTypeStack) {
                    builder.append(unprocessedType.getName()).append(" "); //$NON-NLS-1$
                }
                LOGGER.debug("Unprocessed types: " + builder);
            }
            // At the end of data model parsing, we expect all entity types to be processed.
            throw new IllegalStateException(currentTypeStack.size() + " types have not been correctly parsed.");
        }
    }

    @Override
    public void visitSimpleType(XSDSimpleTypeDefinition type) {
        String typeName = type.getName();
        TypeMetadata typeMetadata = getNonInstantiableType(targetNamespace, typeName);
        if (typeMetadata == null) {
            typeMetadata = new SimpleTypeMetadata(targetNamespace, typeName);
        }
        List<TypeMetadata> superTypes = new LinkedList<TypeMetadata>();
        if (typeName == null) {
            // Anonymous simple type (expects this is a restriction of a simple type or fails).
            XSDSimpleTypeDefinition baseTypeDefinition = type.getBaseTypeDefinition();
            if (baseTypeDefinition != null) {
                typeName = baseTypeDefinition.getName();
            } else {
                throw new NotImplementedException("Support for " + type);
            }
        } else {
            // Simple type might inherit from other simple types (i.e. UUID from string).
            XSDSimpleTypeDefinition baseType = type.getBaseTypeDefinition();
            if (baseType != null && baseType.getName() != null) {
                superTypes.add(new SoftTypeRef(this, baseType.getTargetNamespace(), baseType.getName(), false));
                EList<XSDConstrainingFacet> facets = type.getFacetContents();
                for (XSDConstrainingFacet currentFacet : facets) {
                    if (currentFacet instanceof XSDMaxLengthFacet) {
                        typeMetadata.setData(MetadataRepository.DATA_MAX_LENGTH,
                                String.valueOf(((XSDMaxLengthFacet) currentFacet).getValue()));
                    } else if (currentFacet instanceof XSDLengthFacet) {
                        typeMetadata.setData(MetadataRepository.DATA_MAX_LENGTH,
                                String.valueOf(((XSDLengthFacet) currentFacet).getValue()));
                    } else if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Ignore simple type facet on type '" + typeName + "': " + currentFacet);
                    }
                }
            }
        }
        if (getNonInstantiableType(targetNamespace, typeName) == null) {
            for (TypeMetadata superType : superTypes) {
                typeMetadata.addSuperType(superType);
            }
            addTypeMetadata(typeMetadata);
        }
    }

    @Override
    public void visitComplexType(XSDComplexTypeDefinition type) {
        String typeName = type.getName();
        boolean isNonInstantiableType = currentTypeStack.isEmpty();
        if (isNonInstantiableType) {
            if (nonInstantiableTypes.get(getUserNamespace()) != null) {
                if (nonInstantiableTypes.get(getUserNamespace()).containsKey(typeName)) {
                    // Ignore another definition of type (already processed).
                    return;
                }
            }
            // There's no current 'entity' type being parsed, this is a complex type not to be used for entity but
            // might be referenced by others entities (for fields, inheritance...).
            ComplexTypeMetadata nonInstantiableType = new ComplexTypeMetadataImpl(targetNamespace, typeName, false);
            // Keep line and column of definition
            nonInstantiableType.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(type.getElement()));
            nonInstantiableType.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(type.getElement()));
            nonInstantiableType.setData(XSD_DOM_ELEMENT, type.getElement());
            addTypeMetadata(nonInstantiableType);
            currentTypeStack.push(nonInstantiableType);
            // If type is used, declare usage
            List<ComplexTypeMetadata> usages = entityTypeUsage.get(type);
            for (ComplexTypeMetadata usage : usages) {
                nonInstantiableType.declareUsage(usage);
            }
        } else {
            // Keep track of the complex type used for entity type (especially for inheritance).
            if (typeName != null) {
                currentTypeStack.peek().setData(MetadataRepository.COMPLEX_TYPE_NAME, typeName);
            }
        }
        XSDComplexTypeContent particle = type.getContent();
        if (particle instanceof XSDParticle) {
            XSDParticle currentParticle = (XSDParticle) particle;
            if (currentParticle.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) currentParticle.getTerm();
                EList<XSDParticle> particles = group.getContents();
                for (XSDParticle p : particles) {
                    XSDParticleContent particleContent = p.getContent();
                    XmlSchemaWalker.walk(particleContent, this);
                }
            }
        } else if (particle != null) {
            throw new IllegalArgumentException("Not supported XML Schema particle: " + particle.getClass().getName());
        }
        // Adds the type information about super types.
        XSDTypeDefinition contentModel = type.getBaseTypeDefinition();
        if (contentModel != null) {
            if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(contentModel.getTargetNamespace())
                    && !Types.ANY_TYPE.equals(contentModel.getName())) {
                SoftTypeRef superType = new SoftTypeRef(this, contentModel.getTargetNamespace(), contentModel.getName(), false);
                if (currentTypeStack.peek() instanceof ContainedComplexTypeMetadata) {
                    superType.declareUsage(currentTypeStack.peek());
                }
                currentTypeStack.peek().addSuperType(superType);
                particle = type.getContent();
                if (particle instanceof XSDParticle) {
                    XSDParticle currentParticle = (XSDParticle) particle;
                    if (currentParticle.getTerm() instanceof XSDModelGroup) {
                        XSDModelGroup group = (XSDModelGroup) currentParticle.getTerm();
                        EList<XSDParticle> particles = group.getContents();
                        for (XSDParticle p : particles) {
                            XSDParticleContent particleContent = p.getContent();
                            XmlSchemaWalker.walk(particleContent, this);
                        }
                    }
                } else if (particle != null) {
                    throw new IllegalArgumentException("Not supported XML Schema particle: " + particle.getClass().getName());
                }
            }
        }
        if (isNonInstantiableType) {
            currentTypeStack.pop();
        }
    }

    @Override
    public void visitElement(XSDElementDeclaration element) {
        if (currentTypeStack.isEmpty()) { // "top level" elements means new MDM entity type
            String typeName = element.getName();
            if (getComplexType(typeName) != null) { // Don't process twice type
                return;
            }
            // Id fields
            Map<String, XSDXPathDefinition> idFields = new LinkedHashMap<String, XSDXPathDefinition>();
            EList<XSDIdentityConstraintDefinition> constraints = element.getIdentityConstraintDefinitions();
            for (XSDIdentityConstraintDefinition constraint : constraints) {
                EList<XSDXPathDefinition> fields = constraint.getFields();
                for (XSDXPathDefinition field : fields) {
                    idFields.put(field.getValue(), field);
                }
            }
            TypeBuilder typeBuilder = type(targetNamespace, typeName);
            try {
                XSDAnnotation annotation = element.getAnnotation();
                for (XmlSchemaAnnotationProcessor processor : XML_ANNOTATIONS_PROCESSORS) {
                    processor.process(annotation, typeBuilder);
                }
            } catch (Exception e) {
                throw new RuntimeException("Annotation processing exception while parsing info for type '" + typeName + "'.",
                        e);
            }
            // Keep line and column of definition
            typeBuilder.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
            typeBuilder.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
            typeBuilder.setData(XSD_DOM_ELEMENT, element.getElement());
            ComplexTypeMetadata type = typeBuilder.build();
            addTypeMetadata(type);
            // Keep usage information
            entityTypeUsage.get(element.getType()).add(type);
            // Walk the fields
            currentTypeStack.push(type);
            {
                XmlSchemaWalker.walk(element.getType(), this);
            }
            currentTypeStack.pop();
            // Super types
            XSDElementDeclaration substitutionGroup = element.getSubstitutionGroupAffiliation();
            if (substitutionGroup != null && !XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(substitutionGroup.getTargetNamespace())
                    && !Types.ANY_TYPE.equals(substitutionGroup.getName())) {
                if (!substitutionGroup.getResolvedElementDeclaration().equals(element)) {
                    SoftTypeRef superType = new SoftTypeRef(this, substitutionGroup.getTargetNamespace(),
                            substitutionGroup.getName(), true);
                    type.addSuperType(superType);
                }
            }
            // Register keys (TMDM-4470).
            for (Map.Entry<String, XSDXPathDefinition> unresolvedId : idFields.entrySet()) {
                SoftIdFieldRef keyField = new SoftIdFieldRef(this, type.getName(), unresolvedId.getKey());
                // Keep line and column of definition
                keyField.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(unresolvedId.getValue().getElement()));
                keyField.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(unresolvedId.getValue().getElement()));
                keyField.setData(XSD_DOM_ELEMENT, unresolvedId.getValue().getElement());
                type.registerKey(keyField);
            }
            // TMDM-6264: An entity type without any key info is a element maybe referenced by others, but never an
            // entity.
            if (type.getKeyFields().isEmpty() && type.getSuperTypes().isEmpty()) {
                Map<String, TypeMetadata> userEntityTypes = entityTypes.get(getUserNamespace());
                if (userEntityTypes != null) {
                    userEntityTypes.remove(type.getName());
                }
            }
        } else { // Non "top level" elements means fields for the MDM entity type being parsed
            FieldBuilder fieldMetadata;
            int minOccurs = ((XSDParticle) element.getContainer()).getMinOccurs();
            int maxOccurs = ((XSDParticle) element.getContainer()).getMaxOccurs();
            if (element.isElementDeclarationReference()
                    && currentTypeStack.peek().getName().equals(element.getResolvedElementDeclaration().getName())) {
                return;
            }
            if (element.getResolvedElementDeclaration() != null
                    && element.getResolvedElementDeclaration().getTargetNamespace() == null) {
                fieldMetadata = createFieldMetadata(element.getResolvedElementDeclaration(), currentTypeStack.peek(), minOccurs,
                        maxOccurs);
            } else {
                fieldMetadata = createFieldMetadata(element, currentTypeStack.peek(), minOccurs, maxOccurs);
            }
            currentTypeStack.peek().with(fieldMetadata);
        }
    }

    private FieldBuilder createFieldMetadata(XSDElementDeclaration element, ComplexTypeMetadata containingType, int minOccurs,
            int maxOccurs) {
        String fieldName = element.getName();
        if (maxOccurs > 0 && minOccurs > maxOccurs) { // Eclipse XSD does not check this
            throw new IllegalArgumentException("Can not parse information on field '" + element.getQName() + "' of type '"
                    + containingType + "' (maxOccurs > minOccurs)");
        }
        SimpleFieldBuilder fieldBuilder = FieldBuilder.field(fieldName);
        try {
            XSDAnnotation annotation = element.getAnnotation();
            for (XmlSchemaAnnotationProcessor processor : XML_ANNOTATIONS_PROCESSORS) {
                processor.process(annotation, fieldBuilder);
            }
        } catch (Exception e) {
            throw new RuntimeException("Annotation processing exception while parsing info for field '" + fieldName
                    + "' in type '" + containingType.getName() + "'", e);
        }
        fieldBuilder.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
        fieldBuilder.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
        fieldBuilder.setData(XSD_DOM_ELEMENT, element.getElement());
        if (minOccurs > 0) {
            fieldBuilder.mandatory();
        }
        if (maxOccurs == -1 || maxOccurs > 1) {
            fieldBuilder.many();
        }
        XSDTypeDefinition schemaType = element.getType();
        if (schemaType instanceof XSDSimpleTypeDefinition) {
            XSDSimpleTypeDefinition simpleSchemaType = (XSDSimpleTypeDefinition) schemaType;
            XSDSimpleTypeDefinition content = simpleSchemaType.getBaseTypeDefinition();
            if (schemaType.getQName() != null) {
                fieldBuilder.as(type(schemaType.getTargetNamespace(), schemaType.getName()));
            } else {
                // Null QNames may happen for anonymous types extending other types.
                TypeBuilder typeBuilder = anonymous();
                if (content != null) {
                    typeBuilder.inherits(type(content.getTargetNamespace(), content.getName()));
                }
                fieldBuilder.as(typeBuilder);
                EList<XSDConstrainingFacet> facets = simpleSchemaType.getFacetContents();
                for (XSDConstrainingFacet currentFacet : facets) {
                    if (currentFacet instanceof XSDMaxLengthFacet) {
                        typeBuilder.maxLength(String.valueOf(((XSDMaxLengthFacet) currentFacet).getValue()));
                    } else if (LOGGER.isTraceEnabled()) {
                        LOGGER.trace("Ignore simple type facet on type '" + fieldName + "': " + currentFacet);
                    }
                }
            }
            if (content != null) {
                if (content.getFacets().size() > 0) {
                    for (int i = 0; i < content.getFacets().size(); i++) {
                        XSDConstrainingFacet item = content.getFacets().get(i);
                        if (item instanceof XSDEnumerationFacet) {
                            return fieldBuilder.enumeration();
                        }
                    }
                } else {
                    return fieldBuilder;
                }
            }
        }
        String qName = element.getType() == null ? null : element.getType().getQName();
        if (qName != null) {
            fieldBuilder.as(type(element.getType().getTargetNamespace(), element.getType().getName()));
        } else { // Ref & anonymous complex type
            XSDElementDeclaration refName = element.getResolvedElementDeclaration();
            if (schemaType != null) {
                fieldBuilder.as(anonymous());
            } else if (refName != null) {
                // Reference being an element, consider references as references to entity type.
                fieldBuilder.as(type(refName.getTargetNamespace(), refName.getName()));
            } else {
                throw new NotImplementedException();
            }
        }
        if (fieldType.getName().startsWith(ANONYMOUS_PREFIX)) {
            currentTypeStack.push((ComplexTypeMetadata) containedField.getType());
            {
                XmlSchemaWalker.walk(schemaType, this);
            }
            currentTypeStack.pop();
        }
        return fieldBuilder;
    }

    public MetadataRepository copy() {
        MetadataRepository repositoryCopy = new MetadataRepository();
        // Copy first non instantiable types...
        for (Map.Entry<String, Map<String, TypeMetadata>> currentNamespace : nonInstantiableTypes.entrySet()) {
            Map<String, TypeMetadata> namespaceCopy = new HashMap<String, TypeMetadata>();
            Map<String, TypeMetadata> namespaceTypes = currentNamespace.getValue();
            if (namespaceTypes != null) {
                for (Map.Entry<String, TypeMetadata> currentType : namespaceTypes.entrySet()) {
                    namespaceCopy.put(currentType.getKey(), currentType.getValue().copy());
                }
                repositoryCopy.nonInstantiableTypes.put(currentNamespace.getKey(), namespaceCopy);
            }
        }
        // ... then copy entity types.
        for (Map.Entry<String, Map<String, TypeMetadata>> currentNamespace : entityTypes.entrySet()) {
            Map<String, TypeMetadata> namespaceCopy = new HashMap<String, TypeMetadata>();
            Map<String, TypeMetadata> namespaceTypes = currentNamespace.getValue();
            if (namespaceTypes != null) {
                for (Map.Entry<String, TypeMetadata> currentType : namespaceTypes.entrySet()) {
                    namespaceCopy.put(currentType.getKey(), currentType.getValue().copy());
                }
                repositoryCopy.entityTypes.put(currentNamespace.getKey(), namespaceCopy);
            }
        }
        return repositoryCopy;
    }

}