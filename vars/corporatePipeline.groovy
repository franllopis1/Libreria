def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

  pipeline {
    agent none
    stages {
      stage('Fluffy Build') {
        parallel {
          stage('Build Java 8') {
            agent {
              node {
                label 'controller'
              }

            }
            post {
              success {
                stash(name: 'Java 8', includes: 'target/**')

              }

            }
            steps {
              runLinuxScript(name: "build.sh")
            }
          }
          stage('Build Java 7') {
            agent {
              node {
                label 'controller'
              }

            }
            post {
              success {
                postBuildSuccess(stashName: "Java 7")
              }

            }
            steps {
              runLinuxScript(name: "build.sh")
            }
          }
        }
      }
      stage('Fluffy Test') {
        parallel {
          stage('Backend Java 8') {
            agent {
              node {
                label 'java8'
              }

            }
            post {
              always {
                junit 'target/surefire-reports/**/TEST*.xml'

              }

            }
            steps {
              unstash 'Java 8'
              sh './jenkins/test-backend.sh'
            }
          }
          stage('Frontend Java 8') {
            agent {
              node {
                label 'java8'
              }

            }
            post {
              always {
                junit 'target/test-results/**/TEST*.xml'

              }

            }
            steps {
              unstash 'Java 8'
              sh './jenkins/test-frontend.sh'
            }
          }
          stage('Performance Java 8') {
            agent {
              node {
                label 'java8'
              }

            }
            steps {
              unstash 'Java 8'
              sh './jenkins/test-performance.sh'
            }
          }
          stage('Static Java 8') {
            agent {
              node {
                label 'java8'
              }

            }
            steps {
              unstash 'Java 8'
              sh './jenkins/test-static.sh'
            }
          }
          stage('Backend Java 7') {
            agent {
              node {
                label 'java7'
              }

            }
            post {
              always {
                junit 'target/surefire-reports/**/TEST*.xml'

              }

            }
            steps {
              unstash 'Java 7'
              sh './jenkins/test-backend.sh'
            }
          }
          stage('Frontend Java 7') {
            agent {
              node {
                label 'java7'
              }

            }
            post {
              always {
                junit 'target/test-results/**/TEST*.xml'

              }

            }
            steps {
              unstash 'Java 7'
              sh './jenkins/test-frontend.sh'
            }
          }
          stage('Performance Java 7') {
            agent {
              node {
                label 'java7'
              }

            }
            steps {
              unstash 'Java 7'
              sh './jenkins/test-performance.sh'
            }
          }
          stage('Static Java 7') {
            agent {
              node {
                label 'java7'
              }

            }
            steps {
              unstash 'Java 7'
              sh './jenkins/test-static.sh'
            }
          }
        }
      }
      stage('Confirm Deploy') {
        when {
          branch 'master'
        }
        steps {
          input(message: 'Okay to Deploy to Staging?', ok: 'Let\'s Do it!')
        }
      }
      stage('Fluffy Deploy') {
        agent {
          node {
            label 'java7'
          }

        }
        when {
          branch 'master'
        }
        steps {
          unstash 'Java 7'
          sh "./jenkins/deploy.sh ${pipelineParams.deployTo}"
        }
      }
    }
  }
}