#
# $Id: GNUmakefile,v 1.7 2001-07-25 21:39:30 whitlock Exp $
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
	Command \
	CatCommand \
	CopyCommand \
	FixMidterm \
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
	Report \
	RunCommand \
	Student \
	StudentPanel \
	StudentsList \
	Submit \
	SummaryReport \
	Survey \
	Test \
	Tester \
	TesterExitException \
	TesterSecurityManager \
	XmlDumper \
	XmlParser \


include $(TOPDIR)/makefiles/java.gmk

