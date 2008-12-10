package org.yaml.snakeyaml.emitter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.DocumentEndEvent;
import org.yaml.snakeyaml.events.DocumentStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;

public class EventConstructor extends Constructor {

    public EventConstructor() {
        this.yamlConstructors.put(null, new ConstructEvent());
    }

    private class ConstructEvent implements Construct {

        @SuppressWarnings("unchecked")
        public Object construct(Node node) {
            Map mapping;
            if (node instanceof ScalarNode) {
                mapping = new HashMap();
            } else {
                mapping = constructMapping((MappingNode) node);
            }
            String className = node.getTag().substring(1) + "Event";
            Event value;
            if (className.equals("AliasEvent")) {
                value = new AliasEvent((String) mapping.get("anchor"), null, null);
            } else if (className.equals("ScalarEvent")) {
                String tag = (String) mapping.get("tag");
                String v = (String) mapping.get("value");
                if (v == null) {
                    v = "";
                }
                List implicitList = (List) mapping.get("implicit");
                boolean implicit[];
                if (implicitList == null) {
                    implicit = new boolean[] { false, true };
                } else {
                    implicit = new boolean[2];
                    implicit[0] = (Boolean) implicitList.get(0);
                    implicit[1] = (Boolean) implicitList.get(1);
                }
                value = new ScalarEvent((String) mapping.get("anchor"), tag, implicit, v, null,
                        null, null);
            } else if (className.equals("SequenceStartEvent")) {
                String tag = (String) mapping.get("tag");
                Boolean implicit = (Boolean) mapping.get("implicit");
                if (implicit == null) {
                    implicit = true;
                }
                value = new SequenceStartEvent((String) mapping.get("anchor"), tag, implicit, null,
                        null, false);
            } else if (className.equals("MappingStartEvent")) {
                String tag = (String) mapping.get("tag");
                Boolean implicit = (Boolean) mapping.get("implicit");
                if (implicit == null) {
                    implicit = true;
                }
                value = new MappingStartEvent((String) mapping.get("anchor"), tag, implicit, null,
                        null, false);
            } else if (className.equals("DocumentEndEvent")) {
                value = new DocumentEndEvent(null, null, false);
            } else if (className.equals("DocumentStartEvent")) {
                Map<String, String> tags = (Map<String, String>) mapping.get("tags");
                List<Long> versionList = (List<Long>) mapping.get("version");
                Integer[] version = null;
                if (versionList != null) {
                    version = new Integer[2];
                    version[0] = versionList.get(0).intValue();
                    version[1] = versionList.get(1).intValue();
                }
                value = new DocumentStartEvent(null, null, false, version, tags);
            } else if (className.equals("MappingEndEvent")) {
                value = new MappingEndEvent(null, null);
            } else if (className.equals("SequenceEndEvent")) {
                value = new SequenceEndEvent(null, null);
            } else if (className.equals("StreamEndEvent")) {
                value = new StreamEndEvent(null, null);
            } else if (className.equals("StreamStartEvent")) {
                value = new StreamStartEvent(null, null);
            } else {
                throw new UnsupportedOperationException();
            }
            return value;
        }
    }
}