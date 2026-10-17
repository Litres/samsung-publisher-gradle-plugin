## Pushing a SNAPSHOT build to local repository

2. Publish to local repository
   ```bash
   ./gradlew :plugin-build:plugin:publishToMavenLocal
   ```
3. Remove local repository to apply remote build repository
   ```bash
   rm -rv ~/.m2/repository/ru/litres/plugin/plugin/
   ```
