/*******************************************************************************
 * Copyright (c) 2005,2006 Faktor Zehn GmbH und andere.
 *
 * Alle Rechte vorbehalten.
 *
 * Dieses Programm und alle mitgelieferten Sachen (Dokumentationen, Beispiele,
 * Konfigurationen, etc.) duerfen nur unter den Bedingungen der 
 * Faktor-Zehn-Community Lizenzvereinbarung - Version 0.1 (vor Gruendung Community) 
 * genutzt werden, die Bestandteil der Auslieferung ist und auch unter
 *   http://www.faktorips.org/legal/cl-v01.html
 * eingesehen werden kann.
 *
 * Mitwirkende:
 *   Faktor Zehn GmbH - initial API and implementation - http://www.faktorzehn.de
 *
 *******************************************************************************/

/* Generated By:JJTree&JavaCC: Do not edit this line. FlParser.java */
package org.faktorips.fl.parser;

public class FlParser/*@bgen(jjtree)*/implements FlParserTreeConstants, FlParserConstants {/*@bgen(jjtree)*/
  protected JJTFlParserState jjtree = new JJTFlParserState();public static void main(String args[]) {
    System.out.println("Reading from standard input..."); //$NON-NLS-1$
    FlParser p = new FlParser(System.in);
    try {
      SimpleNode n = p.start();
      n.dump(""); //$NON-NLS-1$
      System.out.println("Thank you."); //$NON-NLS-1$
    } catch (Exception e) {
      System.out.println("Oops."); //$NON-NLS-1$
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }

/***************
 * Productions *
 ***************/
  final public SimpleNode start() throws ParseException {
 /*@bgen(jjtree) Start */
  ASTStart jjtn000 = new ASTStart(this, JJTSTART);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      expr();
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 0:
        jj_consume_token(0);
        break;
      case 18:
        jj_consume_token(18);
        break;
      default:
        jj_la1[0] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      jjtree.closeNodeScope(jjtn000, true);
      jjtc000 = false;
      {if (true) return jjtn000;}
    } catch (Throwable jjte000) {
      if (jjtc000) {
        jjtree.clearNodeScope(jjtn000);
        jjtc000 = false;
      } else {
        jjtree.popNode();
      }
      if (jjte000 instanceof RuntimeException) {
        {if (true) throw (RuntimeException)jjte000;}
      }
      if (jjte000 instanceof ParseException) {
        {if (true) throw (ParseException)jjte000;}
      }
      {if (true) throw (Error)jjte000;}
    } finally {
      if (jjtc000) {
        jjtree.closeNodeScope(jjtn000, true);
      }
    }
    throw new Error("Missing return statement in function"); //$NON-NLS-1$
  }

  final public void expr() throws ParseException {
    equalsExpr();
  }

  final public void equalsExpr() throws ParseException {
    compareExpr();
    label_1:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 19:
      case 20:
        ;
        break;
      default:
        jj_la1[1] = jj_gen;
        break label_1;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 19:
        jj_consume_token(19);
                         ASTEQNode jjtn001 = new ASTEQNode(this, JJTEQNODE);
                         boolean jjtc001 = true;
                         jjtree.openNodeScope(jjtn001);
        try {
          compareExpr();
        } catch (Throwable jjte001) {
                         if (jjtc001) {
                           jjtree.clearNodeScope(jjtn001);
                           jjtc001 = false;
                         } else {
                           jjtree.popNode();
                         }
                         if (jjte001 instanceof RuntimeException) {
                           {if (true) throw (RuntimeException)jjte001;}
                         }
                         if (jjte001 instanceof ParseException) {
                           {if (true) throw (ParseException)jjte001;}
                         }
                         {if (true) throw (Error)jjte001;}
        } finally {
                         if (jjtc001) {
                           jjtree.closeNodeScope(jjtn001,  2);
                         }
        }
        break;
      case 20:
        jj_consume_token(20);
                                                         ASTNotEQNode jjtn002 = new ASTNotEQNode(this, JJTNOTEQNODE);
                                                         boolean jjtc002 = true;
                                                         jjtree.openNodeScope(jjtn002);
        try {
          compareExpr();
        } catch (Throwable jjte002) {
                                                         if (jjtc002) {
                                                           jjtree.clearNodeScope(jjtn002);
                                                           jjtc002 = false;
                                                         } else {
                                                           jjtree.popNode();
                                                         }
                                                         if (jjte002 instanceof RuntimeException) {
                                                           {if (true) throw (RuntimeException)jjte002;}
                                                         }
                                                         if (jjte002 instanceof ParseException) {
                                                           {if (true) throw (ParseException)jjte002;}
                                                         }
                                                         {if (true) throw (Error)jjte002;}
        } finally {
                                                         if (jjtc002) {
                                                           jjtree.closeNodeScope(jjtn002,  2);
                                                         }
        }
        break;
      default:
        jj_la1[2] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void compareExpr() throws ParseException {
    addExpr();
    label_2:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 21:
      case 22:
      case 23:
      case 24:
        ;
        break;
      default:
        jj_la1[3] = jj_gen;
        break label_2;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 21:
        jj_consume_token(21);
                 ASTLTNode jjtn001 = new ASTLTNode(this, JJTLTNODE);
                 boolean jjtc001 = true;
                 jjtree.openNodeScope(jjtn001);
        try {
          addExpr();
        } catch (Throwable jjte001) {
                 if (jjtc001) {
                   jjtree.clearNodeScope(jjtn001);
                   jjtc001 = false;
                 } else {
                   jjtree.popNode();
                 }
                 if (jjte001 instanceof RuntimeException) {
                   {if (true) throw (RuntimeException)jjte001;}
                 }
                 if (jjte001 instanceof ParseException) {
                   {if (true) throw (ParseException)jjte001;}
                 }
                 {if (true) throw (Error)jjte001;}
        } finally {
                 if (jjtc001) {
                   jjtree.closeNodeScope(jjtn001,  2);
                 }
        }
        break;
      case 22:
        jj_consume_token(22);
                 ASTGTNode jjtn002 = new ASTGTNode(this, JJTGTNODE);
                 boolean jjtc002 = true;
                 jjtree.openNodeScope(jjtn002);
        try {
          addExpr();
        } catch (Throwable jjte002) {
                 if (jjtc002) {
                   jjtree.clearNodeScope(jjtn002);
                   jjtc002 = false;
                 } else {
                   jjtree.popNode();
                 }
                 if (jjte002 instanceof RuntimeException) {
                   {if (true) throw (RuntimeException)jjte002;}
                 }
                 if (jjte002 instanceof ParseException) {
                   {if (true) throw (ParseException)jjte002;}
                 }
                 {if (true) throw (Error)jjte002;}
        } finally {
                 if (jjtc002) {
                   jjtree.closeNodeScope(jjtn002,  2);
                 }
        }
        break;
      case 23:
        jj_consume_token(23);
                 ASTLENode jjtn003 = new ASTLENode(this, JJTLENODE);
                 boolean jjtc003 = true;
                 jjtree.openNodeScope(jjtn003);
        try {
          addExpr();
        } catch (Throwable jjte003) {
                 if (jjtc003) {
                   jjtree.clearNodeScope(jjtn003);
                   jjtc003 = false;
                 } else {
                   jjtree.popNode();
                 }
                 if (jjte003 instanceof RuntimeException) {
                   {if (true) throw (RuntimeException)jjte003;}
                 }
                 if (jjte003 instanceof ParseException) {
                   {if (true) throw (ParseException)jjte003;}
                 }
                 {if (true) throw (Error)jjte003;}
        } finally {
                 if (jjtc003) {
                   jjtree.closeNodeScope(jjtn003,  2);
                 }
        }
        break;
      case 24:
        jj_consume_token(24);
                 ASTGENode jjtn004 = new ASTGENode(this, JJTGENODE);
                 boolean jjtc004 = true;
                 jjtree.openNodeScope(jjtn004);
        try {
          addExpr();
        } catch (Throwable jjte004) {
                 if (jjtc004) {
                   jjtree.clearNodeScope(jjtn004);
                   jjtc004 = false;
                 } else {
                   jjtree.popNode();
                 }
                 if (jjte004 instanceof RuntimeException) {
                   {if (true) throw (RuntimeException)jjte004;}
                 }
                 if (jjte004 instanceof ParseException) {
                   {if (true) throw (ParseException)jjte004;}
                 }
                 {if (true) throw (Error)jjte004;}
        } finally {
                 if (jjtc004) {
                   jjtree.closeNodeScope(jjtn004,  2);
                 }
        }
        break;
      default:
        jj_la1[4] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void addExpr() throws ParseException {
    multExpr();
    label_3:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 25:
      case 26:
        ;
        break;
      default:
        jj_la1[5] = jj_gen;
        break label_3;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 25:
        jj_consume_token(25);
                     ASTAddNode jjtn001 = new ASTAddNode(this, JJTADDNODE);
                     boolean jjtc001 = true;
                     jjtree.openNodeScope(jjtn001);
        try {
          multExpr();
        } catch (Throwable jjte001) {
                     if (jjtc001) {
                       jjtree.clearNodeScope(jjtn001);
                       jjtc001 = false;
                     } else {
                       jjtree.popNode();
                     }
                     if (jjte001 instanceof RuntimeException) {
                       {if (true) throw (RuntimeException)jjte001;}
                     }
                     if (jjte001 instanceof ParseException) {
                       {if (true) throw (ParseException)jjte001;}
                     }
                     {if (true) throw (Error)jjte001;}
        } finally {
                     if (jjtc001) {
                       jjtree.closeNodeScope(jjtn001,  2);
                     }
        }
        break;
      case 26:
        jj_consume_token(26);
                                                  ASTSubNode jjtn002 = new ASTSubNode(this, JJTSUBNODE);
                                                  boolean jjtc002 = true;
                                                  jjtree.openNodeScope(jjtn002);
        try {
          multExpr();
        } catch (Throwable jjte002) {
                                                  if (jjtc002) {
                                                    jjtree.clearNodeScope(jjtn002);
                                                    jjtc002 = false;
                                                  } else {
                                                    jjtree.popNode();
                                                  }
                                                  if (jjte002 instanceof RuntimeException) {
                                                    {if (true) throw (RuntimeException)jjte002;}
                                                  }
                                                  if (jjte002 instanceof ParseException) {
                                                    {if (true) throw (ParseException)jjte002;}
                                                  }
                                                  {if (true) throw (Error)jjte002;}
        } finally {
                                                  if (jjtc002) {
                                                    jjtree.closeNodeScope(jjtn002,  2);
                                                  }
        }
        break;
      default:
        jj_la1[6] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void multExpr() throws ParseException {
    unaryExpr();
    label_4:
    while (true) {
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 27:
      case 28:
        ;
        break;
      default:
        jj_la1[7] = jj_gen;
        break label_4;
      }
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 27:
        jj_consume_token(27);
                       ASTMultNode jjtn001 = new ASTMultNode(this, JJTMULTNODE);
                       boolean jjtc001 = true;
                       jjtree.openNodeScope(jjtn001);
        try {
          unaryExpr();
        } catch (Throwable jjte001) {
                       if (jjtc001) {
                         jjtree.clearNodeScope(jjtn001);
                         jjtc001 = false;
                       } else {
                         jjtree.popNode();
                       }
                       if (jjte001 instanceof RuntimeException) {
                         {if (true) throw (RuntimeException)jjte001;}
                       }
                       if (jjte001 instanceof ParseException) {
                         {if (true) throw (ParseException)jjte001;}
                       }
                       {if (true) throw (Error)jjte001;}
        } finally {
                       if (jjtc001) {
                         jjtree.closeNodeScope(jjtn001,  2);
                       }
        }
        break;
      case 28:
        jj_consume_token(28);
                                                      ASTDivNode jjtn002 = new ASTDivNode(this, JJTDIVNODE);
                                                      boolean jjtc002 = true;
                                                      jjtree.openNodeScope(jjtn002);
        try {
          unaryExpr();
        } catch (Throwable jjte002) {
                                                      if (jjtc002) {
                                                        jjtree.clearNodeScope(jjtn002);
                                                        jjtc002 = false;
                                                      } else {
                                                        jjtree.popNode();
                                                      }
                                                      if (jjte002 instanceof RuntimeException) {
                                                        {if (true) throw (RuntimeException)jjte002;}
                                                      }
                                                      if (jjte002 instanceof ParseException) {
                                                        {if (true) throw (ParseException)jjte002;}
                                                      }
                                                      {if (true) throw (Error)jjte002;}
        } finally {
                                                      if (jjtc002) {
                                                        jjtree.closeNodeScope(jjtn002,  2);
                                                      }
        }
        break;
      default:
        jj_la1[8] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
    }
  }

  final public void unaryExpr() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 25:
    case 26:
    case 29:
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case 25:
        jj_consume_token(25);
            ASTPlusNode jjtn001 = new ASTPlusNode(this, JJTPLUSNODE);
            boolean jjtc001 = true;
            jjtree.openNodeScope(jjtn001);
        try {
          unaryExpr();
        } catch (Throwable jjte001) {
            if (jjtc001) {
              jjtree.clearNodeScope(jjtn001);
              jjtc001 = false;
            } else {
              jjtree.popNode();
            }
            if (jjte001 instanceof RuntimeException) {
              {if (true) throw (RuntimeException)jjte001;}
            }
            if (jjte001 instanceof ParseException) {
              {if (true) throw (ParseException)jjte001;}
            }
            {if (true) throw (Error)jjte001;}
        } finally {
            if (jjtc001) {
              jjtree.closeNodeScope(jjtn001,  1);
            }
        }
        break;
      case 26:
        jj_consume_token(26);
                                           ASTMinusNode jjtn002 = new ASTMinusNode(this, JJTMINUSNODE);
                                           boolean jjtc002 = true;
                                           jjtree.openNodeScope(jjtn002);
        try {
          unaryExpr();
        } catch (Throwable jjte002) {
                                           if (jjtc002) {
                                             jjtree.clearNodeScope(jjtn002);
                                             jjtc002 = false;
                                           } else {
                                             jjtree.popNode();
                                           }
                                           if (jjte002 instanceof RuntimeException) {
                                             {if (true) throw (RuntimeException)jjte002;}
                                           }
                                           if (jjte002 instanceof ParseException) {
                                             {if (true) throw (ParseException)jjte002;}
                                           }
                                           {if (true) throw (Error)jjte002;}
        } finally {
                                           if (jjtc002) {
                                             jjtree.closeNodeScope(jjtn002,  1);
                                           }
        }
        break;
      case 29:
        jj_consume_token(29);
                                                                           ASTNotNode jjtn003 = new ASTNotNode(this, JJTNOTNODE);
                                                                           boolean jjtc003 = true;
                                                                           jjtree.openNodeScope(jjtn003);
        try {
          unaryExpr();
        } catch (Throwable jjte003) {
                                                                           if (jjtc003) {
                                                                             jjtree.clearNodeScope(jjtn003);
                                                                             jjtc003 = false;
                                                                           } else {
                                                                             jjtree.popNode();
                                                                           }
                                                                           if (jjte003 instanceof RuntimeException) {
                                                                             {if (true) throw (RuntimeException)jjte003;}
                                                                           }
                                                                           if (jjte003 instanceof ParseException) {
                                                                             {if (true) throw (ParseException)jjte003;}
                                                                           }
                                                                           {if (true) throw (Error)jjte003;}
        } finally {
                                                                           if (jjtc003) {
                                                                             jjtree.closeNodeScope(jjtn003,  1);
                                                                           }
        }
        break;
      default:
        jj_la1[9] = jj_gen;
        jj_consume_token(-1);
        throw new ParseException();
      }
      break;
    case BOOLEAN_LITERAL:
    case INTEGER_LITERAL:
    case DECIMAL_LITERAL:
    case STRING_LITERAL:
    case MONEY_LITERAL:
    case NULL_LITERAL:
    case IDENTIFIER:
    case 30:
      primitiveExpr();
      break;
    default:
      jj_la1[10] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final public void primitiveExpr() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case 30:
      jj_consume_token(30);
      expr();
                 ASTParenthesisNode jjtn001 = new ASTParenthesisNode(this, JJTPARENTHESISNODE);
                 boolean jjtc001 = true;
                 jjtree.openNodeScope(jjtn001);
      try {
        jj_consume_token(31);
      } finally {
                 if (jjtc001) {
                   jjtree.closeNodeScope(jjtn001,  1);
                 }
      }
      break;
    default:
      jj_la1[11] = jj_gen;
      if (jj_2_1(2)) {
        functionCallExpr();
      } else {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case BOOLEAN_LITERAL:
        case INTEGER_LITERAL:
        case DECIMAL_LITERAL:
        case STRING_LITERAL:
        case MONEY_LITERAL:
        case NULL_LITERAL:
        case IDENTIFIER:
          literal();
          break;
        default:
          jj_la1[12] = jj_gen;
          jj_consume_token(-1);
          throw new ParseException();
        }
      }
    }
  }

  final public void functionCallExpr() throws ParseException {
 /*@bgen(jjtree) FunctionCallNode */
  ASTFunctionCallNode jjtn000 = new ASTFunctionCallNode(this, JJTFUNCTIONCALLNODE);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      jj_consume_token(IDENTIFIER);
      jj_consume_token(30);
      switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
      case BOOLEAN_LITERAL:
      case INTEGER_LITERAL:
      case DECIMAL_LITERAL:
      case STRING_LITERAL:
      case MONEY_LITERAL:
      case NULL_LITERAL:
      case IDENTIFIER:
      case 25:
      case 26:
      case 29:
      case 30:
        argumentList();
        break;
      default:
        jj_la1[13] = jj_gen;
        ;
      }
      jj_consume_token(31);
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void argumentList() throws ParseException {
 /*@bgen(jjtree) ArgListNode */
  ASTArgListNode jjtn000 = new ASTArgListNode(this, JJTARGLISTNODE);
  boolean jjtc000 = true;
  jjtree.openNodeScope(jjtn000);
    try {
      expr();
      label_5:
      while (true) {
        switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
        case 18:
          ;
          break;
        default:
          jj_la1[14] = jj_gen;
          break label_5;
        }
        jj_consume_token(18);
        expr();
      }
    } catch (Throwable jjte000) {
          if (jjtc000) {
            jjtree.clearNodeScope(jjtn000);
            jjtc000 = false;
          } else {
            jjtree.popNode();
          }
          if (jjte000 instanceof RuntimeException) {
            {if (true) throw (RuntimeException)jjte000;}
          }
          if (jjte000 instanceof ParseException) {
            {if (true) throw (ParseException)jjte000;}
          }
          {if (true) throw (Error)jjte000;}
    } finally {
          if (jjtc000) {
            jjtree.closeNodeScope(jjtn000, true);
          }
    }
  }

  final public void literal() throws ParseException {
    switch ((jj_ntk==-1)?jj_ntk():jj_ntk) {
    case IDENTIFIER:
          ASTIdentifierNode jjtn001 = new ASTIdentifierNode(this, JJTIDENTIFIERNODE);
          boolean jjtc001 = true;
          jjtree.openNodeScope(jjtn001);
      try {
        jj_consume_token(IDENTIFIER);
      } finally {
          if (jjtc001) {
            jjtree.closeNodeScope(jjtn001, true);
          }
      }
      break;
    case BOOLEAN_LITERAL:
      ASTBooleanNode jjtn002 = new ASTBooleanNode(this, JJTBOOLEANNODE);
      boolean jjtc002 = true;
      jjtree.openNodeScope(jjtn002);
      try {
        jj_consume_token(BOOLEAN_LITERAL);
      } finally {
      if (jjtc002) {
        jjtree.closeNodeScope(jjtn002, true);
      }
      }
      break;
    case INTEGER_LITERAL:
      ASTIntegerNode jjtn003 = new ASTIntegerNode(this, JJTINTEGERNODE);
      boolean jjtc003 = true;
      jjtree.openNodeScope(jjtn003);
      try {
        jj_consume_token(INTEGER_LITERAL);
      } finally {
      if (jjtc003) {
        jjtree.closeNodeScope(jjtn003, true);
      }
      }
      break;
    case DECIMAL_LITERAL:
      ASTDecimalNode jjtn004 = new ASTDecimalNode(this, JJTDECIMALNODE);
      boolean jjtc004 = true;
      jjtree.openNodeScope(jjtn004);
      try {
        jj_consume_token(DECIMAL_LITERAL);
      } finally {
      if (jjtc004) {
        jjtree.closeNodeScope(jjtn004, true);
      }
      }
      break;
    case STRING_LITERAL:
      ASTStringNode jjtn005 = new ASTStringNode(this, JJTSTRINGNODE);
      boolean jjtc005 = true;
      jjtree.openNodeScope(jjtn005);
      try {
        jj_consume_token(STRING_LITERAL);
      } finally {
      if (jjtc005) {
        jjtree.closeNodeScope(jjtn005, true);
      }
      }
      break;
    case MONEY_LITERAL:
      ASTMoneyNode jjtn006 = new ASTMoneyNode(this, JJTMONEYNODE);
      boolean jjtc006 = true;
      jjtree.openNodeScope(jjtn006);
      try {
        jj_consume_token(MONEY_LITERAL);
      } finally {
      if (jjtc006) {
        jjtree.closeNodeScope(jjtn006, true);
      }
      }
      break;
    case NULL_LITERAL:
      ASTNullNode jjtn007 = new ASTNullNode(this, JJTNULLNODE);
      boolean jjtc007 = true;
      jjtree.openNodeScope(jjtn007);
      try {
        jj_consume_token(NULL_LITERAL);
      } finally {
      if (jjtc007) {
        jjtree.closeNodeScope(jjtn007, true);
      }
      }
      break;
    default:
      jj_la1[15] = jj_gen;
      jj_consume_token(-1);
      throw new ParseException();
    }
  }

  final private boolean jj_2_1(int xla) {
    jj_la = xla; jj_lastpos = jj_scanpos = token;
    try { return !jj_3_1(); }
    catch(LookaheadSuccess ls) { return true; }
    finally { jj_save(0, xla); }
  }

  final private boolean jj_3_1() {
    if (jj_3R_6()) return true;
    return false;
  }

  final private boolean jj_3R_6() {
    if (jj_scan_token(IDENTIFIER)) return true;
    if (jj_scan_token(30)) return true;
    return false;
  }

  public FlParserTokenManager token_source;
  JavaCharStream jj_input_stream;
  public Token token, jj_nt;
  private int jj_ntk;
  private Token jj_scanpos, jj_lastpos;
  private int jj_la;
  public boolean lookingAhead = false;
  private boolean jj_semLA;
  private int jj_gen;
  final private int[] jj_la1 = new int[16];
  static private int[] jj_la1_0;
  static {
      jj_la1_0();
   }
   private static void jj_la1_0() {
      jj_la1_0 = new int[] {0x40001,0x180000,0x180000,0x1e00000,0x1e00000,0x6000000,0x6000000,0x18000000,0x18000000,0x26000000,0x6600db80,0x40000000,0xdb80,0x6600db80,0x40000,0xdb80,};
   }
  final private JJCalls[] jj_2_rtns = new JJCalls[1];
  private boolean jj_rescan = false;
  private int jj_gc = 0;

  public FlParser(java.io.InputStream stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new FlParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.InputStream stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public FlParser(java.io.Reader stream) {
    jj_input_stream = new JavaCharStream(stream, 1, 1);
    token_source = new FlParserTokenManager(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(java.io.Reader stream) {
    jj_input_stream.ReInit(stream, 1, 1);
    token_source.ReInit(jj_input_stream);
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public FlParser(FlParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  public void ReInit(FlParserTokenManager tm) {
    token_source = tm;
    token = new Token();
    jj_ntk = -1;
    jjtree.reset();
    jj_gen = 0;
    for (int i = 0; i < 16; i++) jj_la1[i] = -1;
    for (int i = 0; i < jj_2_rtns.length; i++) jj_2_rtns[i] = new JJCalls();
  }

  final private Token jj_consume_token(int kind) throws ParseException {
    Token oldToken;
    if ((oldToken = token).next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    if (token.kind == kind) {
      jj_gen++;
      if (++jj_gc > 100) {
        jj_gc = 0;
        for (int i = 0; i < jj_2_rtns.length; i++) {
          JJCalls c = jj_2_rtns[i];
          while (c != null) {
            if (c.gen < jj_gen) c.first = null;
            c = c.next;
          }
        }
      }
      return token;
    }
    token = oldToken;
    jj_kind = kind;
    throw generateParseException();
  }

  static private final class LookaheadSuccess extends java.lang.Error { }
  final private LookaheadSuccess jj_ls = new LookaheadSuccess();
  final private boolean jj_scan_token(int kind) {
    if (jj_scanpos == jj_lastpos) {
      jj_la--;
      if (jj_scanpos.next == null) {
        jj_lastpos = jj_scanpos = jj_scanpos.next = token_source.getNextToken();
      } else {
        jj_lastpos = jj_scanpos = jj_scanpos.next;
      }
    } else {
      jj_scanpos = jj_scanpos.next;
    }
    if (jj_rescan) {
      int i = 0; Token tok = token;
      while (tok != null && tok != jj_scanpos) { i++; tok = tok.next; }
      if (tok != null) jj_add_error_token(kind, i);
    }
    if (jj_scanpos.kind != kind) return true;
    if (jj_la == 0 && jj_scanpos == jj_lastpos) throw jj_ls;
    return false;
  }

  final public Token getNextToken() {
    if (token.next != null) token = token.next;
    else token = token.next = token_source.getNextToken();
    jj_ntk = -1;
    jj_gen++;
    return token;
  }

  final public Token getToken(int index) {
    Token t = lookingAhead ? jj_scanpos : token;
    for (int i = 0; i < index; i++) {
      if (t.next != null) t = t.next;
      else t = t.next = token_source.getNextToken();
    }
    return t;
  }

  final private int jj_ntk() {
    if ((jj_nt=token.next) == null)
      return (jj_ntk = (token.next=token_source.getNextToken()).kind);
    else
      return (jj_ntk = jj_nt.kind);
  }

  private java.util.Vector jj_expentries = new java.util.Vector();
  private int[] jj_expentry;
  private int jj_kind = -1;
  private int[] jj_lasttokens = new int[100];
  private int jj_endpos;

  private void jj_add_error_token(int kind, int pos) {
    if (pos >= 100) return;
    if (pos == jj_endpos + 1) {
      jj_lasttokens[jj_endpos++] = kind;
    } else if (jj_endpos != 0) {
      jj_expentry = new int[jj_endpos];
      for (int i = 0; i < jj_endpos; i++) {
        jj_expentry[i] = jj_lasttokens[i];
      }
      boolean exists = false;
      for (java.util.Enumeration e = jj_expentries.elements(); e.hasMoreElements();) {
        int[] oldentry = (int[])(e.nextElement());
        if (oldentry.length == jj_expentry.length) {
          exists = true;
          for (int i = 0; i < jj_expentry.length; i++) {
            if (oldentry[i] != jj_expentry[i]) {
              exists = false;
              break;
            }
          }
          if (exists) break;
        }
      }
      if (!exists) jj_expentries.addElement(jj_expentry);
      if (pos != 0) jj_lasttokens[(jj_endpos = pos) - 1] = kind;
    }
  }

  public ParseException generateParseException() {
    jj_expentries.removeAllElements();
    boolean[] la1tokens = new boolean[32];
    for (int i = 0; i < 32; i++) {
      la1tokens[i] = false;
    }
    if (jj_kind >= 0) {
      la1tokens[jj_kind] = true;
      jj_kind = -1;
    }
    for (int i = 0; i < 16; i++) {
      if (jj_la1[i] == jj_gen) {
        for (int j = 0; j < 32; j++) {
          if ((jj_la1_0[i] & (1<<j)) != 0) {
            la1tokens[j] = true;
          }
        }
      }
    }
    for (int i = 0; i < 32; i++) {
      if (la1tokens[i]) {
        jj_expentry = new int[1];
        jj_expentry[0] = i;
        jj_expentries.addElement(jj_expentry);
      }
    }
    jj_endpos = 0;
    jj_rescan_token();
    jj_add_error_token(0, 0);
    int[][] exptokseq = new int[jj_expentries.size()][];
    for (int i = 0; i < jj_expentries.size(); i++) {
      exptokseq[i] = (int[])jj_expentries.elementAt(i);
    }
    return new ParseException(token, exptokseq, tokenImage);
  }

  final public void enable_tracing() {
  }

  final public void disable_tracing() {
  }

  final private void jj_rescan_token() {
    jj_rescan = true;
    for (int i = 0; i < 1; i++) {
      JJCalls p = jj_2_rtns[i];
      do {
        if (p.gen > jj_gen) {
          jj_la = p.arg; jj_lastpos = jj_scanpos = p.first;
          switch (i) {
            case 0: jj_3_1(); break;
          }
        }
        p = p.next;
      } while (p != null);
    }
    jj_rescan = false;
  }

  final private void jj_save(int index, int xla) {
    JJCalls p = jj_2_rtns[index];
    while (p.gen > jj_gen) {
      if (p.next == null) { p = p.next = new JJCalls(); break; }
      p = p.next;
    }
    p.gen = jj_gen + xla - jj_la; p.first = token; p.arg = xla;
  }

  static final class JJCalls {
    int gen;
    Token first;
    int arg;
    JJCalls next;
  }

}
