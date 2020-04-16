#!/usr/bin/env bash

nohup target/universal/stage/bin/oct-contact -Dconfig.resource=staging.conf -Dplay.evolutions.db.default.autoApply=true -Dplay.evolutions.db.default.autoApplyDowns=true -Dhttp.port=3467 -J-Xmx2G &> staging.out &
