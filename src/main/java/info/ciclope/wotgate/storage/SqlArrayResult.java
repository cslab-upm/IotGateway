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

package info.ciclope.wotgate.storage;

import io.vertx.core.json.JsonArray;

public class SqlArrayResult {
    private final JsonArray result;
    private final Integer total;


    public SqlArrayResult(JsonArray result, Integer total) {
        this.result = result;
        this.total = total;
    }

    public JsonArray getResult() {
        return result;
    }

    public Integer getTotal() {
        return total;
    }

}
