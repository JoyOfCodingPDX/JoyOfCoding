#
# $Id: GNUmakefile,v 1.1 2001-02-19 23:46:43 whitlock Exp $
#

# example top level makefile... This will just descend to a directory.
default: all

TOPDIR=../../..
PACKAGE=edu.pdx.cs410J
JAR_FILE=cs410J.jar

JAVA_CLASSES= \
        AbstractAirline \
        AbstractFlight \
        AirportNames \
        Dumper \
        Parser \
        ParserException \

# SUBDIRS=examples familyTree grader

include $(TOPDIR)/makefiles/java.gmk
include $(TOPDIR)/makefiles/subdirs.gmk

