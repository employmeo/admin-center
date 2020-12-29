#!/usr/bin/env bash

upload_dir=/home/ec2-user/drops
build_dir=/opt/talytica/application/builds
deploy_dir=/opt/talytica/application/deploy

cp ${upload_dir}/drops/*.jar ${build_dir}

release_build=$(ls -rt1 ${build_dir}/*.jar | tail -n1)
application_name=$(echo $release_build | awk -F'-' '{print $1}')

rm -f ${deploy_dir}/*.jar

ln -s ${release_build} ${deploy_dir}/${application_name}.jar