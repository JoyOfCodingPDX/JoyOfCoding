#!/bin/sh

echo
echo "Xerces-Java <cough> Build System"
echo "------------------------"

if [ "$JAVA_HOME" = "" ] ; then
   echo "ERROR: JAVA_HOME not found in your environment."
   echo 
   echo "Please, set the JAVA_HOME variable in your environment to match the"
   echo "location of the Java Virtual Machine you want to use."
   exit 1
fi

is_cygwin=0
uname | grep CYGWIN
if [ $? -eq 0 ] ; then
  is_cygwin=1
fi

ANT_HOME=../jars

if [ $is_cygwin -eq 1 ]; then
  JAVA_HOME=`cygpath -u "$JAVA_HOME"`
fi

LOCALCLASSPATH=$JAVA_HOME/lib/tools.jar:$ANT_HOME/ant.jar:$ANT_HOME/jaxp.jar:$ANT_HOME/parser.jar:$ANT_HOME/ant-optional.jar

if [ $is_cygwin -eq 1 ]; then
  LOCALCLASSPATH=`cygpath -w -p "$LOCALCLASSPATH"`
  ANT_HOME=`cygpath -w "$ANT_HOME"`
fi

echo Building with classpath \"$LOCALCLASSPATH\"
echo Starting Ant...
echo
"$JAVA_HOME"/bin/java -Dant.home=$ANT_HOME -classpath $LOCALCLASSPATH org.apache.tools.ant.Main $@
