#!/bin/bash

set -e

if [ "$EUID" -ne 0 ]; then
    echo "Please run as root"
    exit
fi

# java
apt update
apt -y install software-properties-common
add-apt-repository -y ppa:webupd8team/java
apt update
apt -y install oracle-java8-installer

# postgres
echo "deb http://apt.postgresql.org/pub/repos/apt/ $(lsb_release -cs)-pgdg main" > /etc/apt/sources.list.d/pgdg.list
apt -y install wget ca-certificates
wget --quiet -O - https://www.postgresql.org/media/keys/ACCC4CF8.asc | apt-key add -
apt update
apt -y install postgresql-9.6

sleep 5

sudo -u postgres psql -c "CREATE USER tracker WITH PASSWORD 'tracker';"
sudo -u postgres psql -c "CREATE DATABASE tracker OWNER tracker;"

TRACKER_USERNAME=tracker
useradd -m -U ${TRACKER_USERNAME}
chmod 775 /home/${TRACKER_USERNAME}

cat <<EOF > /etc/systemd/system/tracker-api.service
[Unit]
Description=GPS Tracker API
Requires=postgresql.service
Wants=network-online.target
After=network-online.target postgresql.service
[Service]
WorkingDirectory=/home/${TRACKER_USERNAME}
ExecStart=/usr/bin/java -jar /home/${TRACKER_USERNAME}/tracker-api-1.0.0.jar
Type=simple
User=${TRACKER_USERNAME}
Restart=on-failure
RestartSec=5
[Install]
WantedBy=multi-user.target
EOF

systemctl daemon-reload
systemctl enable tracker-api
