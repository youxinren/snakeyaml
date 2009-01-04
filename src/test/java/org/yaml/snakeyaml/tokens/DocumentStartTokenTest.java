package org.yaml.snakeyaml.tokens;

import junit.framework.TestCase;

import org.yaml.snakeyaml.error.Mark;

public class DocumentStartTokenTest extends TestCase {

    public void testGetTokenId() {
        Mark mark = new Mark("test1", 0, 0, 0, "*The first line.\nThe last line.", 0);
        DocumentStartToken token = new DocumentStartToken(mark, mark);
        assertEquals("<document start>", token.getTokenId());
    }
}