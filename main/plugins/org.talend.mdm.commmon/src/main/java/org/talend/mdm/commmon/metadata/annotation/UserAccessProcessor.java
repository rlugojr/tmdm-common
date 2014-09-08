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

import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.talend.mdm.commmon.metadata.builder.FieldBuilder;
import org.talend.mdm.commmon.metadata.builder.TypeBuilder;
import org.w3c.dom.Element;

public class UserAccessProcessor implements XmlSchemaAnnotationProcessor {

    @Override
    public void process(XSDAnnotation annotation, TypeBuilder typeBuilder) {
        if (annotation != null) {
            EList<Element> appInfoElements = annotation.getApplicationInformation();
            for (Element appInfo : appInfoElements) {
                String source = appInfo.getAttribute("source");
                String textContent = appInfo.getTextContent();
                if ("X_Hide".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    typeBuilder.hide(textContent);
                } else if ("X_Write".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    typeBuilder.write(textContent);
                } else if ("X_Deny_Create".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    typeBuilder.denyCreate(textContent);
                } else if ("X_Deny_LogicalDelete".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    typeBuilder.denyLogicalDelete(textContent);
                } else if ("X_Deny_PhysicalDelete".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    typeBuilder.denyPhysicalDelete(textContent);
                } else if ("X_Workflow".equals(source)) {  //$NON-NLS-1$//$NON-NLS-2$
                    // including Writable, Read-only and Hidden
                    typeBuilder.workflow(textContent);
                }
            }
        }
    }

    @Override
    public FieldBuilder process(XSDAnnotation annotation, FieldBuilder fieldBuilder) {
        if (annotation != null) {
            EList<Element> appInfoElements = annotation.getApplicationInformation();
            for (Element appInfo : appInfoElements) {
                String source = appInfo.getAttribute("source");
                String textContent = appInfo.getTextContent();
                if ("X_Hide".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    fieldBuilder.hide(textContent);
                } else if ("X_Write".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    fieldBuilder.write(textContent);
                } else if ("X_Deny_Create".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    fieldBuilder.denyCreate(textContent);
                } else if ("X_Deny_LogicalDelete".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    fieldBuilder.denyLogicalDelete(textContent);
                } else if ("X_Deny_PhysicalDelete".equals(source)) { //$NON-NLS-1$ //$NON-NLS-2$
                    fieldBuilder.denyPhysicalDelete(textContent);
                } else if ("X_Workflow".equals(source)) {  //$NON-NLS-1$//$NON-NLS-2$
                    // including Writable, Read-only and Hidden
                    fieldBuilder.workflow(textContent);
                }
            }
        }
        return fieldBuilder;
    }
}
