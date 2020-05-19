#!/bin/bash
# BuildAndRun.sh
# 编译并运行java代码

file="complaints.java"

# 进入src目录
cd src

class=$(echo $file | awk -F '.' '{print $1}')

echo "开始编译，请等待!!!"
echo $class
echo "------------------------------------"

# 编译
javac $file

if [ $? -eq 0 ]; then
    echo "编译成功, 准备运行!!!"
    echo "------------------------------------"

    # 运行
    java $class
    if [ $? -eq 0 ]; then
        echo "------------------------------------"
        echo "运行完毕!!!"
    else
        echo "------------------------------------"
        echo "运行时出错!!!"
    fi
else 
    echo "------------------------------------"
    echo "编译时出现错误!!!"
fi

# 防止影响后面的测试，运行完毕之后，可以将生成的class文件删除
if [ -f $class.class ]; then
    rm -rf $class.class
fi