pipeline {
    agent any

    environment {
        GHCR_IMAGE = "ghcr.io/alskill/jenkins-maven-demo"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Check Tools') {
            steps {
                sh '''
                    echo "=== Java ==="
                    java -version

                    echo "=== Maven ==="
                    mvn -version

                    echo "=== Docker ==="
                    docker --version

                    echo "=== Trivy ==="
                    trivy --version
                '''
            }
        }

        stage('Build and Test') {
            steps {
                sh '''
                    echo "Building Maven application..."
                    mvn -B clean package
                '''
            }
        }

        stage('Verify JAR') {
            steps {
                sh '''
                    echo "Checking generated JAR..."
                    ls -lh target/

                    test -f target/jenkins-maven-demo-1.0-SNAPSHOT.jar

                    echo "JAR verified successfully."
                '''
            }
        }

        stage('Docker Build') {
            steps {
                sh '''
                    echo "Building Docker image..."

                    docker build \
                        -t ${GHCR_IMAGE}:${BUILD_NUMBER} \
                        -t ${GHCR_IMAGE}:latest \
                        .

                    echo "Docker image built successfully."

                    docker images | grep jenkins-maven-demo
                '''
            }
        }

        stage('Trivy Scan') {
            steps {
                sh '''
                    echo "Scanning Docker image with Trivy..."

                    trivy image \
                        --severity HIGH,CRITICAL \
                        --exit-code 0 \
                        ${GHCR_IMAGE}:${BUILD_NUMBER}
                '''
            }
        }

        stage('Push to GHCR') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'ghcr-credentials',
                        usernameVariable: 'GHCR_USERNAME',
                        passwordVariable: 'GHCR_TOKEN'
                    )
                ]) {
                    sh '''
                        echo "Logging in to GitHub Container Registry..."

                        echo "$GHCR_TOKEN" | docker login ghcr.io \
                            -u "$GHCR_USERNAME" \
                            --password-stdin

                        echo "Pushing image to GHCR..."

                        docker push ${GHCR_IMAGE}:${BUILD_NUMBER}
                        docker push ${GHCR_IMAGE}:latest

                        echo "GHCR push successful."

                        docker logout ghcr.io
                    '''
                }
            }
        }

        stage('Deploy to Docker Desktop') {
            steps {
                sh '''
                    echo "Stopping previous application container..."

                    docker rm -f jenkins-maven-demo-app 2>/dev/null || true

                    echo "Starting application container..."

                    docker run -d \
                        --name jenkins-maven-demo-app \
                        -p 8081:8080 \
                        ${GHCR_IMAGE}:${BUILD_NUMBER}

                    echo "Container started."

                    docker ps | grep jenkins-maven-demo-app
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Waiting for application..."
                    sleep 5

                    echo "=== Container ==="
                    docker ps | grep jenkins-maven-demo-app

                    echo "=== Application Logs ==="
                    docker logs jenkins-maven-demo-app

                    echo "Health check completed."
                '''
            }
        }
    }

    post {
        success {
            echo '''
            ========================================
            Pipeline Successful!
            ========================================

            Maven Build       : SUCCESS
            Docker Build      : SUCCESS
            Trivy Scan        : COMPLETED
            GHCR Push         : SUCCESS
            Docker Deployment : SUCCESS

            GHCR Image:
            ghcr.io/alskill/jenkins-maven-demo:latest

            Application:
            http://localhost:8081

            ========================================
            '''
        }

        failure {
            echo '''
            ========================================
            Pipeline Failed
            ========================================

            Check the Jenkins console output.

            ========================================
            '''
        }
    }
}