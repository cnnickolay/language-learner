#!/bin/bash

bower --allow-root install
./activator dist
cd target/universal && rm -rf language-learner-1.0-SNAPSHOT && unzip language-learner-1.0-SNAPSHOT.zip && cd language-learner-1.0-SNAPSHOT

nohup ./bin/language-learner -Dconfig.file=/tmp/language-learner/conf/application_prod.conf -Dplay.evolutions.db.default.autoApply=true -Dhttp.port=80 &> /var/log/language-learner/language-learner.log &
