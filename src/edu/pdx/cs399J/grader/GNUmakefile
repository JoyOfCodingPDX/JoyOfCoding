#
# $Id: GNUmakefile,v 1.4 2001-02-26 00:53:21 whitlock Exp $
#

# example leaf node for building a java package
default: all

TOPDIR=../../../..
PACKAGE=edu.pdx.cs410J.grader
JAR_FILE=grader.jar
MANIFEST=Manifest.mf

EXTRA_JARS= \
	xerces.jar \
        mail.jar \
        activation.jar \


JAVA_CLASSES= \
	Assignment \
	AssignmentPanel \
	ClassPanel \
	CloseStream \
	Grade \
	GradeBook \
        GradeBookPanel \
        GradePanel \
	NewAssignmentDialog \
	Notable \
	NotesPanel \
	ParseTextFile \
	Student \
	StudentPanel \
	StudentsList \
	Submit \
	SummaryReport \
	Survey \
	Tester \
	TesterExitException \
	TesterSecurityManager \
	XmlDumper \
	XmlParser \


include $(TOPDIR)/makefiles/java.gmk

