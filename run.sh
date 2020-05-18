#!/bin/bash
# BuildAndRun.sh
# compile and execute java

file="complaints.java"

# cd src directory
cd src

class=$(echo $file | awk -F '.' '{print $1}')

echo "Start compile, please wait."
echo $class
echo "------------------------------------"

# compile
javac $file

if [ $? -eq 0 ]; then
    echo "Success!Prepare to execute."
    echo "------------------------------------"

    # execute
    java $class
    if [ $? -eq 0 ]; then
        echo "------------------------------------"
        echo "Complete execute."
    else
        echo "------------------------------------"
        echo "Execute error"
    fi
else
    echo "------------------------------------"
    echo "Compile error"
fi

# delete class
if [ -f $class.class ]; then
    rm -rf $class.class
fi
