pipeline {
    agent any
    tools {        
        gradle 'Gradle 7.5.1'
    }
    stages {
        stage('Build') {
            steps {
                sh 'java -version'
                sh 'java -jar rest_boton/build/libs/rest-1.0.jar'
            }
        }
        stage('Test-sonar'){
            when {
                branch '*'
            }
            steps {
                sh 'make check'
                junit 'reports/**/*.xml'
            }
    }
         stage('Test-veracode'){
             when {
                branch 'Master'
            }
            steps {
                sh 'make check'
                junit 'reports/**/*.xml'
            }
     }
         stage('Test-publicar'){
         steps {
             sh 'make check'
             junit 'reports/**/*.xml'
             }
         }
         stage('public-toDockerhub') {
         when {
             branch 'Master'
             }
             steps {
         withCredentials([[$class: 'UsernamePasswordMultiBinding', credentialsId: jenkins_registry_cred_id, usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD']]) {
             sh "docker login -e ${docker_email} -u ${env.USERNAME} -p ${env.PASSWORD} ${docker_registry_url}"
             }
             }
         }
     stage('Deploy-qa') {
         when {
             branch 'Master'
             }
             steps {
             sh 'echo publish'
             sh 'kubeclt apply -f ingress.yaml'
         }
     }
     }
}
