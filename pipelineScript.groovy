pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'to-do-app:latest'
        AWS_REGION = 'us-east-1'
        ECR_REPOSITORY_NAME = 'to-do-app-repo'
        ECS_CLUSTER_NAME = 'to-do-app-cluster'
        ECS_SERVICE_NAME = 'to-do-app-service'
        ECS_TASK_DEFINITION = 'to-do-app-task'
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
                    bat 'docker run --rm to-do-app:latest npm test'
                }
            }
        }
        stage('SonarQube Analysis') {
            environment {
                scannerHome = tool 'SonarScanner'
            }
            steps {
                withSonarQubeEnv('SonarQube') {
                    bat "${env.scannerHome}/bin/sonar-scanner.bat"
                }
            }
        }
        stage('Code Quality Analysis') {
            steps {
                script {
                    bat 'echo "Running code quality analysis..."'
                    // Add your code quality analysis commands here
                }
            }
        }
        stage('Push to ECR') {
            steps {
                script {
                    bat 'aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin <your-aws-account-id>.dkr.ecr.${AWS_REGION}.amazonaws.com'
                    bat 'docker tag to-do-app:latest <your-aws-account-id>.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY_NAME}:latest'
                    bat 'docker push <your-aws-account-id>.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY_NAME}:latest'
                }
            }
        }
        stage('Deploy to ECS') {
            steps {
                script {
                    // Register new task definition
                    bat """
                    set TASK_FAMILY=$(aws ecs describe-task-definition --task-definition ${ECS_TASK_DEFINITION} --query 'taskDefinition.family' --output text)
                    set CONTAINER_DEFINITION=$(aws ecs describe-task-definition --task-definition ${ECS_TASK_DEFINITION} --query 'taskDefinition.containerDefinitions[0]' --output json)
                    set NEW_CONTAINER_DEFINITION=$(echo %CONTAINER_DEFINITION% | jq --arg IMAGE "<your-aws-account-id>.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY_NAME}:latest" '.image = $IMAGE')
                    aws ecs register-task-definition --family %TASK_FAMILY% --container-definitions "%NEW_CONTAINER_DEFINITION%"
                    """
                    
                    // Update the ECS service with the new task definition
                    bat """
                    set TASK_REVISION=$(aws ecs describe-task-definition --task-definition ${ECS_TASK_DEFINITION} --query 'taskDefinition.revision' --output text)
                    aws ecs update-service --cluster ${ECS_CLUSTER_NAME} --service ${ECS_SERVICE_NAME} --task-definition ${ECS_TASK_DEFINITION}:${TASK_REVISION}
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            script {
                bat 'echo "Pipeline completed successfully."'
            }
        }
        failure {
            script {
                bat 'echo "Pipeline failed."'
            }
        }
    }
}
