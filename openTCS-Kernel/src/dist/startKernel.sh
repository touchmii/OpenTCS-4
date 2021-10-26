#!/bin/sh
#
# Start the openTCS kernel.
#

# Set base directory names.
export OPENTCS_BASE=.
export OPENTCS_HOME=.
export OPENTCS_CONFIGDIR="${OPENTCS_HOME}/config"
export OPENTCS_LIBDIR="${OPENTCS_BASE}/lib"

# Set the class path
export OPENTCS_CP="${OPENTCS_LIBDIR}/*"
export OPENTCS_CP="${OPENTCS_CP}:${OPENTCS_LIBDIR}/openTCS-extensions/*"

if [ -n "${OPENTCS_JAVAVM}" ]; then
    export JAVA="${OPENTCS_JAVAVM}"
else
    # XXX Be a bit more clever to find out the name of the JVM runtime.
    export JAVA="java"
fi

# Start kernel
${JAVA} -enableassertions \
    -Dopentcs.base="${OPENTCS_BASE}" \
    -Dopentcs.home="${OPENTCS_HOME}" \
    -Dopentcs.cfg4j.reload.interval=10000 \
#    -Djava.util.logging.config.file=${OPENTCS_CONFIGDIR}/logging.config \
    -Dlogback.configurationFile=${OPENTCS_CONFIGDIR}/logback.xml \
    -Djava.security.policy=file:${OPENTCS_CONFIGDIR}/java.policy \
    -XX:-OmitStackTraceInFastThrow \
    -classpath "${OPENTCS_CP}" \
    org.opentcs.kernel.RunKernel
