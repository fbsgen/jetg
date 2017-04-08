#!/bin/sh

set -e

VERSION=$1
[ ! -n "$VERSION" ] && echo "Enter release version: " && read VERSION

# post-release
PR_VERSION=$2
[ ! -n "$PR_VERSION" ] && echo "Enter post-release version: " && read PR_VERSION

echo "Releasing $VERSION - are you sure? (y/n):" && read CONFIRM && [ "$CONFIRM" != "y" ] && exit 0

mvn versions:set -DnewVersion=$VERSION -DgenerateBackupPoms=false && \
git add -u . && git commit -m "$VERSION" && \
mvn -Prelease deploy && mvn scm:tag && \
mvn versions:set -DnewVersion=$PR_VERSION -DgenerateBackupPoms=false && \
git add -u . && git commit -m "$PR_VERSION" && \
git push origin master
