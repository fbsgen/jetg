#!/bin/sh

set -e

VERSION=$1
[ ! -n "$VERSION" ] && echo "Enter release version: " && read VERSION

# post-release
PR_VERSION=$2
[ ! -n "$PR_VERSION" ] && echo "Enter post-release version: " && read PR_VERSION

echo "Releasing $VERSION - are you sure? (y/n):" && read CONFIRM && [ "$CONFIRM" != "y" ] && exit 0

[ ! -n "$MVN" ] && MVN=mvn

$MVN versions:set -DnewVersion=$VERSION && \
git clean -f && git add -u && git commit -m "$VERSION" && \
$MVN -Prelease deploy && $MVN scm:tag && \
$MVN versions:set -DnewVersion=$PR_VERSION && \
git clean -f && git add -u && git commit -m "$PR_VERSION" && \
git push origin master
