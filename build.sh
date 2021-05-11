#!/usr/bin/env bash

#oe指定jdk版本
export JAVA_HOME=/usr/local/jdk1.8.0_65
export PATH=$JAVA_HOME/bin:$PATH

#获取output
output="$(pwd)/output"
mkdir -p $output
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "[INFO]开始更新代码......"
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"

git pull

echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"
echo "[INFO]开始Maven打包......"
echo "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++"


mvn  clean package  -Dmaven.test.skip=true -U

if [ $? -ne 0 ]; then
    echo ""
    echo "***********************************************************"
    echo "[INFO]Maven打包失败"
    echo "***********************************************************"
    exit 1
fi
cp daedalus-server/target/daedalus-server.jar ${output}/daedalus-server.jar.build
cp control.sh ${output}
cp Dockerfile ${output}
chmod 744 ${output}/control.sh
if [ $? -ne 0 ]; then
    echo ""
    echo "***********************************************************"
    echo "[INFO]移动打包文件失败"
    echo "***********************************************************"
    exit 1
fi
echo ""
echo "***********************************************************"
echo "[INFO]Maven打包成功!!!!"
echo "***********************************************************"