#
# $Id: GNUmakefile,v 1.9 2002-03-02 23:40:58 whitlock Exp $
#

# example leaf node for building a java package
default: all

TOPDIR=../../../..
PACKAGE=edu.pdx.cs399J.grader
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
	GenerateMailrc \
	Grade \
	GradeBook \
        GradeBookGUI \
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

