#!/bin/bash
# BuildAndRun.sh


file="complaints.java"


cd src

class=$(echo $file | awk -F '.' '{print $1}')

echo "Start Compile"
echo $class
echo "------------------------------------"


javac $file

if [ $? -eq 0 ]; then
    echo "Success.Start execute"
    echo "------------------------------------"


    java $class
    if [ $? -eq 0 ]; then
        echo "------------------------------------"
        echo "Finish execute"
    else
        echo "------------------------------------"
        echo "Execute error"
    fi
else
    echo "------------------------------------"
    echo "Compile error"
fi


if [ -f $class.class ]; then
    rm -rf $class.class
fi
