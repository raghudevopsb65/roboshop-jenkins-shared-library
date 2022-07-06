def pipelineInit() {
  stage('Initiate Repo') {
    sh 'rm -rf *'
    git branch: 'main', url: "https://github.com/raghudevopsb65/${COMPONENT}.git"
  }
}

def publishArtifacts() {
  stage("Prepare Artifacts"){
    sh """
      zip -r ${COMPONENT}.zip node_modules server.js
    """
  }
}
