pipeline {
    agent any
    stages {
        stage("Checkout") {
            steps {
                echo 'Cloning repository...'
                git branch: 'main',
                    credentialsId: 'GIT_HUB',
                    url: 'https://github.com/adarshadhal/mywebapp.git'
            }
        }
        stage("Compile") {
            steps {
                echo 'Compiling...'
                dir('myapp') {
                    sh 'mvn clean compile'
                }
            }
        }
        stage("Code Review") {
            steps {
                echo 'Code review...'
                dir('myapp') {
                    sh 'mvn -P metrics pmd:pmd'
                }
            }
        }
        stage("Code Verify") {
            steps {
                echo 'Code verify...'
                dir('myapp') {
                    sh 'mvn verify'
                }
            }
            post {
                success {
                    jacoco changeBuildStatus: true, runAlways: true, skipCopyOfSrcFiles: true
                }
            }
        }
        stage("Unit Test") {
            steps {
                echo 'Unit testing...'
                dir('myapp') {
                    sh 'mvn test'
                }
            }
        }
        stage("Package") {
            steps {
                echo 'Packaging...'
                dir('myapp') {
                    sh 'mvn clean package'
                }
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
}
