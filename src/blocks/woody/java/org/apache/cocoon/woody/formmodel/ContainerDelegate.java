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
package org.apache.cocoon.woody.formmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.cocoon.woody.Constants;
import org.apache.cocoon.woody.FormContext;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

/**
 * Helper class for the implementation of widgets containing other widgets.
 *
 * CVS $Id: ContainerDelegate.java,v 1.3 2004/01/06 12:36:07 joerg Exp $
 * @author Timothy Larson
 */
public class ContainerDelegate {
//    private WidgetDefinition definition;
    private List widgets;
    private Map widgetsById;

    private static final String WIDGETS_EL = "widgets";

    public ContainerDelegate(WidgetDefinition definition) {
        widgets = new ArrayList();
        widgetsById = new HashMap();
//        this.definition = definition;
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
        widgetsById.put(widget.getId(), widget);
    }

    public void readFromRequest(FormContext formContext) {
        Iterator widgetIt = widgets.iterator();
        while (widgetIt.hasNext()) {
            Widget widget = (Widget)widgetIt.next();
            widget.readFromRequest(formContext);
        }
    }

    public boolean validate(FormContext formContext) {
        boolean valid = true;
        Iterator widgetIt = widgets.iterator();
        while (widgetIt.hasNext()) {
            Widget widget = (Widget)widgetIt.next();
            valid = valid & widget.validate(formContext);
        }
        return valid;
    }

    public boolean hasWidget(String id) {
        return widgetsById.containsKey(id);
    }

    public Widget getWidget(String id) {
        return (Widget)widgetsById.get(id);
    }

    public Iterator iterator() {
        return widgets.iterator();
    }

    /**
     * Returns false if there is at least one field which has no value.
     */
    public boolean widgetsHaveValues() {
        Iterator widgetsIt = widgets.iterator();
        while(widgetsIt.hasNext()) {
            Widget widget = (Widget)widgetsIt.next();
            if (widget.getValue() == null)
                return false;
        }
        return true;
    }

    public void generateSaxFragment(ContentHandler contentHandler, Locale locale) throws SAXException {
        contentHandler.startElement(Constants.WI_NS, WIDGETS_EL, Constants.WI_PREFIX_COLON + WIDGETS_EL, Constants.EMPTY_ATTRS);
        Iterator widgetIt = widgets.iterator();
        while (widgetIt.hasNext()) {
            Widget widget = (Widget)widgetIt.next();
            widget.generateSaxFragment(contentHandler, locale);
        }
        contentHandler.endElement(Constants.WI_NS, WIDGETS_EL, Constants.WI_PREFIX_COLON + WIDGETS_EL);
    }
}

