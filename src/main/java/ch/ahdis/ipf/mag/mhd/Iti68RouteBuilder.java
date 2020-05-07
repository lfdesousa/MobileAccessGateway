/*
 * Copyright 2020 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ch.ahdis.ipf.mag.mhd;

import java.util.Arrays;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import ch.ahdis.ipf.mag.Config;
import lombok.extern.slf4j.Slf4j;

/**
 * IHE MHD: Retrieve Document [ITI-68] for Document Responder see also
 * https://oehf.github.io/ipf-docs/docs/ihe/iti68/
 * https://oehf.github.io/ipf-docs/docs/boot-fhir/
 * https://camel.apache.org/components/latest/servlet-component.html
 */
@Slf4j
@Component
class Iti68RouteBuilder extends RouteBuilder {

    private final Config config;
    
    public Iti68RouteBuilder(final Config config) {
        super();
        this.config = config;
        log.debug("Iti68RouteBuilder initialized");
    }


    @Override
    public void configure() throws Exception {
        log.debug("Iti66RouteBuilder configure");
        final String xds43Endpoint = String.format("xds-iti43://%s/xds/iti43" +
                "?secure=%s", this.config.getHostUrl(), this.config.isHttps() ? "true" : "false");

        from("mhd-iti68:camel/xdsretrieve").routeId("mdh-retrievedoc-adapter")
                // pass back errors to the endpoint
                .errorHandler(noErrorHandler())
                // translate, forward, translate back
                .process(Utils.queryParameterToRetrieveDocumentSet())
                .to(xds43Endpoint)
                .process(Utils.retrievedDocumentSetToHttResponse());
    }

}
