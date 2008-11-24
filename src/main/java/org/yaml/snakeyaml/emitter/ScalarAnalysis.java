/**
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.emitter;

/**
 * @see PyYAML 3.06 for more information
 */
class ScalarAnalysis {
    // TODO make getters
    public String scalar;
    public boolean empty;
    public boolean multiline;
    public boolean allowFlowPlain;
    public boolean allowBlockPlain;
    public boolean allowSingleQuoted;
    public boolean allowDoubleQuoted;
    public boolean allowBlock;

    public ScalarAnalysis(final String scalar, final boolean empty, final boolean multiline,
            final boolean allowFlowPlain, final boolean allowBlockPlain,
            final boolean allowSingleQuoted, final boolean allowDoubleQuoted,
            final boolean allowBlock) {
        this.scalar = scalar;
        this.empty = empty;
        this.multiline = multiline;
        this.allowFlowPlain = allowFlowPlain;
        this.allowBlockPlain = allowBlockPlain;
        this.allowSingleQuoted = allowSingleQuoted;
        this.allowDoubleQuoted = allowDoubleQuoted;
        this.allowBlock = allowBlock;
    }
}