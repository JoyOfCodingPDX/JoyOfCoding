#
# $Id: GNUmakefile,v 1.1 2001-02-19 23:04:40 whitlock Exp $
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
	Hello \
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

