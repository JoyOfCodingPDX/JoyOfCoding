#
# $Id: GNUmakefile,v 1.2 2001-07-26 01:14:40 whitlock Exp $
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

SUBDIRS= java core xml net security gui familyTree grader

include $(TOPDIR)/makefiles/java.gmk
include $(TOPDIR)/makefiles/subdirs.gmk

