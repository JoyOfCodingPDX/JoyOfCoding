#
# $Id: GNUmakefile,v 1.3 2002-03-02 23:40:43 whitlock Exp $
#

# example top level makefile... This will just descend to a directory.
default: all

TOPDIR=../../..
PACKAGE=edu.pdx.cs399J
JAR_FILE=cs399J.jar

JAVA_CLASSES= \
        AbstractAirline \
        AbstractFlight \
        AirportNames \
        Dumper \
        Parser \
        ParserException \

SUBDIRS= java core xml net security gui familyTree grader

include $(TOPDIR)/makefiles/java.gmk
include $(TOPDIR)/makefiles/subdirs.gmk

