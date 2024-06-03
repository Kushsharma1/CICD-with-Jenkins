pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'to-do-app:latest'
        SONARQUBE_SCANNER = 'SonarQube Scanner' 
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
                    docker.build(DOCKER_IMAGE)
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    def workspace = pwd()
                    def workspaceUnix = workspace.replace('\\', '/')

                    docker.image(DOCKER_IMAGE).inside("-v ${workspaceUnix}:${workspaceUnix} -w ${workspaceUnix}") {
                        bat 'npm install'
                        bat 'npm test'
                    }
                }
            }
        }
        stage('Code Quality Analysis') {
            steps {
                script {
                    def scannerHome = tool name: SONARQUBE_SCANNER
                    withSonarQubeEnv('SonarQube') {
                        bat "${scannerHome}/bin/sonar-scanner"
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
