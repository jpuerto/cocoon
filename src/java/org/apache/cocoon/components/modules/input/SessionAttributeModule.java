/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Apache Cocoon" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation and was  originally created by
 Stefano Mazzocchi  <stefano@apache.org>. For more  information on the Apache
 Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.cocoon.components.modules.input;

import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.environment.ObjectModelHelper;
import org.apache.cocoon.environment.Request;
import org.apache.cocoon.environment.Session;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * SessionAttributeModule accesses session attributes. If the
 * attribute name contains an askerisk "*" this is considered a
 * wildcard and all attributes that would match this wildcard are
 * considered to be part of an array of that name for
 * getAttributeValues. Only one "*" is allowed.
 *
 * @author <a href="mailto:haul@apache.org">Christian Haul</a>
 * @version CVS $Id: SessionAttributeModule.java,v 1.3 2003/11/18 17:10:40 vgritsenko Exp $
 */
public class SessionAttributeModule extends AbstractInputModule implements ThreadSafe {

    public Object getAttribute( String name, Configuration modeConf, Map objectModel )
        throws ConfigurationException {

        String pname = (String) this.settings.get("parameter", name);
        if ( modeConf != null ) {
            pname = modeConf.getAttribute( "parameter", pname );
            // preferred
            pname = modeConf.getChild("parameter").getValue(pname);
        }
        return ObjectModelHelper.getRequest(objectModel).getSession().getAttribute( pname );
    }


    public Iterator getAttributeNames( Configuration modeConf, Map objectModel )
        throws ConfigurationException {

        return new IteratorHelper(ObjectModelHelper.getRequest(objectModel).getSession().getAttributeNames());
    }


    public Object[] getAttributeValues( String name, Configuration modeConf, Map objectModel )
        throws ConfigurationException {

        Request request = ObjectModelHelper.getRequest(objectModel);
        String wildcard = (String) this.settings.get("parameter", name);
        if ( modeConf != null ) {
            wildcard = modeConf.getAttribute( "parameter", wildcard );
            // preferred
            wildcard = modeConf.getChild("parameter").getValue(wildcard);
        }
        int wildcardIndex = wildcard.indexOf( "*" );
        if ( wildcardIndex != -1 ) {
            // "*" contained in attribute name => combine all
            // attributes' values that match prefix, suffix

            // split the attribute's name so that the "*" could be
            // determined by looking at the attributes' names that
            // start with the prefix and end with the suffix
            //
            String prefix = wildcard.substring( 0, wildcardIndex );
            String suffix;
            if ( wildcard.length() >= wildcardIndex + 1 ) {
                suffix = wildcard.substring( wildcardIndex + 1 );
            } else {
                suffix = "";
            }
            SortedSet names = new TreeSet();
            Session session = request.getSession();
            Enumeration allNames = session.getAttributeNames();

            while (allNames.hasMoreElements()) {
                String pname = (String) allNames.nextElement();
                if ( pname.startsWith( prefix ) && pname.endsWith( suffix ) ) {
                    names.add(pname);
                }
            }

            List values = new LinkedList();
            Iterator j = names.iterator();
            while (j.hasNext()){
                String pname = (String) j.next();
                values.add( session.getAttribute(pname) );
            }

            return values.toArray();

        } else {
            // no "*" in attribute name => just return all values of
            // this one attribute. Make sure, it's an array.

            Object value = request.getSession().getAttribute( wildcard );
            if ( value != null && !value.getClass().isArray() ) {
                Object[] values = new Object[1];
                values[0] = value;
                return values;
            } else {
                return (Object[]) value;
            }

        }

    }
}
