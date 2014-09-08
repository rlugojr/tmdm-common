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

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.util.XSDParser;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.SimpleFieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;
import org.w3c.dom.Element;

public class LookupFieldProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(XSDAnnotation annotation, TypeBuilder typeBuilder) {
        if (annotation != null) {
            EList<Element> annotations = annotation.getApplicationInformation();
            for (Element appInfo : annotations) {
                if ("X_Lookup_Field".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    String path = appInfo.getTextContent();
                    SimpleFieldBuilder field = FieldBuilder.field(path);
                    field.setData(MetadataRepository.XSD_LINE_NUMBER, XSDParser.getStartLine(appInfo));
                    field.setData(MetadataRepository.XSD_COLUMN_NUMBER, XSDParser.getStartColumn(appInfo));
                    field.setData(MetadataRepository.XSD_DOM_ELEMENT, appInfo);
                    typeBuilder.lookupField(field);
                }
            }
        }
    }

    @Override
    public FieldBuilder process(XSDAnnotation annotation, FieldBuilder fieldBuilder) {
        throw new UnsupportedOperationException();
    }
}
