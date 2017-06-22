#!/usr/bin/env bash
set -e

if [ "$#" -ne 2 ]; then
    echo "Please provide release and next development versions"
    echo "Example: bash release.sh 0.1 0.2-SNAPSHOT"
    exit 1;
fi

RELEASE_VERSION=$1
DEVELOP_VERSION=$2

function updateReleaseVersion {
    echo "Updating versions to ${RELEASE_VERSION} in pom.xml files"
    mvn versions:set -DnewVersion=${RELEASE_VERSION}
    mvn versions:commit
    git add -A && git commit -am "release version ${RELEASE_VERSION}"
    echo "Done"
}

function updateDevelopVersion {
    echo "Updating versions to ${DEVELOP_VERSION} in pom.xml files"
    mvn versions:set -DnewVersion=${DEVELOP_VERSION}
    mvn versions:commit
    git add -A && git commit -am "update development version to ${DEVELOP_VERSION}"
    git push origin master
    echo "Done"
}

function tagRelease {
    echo "Tagging release v${RELEASE_VERSION}"
    git tag -a v${RELEASE_VERSION} -m "version ${RELEASE_VERSION}"
    echo "Done"
}

function pushRelease {
    echo "Pushing release v${RELEASE_VERSION}"
    git push origin master
    git push origin v${RELEASE_VERSION}
    echo "Done"
}

function buildDistribution {
    echo "Building project and generating distribution for release v${RELEASE_VERSION}"
    mvn clean install
    echo "Done"
}

function main {
    updateReleaseVersion
    buildDistribution
    tagRelease
    pushRelease
    updateDevelopVersion
}

main