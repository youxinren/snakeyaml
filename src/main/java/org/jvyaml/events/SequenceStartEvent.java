/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @see PyYAML for more information
 */
public class SequenceStartEvent extends CollectionStartEvent {
    public SequenceStartEvent(final String anchor, final String tag, final boolean implicit, final boolean flowStyle) {
        super(anchor,tag,implicit,flowStyle);
    }
 }
