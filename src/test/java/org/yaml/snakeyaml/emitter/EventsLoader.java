package org.yaml.snakeyaml.emitter;

import org.yaml.snakeyaml.Loader;

public class EventsLoader extends Loader {

    public EventsLoader() {
        this.constructor = new EventConstructor();
    }

}