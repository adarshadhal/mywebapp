pipeline {
    agent any
    stages {
        stage("Compile") {
            steps {
                echo 'Compiling...'
                sh 'mvn clean compile'
            }
        }
        stage("Code Review") {
            steps {
                echo 'Code review...'
                sh 'mvn -P metrics pmd:pmd'
            }
        }
        stage("Code Verify") {
            steps {
                echo 'Code verify...'
                sh 'mvn verify'
            }
            post {
                success {
                    jacoco changeBuildStatus: true, runAlways: true, skipCopyOfSrcFiles: true
                }
            }
        }
        stage("Unit Test") {
                echo 'Unit testing...'
                sh 'mvn test'
            }
        }
        stage("Package") {
            steps {
                echo 'Packaging...'
                sh 'mvn clean package'
            }
        }
        stage("Docker Build") {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t ad:1 .'
            }
        }
        stage("Docker Run") {
            steps {
                echo 'Running container...'
                sh '''
                    docker stop mywebapp || true
                    docker rm mywebapp || true
                    docker run -d -p 9090:8080 --name mywebapp ad:1
                '''
            }
        }
    }
    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
}

