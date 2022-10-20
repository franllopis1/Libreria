def call(Map config = [:]) {
    archiveArtifacts 'target/*.jar'
    stash(name: "${config.stashName}", includes: 'target/**')
}