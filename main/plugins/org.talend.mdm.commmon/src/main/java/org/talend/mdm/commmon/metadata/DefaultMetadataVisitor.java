/*
 * Copyright (C) 2006-2015 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Default visitor for data model classes in package org.talend.mdm.commmon.metadata.
 */
public class DefaultMetadataVisitor<T> implements MetadataVisitor<T> {

    /**
     * Visit all types located in <code>repository</code>.
     * @param repository A {@link MetadataRepository}.
     * @return Result typed as T.
     */
    public T visit(MetadataRepository repository) {
        Collection<ComplexTypeMetadata> types = repository.getUserComplexTypes();
        for (TypeMetadata type : types) {
            type.accept(this);
        }
        types = repository.getNonInstantiableTypes();
        for (TypeMetadata type : types) {
            type.accept(this);
        }
        return null;
    }

    /**
     * @param simpleType A simple typed field.
     * @return Result typed as T.
     */
    public T visit(SimpleTypeMetadata simpleType) {
        return null;
    }

    /**
     * Visit all fields declared in <code>complexType</code>.
     * @param complexType A complex type.
     * @return Result typed as T.
     */
    public T visit(ComplexTypeMetadata complexType) {
        List<FieldMetadata> copy = new ArrayList<FieldMetadata>(complexType.getFields());
        Collection<FieldMetadata> keyFields = complexType.getKeyFields();
        for (FieldMetadata keyField : keyFields) {
            keyField.accept(this);
            copy.remove(keyField);
        }
        for (FieldMetadata field : copy) {
          field.accept(this);            
        }
        return null;
    }

    /**
     * Visit all field declared in contained type.
     * @param containedType A contained type.
     * @return Result typed as T.
     */
    public T visit(ContainedComplexTypeMetadata containedType) {
        Collection<FieldMetadata> fields = containedType.getFields();
        for (FieldMetadata field : fields) {
            field.accept(this);
        }

        return null;
    }

    public T visit(ReferenceFieldMetadata referenceField) {
        return null;
    }

    public T visit(ContainedTypeFieldMetadata containedField) {
        return containedField.getContainedType().accept(this);
    }

    public T visit(FieldMetadata fieldMetadata) {
        return null;
    }

    public T visit(SimpleTypeFieldMetadata simpleField) {
        return null;
    }

    public T visit(EnumerationFieldMetadata enumField) {
        return null;
    }

}
