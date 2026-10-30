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
    }
}
