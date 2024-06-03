pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'to-do-app:latest'
        SONARQUBE_SCANNER = 'SonarQube Scanner' // The name of your SonarQube scanner installation
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
                    bat 'docker build -t %DOCKER_IMAGE% .'
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    bat 'docker run --rm %DOCKER_IMAGE% npm install'
                    bat 'docker run --rm %DOCKER_IMAGE% npm test'
                }
            }
        }
        stage('Code Quality Analysis') {
            steps {
                script {
                    def scannerHome = tool name: SONARQUBE_SCANNER
                    withSonarQubeEnv('SonarQube') {
                        bat "\"${scannerHome}\\bin\\sonar-scanner.bat\""
                    }
                }
            }
        }
        stage('Deploy') {
            steps {
                script {
                    bat 'docker-compose down'
                    bat 'docker-compose up -d'
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
