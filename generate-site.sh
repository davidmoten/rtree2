#!/bin/bash
set -e
mvn site
cd ../davidmoten.github.io
git pull
mkdir -p rtree2
cp -r ../rtree2/target/site/* rtree2/
git add .
git commit -am "update site reports"
git push
