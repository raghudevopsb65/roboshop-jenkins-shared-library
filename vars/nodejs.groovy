def call() {
  node() {

    common.pipelineInit()

    stage('Download Dependencies') {
      sh '''
        ls -ltr
        npm install 
      '''
    }

    // If both are equal then it is definitely a tag
    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      common.publishArtifacts()
    }

  }
}
