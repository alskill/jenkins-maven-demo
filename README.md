# Jenkins Freestyle Maven Project

## 📌 Project Overview

This project demonstrates how to create a **Jenkins Freestyle Project** that:

* Gets Java source code from GitHub
* Uses Maven to build the Java application
* Compiles the Java source code
* Packages the application into a JAR file
* Installs the JAR into the Maven local repository
* Displays build information in the Jenkins console

---

# 1. Technologies Used

| Tool              | Purpose                         |
| ----------------- | ------------------------------- |
| Jenkins           | CI/CD automation                |
| Git               | Version control                 |
| GitHub            | Source code repository          |
| Java              | Application language            |
| Maven             | Build and dependency management |
| Docker            | Running Jenkins                 |
| Git Bash          | Command-line environment        |
| Freestyle Project | Jenkins job type                |

---

# 2. Project Structure

The project structure is:

```text
jenkins-maven-demo/
│
├── .gitignore
├── pom.xml
│
└── src/
    └── main/
        └── java/
            └── App.java
```

---

# 3. Create the Project Directory

Open Git Bash:

```bash
cd ~/OneDrive/Desktop/lab/jenkins
```

Create the project:

```bash
mkdir jenkins-maven-demo
cd jenkins-maven-demo
```

---

# 4. Create the Java Application

Create the directory:

```bash
mkdir -p src/main/java
```

Create the Java file:

```bash
notepad src/main/java/App.java
```

Add:

```java
public class App {

    public static void main(String[] args) {
        System.out.println("Hello from Jenkins Maven Project!");
        System.out.println("Build successful!");
    }
}
```

Save the file.

Verify:

```bash
head -n 5 src/main/java/App.java
```

Expected:

```text
public class App {

    public static void main(String[] args) {
        System.out.println("Hello from Jenkins Maven Project!");
        System.out.println("Build successful!");
```

---

# 5. Create Maven `pom.xml`

Create the Maven configuration file:

```bash
notepad pom.xml
```

Add:

```xml
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">

    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>jenkins-maven-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>17</maven.compiler.source>
        <maven.compiler.target>17</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
        </plugins>
    </build>

</project>
```

Verify:

```bash
head -n 3 pom.xml
```

Expected:

```text
<?xml version="1.0" encoding="UTF-8"?>

<project xmlns="http://maven.apache.org/POM/4.0.0"
```

---

# 6. Important Markdown Mistake

During the project setup, Markdown code fences were accidentally copied into the files.

For example, this was incorrectly added to `pom.xml`:

````text
```xml
````

And this was incorrectly added to `App.java`:

````text
```java
````

These backtick characters are **not part of Java or XML syntax**.

This caused errors such as:

```text
illegal character: '`'
```

and:

```text
Non-parseable POM
```

### Lesson

When copying code from a Markdown document, copy **only the code inside the code block**, not:

```text
```

````

---

# 7. Create `.gitignore`

Create:

```bash
notepad .gitignore
````

Add:

```text
target/
*.class
```

This prevents generated Maven files from being committed to Git.

---

# 8. Initialize Git

From the project directory:

```bash
git init
```

Check:

```bash
git status
```

---

# 9. Connect GitHub Repository

GitHub repository:

```text
https://github.com/alskill/jenkins-maven-demo.git
```

Add the remote:

```bash
git remote add origin https://github.com/alskill/jenkins-maven-demo.git
```

Verify:

```bash
git remote -v
```

---

# 10. Commit the Project

Add the files:

```bash
git add .
```

Commit:

```bash
git commit -m "Add Maven Java application"
```

Push:

```bash
git branch -M main
git push -u origin main
```

---

# 11. Jenkins Setup

Jenkins was already running in Docker.

Check:

```bash
docker ps
```

The Jenkins container was named:

```text
jenkins
```

Jenkins was accessed through:

```text
http://localhost:8080
```

### Important

Do not create another Jenkins container if one already exists.

---

# 12. Jenkins Maven Installation

Jenkins already had Maven configured:

```text
Maven-3.9.9
```

Jenkins also had Java:

```text
OpenJDK 21.0.12.1 LTS
```

---

# 13. Create Jenkins Freestyle Project

In Jenkins:

```text
New Item
```

Project name:

```text
jenkins-maven-demo
```

Select:

```text
Freestyle project
```

Click:

```text
OK
```

---

# 14. Configure GitHub

Under:

```text
Source Code Management
```

Select:

```text
Git
```

Repository URL:

```text
https://github.com/alskill/jenkins-maven-demo.git
```

Branch:

```text
*/main
```

Jenkins will then clone the project from GitHub.

---

# 15. Configure Maven Build

Under:

```text
Build Steps
```

Select:

```text
Invoke top-level Maven targets
```

Maven Version:

```text
Maven-3.9.9
```

Goals:

```text
clean package install
```

---

# 16. Meaning of Maven Commands

## `mvn clean`

Deletes the previous `target` directory.

Example:

```text
target/
```

is removed.

---

## `mvn compile`

Compiles Java source code.

The compiled `.class` files are placed inside:

```text
target/classes/
```

---

## `mvn test`

Runs the project's tests.

In this project:

```text
No tests to run.
```

because we have not created test files yet.

---

## `mvn package`

Packages the application.

It creates:

```text
target/jenkins-maven-demo-1.0-SNAPSHOT.jar
```

---

## `mvn install`

Installs the generated artifact into Maven's local repository.

Jenkins installed it under:

```text
/var/jenkins_home/.m2/repository/com/example/jenkins-maven-demo/1.0-SNAPSHOT/
```

---

# 17. Complete Maven Command

Our Jenkins job uses:

```bash
mvn clean package install
```

Meaning:

```text
clean
  ↓
Remove old target
  ↓
compile
  ↓
test
  ↓
package
  ↓
Create JAR
  ↓
install
  ↓
Store artifact in .m2
```

---

# 18. First Jenkins Error — Invalid `pom.xml`

The first Jenkins build failed with:

```text
Non-parseable POM
```

The important error was:

```text
only whitespace content allowed before start tag
```

The reason was that `pom.xml` contained:

```text
cat > pom.xml <<'EOF'
```

before the XML.

### Fix

Opened the file:

```bash
notepad pom.xml
```

Deleted the incorrect content.

Added only valid XML.

Verified:

```bash
head -n 3 pom.xml
```

Then committed and pushed the fix.

---

# 19. Second Jenkins Error — Invalid `App.java`

After fixing `pom.xml`, Maven reached Java compilation.

Jenkins reported:

```text
illegal character: '`'
```

The error pointed to:

```text
src/main/java/App.java
```

The cause was Markdown code fences inside the Java file.

### Fix

Opened:

```bash
notepad src/main/java/App.java
```

Deleted the incorrect content.

Added:

```java
public class App {

    public static void main(String[] args) {
        System.out.println("Hello from Jenkins Maven Project!");
        System.out.println("Build successful!");
    }
}
```

Verified:

```bash
head -n 5 src/main/java/App.java
```

Then committed and pushed:

```bash
git add src/main/java/App.java
git commit -m "Fix Java source file"
git push origin main
```

---

# 20. Successful Maven Build

After fixing both files, Jenkins successfully executed:

```text
clean
compile
test
package
install
```

The important result was:

```text
[INFO] BUILD SUCCESS
```

The JAR was created:

```text
/var/jenkins_home/workspace/jenkins-maven-demo/target/jenkins-maven-demo-1.0-SNAPSHOT.jar
```

---

# 21. Maven Artifact

The generated JAR:

```text
jenkins-maven-demo-1.0-SNAPSHOT.jar
```

is located in:

```text
target/
```

Jenkins also installed the artifact into:

```text
/var/jenkins_home/.m2/repository/
```

The complete location is:

```text
/var/jenkins_home/.m2/repository/com/example/jenkins-maven-demo/1.0-SNAPSHOT/
```

---

# 22. Jenkins Workspace

Jenkins checked out the project into:

```text
/var/jenkins_home/workspace/jenkins-maven-demo/
```

This is the Jenkins **workspace** for this job.

The workspace contains:

```text
jenkins-maven-demo/
├── .gitignore
├── pom.xml
└── src/
    └── main/
        └── java/
            └── App.java
```

After Maven runs, it also contains:

```text
target/
```

---

# 23. Adding an Execute Shell Step

We also added an additional Jenkins build step:

```text
Execute shell
```

The purpose is to display information in the Jenkins console.

The shell commands are:

```bash
echo "=============================="
echo " Jenkins Maven Build Details "
echo "=============================="

echo "Java Version:"
java -version

echo ""
echo "Jenkins Workspace:"
pwd

echo ""
echo "Project Files:"
ls -la

echo ""
echo "Target Directory:"
ls -lh target

echo ""
echo "Generated JAR:"
ls -lh target/*.jar

echo ""
echo "Build Completed Successfully!"
echo "=============================="
```

---

# 24. Maven `mvn: not found` Issue

When we added:

```bash
mvn -version
```

inside the Execute Shell step, Jenkins reported:

```text
mvn: not found
```

This did **not** mean Maven was broken.

The Maven build had already succeeded:

```text
[INFO] BUILD SUCCESS
```

The reason was:

```text
Invoke top-level Maven targets
```

uses the Jenkins-configured Maven installation:

```text
Maven-3.9.9
```

But a normal:

```text
Execute shell
```

step does not automatically put that Maven installation into the shell's `PATH`.

Therefore:

```text
Invoke top-level Maven targets
        ↓
Maven-3.9.9
        ↓
BUILD SUCCESS
```

but:

```text
Execute shell
        ↓
mvn -version
        ↓
mvn: not found
```

---

# 25. Correct Execute Shell Step

Do not use:

```bash
mvn -version
```

inside the shell step.

Use:

```bash
echo "=============================="
echo " Jenkins Maven Build Details "
echo "=============================="

echo "Java Version:"
java -version

echo ""
echo "Jenkins Workspace:"
pwd

echo ""
echo "Project Files:"
ls -la

echo ""
echo "Target Directory:"
ls -lh target

echo ""
echo "Generated JAR:"
ls -lh target/*.jar

echo ""
echo "Build Completed Successfully!"
echo "=============================="
```

---

# 26. Final Jenkins Build Flow

The final Freestyle project flow is:

```text
GitHub
   │
   │ Git checkout
   ↓
Jenkins Workspace
   │
   ↓
Invoke Maven
   │
   │ mvn clean package install
   ↓
Clean
   │
   ↓
Compile
   │
   ↓
Test
   │
   ↓
Package
   │
   ↓
Create JAR
   │
   ↓
Install to .m2
   │
   ↓
Execute Shell
   │
   ├── Java version
   ├── Workspace
   ├── Project files
   ├── target directory
   └── Generated JAR
   │
   ↓
BUILD SUCCESS
```

---

# 27. Important Jenkins Concepts Learned

### Freestyle Project

A Jenkins job configured mainly through the Jenkins web UI.

### Source Code Management

Jenkins gets source code from Git/GitHub.

### Workspace

Directory where Jenkins checks out and builds the project.

```text
/var/jenkins_home/workspace/jenkins-maven-demo/
```

### Maven

Build automation and dependency management tool.

### `pom.xml`

Maven's main configuration file.

### Artifact

The output produced by the build.

In this project:

```text
jenkins-maven-demo-1.0-SNAPSHOT.jar
```

### Maven Local Repository

Location where Maven stores installed artifacts:

```text
~/.m2/repository/
```

For Jenkins:

```text
/var/jenkins_home/.m2/repository/
```

### Build Success

The Jenkins job completed all configured build steps successfully.

---

# 28. Useful Troubleshooting

## POM parsing error

Error:

```text
Non-parseable POM
```

Check:

```bash
head -n 3 pom.xml
```

Make sure the first line is:

```text
<?xml version="1.0" encoding="UTF-8"?>
```

---

## Java illegal character error

Error:

```text
illegal character: '`'
```

Check:

```bash
head -n 5 src/main/java/App.java
```

Remove Markdown code fences such as:

````text
```java
````

and:

```text
```

````

---

## Maven not found in Git Bash

Error:

```text
mvn: command not found
````

This can happen because Maven is not installed/configured in the local Windows Git Bash PATH.

Jenkins can still have its own Maven installation.

Check the Jenkins Maven configuration:

```text
Manage Jenkins
→ Tools
→ Maven installations
```

---

## Jenkins Maven build succeeds but shell `mvn` fails

This usually means Jenkins Maven is configured correctly but the Maven executable is not available in the shell's PATH.

Use Jenkins':

```text
Invoke top-level Maven targets
```

for Maven commands.

---

# 29. Final Result

The project successfully demonstrates:

```text
GitHub
  ↓
Jenkins
  ↓
Git Checkout
  ↓
Maven
  ↓
Clean
  ↓
Compile
  ↓
Test
  ↓
Package
  ↓
JAR
  ↓
Install
  ↓
Maven Local Repository
  ↓
Jenkins Console
  ↓
BUILD SUCCESS
```

## 🎯 Project Status

**Status: Completed Successfully ✅**

The Jenkins Freestyle Maven project can now:

* Clone the project from GitHub
* Read `pom.xml`
* Compile Java
* Run Maven lifecycle phases
* Create a JAR
* Install the JAR into `.m2`
* Display build information
* Report `BUILD SUCCESS`
