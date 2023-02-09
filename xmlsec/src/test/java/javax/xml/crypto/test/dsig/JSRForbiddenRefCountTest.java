/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package javax.xml.crypto.test.dsig;

import java.io.File;
import java.security.Security;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dom.DOMCryptoContext;

import org.apache.jcp.xml.dsig.internal.dom.DOMSignedInfo;
import org.apache.xml.security.utils.Constants;
import org.w3c.dom.Element;

/**
 * This is a test for a forbidden number of references when secure validation is enabled.
 */
public class JSRForbiddenRefCountTest extends org.junit.Assert {

    static {
        Security.insertProviderAt(new org.apache.jcp.xml.dsig.internal.dom.XMLDSigRI(), 1);
    }

    @org.junit.Test
    public void testReferenceCount() throws Exception {
        Element signedInfoElement = 
            getSignedInfoElement("src/test/resources/interop/c14n/Y4", "signature-manifest.xml");
        
        InternalDOMCryptoContext context = new InternalDOMCryptoContext();

        try {
            new DOMSignedInfo(signedInfoElement, context, null);
        } catch (MarshalException ex) {
            String error = 
                "A maxiumum of 30 references per Manifest are allowed with secure validation";
            assertTrue(ex.getMessage().contains(error));
        }
        context.setProperty("org.apache.jcp.xml.dsig.secureValidation", Boolean.FALSE);
        new DOMSignedInfo(signedInfoElement, context, null);
    }
    
    private static class InternalDOMCryptoContext extends DOMCryptoContext {
        //
    }
    
    private Element getSignedInfoElement(
        String directory, String file
    ) throws Exception {
        String basedir = System.getProperty("basedir");
        if (basedir != null && !"".equals(basedir)) {
            directory = basedir + "/" + directory;
        }

        File f = new File(directory + "/" + file);
        javax.xml.parsers.DocumentBuilderFactory dbf =
            javax.xml.parsers.DocumentBuilderFactory.newInstance();

        dbf.setNamespaceAware(true);

        javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
        org.w3c.dom.Document doc = db.parse(f);

        return (Element) doc.getElementsByTagNameNS(Constants.SignatureSpecNS,
                                                 Constants._TAG_SIGNEDINFO).item(0);
    }

}
