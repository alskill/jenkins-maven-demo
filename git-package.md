# Jenkins Maven Project — GitHub Packages Setup

## Objective

Configure a Jenkins Maven project to:

1. Build a Java Maven application.
2. Run JUnit tests.
3. Create a JAR file.
4. Authenticate securely with GitHub Packages.
5. Publish the Maven JAR to GitHub Packages.
6. Archive the generated JAR in Jenkins.

---

# 1. Project Structure

The project structure is:

```text
jenkins-maven-demo/
├── .git/
├── .gitignore
├── .mvn/
│   └── settings.xml
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   └── java/
    │       └── App.java
    └── test/
        └── java/
            └── AppTest.java
```

---

# 2. Maven Project Configuration

The project uses Maven for:

* Dependency management
* Compilation
* Testing
* Packaging
* Installing
* Deploying

The important Maven lifecycle command for publishing the package is:

```bash
mvn clean deploy
```

`deploy` publishes the generated Maven package to the repository configured in `distributionManagement`.

---

# 3. Configure `pom.xml`

Open:

```text
pom.xml
```

Add the GitHub Packages repository inside the `<project>` element:

```xml
<!-- GitHub Packages -->
<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/alskill/jenkins-maven-demo</url>
    </repository>
</distributionManagement>
```

The `<distributionManagement>` section must be **before**:

```xml
</project>
```

The repository ID is:

```text
github
```

This ID must match the ID used in `settings.xml`.

---

# 4. Create `.mvn` Directory

From the project directory:

```bash
cd ~/OneDrive/Desktop/lab/jenkins/jenkins-maven-demo
```

Create the directory:

```bash
mkdir -p .mvn
```

Create the settings file:

```bash
touch .mvn/settings.xml
```

---

# 5. Configure `settings.xml`

Open:

```text
.mvn/settings.xml
```

Add:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0
          https://maven.apache.org/xsd/settings-1.2.0.xsd">

    <servers>
        <server>
            <id>github</id>
            <username>${env.GITHUB_USERNAME}</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
    </servers>

    <profiles>
        <profile>
            <id>default</id>

            <properties>
                <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
            </properties>
        </profile>
    </profiles>

    <activeProfiles>
        <activeProfile>default</activeProfile>
    </activeProfiles>

</settings>
```

### Important

Do **not** put the actual GitHub token inside `settings.xml`.

The file uses environment variables:

```text
GITHUB_USERNAME
GITHUB_TOKEN
```

Jenkins supplies these values securely through Jenkins Credentials.

---

# 6. Understand the Credential Flow

The authentication flow is:

```text
GitHub PAT
     ↓
Jenkins Credentials
     ↓
GITHUB_USERNAME
GITHUB_TOKEN
     ↓
settings.xml
     ↓
Maven
     ↓
GitHub Packages
```

The GitHub token should never be hard-coded in:

* `pom.xml`
* `settings.xml`
* Shell scripts
* GitHub repository
* README files
* Jenkins console output

---

# 7. Create GitHub Personal Access Token

Go to your GitHub account settings.

Open:

```text
Settings
→ Developer settings
→ Personal access tokens
→ Tokens (classic)
→ Generate new token
→ Generate new token (classic)
```

Create a token with a descriptive name, for example:

```text
jenkins-maven-github-packages
```

Select the required package permissions:

```text
write:packages
read:packages
```

If repository access is required for the repository/package configuration, provide the appropriate repository permission as well.

Generate the token.

### Important

GitHub normally shows the token when it is created.

Copy it and store it securely.

**Never commit the token to Git.**

**Never share the token in chat.**

---

# 8. Create Jenkins Credential

Open Jenkins:

```text
Jenkins
→ Manage Jenkins
→ Credentials
→ System
→ Global credentials
→ Add Credentials
```

Configure:

| Setting     | Value                            |
| ----------- | -------------------------------- |
| Kind        | Username with password           |
| Scope       | Global                           |
| Username    | GitHub username                  |
| Password    | GitHub Personal Access Token     |
| ID          | `github-packages`                |
| Description | `GitHub Packages authentication` |

Click:

```text
Create
```

---

# 9. Bind Jenkins Credential to Environment Variables

Open:

```text
Jenkins
→ jenkins-maven-demo
→ Configure
```

Go to:

```text
Build Environment
```

Enable:

```text
Use secret text(s) or file(s)
```

Add:

```text
Username and password (separated)
```

Configure:

| Setting           | Value             |
| ----------------- | ----------------- |
| Username Variable | `GITHUB_USERNAME` |
| Password Variable | `GITHUB_TOKEN`    |
| Credentials       | `github-packages` |

The important part is that the variable names match `settings.xml`:

```xml
<username>${env.GITHUB_USERNAME}</username>
<password>${env.GITHUB_TOKEN}</password>
```

---

# 10. Configure Jenkins Git Repository

Open:

```text
Jenkins
→ jenkins-maven-demo
→ Configure
```

Under:

```text
Source Code Management
→ Git
```

Repository URL:

```text
https://github.com/alskill/jenkins-maven-demo.git
```

Branch:

```text
*/main
```

Jenkins should therefore build the `main` branch.

---

# 11. Configure Maven in Jenkins

Under the Maven build step:

```text
Build
→ Invoke top-level Maven targets
```

Select the configured Maven installation:

```text
Maven-3.9.9
```

Use:

```text
clean deploy -s .mvn/settings.xml
```

### Why `-s .mvn/settings.xml`?

The settings file is located at:

```text
.mvn/settings.xml
```

Therefore Maven must be given the correct path:

```text
-s .mvn/settings.xml
```

Using:

```text
-s settings.xml
```

would look for:

```text
settings.xml
```

in the project root instead of:

```text
.mvn/settings.xml
```

---

# 12. Why Use `deploy`?

The Maven lifecycle commands have different purposes.

| Command       | Purpose                                                   |
| ------------- | --------------------------------------------------------- |
| `mvn clean`   | Removes previous build files                              |
| `mvn compile` | Compiles source code                                      |
| `mvn test`    | Runs tests                                                |
| `mvn package` | Creates the JAR                                           |
| `mvn install` | Installs the JAR into the local Maven repository          |
| `mvn deploy`  | Publishes the package to the configured remote repository |

For GitHub Packages, the important command is:

```bash
mvn clean deploy
```

---

# 13. Archive Jenkins Artifact

In Jenkins:

```text
Post-build Actions
→ Archive the artifacts
```

Use:

```text
target/*.jar
```

This allows Jenkins to keep the generated JAR as a build artifact.

The generated file is:

```text
target/jenkins-maven-demo-1.0-SNAPSHOT.jar
```

---

# 14. Expected Jenkins Build Flow

The complete Jenkins flow is:

```text
Developer pushes code
        ↓
GitHub repository
        ↓
Jenkins checks out main
        ↓
Maven starts
        ↓
clean
        ↓
compile
        ↓
JUnit tests
        ↓
package
        ↓
JAR created
        ↓
install
        ↓
deploy
        ↓
settings.xml
        ↓
Jenkins credentials
        ↓
GitHub Packages
        ↓
JAR published
        ↓
Jenkins archives JAR
```

---

# 15. Expected Successful Build

A successful Maven build should contain:

```text
BUILD SUCCESS
```

The build should also show the JAR:

```text
target/jenkins-maven-demo-1.0-SNAPSHOT.jar
```

JUnit should show something similar to:

```text
Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
```

---

# 16. Common Errors and Solutions

## Error 1 — Repository not specified

Error:

```text
repository element was not specified in the POM
inside distributionManagement
```

### Cause

Maven cannot find the repository configuration in `pom.xml`.

### Check

```xml
<distributionManagement>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/alskill/jenkins-maven-demo</url>
    </repository>
</distributionManagement>
```

Make sure it is inside:

```xml
<project>
```

and before:

```xml
</project>
```

---

# 17. Error 2 — Unauthorized 401

Error:

```text
status code: 401, reason phrase: Unauthorized
```

### Meaning

GitHub rejected the authentication credentials.

### Check

Jenkins credential:

```text
ID: github-packages
```

Check:

```text
Username = correct GitHub username
Password = correct GitHub PAT
```

Check the PAT permissions:

```text
write:packages
read:packages
```

Check Jenkins environment variables:

```text
GITHUB_USERNAME
GITHUB_TOKEN
```

Check the Maven command:

```text
clean deploy -s .mvn/settings.xml
```

---

# 18. Error 3 — Wrong Settings File Path

Incorrect:

```text
clean deploy -s settings.xml
```

Correct:

```text
clean deploy -s .mvn/settings.xml
```

Because the file is:

```text
.mvn/settings.xml
```

---

# 19. Error 4 — `mvn: command not found`

If an Execute Shell step contains:

```bash
mvn -version
```

Jenkins may report:

```text
mvn: not found
```

This can happen because the Maven installation configured for the Jenkins Maven build step is not necessarily available in the normal shell `PATH`.

Use the Jenkins Maven build step:

```text
Invoke top-level Maven targets
```

with the configured Maven installation:

```text
Maven-3.9.9
```

---

# 20. Verify Git Changes

Before pushing changes:

```bash
git status
```

Check the latest commit:

```bash
git log -1 --oneline
```

Check the GitHub remote:

```bash
git remote -v
```

Expected repository:

```text
https://github.com/alskill/jenkins-maven-demo.git
```

---

# 21. Commit and Push

After making project changes:

```bash
git add pom.xml .mvn/settings.xml
```

Commit:

```bash
git commit -m "Configure GitHub Packages deployment"
```

Push:

```bash
git push origin main
```

---

# 22. Security Best Practices

Never do this:

```xml
<password>github-token-here</password>
```

Never put the actual token in:

```text
pom.xml
settings.xml
README.md
GitHub source code
Shell scripts
```

Never print the token:

```bash
echo $GITHUB_TOKEN
```

Use Jenkins Credentials instead.

The recommended flow is:

```text
GitHub PAT
    ↓
Jenkins Credentials
    ↓
Environment Variables
    ↓
Maven settings.xml
    ↓
GitHub Packages
```

---

# 23. Final Configuration Checklist

Before running the final Jenkins build, verify:

```text
☑ GitHub repository is correct
☑ Jenkins branch is */main
☑ pom.xml contains distributionManagement
☑ repository ID is github
☑ GitHub Packages URL is correct
☑ .mvn/settings.xml exists
☑ settings.xml uses GITHUB_USERNAME
☑ settings.xml uses GITHUB_TOKEN
☑ Jenkins credential ID is github-packages
☑ GitHub PAT has package permissions
☑ Username variable is GITHUB_USERNAME
☑ Password variable is GITHUB_TOKEN
☑ Maven version is Maven-3.9.9
☑ Maven command is clean deploy -s .mvn/settings.xml
☑ Archive pattern is target/*.jar
```

---

# 24. Final Result

After successful configuration, Jenkins will:

```text
Checkout source code
        ↓
Run Maven
        ↓
Run JUnit tests
        ↓
Build JAR
        ↓
Authenticate using Jenkins Credentials
        ↓
Deploy Maven package to GitHub Packages
        ↓
Archive JAR in Jenkins
```

The generated Maven artifact is:

```text
jenkins-maven-demo-1.0-SNAPSHOT.jar
```

The important Jenkins Maven command is:

```bash
mvn clean deploy -s .mvn/settings.xml
```

This setup keeps the GitHub authentication token outside the source code and allows Jenkins to securely provide the credentials during the build.
