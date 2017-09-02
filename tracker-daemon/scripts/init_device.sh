#!/bin/bash

# Settings
API_BASE_URL=THE_API_BASE_URL_GOES_HERE
API_SECRET=THE_API_SECRET_GOES_HERE


# Script

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

TRACKER_USERNAME=tracker

useradd -m -U ${TRACKER_USERNAME}
chmod 775 /home/${TRACKER_USERNAME}

echo "${TRACKER_USERNAME} ALL=(ALL) NOPASSWD: /bin/systemctl poweroff,/bin/systemctl halt,/bin/systemctl reboot" \
    >> /etc/sudoers

sudo -u tracker \
    curl \
    -f -s \
    -o /home/${TRACKER_USERNAME}/device.json \
    -H "X-Api-Secret: ${API_SECRET}" \
    ${API_BASE_URL}/devices/uuid-of/${DEVICE_ID}

cat <<EOF > /etc/systemd/system/tracker.service
[Unit]
Description=GPS Tracker
[Service]
WorkingDirectory=/home/${TRACKER_USERNAME}
ExecStart=/usr/bin/java -jar /home/${TRACKER_USERNAME}/tracker-daemon-1.0.0-jar-with-dependencies.jar
Type=simple
User=${TRACKER_USERNAME}
[Install]
WantedBy=multi-user.target
EOF

echo Success
