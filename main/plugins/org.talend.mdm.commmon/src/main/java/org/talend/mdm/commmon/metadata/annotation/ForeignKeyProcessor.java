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
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;
import org.w3c.dom.Element;

public class ForeignKeyProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(XSDAnnotation annotation, TypeBuilder typeBuilder) {
        throw new UnsupportedOperationException();
    }

    @Override
    public FieldBuilder process(XSDAnnotation annotation, FieldBuilder fieldBuilder) {
        if (annotation != null) {
            EList<Element> annotations = annotation.getApplicationInformation();
            // Process X_ForeignKey annotation first to get referenced type right
            for (Element appInfo : annotations) {
                String source = appInfo.getAttribute("source"); //$NON-NLS-1$
                if ("X_ForeignKey".equals(source)) { //$NON-NLS-1$
                    String path = appInfo.getTextContent();
                    fieldBuilder.fk().referencedType(TypeBuilder.type(StringUtils.EMPTY, path));
                    fieldBuilder.fk().referencedField(FieldBuilder.field(path));
                } else if ("X_ForeignKey_Filter".equals(source)) { //$NON-NLS-1$
                    fieldBuilder.fk().filter(appInfo.getTextContent());
                }
            }
            // Then proceed to other FK related annotations
            for (Element appInfo : annotations) {
                String source = appInfo.getAttribute("source"); //$NON-NLS-1$
                if ("X_ForeignKeyInfo".equals(source)) { //$NON-NLS-1$
                    String path = appInfo.getTextContent();
                    fieldBuilder.fk().foreignKeyInfo(FieldBuilder.field(path));
                } else if ("X_FKIntegrity".equals(source)) { //$NON-NLS-1$
                    fieldBuilder.fk().fkIntegrity(Boolean.valueOf(appInfo.getTextContent()));
                } else if ("X_FKIntegrity_Override".equals(source)) { //$NON-NLS-1$
                    fieldBuilder.fk().allowIntegrityOverride(Boolean.valueOf(appInfo.getTextContent()));
                }
            }
        }
        return fieldBuilder;
    }
}
