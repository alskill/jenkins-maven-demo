pipeline {
    agent any

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
                        -t alskill/jenkins-maven-demo:${BUILD_NUMBER} \
                        -t alskill/jenkins-maven-demo:latest \
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
                        alskill/jenkins-maven-demo:${BUILD_NUMBER}
                '''
            }
        }

        stage('Deploy to Docker Desktop') {
            steps {
                sh '''
                    echo "Stopping previous container if it exists..."

                    docker rm -f jenkins-maven-demo-app 2>/dev/null || true

                    echo "Starting application container..."

                    docker run -d \
                        --name jenkins-maven-demo-app \
                        -p 8081:8080 \
                        alskill/jenkins-maven-demo:${BUILD_NUMBER}

                    echo "Container started."
                    docker ps | grep jenkins-maven-demo-app
                '''
            }
        }

        stage('Health Check') {
            steps {
                sh '''
                    echo "Checking running container..."

                    sleep 5

                    docker ps | grep jenkins-maven-demo-app

                    echo "Checking application logs..."
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
            Docker Deployment : SUCCESS

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