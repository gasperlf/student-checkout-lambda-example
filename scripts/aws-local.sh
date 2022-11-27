#!/usr/bin/env bash

echo "Start executing script"

awslocal s3 mb s3://studentcheckoutkucket --endpoint-url http://localhost:4566 --region us-west-1
awslocal sns create-topic --name StudentCheckoutTopic --endpoint-url http://localhost:4566 --region us-west-1
awslocal s3 cp students.json s3://studentcheckoutkucket --endpoint-url http://localhost:4566 --region us-west-1
echo "End executing script"
