#!/usr/bin/env bash

ARTIFACT=repomanager
VERSION=0.6.0
export PATH=/Library/Java/JavaVirtualMachines/graalvm-ce-lts-java11-19.3.2/Contents/Home/bin:"$PATH"
export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-lts-java11-19.3.2/Contents/Home

echo "[-->] Cleaning target directory & creating new one"
rm -rf build
mkdir -p build/native-image

echo "[-->] Build Spring Boot App with gradle assemble"
./gradlew assemble

echo "[-->] Expanding the Spring Boot fat jar"
JAR="$ARTIFACT-$VERSION.jar"
cd build/native-image
jar -xvf ../libs/$JAR >/dev/null 2>&1
cp -R META-INF BOOT-INF/classes

echo "[-->] Set the classpath to the contents of the fat jar (where the libs contain the Spring Graal AutomaticFeature)"
LIBPATH=`find BOOT-INF/lib | tr '\n' ':'`
CP=BOOT-INF/classes:$LIBPATH

native-image --no-server \
             --no-fallback \
             -cp $CP \
             -H:Name=RepoMgr \
             -H:Class=com.repomgr.repomanager.RepoManagerApplication \
             -H:+AllowVMInspection \
             -H:+TraceClassInitialization \
             -H:+ReportUnsupportedElementsAtRuntime \
             -H:+ReportExceptionStackTraces \
             --initialize-at-build-time=org.springframework.util.unit.DataSize \
             --initialize-at-build-time=com.repomgr.repomanager.infrastructure.repository.VersionRepository \
             --initialize-at-build-time=com.repomgr.repomanager.infrastructure.repository.UserRepository \
             --initialize-at-build-time=org.springframework.data.repository.CrudRepository \
             --initialize-at-build-time=org.springframework.data.util.Streamable \
             --initialize-at-build-time=org.springframework.data.domain.Pageable \
             --initialize-at-build-time=org.springframework.data.domain.Sort \
             --initialize-at-build-time=org.springframework.data.jpa.domain.Specification \
             --initialize-at-build-time=org.springframework.data.jpa.repository.JpaSpecificationExecutor \
             --initialize-at-build-time=org.springframework.security.core.userdetails.UserDetailsService \
             --allow-incomplete-classpath \
             -Dspring.graal.remove-unused-autoconfig=true \
             -Dspring.graal.remove-yaml-support=true
