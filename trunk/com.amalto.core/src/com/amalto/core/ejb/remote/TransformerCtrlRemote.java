 /*
 * Generated by XDoclet - Do not edit!
 * this class was prodiuced by xdoclet automagically...
 */
package com.amalto.core.ejb.remote;

import java.util.*;

/**
 * This class is remote adapter to TransformerCtrl. It provides convenient way to access
 * facade session bean. Inverit from this class to provide reasonable caching and event handling capabilities.
 *
 * Remote facade for TransformerCtrl.
 * @deprecated - use TransformerV2 package
 * @xdoclet-generated at 10-08-09
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */

public class TransformerCtrlRemote extends Observable
{
    static TransformerCtrlRemote _instance = null;
    public static TransformerCtrlRemote getInstance() {
        if(_instance == null) {
	   _instance = new TransformerCtrlRemote();
	}
	return _instance;
    }

  /**
   * cached remote session interface
   */
  com.amalto.core.ejb.remote.TransformerCtrl _session = null;
  /**
   * return session bean remote interface
   */
   protected com.amalto.core.ejb.remote.TransformerCtrl getSession() {
      try {
   	if(_session == null) {
	   _session = com.amalto.core.ejb.local.TransformerCtrlUtil.getHome().create();
	}
	return _session;
      } catch(Exception ex) {
        // just catch it here and return null.
        // somebody can provide better solution
	ex.printStackTrace();
	return null;
      }
   }

   public com.amalto.core.ejb.TransformerPOJOPK putTransformer ( com.amalto.core.ejb.TransformerPOJO transformer )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.ejb.TransformerPOJOPK retval;
       retval =  getSession().putTransformer( transformer );

      return retval;

   }

   public com.amalto.core.ejb.TransformerPOJO getTransformer ( com.amalto.core.ejb.TransformerPOJOPK pk )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.ejb.TransformerPOJO retval;
       retval =  getSession().getTransformer( pk );

      return retval;

   }

   public com.amalto.core.ejb.TransformerPOJO getTransformerV1 ( com.amalto.core.ejb.TransformerPOJOPK pk )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.ejb.TransformerPOJO retval;
       retval =  getSession().getTransformerV1( pk );

      return retval;

   }

   public com.amalto.core.ejb.TransformerPOJO existsTransformer ( com.amalto.core.ejb.TransformerPOJOPK pk )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.ejb.TransformerPOJO retval;
       retval =  getSession().existsTransformer( pk );

      return retval;

   }

   public com.amalto.core.ejb.TransformerPOJOPK removeTransformer ( com.amalto.core.ejb.TransformerPOJOPK pk )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.ejb.TransformerPOJOPK retval;
       retval =  getSession().removeTransformer( pk );

      return retval;

   }

   public void execute ( com.amalto.core.ejb.TransformerPOJOPK transformerPK,com.amalto.core.util.TypedContent content,com.amalto.core.util.TransformerPluginContext context,com.amalto.core.util.TransformerPluginCallBack callBack )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
      getSession().execute( transformerPK,content,context,callBack );

   }

   public java.util.Collection getTransformerPKs ( java.lang.String regex )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        java.util.Collection retval;
       retval =  getSession().getTransformerPKs( regex );

      return retval;

   }

   public java.util.Collection getTransformerV1PKs ( java.lang.String regex )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        java.util.Collection retval;
       retval =  getSession().getTransformerV1PKs( regex );

      return retval;

   }

   public com.amalto.core.util.TransformerPluginContext process ( com.amalto.core.util.TypedContent content,com.amalto.core.ejb.TransformerPOJOPK transformerPOJOPK,java.util.HashMap decisionTable )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.util.TransformerPluginContext retval;
       retval =  getSession().process( content,transformerPOJOPK,decisionTable );

      return retval;

   }

   public com.amalto.core.util.TransformerPluginContext process ( com.amalto.core.util.TypedContent content,com.amalto.core.ejb.TransformerPOJOPK transformerPOJOPK,java.util.HashMap decisionTable,java.lang.String username,com.amalto.core.util.TransformerPluginContext context,com.amalto.core.util.TransformerPluginCallBack processCallBack )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.util.TransformerPluginContext retval;
       retval =  getSession().process( content,transformerPOJOPK,decisionTable,username,context,processCallBack );

      return retval;

   }

   public com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK processBytesAsBackgroundJob ( byte[] bytes,java.lang.String contentType,com.amalto.core.ejb.TransformerPOJOPK transformerPOJOPK,java.util.HashMap decisionTable )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK retval;
       retval =  getSession().processBytesAsBackgroundJob( bytes,contentType,transformerPOJOPK,decisionTable );

      return retval;

   }

   public com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK processFileAsBackgroundJob ( java.lang.String filename,java.lang.String contentType,com.amalto.core.ejb.TransformerPOJOPK transformerPOJOPK,java.util.HashMap decisionTable )
	  throws com.amalto.core.util.XtentisException, java.rmi.RemoteException
   {
        com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK retval;
       retval =  getSession().processFileAsBackgroundJob( filename,contentType,transformerPOJOPK,decisionTable );

      return retval;

   }

  /**
   * override this method to provide feedback to interested objects
   * in case collections were changed.
   */
  public void invalidate() {

  	setChanged();
	notifyObservers();
  }
}
