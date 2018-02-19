#!/bin/bash

BRANCH="$(git ls-remote origin | sed -n "\|$TRAVIS_COMMIT\s\+refs/heads/|{s///p}")"
PARSED_TAG="$(echo $TRAVIS_TAG | sed 's/RELEASE-//')"
PARSED_VERSION="$(echo $PARSED_TAG | sed 's/v//')"

echo "Checking out origin/$BRANCH"
git checkout -b $BRANCH origin/$BRANCH
mvn clean -B -DreleaseVersion=$PARSED_VERSION -Dtag=$PARSED_TAG -Dusername=$GITHUB_USER -Dpassword=$GITHUB_PASS \
release:prepare release:perform --settings .travis.settings.xml
git push --delete origin $TRAVIS_TAG