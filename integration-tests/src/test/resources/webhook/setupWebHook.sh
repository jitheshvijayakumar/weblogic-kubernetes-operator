#!/bin/bash -x
# Copyright 2019, Oracle Corporation and/or its affiliates.  All rights reserved.
# Licensed under the Universal Permissive License v 1.0 as shown at http://oss.oracle.com/licenses/upload
webhookDir=$1
webhookResourceDir=$2
operatorNS=$3
if [ ! -d "$webhookDir" ]; then
    echo "Installing webhook files ${webhookDir}..."
    mkdir ${webhookDir}
    cp -rf ${webhookResourceDir}/* ${webhookDir}/.
    cp ${webhookResource}/../../../../../src/scripts/scaling/scalingAction.sh ${webhookDir}
    echo "cloning webhook executable"
    cd ${webhookDir}
    wget  https://github.com/adnanh/webhook/releases/download/2.6.9/webhook-linux-amd64.tar.gz
    tar -xvf webhook-linux-amd64.tar.gz
    cp webhook-linux-amd64/webhook ${webhookDir}
fi
cd ${webhookDir}
docker build . -t webhook:latest
# Here we need internal Certificate
operator_cert_data=`kubectl get cm -n ${operatorNS} weblogic-operator-cm -o jsonpath='{.data.internalOperatorCert}'`
chmod +w ${webhookDir}/webhook-deployment.yaml
sed -i -e "s:@INTERNAL_OPERATOR_CERT@:${operator_cert_data}:g" ${webhookDir}/webhook-deployment.yaml
kubectl apply -f ${webhookDir}/webhook-deployment.yaml
kubectl apply -f ${webhookDir}/crossrbac_monitoring.yaml
echo "Run the script [setupWebHook.sh] ..."