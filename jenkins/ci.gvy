pipeline{
    agent 'any'
    stages{
        stage("stage-1"){
            steps{
                echo 'compile'
                git branch: 'main',
                    credentialsId: 'GIT_HUB',
                    url: 'https://github.com/adarshadhal/mywebapp.git'
                    sh 'mvn clean compile'
            }           
        }
        stage('stage-02'){
            steps{
                echo 'Code review'
                sh 'mvn -P metrics pmd:pmd'
            }
        }
        stage ("code verify"){
            steps {
                echo 'code verify..'
                sh 'mvn verify'
            }
            post{
                success{
                    jacoco changeBuildStatus: true, runAlways: true, skipCopyOfSrcFiles: true
                }
            }
        }
         stage ("code uni-test"){
            steps {
                echo 'unitest'
                sh 'mvn test'
            }
        }
        stage ("code package"){
            steps{
                echo 'code package..'
                sh 'mvn package'
            }
        }

    }
}
