// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.commmon.util.datamodel.management;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.SAXException;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSType;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public abstract class SchemaManager {

    private static final List<String> typesFilter = new ArrayList<String>() {

        {
            add("anyType");
        }
    };

    public DataModelBean updateToDatamodelPool(String revisionID, String uniqueID, String dataModelSchema)
            throws SchemaManagerException, SAXException {
        DataModelID dataModelID = new DataModelID(uniqueID, revisionID);
        if (dataModelSchema == null)
            throw new SchemaManagerException("Data model schema can not be empty! ");
        // check exist in pool
        if (existInPool(dataModelID)) {
            // if exist remove
            removeFromPool(dataModelID);
        } else {
            // do nothing
        }
        // parse to pojo
        DataModelBean dataModelBean = instantiateDataModelBean(dataModelSchema);
        // put to pool
        addToPool(dataModelID, dataModelBean);
        // dump
        // dataModelBean.dump();//FIXME

        return dataModelBean;
    }

    protected abstract boolean existInPool(DataModelID dataModelID);

    protected abstract void removeFromPool(DataModelID dataModelID);

    protected abstract void addToPool(DataModelID dataModelID, DataModelBean dataModelBean);

    protected abstract DataModelBean getFromPool(DataModelID dataModelID) throws Exception;

    private DataModelBean instantiateDataModelBean(String dataModelSchema) throws SAXException {
        DataModelBean dataModelBean = new DataModelBean();
        XSSchemaSet result = null;
        // parse
        XSOMParser reader = new XSOMParser();
        reader.setAnnotationParser(new DomAnnotationParserFactory());
        reader.setEntityResolver(new SecurityEntityResolver());
        reader.parse(new StringReader(dataModelSchema));
        result = reader.getResult();

        iterateDatamodel(result, dataModelBean);
        // TODO

        return dataModelBean;
    }

    private void iterateDatamodel(XSSchemaSet result, DataModelBean dataModelBean) {
        // iterate each XSSchema object. XSSchema is a per-namespace schema.
        Iterator itr = result.iterateSchema();
        while (itr.hasNext()) {
            XSSchema s = (XSSchema) itr.next();
            // System.out.println("Target namespace: "+s.getTargetNamespace());
            iterateTypes(s, dataModelBean);
            iterateConcepts(s, dataModelBean);
        }
    }

    private void iterateConcepts(XSSchema s, DataModelBean dataModelBean) {
        // System.out.println(s.getElementDecls());
        Iterator iter = s.iterateElementDecls();
        while (iter.hasNext()) {
            XSElementDecl e = (XSElementDecl) iter.next();
            if (e != null)
                dataModelBean.addBusinessConcept(new BusinessConcept(e));
        }
    }

    private void iterateTypes(XSSchema s, DataModelBean dataModelBean) {
        // System.out.println(s.getElementDecls());
        Iterator iter = s.iterateComplexTypes();// FIXME only support complex types, is it enough?
        while (iter.hasNext()) {
            XSType t = (XSType) iter.next();
            if (t != null && !typesFilter.contains(t.getName()))
                dataModelBean.addReusableType(new ReusableType(t));
        }
    }

    /**
     * DOC HSHU Comment method "removeFromDatamodelPool".
     * 
     * @param revisionID
     * @param uniqueID
     */
    public void removeFromDatamodelPool(String revisionID, String uniqueID) {
        removeFromPool(new DataModelID(uniqueID, revisionID));
    }

    /**
     * DOC HSHU Comment method "getBusinessConcept".
     * 
     * @param conceptName
     * @param dataModelID
     * @return
     * @throws Exception
     */
    public BusinessConcept getBusinessConcept(String conceptName, DataModelID dataModelID) throws Exception {
        BusinessConcept targetBusinessConcept = null;
        DataModelBean dataModelBean = getFromPool(dataModelID);
        List<BusinessConcept> businessConcepts = dataModelBean.getBusinessConcepts();
        for (BusinessConcept businessConcept : businessConcepts) {
            if (businessConcept.getName().equals(conceptName)) {
                targetBusinessConcept = businessConcept;
                break;
            }
        }
        return targetBusinessConcept;
    }

}