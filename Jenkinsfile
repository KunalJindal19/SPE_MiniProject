pipeline {
    agent any

    tools {
        // JDK registered in Manage Jenkins -> Tools -> JDK installations
        jdk 'jdk21'
    }

    triggers {
        // Start a build when GitHub sends a push webhook
        githubPush()
    }

    options {
        // We check out explicitly in the Checkout stage
        skipDefaultCheckout()
        // Never run two deployments at the same time
        disableConcurrentBuilds()
    }

    environment {
        DOCKER_IMAGE = 'kunaljindal19/calculator-api'
        // Each build produces an image tagged with its build number; also read by docker-compose.yml
        IMAGE_TAG = "${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                // Checks out the commit that triggered this build
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh './mvnw -B -ntp clean package -DskipTests'
                archiveArtifacts artifacts: 'target/calculator-api.jar', fingerprint: true
            }
        }

        stage('Test') {
            steps {
                // A failing test makes mvn exit with an error, which fails the build
                sh './mvnw -B -ntp test'
            }
            post {
                always {
                    // Publish JUnit XML reports (runs even when tests fail)
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$IMAGE_TAG -t $DOCKER_IMAGE:latest .'
            }
        }

        stage('Push') {
            steps {
                // Docker Hub username + access token come from the Jenkins credentials store
                withCredentials([usernamePassword(credentialsId: 'dockerhub-creds',
                                                  usernameVariable: 'DOCKER_USER',
                                                  passwordVariable: 'DOCKER_PASS')]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker push $DOCKER_IMAGE:$IMAGE_TAG
                        docker push $DOCKER_IMAGE:latest
                    '''
                }
            }
            post {
                always {
                    sh 'docker logout'
                }
            }
        }

        stage('Deploy') {
            steps {
                // Starts or updates the stack; --wait blocks until the containers are healthy
                sh 'docker compose up -d --remove-orphans --wait --wait-timeout 120'
                // End-to-end check through the Nginx proxy
                sh 'curl -fsS http://localhost/actuator/health | grep UP'
                sh 'docker compose ps'
            }
        }
    }

    post {
        success {
            emailext to: '$DEFAULT_RECIPIENTS',
                     subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     mimeType: 'text/html',
                     body: """<p>Build <b>#${env.BUILD_NUMBER}</b> of <b>${env.JOB_NAME}</b> succeeded.</p>
                              <p>Deployed image: <code>${env.DOCKER_IMAGE}:${env.IMAGE_TAG}</code></p>
                              <p><a href="${env.BUILD_URL}">Open build in Jenkins</a></p>"""
        }
        failure {
            emailext to: '$DEFAULT_RECIPIENTS',
                     subject: "FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                     mimeType: 'text/html',
                     attachLog: true,
                     body: """<p>Build <b>#${env.BUILD_NUMBER}</b> of <b>${env.JOB_NAME}</b> failed.</p>
                              <p>The console log is attached.</p>
                              <p><a href="${env.BUILD_URL}">Open build in Jenkins</a></p>"""
        }
        always {
            // Remove dangling image layers left behind by rebuilds
            sh 'docker image prune -f'
        }
    }
}
