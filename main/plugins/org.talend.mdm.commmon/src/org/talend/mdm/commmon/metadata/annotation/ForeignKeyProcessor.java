/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.annotation;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.util.XSDParser;
import org.talend.mdm.commmon.metadata.*;
import org.w3c.dom.Element;

public class ForeignKeyProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(MetadataRepository repository, ComplexTypeMetadata type, XSDAnnotation annotation,
            XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            EList<Element> annotations = annotation.getApplicationInformation();
            // Process X_ForeignKey annotation first to get referenced type right
            for (Element appInfo : annotations) {
                String source = appInfo.getAttribute("source"); //$NON-NLS-1$
                if ("X_ForeignKey".equals(source)) { //$NON-NLS-1$
                    handleForeignKey(repository, type, state, appInfo);
                } else if("X_ForeignKey_Filter".equals(source)) { //$NON-NLS-1$
                    state.setForeignKeyFilter(appInfo.getTextContent());
                }
            }
            // Then proceed to other FK related annotations
            for (Element appInfo : annotations) {
                String source = appInfo.getAttribute("source"); //$NON-NLS-1$
                 if ("X_ForeignKeyInfo".equals(source)) { //$NON-NLS-1$
                    handleForeignKeyInfo(repository, state, appInfo);
                } else if ("X_FKIntegrity".equals(source)) { //$NON-NLS-1$
                    state.setFkIntegrity(Boolean.valueOf(appInfo.getTextContent()));
                } else if ("X_FKIntegrity_Override".equals(source)) { //$NON-NLS-1$
                    state.setFkIntegrityOverride(Boolean.valueOf(appInfo.getTextContent()));
                }
            }
        }
    }

    private void handleForeignKeyInfo(MetadataRepository repository, XmlSchemaAnnotationProcessorState state, Element appInfo) {
        String path = appInfo.getTextContent();
        FieldMetadata fieldMetadata = getFieldMetadata(repository, (ComplexTypeMetadata) state.getReferencedType(), appInfo, path);
        state.setForeignKeyInfo(fieldMetadata);
    }

    private void handleForeignKey(MetadataRepository repository, ComplexTypeMetadata type,
            XmlSchemaAnnotationProcessorState state, Element appInfo) {
        state.markAsReference();
        String path = appInfo.getTextContent();
        FieldMetadata fieldMetadata = getFieldMetadata(repository, type, appInfo, path);
        state.setReferencedField(fieldMetadata);
        // Only reference instantiable types.
        state.setReferencedType(new SoftTypeRef(repository, repository.getUserNamespace(), getTypeName(type, path), true));
    }

    private static FieldMetadata getFieldMetadata(MetadataRepository repository, ComplexTypeMetadata type, Element appInfo,
            String path) {
        String typeName = getTypeName(type, path);
        String fieldPath = StringUtils.substringAfter(path, "/").trim(); //$NON-NLS-1$
        FieldMetadata fieldMetadata;
        if (!fieldPath.isEmpty()) {
            fieldMetadata = new SoftFieldRef(repository, fieldPath, typeName);
        } else {
            fieldMetadata = new SoftIdFieldRef(repository, typeName);
        }
        fieldMetadata.setData(MetadataRepository.XSD_LINE_NUMBER, XSDParser.getStartLine(appInfo));
        fieldMetadata.setData(MetadataRepository.XSD_COLUMN_NUMBER, XSDParser.getStartColumn(appInfo));
        fieldMetadata.setData(MetadataRepository.XSD_DOM_ELEMENT, appInfo);
        return fieldMetadata;
    }

    private static String getTypeName(ComplexTypeMetadata type, String path) {
        String typeName = StringUtils.substringBefore(path, "/").trim(); //$NON-NLS-1$
        if (".".equals(typeName)) { //$NON-NLS-1$
            typeName = type.getName();
        }
        return typeName;
    }

}
