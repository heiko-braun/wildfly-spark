#!/bin/sh

set -o xtrace

rm -rf modules/system/layers/base/org/wildfly/extension/spark/
rm -f standalone/configuration/standalone-spark.xml
rm -f domain/configuration/spark-domain.xml
rm -f domain/configuration/spark-host.xml