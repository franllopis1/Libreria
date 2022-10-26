def call(body) {
    def pipelineParams= [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = pipelineParams
    body()

  pipeline {
    agent any
    options {
    durabilityHint 'MAX_SURVIVABILITY'
    preserveStashes(buildCount: 5)
  }
    stages {
      stage('Fluffy Build') {
        parallel {
          stage('Build Java 8') {
            agent {
              node {
                label 'controller'
              }
            }
            steps{
                echo 'prueba'
            }
          }
        }
      }
    }
  }
}
