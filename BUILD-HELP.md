# Build Handbook

## Maven Release 

```bash
# create a new release
mvn gitflow:release


mvn deploy --batch-mode --update-snapshots -P sign,!build-extras,deploy-ossrh -Dmaven.test.skip=true

```