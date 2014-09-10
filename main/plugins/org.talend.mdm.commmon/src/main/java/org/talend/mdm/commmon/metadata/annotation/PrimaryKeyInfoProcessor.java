/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.commmon.metadata.annotation;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.util.XSDParser;
import org.talend.mdm.commmon.metadata.*;
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;
import org.w3c.dom.Element;

import static org.talend.mdm.commmon.metadata.builder.TypeBuilder.type;

public class PrimaryKeyInfoProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(XSDAnnotation annotation, TypeBuilder typeBuilder) {
        if (annotation != null) {
            EList<Element> appInfoElements = annotation.getApplicationInformation();
            for (Element appInfo : appInfoElements) {
                if ("X_PrimaryKeyInfo".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$ //$NON-NLS-2$
                    typeBuilder.pkInfo(handlePrimaryKeyInfo(appInfo));
                }
            }
        }
    }

    @Override
    public FieldBuilder process(XSDAnnotation annotation, FieldBuilder fieldBuilder) {
        // Nothing to do (only for types).
        return fieldBuilder;
    }

    private FieldBuilder handlePrimaryKeyInfo(Element appInfo) {
        String path = appInfo.getTextContent();
        String typeName = StringUtils.substringBefore(path, "/").trim(); //$NON-NLS-1$
        String fieldName = StringUtils.substringAfter(path, "/").trim(); //$NON-NLS-1$
        FieldBuilder field = FieldBuilder.field(fieldName, type(typeName));
        field.setData(MetadataRepository.XSD_LINE_NUMBER, XSDParser.getStartLine(appInfo));
        field.setData(MetadataRepository.XSD_COLUMN_NUMBER, XSDParser.getStartColumn(appInfo));
        field.setData(MetadataRepository.XSD_DOM_ELEMENT, appInfo);
        return field;
    }
}
