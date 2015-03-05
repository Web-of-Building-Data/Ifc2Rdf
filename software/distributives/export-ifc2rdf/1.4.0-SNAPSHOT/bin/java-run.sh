#! /bin/sh

#
# Created by: Nam Vu Hoang, nam.vuhoang@aalto.fi
# Creation date: 22.10.2013
#

[ ${JAVA_HOME} ] && JAVA=${JAVA_HOME}/bin/java || JAVA=java

function add_classpath() {
   for f in $1/*; do
      [ -d $f ] && add_classpath $f || TMP_CP="$TMP_CP":"$f"
   done
}

# Are we running within Cygwin on some version of Windows?
cygwin=false;
case "`uname -s`" in
    CYGWIN*) cygwin=true ;;
esac

LIB_HOME=`dirname "$0"`

if $cygwin; then
    UNIX_STYLE_HOME=`cygpath "$LIB_HOME"`
else
    UNIX_STYLE_HOME=$LIB_HOME
fi


# Then add all library jars to the classpath.
TMP_CP=""
add_classpath $UNIX_STYLE_HOME/../lib

# Now add the system classpath to the classpath. If running
# Cygwin we also need to change the classpath to Windows format.
if $cygwin; then
    TMP_CP=`cygpath -w -p $TMP_CP`
    TMP_CP=$TMP_CP';'$CLASSPATH
else
    TMP_CP=$TMP_CP:$CLASSPATH
fi

$JAVA -cp $TMP_CP "$@"
