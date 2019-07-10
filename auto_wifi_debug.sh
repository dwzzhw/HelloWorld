#!/bin/bash
ip_result=`adb shell ifconfig | grep "inet addr:" | awk 'gsub(/^ *| *$/, "")'2>&1`

echo "ifconfig output=$ip_result"

if [[ $ip_result =~ "inet addr:" ]]
then
    echo "Got ipV4 info string"
else
    echo "Could not find ipV4 info"
    exit 0
fi

#ip_addr=`echo "${ip_result:10}" | awk -F "" '{print $0}'`
#echo `expr 999 \* 4`
ip_addr=`echo "${ip_result:10}"`

eval $(echo $ip_addr | awk -F " " '{print "ip_addr="$1}')

echo "IP="$ip_addr

adb tcpip 5555
adb connect ${ip_addr}":5555"