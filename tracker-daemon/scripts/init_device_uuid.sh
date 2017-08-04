#!/bin/bash

# Settings
API_BASE_URL=THE_API_BASE_URL_GOES_HERE
API_SECRET=THE_API_SECRET_GOES_HERE


# Script
set -e

if [ "$API_BASE_URL" = "THE_API_BASE_URL_GOES_HERE" ]; then
    echo "Please edit the API_BASE_URL variable in this script before running"
    exit
fi

if [ "$API_SECRET" = "THE_API_SECRET_GOES_HERE" ]; then
    echo "Please edit the API_SECRET variable in this script before running"
    exit
fi

if [ "$EUID" -ne 0 ]; then
    echo "Please run as root"
    exit
fi

DEVICE_ID=$((16#$(sed -n '/^Serial/{s/.* //;p}' /proc/cpuinfo)))

mkdir -p /var/lib/tracker
curl \
    -f -s \
    -o /var/lib/tracker/device.json \
    -H "X-Api-Secret: ${API_SECRET}" \
    ${API_BASE_URL}/devices/uuid-of/${DEVICE_ID}

echo Success
