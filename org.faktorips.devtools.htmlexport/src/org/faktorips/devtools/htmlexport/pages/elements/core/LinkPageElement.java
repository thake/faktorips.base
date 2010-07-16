package org.faktorips.devtools.htmlexport.pages.elements.core;

import org.faktorips.devtools.htmlexport.generators.ILayouter;

/**
 * {@link PageElement} representing a link
 * 
 * @author dicker
 * 
 */
public class LinkPageElement extends AbstractCompositePageElement {

    /**
     * the link target for e.g. the frame, where the linked file should be loaded
     */
    private String target;
    /**
     * the path of the link
     */
    private String path;

    /**
     * @param path
     * @param target
     * @param pageElements
     */
    public LinkPageElement(String path, String target, PageElement... pageElements) {
        this(path, target);
        addPageElements(pageElements);
    }

    /**
     * @param path
     * @param target
     * @param text
     */
    public LinkPageElement(String path, String target, String text) {
        this(path, target, new TextPageElement(text));
    }

    /**
     * @param path
     * @param target
     */
    private LinkPageElement(String path, String target) {
        this.path = path;
        this.target = target;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return target;
    }

    /**
     * @return the pathFromRoot
     */
    public String getPathFromRoot() {
        return path;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.faktorips.devtools.htmlexport.pages.elements.core.AbstractCompositePageElement#acceptLayouter
     * (org.faktorips.devtools.htmlexport.generators.ILayouter)
     */
    @Override
    public void acceptLayouter(ILayouter layouter) {
        layouter.layoutLinkPageElement(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.faktorips.devtools.htmlexport.pages.elements.core.AbstractCompositePageElement#build()
     */
    @Override
    public void build() {
    }

    /**
     * sets the target
     * 
     * @param target
     */
    public void setTarget(String target) {
        this.target = target;
    }
}
