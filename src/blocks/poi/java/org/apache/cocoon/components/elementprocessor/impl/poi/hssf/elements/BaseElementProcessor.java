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

package org.apache.cocoon.components.elementprocessor.impl.poi.hssf.elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.avalon.framework.logger.AbstractLogEnabled;
import org.apache.cocoon.CascadingIOException;
import org.apache.cocoon.components.elementprocessor.ElementProcessor;
import org.apache.cocoon.components.elementprocessor.impl.poi.POIFSElementProcessor;
import org.apache.cocoon.components.elementprocessor.types.Attribute;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 * The BaseElementProcessor class provides default behavior for
 * classes that can handle a particular XML element's content.
 *
 * It is intended that all ElementProcessors should extend this class.
 * This class is declared Abstract although it has no abstract
 * methods. It can be used 'as is' for a no-op ElementProcessor, and
 * an ElementProcessor that chooses to simply extend this class with
 * no overrides will be a no-op ElementProcessor.
 *
 * @author Marc Johnson (marc_johnson27591@hotmail.com)
 * @version CVS $Id: BaseElementProcessor.java,v 1.3 2004/01/31 08:50:39 antonio Exp $
 */
public abstract class BaseElementProcessor extends AbstractLogEnabled
     implements POIFSElementProcessor
{
    private Map                  _attributes;
    private POIFSFileSystem      _filesystem;
    private BaseElementProcessor _parent;
    private StringBuffer         _data;
    
    /**
     * Constructor
     *
     * @param implied_attributes an array of Attributes which might
     *                           not appear in the initialization call
     *                           because they are implied, and the SAX
     *                           processor will not automatically
     *                           generate them.
     */

    protected BaseElementProcessor(final Attribute [] implied_attributes) {
        _attributes = new HashMap();
        _filesystem = null;
        _parent     = null;
        _data       = new StringBuffer();

        if (implied_attributes != null) {
            for (int k = 0; k < implied_attributes.length; k++) {
                _attributes.put(implied_attributes[ k ].getName(),
                                implied_attributes[ k ]);
            }
        }
    }

    /**
     * @return the Attributes managed by this ElementProcessor. May be
     *         empty, but never null
     */

    protected Iterator getAttributes() {
        return _attributes.values().iterator();
    }

    /**
     * Get the value of an attribute
     *
     * @param name the name of the attribute to look up
     *
     * @return the value of the specified attribute; will be null if
     *         the attribute doesn't exist
     */

    protected String getValue(final String name) {
        String    value = null;
        Attribute attr  = ( Attribute ) _attributes.get(name);

        if (attr != null) {
            value = attr.getValue();
        }
        return value;
    }

    /**
     * @return the POIFSFileSystem object
     */

    protected POIFSFileSystem getFilesystem() {
        return _filesystem;
    }


    /**
     * @return the parent ElementProcessor; may be null
     */

    protected ElementProcessor getParent() {
        return _parent;
    }

    /**
     * get the nearest ancestor that happens to be an instance of the
     * specified class
     *
     * @param theclass the class we're looking for
     *
     * @return an ancestor of the specified class; null if no such
     *         ancestor exists
     */

    protected ElementProcessor getAncestor(final Class theclass) {
    	ElementProcessor parent = getParent();
    	if (parent == null || parent.getClass().equals(theclass)) {
    	    return parent;
    	} else {
    	    return ((BaseElementProcessor)parent).getAncestor(theclass);
    	}
    }

    /**
     * @return the data, both regular characters and whitespace
     *         characters, collected so far
     */

    protected String getData() {
        return _data.toString().trim();
    }

    /**
     * get the workbook we're working on. If it isn't in this
     * instance, check the parent
     *
     * @return the workbook we're working on. Never null
     *
     * @exception IOException if the workbook is missing
     */

    protected Workbook getWorkbook() throws IOException {
        if (_parent != null) {
            return _parent.getWorkbook();
        } else {
            // hit the end of the containment hierarchy without
            // finding it. This Is Bad
            throw new IOException("Cannot find the workbook object");
        }
    }

    /**
     * get the current sheet we're working on. If it isn't in this
     * instance, check the parent
     *
     * @return the current sheet we're working on. Never null
     *
     * @exception IOException if the sheet is missing
     */

    protected Sheet getSheet() throws IOException {
        if (_parent != null) {
            return _parent.getSheet();
        } else {
            // hit the end of the containment hierarchy without
            // finding it. This Is Bad
            throw new IOException("Cannot find the sheet object");
        }
    }

    /**
     * get the current cell we're working on. If it isn't in this
     * instance, check the parent
     *
     * @return the current cell we're working on. Never null
     *
     * @exception IOException if the cell is missing
     */

    protected Cell getCell() throws IOException {
        if (_parent != null) {
            return _parent.getCell();
        } else {
            // hit the end of the containment hierarchy without
            // finding it. This Is Bad
            throw new IOException("Cannot find the cell object");
        }
    }

    /* ********** START implementation of ElementProcessor ********** */

    /**
     * The implementation should walk the array of attributes and
     * perform appropriate actions based on that data. The array of
     * attributes is guaranteed never to be null, but it can be
     * empty. An implementation can expect code like this to work
     * without throwing runtime exceptions:
     * <br>
     * <code>
     * for (int k = 0; k < attributes.length; k++)<br>
     * {<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;Attribute attr = attributes[ k ];<br>
     * &nbsp;&nbsp;&nbsp;&nbsp;// process attribute</br>
     * }</br>
     * </code>
     * <br>
     * <b>NOTE: if the XML DTD or schema includes <i>IMPLIED</i>
     * attributes for an element, those attributes are not included in
     * the Attribute array - they're not in the data provided in the
     * startElement call. The constructor for the ElementProcessor
     * should set those implied attributes itself, and allow them to
     * be overridden if the XML source provided <i>explicit</i> values
     * for them.</b>
     * <br>
     * The parent ElementProcessor is a reference to the
     * ElementProcessor whose corresponding XML element contains this
     * ElementProcessor's corresponding XML element. It will not be
     * null unless this ElementProcessor's corresponding XML element
     * is the top-level element in the XML document. Whether this
     * ElementProcessor needs to establish a containment relationship
     * with its parent or not, and whether it needs to do so at the
     * beginning, middle, or end of its life cycle, are private
     * matters left to the discretion of the individual
     * ElementProcessor implementation. If the ElementProcessor needs
     * to interact with its parent ElementProcessor, but is not ready
     * to do so when the initialize method is called, it must cache
     * the reference to its parent.
     * <br>
     *
     * @param attributes the array of Attribute instances; may be
     *                   empty, will never be null
     * @param parent the parent ElementProcessor; may be null
     *
     * @exception IOException if anything is wrong
     */

    public void initialize(final Attribute [] attributes,
                           final ElementProcessor parent) throws IOException {
        try {
            _parent = ( BaseElementProcessor ) parent;
        } catch (ClassCastException ignored) {
            throw new CascadingIOException(
                "parent is not compatible with this serializer", ignored);
        }

        // can't trust the guarantee -- an overriding implementation
        // may have screwed this up
        if (attributes != null) {
            for (int k = 0; k < attributes.length; k++) {
                _attributes.put(attributes[ k ].getName(), attributes[ k ]);
            }
        }
    }

    /**
     * The data provided in this method call comes from the
     * corresponding XML element's contents. The array is guaranteed
     * not to be null and will never be empty.
     * <br>
     * The POIFSSerializer will make 0 to many calls to this method;
     * all such calls will occur after the initialize method is called
     * and before the endProcessing method is called.
     * <br>
     * Calls to this method may be interleaved with calls to
     * acceptWhitespaceCharacters. All calls to acceptCharacters and
     * acceptWhitespaceCharacters are guaranteed to be in the same
     * order as their data is in the element.
     *
     * @param data the character data
     */

    public void acceptCharacters(final char [] data) {
        if (data != null) {
            _data.append(data);
        }
    }

    /**
     * The data provided in this method call comes from the
     * corresponding XML element's contents. The array is guaranteed
     * not to be null and will never be empty.
     * <br>
     * The POIFSSerializer will make 0 to many calls to this method;
     * all such calls will occur after the initialize method is called
     * and before the endProcessing method is called.
     * <br>
     * Calls to this method may be interleaved with calls to
     * acceptCharacters. All calls to acceptCharacters and
     * acceptWhitespaceCharacters are guaranteed to be in the same
     * order as their data is in the element.
     *
     * @param data the whitespace characters
     */

    public void acceptWhitespaceCharacters(final char [] data) {
        if (data != null) {
            _data.append(data);
        }
    }

    /**
     * This is the last method call executed by the
     * POIFSSerializer. When this method is called, the
     * ElementProcessor should finish its work and perform its final
     * interactions with its parent ElementProcessor and the
     * filesystem, if necessary.
     * <br>
     * If the implementation cached the parent ElementProcessor
     * reference, the filesystem reference, or both, when the
     * initialize method was called, it should null out those
     * references.
     *
     * @exception IOException
     */

    public void endProcessing() throws IOException {
        _filesystem = null;
        _parent     = null;
    }

    /* **********  END  implementation of ElementProcessor ********** */
    /* ********** START implementation of POIFSElementProcessor ********** */

    /**
     * Set the POIFSFileSystem for the element processor
     *
     * @param fs the POIFSFileSystem instance
     */

    public void setFilesystem(POIFSFileSystem fs) {
        _filesystem = fs;
    }

    /* **********  END  implementation of POIFSElementProcessor ********** */
}   // end package scope abstract class BaseElementProcessor
