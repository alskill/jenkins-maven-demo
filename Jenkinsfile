pipeline {
    agent any

    environment {
        GITHUB_CREDS = credentials('github-packages')
        JAVA_HOME    = tool name: 'jdk21'
        MAVEN_HOME = tool name: 'Maven-3.9.9'
        PATH         = "${JAVA_HOME}/bin:${PATH}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build & Deploy') {
            steps {
                configFileProvider([configFile(fileId: 'maven-github-settings', variable: 'MAVEN_SETTINGS')]) {
                    sh """
                        export GITHUB_USERNAME=${GITHUB_CREDS_USR}
                        export GITHUB_TOKEN=${GITHUB_CREDS_PSW}

                        ${MAVEN_HOME}/bin/mvn -s $MAVEN_SETTINGS -B clean package
                        ${MAVEN_HOME}/bin/mvn -s $MAVEN_SETTINGS -B deploy
                    """
                }
            }
        }
    }

    post {
        success {
            echo "✅ Build and deployment to GitHub Packages completed successfully."
        }
        failure {
            echo "❌ Pipeline failed. Check the console output for details."
        }
    }
}