pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'to-do-app:latest'
    }

    stages {
        stage('Clone Repository') {
            steps {
                git 'https://github.com/Kushsharma1/CICD-with-Jenkins.git'
            }
        }
        stage('Build') {
            steps {
                script {
                    bat 'docker build -t to-do-app:latest .'
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    bat 'echo "Skipping tests for now"'
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                script {
                    bat 'echo "Skipping SonarQube analysis for now"'
                }
            }
        }
        stage('Code Quality Analysis') {
            steps {
                script {
                    bat 'echo "Skipping code quality analysis for now"'
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    bat 'echo "Skipping deployment steps for now"'
                }
            }
        }
        stage('Release to AWS') {
            steps {
                script {
                    bat 'echo "Release to AWS (commented out actual implementation)"'
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
