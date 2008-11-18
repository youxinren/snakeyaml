/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.composer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.MappingEndEvent;
import org.yaml.snakeyaml.events.MappingStartEvent;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.events.SequenceEndEvent;
import org.yaml.snakeyaml.events.SequenceStartEvent;
import org.yaml.snakeyaml.events.StreamEndEvent;
import org.yaml.snakeyaml.events.StreamStartEvent;
import org.yaml.snakeyaml.nodes.CollectionNode;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * @see PyYAML 3.06 for more information
 */
public class ComposerImpl implements Composer {
    private Parser parser;
    private Resolver resolver;
    private Map<String, Node> anchors;

    public ComposerImpl(final Parser parser, final Resolver resolver) {
        this.parser = parser;
        this.resolver = resolver;
        this.anchors = new HashMap<String, Node>();
    }

    public boolean checkNode() {
        // Drop the STREAM-START event.
        if (parser.checkEvent(StreamStartEvent.class)) {
            parser.getEvent();
        }
        // If there are more documents available?
        return !parser.checkEvent(StreamEndEvent.class);
    }

    public Node getNode() {
        // Get the root node of the next document.
        if (!parser.checkEvent(StreamEndEvent.class)) {
            return composeDocument();
        } else {
            return (Node) null;
        }
    }

    public Node getSingleNode() {
        // Drop the STREAM-START event.
        parser.getEvent();
        // Compose a document if the stream is not empty.
        Node document = null;
        if (!parser.checkEvent(StreamEndEvent.class)) {
            document = composeDocument();
        }
        // Ensure that the stream contains no more documents.
        if (!parser.checkEvent(StreamEndEvent.class)) {
            Event event = parser.getEvent();
            throw new ComposerException("expected a single document in the stream", document
                    .getStartMark(), "but found another document", event.getStartMark());
        }
        // Drop the STREAM-END event.
        parser.getEvent();
        return document;
    }

    public Node composeDocument() {
        // Drop the DOCUMENT-START event.
        parser.getEvent();
        // Compose the root node.
        final Node node = composeNode(null, null);
        // Drop the DOCUMENT-END event.
        parser.getEvent();
        this.anchors.clear();
        return node;
    }

    public Node composeNode(final Node parent, final Object index) {
        if (parser.checkEvent(AliasEvent.class)) {
            AliasEvent event = (AliasEvent) parser.getEvent();
            String anchor = event.getAnchor();
            if (!anchors.containsKey(anchor)) {
                throw new ComposerException(null, null, "found undefined alias " + anchor, event
                        .getStartMark());
            }
            return (Node) anchors.get(anchor);
        }
        Event event = parser.peekEvent();
        String anchor = null;
        if (event instanceof NodeEvent) {
            anchor = ((NodeEvent) event).getAnchor();
        }
        if (anchor != null) {
            if (anchors.containsKey(anchor)) {
                throw new ComposerException("found duplicate anchor " + anchor
                        + "; first occurence", this.anchors.get(anchor).getStartMark(),
                        "second occurence", event.getStartMark());
            }
        }
        resolver.descendResolver(parent, index);
        Node node = null;
        if (parser.checkEvent(ScalarEvent.class)) {
            node = composeScalarNode(anchor);
        } else if (parser.checkEvent(SequenceStartEvent.class)) {
            node = composeSequenceNode(anchor);
        } else if (parser.checkEvent(MappingStartEvent.class)) {
            node = composeMappingNode(anchor);
        }
        resolver.ascendResolver();
        return node;
    }

    private Node composeScalarNode(String anchor) {
        ScalarEvent ev = (ScalarEvent) parser.getEvent();
        String tag = ev.getTag();
        if (tag == null || tag.equals("!")) {
            tag = resolver.resolve(ScalarNode.class, ev.getValue(), ev.getImplicit()[0]);
        }
        Node node = new ScalarNode(tag, ev.getValue(), ev.getStartMark(), ev.getEndMark(), ev
                .getStyle());
        if (anchor != null) {
            anchors.put(anchor, node);
        }
        return node;
    }

    @SuppressWarnings("unchecked")
    private Node composeSequenceNode(String anchor) {
        SequenceStartEvent startEvent = (SequenceStartEvent) parser.getEvent();
        String tag = startEvent.getTag();
        if (tag == null || tag.equals("!")) {
            // TODO get rid of TRU and FALS
            tag = resolver.resolve(SequenceNode.class, null, startEvent.getImplicit());
        }
        CollectionNode node = new SequenceNode(tag, new ArrayList<Object>(), startEvent
                .getStartMark(), null, startEvent.getFlowStyle());
        if (anchor != null) {
            anchors.put(anchor, node);
        }
        int index = 0;
        while (!parser.checkEvent(SequenceEndEvent.class)) {
            ((List<Object>) node.getValue()).add(composeNode(node, new Integer(index)));
            index++;
        }
        Event endEvent = parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }

    private Node composeMappingNode(String anchor) {
        MappingStartEvent startEvent = (MappingStartEvent) parser.getEvent();
        String tag = startEvent.getTag();
        if (tag == null || tag.equals("!")) {
            tag = resolver.resolve(MappingNode.class, null, startEvent.getImplicit());
        }
        // TODO must be list instead !!!
        MappingNode node = new MappingNode(tag, new HashMap(), startEvent.getStartMark(), null,
                startEvent.getFlowStyle());
        if (anchor != null) {
            anchors.put(anchor, node);
        }
        while (!parser.checkEvent(MappingEndEvent.class)) {
            // final Event key = parser.peekEvent();
            Node itemKey = composeNode(node, null);
            Node itemValue = composeNode(node, itemKey);
            // TODO unclear,different from PyYAML, it must be List !!!
            ((Map) node.getValue()).put(itemKey, itemValue);// 
        }
        Event endEvent = parser.getEvent();
        node.setEndMark(endEvent.getEndMark());
        return node;
    }
}