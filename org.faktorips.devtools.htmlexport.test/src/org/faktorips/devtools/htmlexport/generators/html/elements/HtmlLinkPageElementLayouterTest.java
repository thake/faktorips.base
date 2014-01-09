/*******************************************************************************
 * Copyright (c) Faktor Zehn AG. <http://www.faktorzehn.org>
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version 3
 * and if and when this source code belongs to the faktorips-runtime or faktorips-valuetype
 * component under the terms of the LGPL Lesser General Public License version 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and the
 * possibility of alternative license terms.
 *******************************************************************************/

package org.faktorips.devtools.htmlexport.generators.html.elements;

import org.faktorips.devtools.htmlexport.helper.path.TargetType;
import org.faktorips.devtools.htmlexport.pages.elements.core.LinkPageElement;
import org.faktorips.devtools.htmlexport.pages.elements.core.Style;
import org.junit.Before;
import org.junit.Test;

public class HtmlLinkPageElementLayouterTest extends AbstractHtmlPageElementLayouterTest {

    private static final String PATH_TO_ROOT = "../../";
    private static final String FILE_EXTENSION = ".html";

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        layouter.setPathToRoot(PATH_TO_ROOT);
    }

    @Test
    public void testLink() throws Exception {
        String path = "xyz/sub/file";
        TargetType target = TargetType.CONTENT;
        String text = "Linktext";

        LinkPageElement pageElement = new LinkPageElement(path, target, text);

        HtmlLinkPageElementLayouter elementLayouter = new HtmlLinkPageElementLayouter(pageElement, layouter);

        elementLayouter.layout();

        String generateText = layouter.generateText();

        assertXpathExists(generateText, "/a[@target='" + target.getId() + "'][@href='" + PATH_TO_ROOT + path
                + FILE_EXTENSION + "'][.='" + text + "']");
    }

    @Test
    public void testLinkMitStyle() throws Exception {
        String path = "xyz/sub/file";
        TargetType target = TargetType.CONTENT;
        String text = "Linktext";

        LinkPageElement pageElement = new LinkPageElement(path, target, text);
        pageElement.addStyles(Style.BOLD);

        HtmlLinkPageElementLayouter elementLayouter = new HtmlLinkPageElementLayouter(pageElement, layouter);

        elementLayouter.layout();

        assertXpathExists(layouter.generateText(), "/a[@class='BOLD']");
    }

    @Test
    public void testLinkMitBlockStyle() throws Exception {
        String path = "xyz/sub/file";
        TargetType target = TargetType.CONTENT;
        String text = "Linktext";

        LinkPageElement pageElement = new LinkPageElement(path, target, text);
        pageElement.addStyles(Style.BLOCK);

        HtmlLinkPageElementLayouter elementLayouter = new HtmlLinkPageElementLayouter(pageElement, layouter);

        elementLayouter.layout();

        assertXpathExists(layouter.generateText(), "/div/a[.='" + text + "']");
    }

    @Test
    public void testLinkMitAnker() throws Exception {
        String path = "xyz/sub/file";
        TargetType target = TargetType.CONTENT;
        String text = "Linktext";
        String linkAnchor = "anker";

        LinkPageElement pageElement = new LinkPageElement(path, target, text);
        pageElement.setLinkAnchor(linkAnchor);

        HtmlLinkPageElementLayouter elementLayouter = new HtmlLinkPageElementLayouter(pageElement, layouter);

        elementLayouter.layout();

        String generateText = layouter.generateText();

        assertXpathExists(generateText, "/a[.='" + text + "'][@href='" + PATH_TO_ROOT + path + FILE_EXTENSION + "#"
                + linkAnchor + "']");
    }

}
