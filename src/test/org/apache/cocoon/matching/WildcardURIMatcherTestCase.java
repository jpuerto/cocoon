/*
* Copyright 1999-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.cocoon.matching;

import java.util.Map;
import junit.framework.Test;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.apache.avalon.framework.parameters.Parameters;
import org.apache.cocoon.SitemapComponentTestCase;


public class WildcardURIMatcherTestCase extends SitemapComponentTestCase {

    /**
     * Run this test suite from commandline
     *
     * @param args commandline arguments (ignored)
     */
    public static void main( String[] args ) {
        TestRunner.run(suite());
    }
    
    /** Create a test suite.
     * This test suite contains all test cases of this class.
     * @return the Test object containing all test cases.
     */
    public static Test suite() {
        TestSuite suite = new TestSuite(WildcardURIMatcherTestCase.class);
        return suite;
    }
    
    public void testWildcardURIMatch() throws Exception {
        getRequest().setRequestURI("/test/foo/bla/end");

        Parameters parameters = new Parameters();

        Map result = match("wildcard-uri", "**", parameters);
        System.out.println(result);
        assertNotNull("Test if resource exists", result);
        assertEquals("Test for **", "test/foo/bla/end", result.get("1"));
        
        result = match("wildcard-uri", "**/bla/*", parameters);
        System.out.println(result);
        assertNotNull("Test if resource exists", result);
        assertEquals("Test for **/bla/* {1}", "test/foo", result.get("1"));
        assertEquals("Test for **/bla/* {2}", "end", result.get("2"));
    }
}
