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

package info.ciclope.wotgate.thing;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import info.ciclope.wotgate.ErrorCode;
import info.ciclope.wotgate.storage.DatabaseStorage;
import info.ciclope.wotgate.storage.SqliteStorage;
import info.ciclope.wotgate.thing.component.ThingConfiguration;
import info.ciclope.wotgate.thing.component.ThingDescription;
import info.ciclope.wotgate.thing.component.ThingContainer;
import info.ciclope.wotgate.thing.handler.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;

import java.io.IOException;
import java.net.URL;

public abstract class AbstractThing extends AbstractVerticle  implements ThingContainer {
    private ThingConfiguration thingConfiguration;
    private ThingDescription thingDescription;
    protected DatabaseStorage databaseStorage;
    private ThingHandlerRegister handlerRegister;
    private ThingHandlers thingHandlers;
    private ThingHandlersStarter thingHandlersStarter;

    @Override
    public void start(Future<Void> startFuture) {
        this.thingConfiguration = new ThingConfiguration(this.config());
        this.thingDescription = loadThingDescription(getThingDescriptionPath());
        if (!loadThingExtraConfiguration()) {
            startFuture.fail(ErrorCode.ERROR_LOAD_THING_EXTRA_CONFIGURATION);
            return;
        }
        handlerRegister = new ThingHandlerRegister(thingConfiguration, thingDescription);
        setDatabaseStorage();
        thingHandlers = new ProductionThingHandlers(this, handlerRegister, databaseStorage);
        thingHandlersStarter = new ProductionThingHandlersStarter(thingConfiguration.getThingName(), databaseStorage, thingHandlers);
        thingHandlersStarter.startThingHandlers(thingDescription, vertx.eventBus());
        registerThingHandlers(handlerRegister);
        startThing(startResult-> {
            if (startResult.succeeded()) {
                startFuture.complete();
            } else {
                startFuture.fail(startResult.cause());
            }
        });
    }

    @Override
    public void stop(Future<Void> stopFuture) throws Exception {
        stopThing(stopResult-> {
            if (stopResult.succeeded()) {
                stopFuture.succeeded();
            } else {{
                stopFuture.fail(stopResult.cause());
            }}
            databaseStorage.stopDatabaseStorage();
        });
        super.stop(stopFuture);
    }

    public abstract String getThingDescriptionPath();

    public abstract boolean loadThingExtraConfiguration();

    public abstract void registerThingHandlers(ThingHandlerRegister register);

    public abstract void startThing(Handler<AsyncResult<Void>> handler);

    public abstract void stopThing(Handler<AsyncResult<Void>> handler);

    @Override
    public ThingConfiguration getThingConfiguration() {
        return thingConfiguration;
    }

    @Override
    public ThingDescription getThingDescription() {
        return thingDescription;
    }

    @Override
    public void setThingDescription(ThingDescription thingDescription) {
        this.thingDescription = thingDescription;
    }

    private void setDatabaseStorage() {
        databaseStorage = new SqliteStorage(vertx);
        databaseStorage.startDatabaseStorage(getThingConfiguration().getThingName());
    }

    private ThingDescription loadThingDescription(String thingDescriptionPath) {
        ObjectMapper objectMapper = new ObjectMapper();
        URL thingDescriptionUrl = getClass().getClassLoader().getResource(thingDescriptionPath);
        JsonObject description;
        try {
            description = new JsonObject((objectMapper.readValue(thingDescriptionUrl, JsonNode.class)).toString());
        } catch (IOException e) {
            description = new JsonObject();
            e.printStackTrace();
        }
        return new ThingDescription(description);
    }
}
