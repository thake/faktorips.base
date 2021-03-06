/*******************************************************************************
 * Copyright (c) Faktor Zehn GmbH - faktorzehn.org
 * 
 * This source code is available under the terms of the AGPL Affero General Public License version
 * 3.
 * 
 * Please see LICENSE.txt for full license terms, including the additional permissions and
 * restrictions as well as the possibility of alternative license terms.
 *******************************************************************************/

/* Generated By:JJTree: Do not edit this line. SimpleNode.java */

package org.faktorips.fl.parser;

public class SimpleNode implements Node {
    protected Node parent;
    protected Node[] children;
    protected int id;
    protected FlParser parser;

    private Token firstToken; // added to the class generated by JavaCC
    private Token lastToken; // added to the class generated by JavaCC

    public SimpleNode(int i) {
        id = i;
    }

    public SimpleNode(FlParser p, int i) {
        this(i);
        parser = p;
    }

    /**
     * Returns the last token that has been retrieved from the parser when this node was closed.
     * This method is added to the SimpleNode class generated by JavaCC.
     */
    public Token getFirstToken() {
        return firstToken;
    }

    /**
     * Returns the last token that has been retrieved from the parser when this node was closed.
     * This method is added to the SimpleNode class generated by JavaCC.
     */
    public Token getLastToken() {
        return lastToken;
    }

    @Override
    public void jjtOpen() {
        // added to the code generated by JavaCC
        firstToken = parser.getToken(1);
    }

    @Override
    public void jjtClose() {
        // added to the code generated by JavaCC
        lastToken = parser.getToken(0);
    }

    @Override
    public void jjtSetParent(Node n) {
        parent = n;
    }

    @Override
    public Node jjtGetParent() {
        return parent;
    }

    @Override
    public void jjtAddChild(Node n, int i) {
        if (children == null) {
            children = new Node[i + 1];
        } else if (i >= children.length) {
            Node c[] = new Node[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }
        children[i] = n;
    }

    @Override
    public Node jjtGetChild(int i) {
        return children[i];
    }

    @Override
    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    /** Accept the visitor. **/
    @Override
    public Object jjtAccept(FlParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /** Accept the visitor. **/
    public Object childrenAccept(FlParserVisitor visitor, Object data) {
        if (children != null) {
            for (Node element : children) {
                element.jjtAccept(visitor, data);
            }
        }
        return data;
    }

    /*
     * You can override these two methods in subclasses of SimpleNode to customize the way the node
     * appears when the tree is dumped. If your output uses more than one line you should override
     * toString(String), otherwise overriding toString() is probably all you need to do.
     */

    @Override
    public String toString() {
        return FlParserTreeConstants.jjtNodeName[id];
    }

    public String toString(String prefix) {
        return prefix + toString();
    }

    /*
     * Override this method if you want to customize how the node dumps out its children.
     */

    public void dump(String prefix) {
        System.out.println(toString(prefix));
        if (children != null) {
            for (Node element : children) {
                SimpleNode n = (SimpleNode)element;
                if (n != null) {
                    n.dump(prefix + " "); //$NON-NLS-1$
                }
            }
        }
    }
}
