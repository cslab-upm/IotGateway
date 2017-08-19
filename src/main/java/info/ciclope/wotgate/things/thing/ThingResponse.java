/*
 *  Copyright (c) 2017, Javier Martínez Villacampa
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package info.ciclope.wotgate.things.thing;

import io.vertx.core.MultiMap;
import io.vertx.core.json.JsonObject;

import java.util.Map;

public class ThingResponse {
    private static final String THING_REQUEST_STATUS = "status";
    private static final String THING_REQUEST_HEADERS = "headers";
    private static final String THING_REQUEST_BODY = "body";

    private final JsonObject response;

    public ThingResponse(String status, JsonObject headers, JsonObject body) {
        this.response = new JsonObject();
        this.response.put(THING_REQUEST_STATUS, status);
        this.response.put(THING_REQUEST_HEADERS, headers);
        this.response.put(THING_REQUEST_BODY, body);
    }

    public ThingResponse(JsonObject httpResponseJson) {
        response = httpResponseJson.copy();
    }

    public JsonObject getResponse() {
        return response;
    }

    public String getStatus() {
        return response.getString(THING_REQUEST_STATUS);
    }

    public JsonObject getHeaders() {
        return response.getJsonObject(THING_REQUEST_HEADERS);
    }

    public String getHeader(String name) {
        return response.getJsonObject(THING_REQUEST_HEADERS).getString(name);
    }

    public JsonObject getBody() {
        return response.getJsonObject(THING_REQUEST_BODY);
    }

    private JsonObject parseMultiMap(MultiMap multimap) {
        JsonObject multimapJson = new JsonObject();
        for (Map.Entry<String, String> entry : multimap) {
            multimapJson.put(entry.getKey(), entry.getValue());
        }

        return multimapJson;
    }
}