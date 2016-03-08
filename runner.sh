#!/bin/bash

./bin/language-learner -Dconfig.file=/tmp/language-learner/conf/application_prod.conf -Dplay.evolutions.db.default.autoApply=true -Dhttp.port=80
