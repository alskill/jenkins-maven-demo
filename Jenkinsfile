pipeline {
    agent any

    environment {
        JAVA_HOME  = tool name: 'jdk21'
        MAVEN_HOME = tool name: 'Maven-3.9.9'

        EC2_HOST = '54.227.98.24'
        EC2_USER = 'ubuntu'
        APP_DIR  = '/opt/jenkins-maven-demo'

        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
    }

    stages {

        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh '''
                    echo "Building Maven application..."
                    mvn -B clean package
                '''
            }
        }

        stage('Test') {
            steps {
                sh '''
                    echo "Running tests..."
                    mvn -B test
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

        stage('Deploy to EC2') {
            steps {
                sshagent(credentials: ['ec2-deploy-key']) {

                    sh '''
                        echo "Creating application directory on EC2..."

                        ssh -o StrictHostKeyChecking=no \
                            ${EC2_USER}@${EC2_HOST} \
                            "sudo mkdir -p ${APP_DIR}/target && sudo chown -R ${EC2_USER}:${EC2_USER} ${APP_DIR}"
                    '''

                    echo "Copying JAR to EC2..."

                    sh '''
                        scp -o StrictHostKeyChecking=no \
                            target/jenkins-maven-demo-1.0-SNAPSHOT.jar \
                            ${EC2_USER}@${EC2_HOST}:${APP_DIR}/target/
                    '''

                    echo "Copying Nginx configuration..."

                    sh '''
                        scp -o StrictHostKeyChecking=no \
                            deploy/nginx.conf \
                            ${EC2_USER}@${EC2_HOST}:${APP_DIR}/
                    '''

                    echo "Copying systemd service..."

                    sh '''
                        scp -o StrictHostKeyChecking=no \
                            deploy/jenkins-maven-demo.service \
                            ${EC2_USER}@${EC2_HOST}:${APP_DIR}/
                    '''
                }
            }
        }

        stage('Configure EC2') {
            steps {
                sshagent(credentials: ['ec2-deploy-key']) {

                    sh '''
                        ssh -o StrictHostKeyChecking=no \
                            ${EC2_USER}@${EC2_HOST} << 'REMOTE'

                        echo "Installing Nginx and curl..."

                        sudo apt-get update
                        sudo apt-get install -y nginx curl

                        echo "Configuring systemd service..."

                        sudo cp ${APP_DIR}/jenkins-maven-demo.service \
                            /etc/systemd/system/jenkins-maven-demo.service

                        sudo systemctl daemon-reload
                        sudo systemctl enable jenkins-maven-demo
                        sudo systemctl restart jenkins-maven-demo

                        echo "Configuring Nginx..."

                        sudo cp ${APP_DIR}/nginx.conf \
                            /etc/nginx/sites-available/jenkins-maven-demo

                        sudo ln -sf \
                            /etc/nginx/sites-available/jenkins-maven-demo \
                            /etc/nginx/sites-enabled/jenkins-maven-demo

                        sudo rm -f /etc/nginx/sites-enabled/default

                        echo "Testing Nginx configuration..."

                        sudo nginx -t

                        sudo systemctl enable nginx
                        sudo systemctl restart nginx

                        echo "Checking application..."

                        sudo systemctl status jenkins-maven-demo --no-pager

                        echo "Deployment completed successfully."

                        REMOTE
                    '''
                }
            }
        }

        stage('Health Check') {
            steps {
                sshagent(credentials: ['ec2-deploy-key']) {

                    sh '''
                        echo "Testing application through Nginx..."

                        ssh -o StrictHostKeyChecking=no \
                            ${EC2_USER}@${EC2_HOST} \
                            "curl -I http://localhost"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo '''
            ========================================
            Deployment Successful!
            ========================================
            Application:
            http://54.227.98.24
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