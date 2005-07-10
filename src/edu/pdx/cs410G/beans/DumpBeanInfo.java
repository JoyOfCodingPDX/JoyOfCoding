package edu.pdx.cs410G.beans;

import java.beans.*;
import java.io.*;
import java.util.Enumeration;

/**
 * Uses the JavaBeans {@link Introspector} to print information about
 * a JavaBean class.
 *
 * @author David Whitlock
 * @since Fall 2004
 */
public class DumpBeanInfo {
  private static PrintStream out = System.out;
  private static PrintStream err = System.err;

  /**
   * Dumps bean information about a class whose name is read from the
   * command line.
   */
  public static void main(String[] args) {
    String className = null;

    for (int i = 0; i < args.length; i++) {
      if (className == null) {
        className = args[i];

      } else {
        err.println("Extraneous command line: " + args[i]);
        System.exit(1);
      }
    }

    if (className == null) {
      err.println("Missing class name");
      System.exit(1);
    }

    Class c;
    try {
      c = Class.forName(className);

    } catch (ClassNotFoundException ex) {
      err.println("Cannot load class \"" + className + "\"");
      System.exit(1);
      c = null;
    }

    BeanInfo info;
    try {
      info = Introspector.getBeanInfo(c);

    } catch (IntrospectionException ex) {
      err.println("While getting BeanInfo on " + c.getName());
      ex.printStackTrace(err);
      System.exit(1);
      info = null;
    }

    BeanDescriptor desc = info.getBeanDescriptor();
    out.println("Bean Class: " + desc.getBeanClass().getName());
    Class customizer = desc.getCustomizerClass();
    if (customizer != null) {
      out.println(indent(2) + "Customizer: " + customizer.getName());
    }

    PropertyDescriptor[] props = info.getPropertyDescriptors();
    if (props.length > 0) {
      out.println(indent(2) + "Properties:");
      for (int i = 0; i < props.length; i++) {
        PropertyDescriptor prop = props[i];
        dumpFeatureDescriptor(prop, 4);
        Class type = prop.getPropertyType();
        if (type != null) {
          out.println(indent(6) + "Type: " + type.getName());
        }
        out.print(indent(6));
        if (prop.isBound()) {
          out.print("Bound ");

        } else {
          out.print("Unbound ");
        }
        if (prop.isConstrained()) {
          out.println("Constrained");

        } else {
          out.println("Unconstrained");
        }

        Class editor = prop.getPropertyEditorClass();
        if (editor != null) {
          out.println(indent(6) + "Property editor: " + editor);
        }
      }
    }

    MethodDescriptor[] methods = info.getMethodDescriptors();
    if (methods.length > 0) {
      out.println(indent(2) + "Methods:");
      for (int i = 0; i < methods.length; i++) {
        MethodDescriptor method = methods[i];
        dumpMethodDescriptor(method, 4);
      }
    }

    EventSetDescriptor[] events = info.getEventSetDescriptors();
    if (events.length > 0) {
      out.println(indent(2) + "Events:");
      for (int i = 0; i < events.length; i++) {
        EventSetDescriptor event = events[i];
        dumpFeatureDescriptor(event, 4);
        out.print(indent(6));
        if (event.isUnicast()) {
          out.print("Unicast, ");

        } else {
          out.print("Multicast, ");
        }

        if (event.isInDefaultEventSet()) {
          out.println("In default event set");

        } else {
          out.println("Not in default event set");
        }

        MethodDescriptor[] listeners =
          event.getListenerMethodDescriptors();
        if (listeners.length > 0) {
          out.println(indent(6) + "Listener Methods");
          for (int j = 0; j < listeners.length; j++) {
            MethodDescriptor listener = listeners[j];
            dumpMethodDescriptor(listener, 8);
          }
        }
      }
    }

  }

  /**
   * Dumps information about a method descritpro
   */
  private static void dumpMethodDescriptor(MethodDescriptor method,
                                           int indent) {
    dumpFeatureDescriptor(method, indent);

    ParameterDescriptor[] params =
      method.getParameterDescriptors();
    if (params != null && params.length > 0) {
      out.println(indent(indent) + "Parameters:");
      for (int i = 0; i < params.length; i++) {
        ParameterDescriptor param = params[i];
        dumpFeatureDescriptor(param, indent + 2);
      }
    }
  }

  /**
   * Dumps information about a feature descriptor
   */
  private static void dumpFeatureDescriptor(FeatureDescriptor desc,
                                            int indent) {
    String displayName = desc.getDisplayName();
    out.print(indent(indent) + desc.getName());
    if (displayName != null && !displayName.equals(desc.getName())) {
      out.print(" \"" + displayName + "\"");
    }
    out.println("");
    
    String description = desc.getShortDescription();
    if (description != null && !description.equals(desc.getName())) {
      out.println(indent(indent + 2) + "\"" + description + "\"");
    }

    StringBuffer flags = new StringBuffer();
    if (desc.isExpert()) {
      flags.append("expert ");
    }
    if (desc.isHidden()) {
      flags.append("hidden ");
    }
    if (desc.isPreferred()) {
      flags.append("preferred ");
    }
    if (flags.length() > 0) {
      out.println(indent(indent + 2) + "Flags: " + flags);
    }

    Enumeration attrs = desc.attributeNames();
    if (attrs.hasMoreElements()) {
      out.println(indent(indent + 2) + "Attributes");

      while (attrs.hasMoreElements()) {
        String name = (String) attrs.nextElement();
        Object value = desc.getValue(name);
        out.println(indent(indent + 4) + name + " = " + value);
      }
    }
  }

  /**
   * Indents a given number of spaces
   */
  private static String indent(int indent) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < indent; i++) {
      sb.append(' ');
    }
    return sb.toString();
  }

}
