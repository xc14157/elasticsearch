/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.rest.action.admin.indices;

import org.elasticsearch.action.admin.indices.template.post.SimulateIndexTemplateAction;
import org.elasticsearch.action.admin.indices.template.post.SimulateIndexTemplateRequest;
import org.elasticsearch.action.admin.indices.template.put.PutIndexTemplateV2Action;
import org.elasticsearch.client.node.NodeClient;
import org.elasticsearch.cluster.metadata.IndexTemplateV2;
import org.elasticsearch.rest.BaseRestHandler;
import org.elasticsearch.rest.RestRequest;
import org.elasticsearch.rest.action.RestToXContentListener;

import java.io.IOException;
import java.util.List;

import static org.elasticsearch.rest.RestRequest.Method.POST;

public class RestSimulateIndexTemplateAction extends BaseRestHandler {

    @Override
    public List<Route> routes() {
        return org.elasticsearch.common.collect.List.of(
            new Route(POST, "/_index_template/_simulate_index/{name}"));
    }

    @Override
    public String getName() {
        return "simulate_index_template_action";
    }

    @Override
    public RestChannelConsumer prepareRequest(final RestRequest request, final NodeClient client) throws IOException {
        SimulateIndexTemplateRequest simulateIndexTemplateRequest = new SimulateIndexTemplateRequest(request.param("name"));
        simulateIndexTemplateRequest.masterNodeTimeout(request.paramAsTime("master_timeout",
            simulateIndexTemplateRequest.masterNodeTimeout()));
        if (request.hasContent()) {
            PutIndexTemplateV2Action.Request indexTemplateRequest = new PutIndexTemplateV2Action.Request("simulating_template");
            indexTemplateRequest.indexTemplate(IndexTemplateV2.parse(request.contentParser()));
            indexTemplateRequest.create(request.paramAsBoolean("create", false));
            indexTemplateRequest.cause(request.param("cause", "api"));

            simulateIndexTemplateRequest.indexTemplateRequest(indexTemplateRequest);
        }

        return channel -> client.execute(SimulateIndexTemplateAction.INSTANCE, simulateIndexTemplateRequest,
            new RestToXContentListener<>(channel));
    }
}
