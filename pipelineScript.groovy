pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'to-do-app:latest'
        SONARQUBE_SCANNER = 'SonarScanner' // Ensure this matches the configured tool name
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
                    def scannerHome = tool name: SONARQUBE_SCANNER, type: 'hudson.plugins.sonar.SonarRunnerInstallation'
                    withSonarQubeEnv('SonarQube') {
                        bat "\"${scannerHome}\\bin\\sonar-scanner.bat\" -Dsonar.projectKey=To-do-app -Dsonar.projectName=To-do-app -Dsonar.projectVersion=1.0 -Dsonar.sources=."
                    }
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
