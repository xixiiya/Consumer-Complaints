#!/bin/bash
# BuildAndRun.sh
# Compile java code

file="complaints.java"

# src directory
cd src

class=$(echo $file | awk -F '.' '{print $1}')

echo "Start Compile"
echo $class
echo "------------------------------------"

# Compile
javac $file

if [ $? -eq 0 ]; then
    echo "Success.Start execute"
    echo "------------------------------------"

    # Execute
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

# Delete class file
if [ -f $class.class ]; then
    rm -rf $class.class
fi
