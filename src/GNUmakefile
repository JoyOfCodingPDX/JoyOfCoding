#
# $Id: GNUmakefile,v 1.2 2001-03-21 17:18:23 whitlock Exp $
#

# example top level makefile... This will just descend to a directory.
# and enable the javadocs target.
default: all

TOPDIR=.
SUBDIRS=edu
JAVADOCS=true

include $(TOPDIR)/makefiles/java.gmk

# Makes CS410J docs

SRC_HOME = /u/whitlock/public_html/src
DOC_HOME = /u/whitlock/public_html/docs
CLASSES_HOME = /u/whitlock/classes:/u/whitlock/jars/mail.jar:/u/whitlock/jars/activation.jar:/u/whitlock/jars/xerces.jar

# Lots of spiffy javadoc stuff
WINDOWTITLE = "CS410J Docs"
JAVALINK = "http://java.sun.com/j2se/1.3/docs/api"
JAVAMAILLINK = "http://java.sun.com/products/javamail/javadocs"
JAFLINK = "http://java.sun.com/products/javabeans/glasgow/javadocs"
XERCESLINK = "http://xml.apache.org/apiDocs"
#JAXPLINK = "http://java.sun.com/xml/docs/api"
#SUNXMLLINK = "http://java.sun.com/xml/docs/api/internal"

PACKAGES = edu.pdx.cs410J \
	edu.pdx.cs410J.examples \
	edu.pdx.cs410J.grader \
	edu.pdx.cs410J.familyTree


JDOCFLAGS = -d $(DOC_HOME)        \
            -sourcepath $(SRC_HOME) \
            -classpath $(CLASSES_HOME) \
            -use \
            -splitIndex \
            -windowtitle $(WINDOWTITLE) \
            -link $(JAVALINK) \
	    -link $(JAVAMAILLINK) \
	    -link $(JAFLINK) \
	    -link $(XERCESLINK) \
	    -private \


# Builds the javadoc documentation for the given packages
docs:
	rm -rf $(DOC_HOME)      # Remove old docs
	mkdir $(DOC_HOME)
	-javadoc $(JDOCFLAGS) $(PACKAGES)
	chmod a+rX $(DOC_HOME)  # Let everyone see the docs
	chmod -R a+rX $(DOC_HOME)
