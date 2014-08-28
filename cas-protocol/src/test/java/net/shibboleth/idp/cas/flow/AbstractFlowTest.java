/*
 * Licensed to the University Corporation for Advanced Internet Development,
 * Inc. (UCAID) under one or more contributor license agreements.  See the
 * NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The UCAID licenses this file to You under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.shibboleth.idp.cas.flow;

import net.shibboleth.idp.spring.IdPPropertiesApplicationContextInitializer;
import net.shibboleth.utilities.java.support.net.HttpServletRequestResponseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.webflow.executor.FlowExecutor;
import org.springframework.webflow.test.MockExternalContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Abstract base class for all tests that exercise flows.
 *
 * @author Marvin S. Addison
 */
@ContextConfiguration(
        locations = {
                "/system/conf/global-system.xml",
                "/system/conf/mvc-beans.xml",
                "/test/test-beans.xml",
                "/test/test-webflow-config.xml",
                "/flows/cas/login/login-beans.xml",
                "/flows/cas/serviceValidate/serviceValidate-beans.xml",
                "/flows/cas/proxyValidate/proxyValidate-beans.xml"},
        initializers = IdPPropertiesApplicationContextInitializer.class)
@WebAppConfiguration
public abstract class AbstractFlowTest extends AbstractTestNGSpringContextTests {

    /** Mock request. */
    protected MockHttpServletRequest request;

    /** Mock response. */
    protected MockHttpServletResponse response;

    /** Mock external context. */
    protected MockExternalContext externalContext;

    /** The web flow executor. */
    @Autowired
    protected FlowExecutor flowExecutor;


    /**
     * Initialize HTTP request, response, and flow context.
     */
    @BeforeMethod
    public void initialize() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        externalContext = new MockExternalContext();
        externalContext.setNativeRequest(request);
        externalContext.setNativeResponse(response);
        HttpServletRequestResponseContext.loadCurrent(request, response);
    }

    @AfterMethod
    public void clearThreadLocals() {
        HttpServletRequestResponseContext.clearCurrent();
    }
}
