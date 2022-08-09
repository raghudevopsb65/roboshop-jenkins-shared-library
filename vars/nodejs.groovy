def call() {
  env.EXTRA_OPTS=""
  node() {

    common.pipelineInit()

    stage('Download Dependencies') {
      sh '''
        ls -ltr
        npm install 
      '''
    }

    common.codeChecks()

    // If both are equal then it is definitely a tag
    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      common.publishArtifacts()
      // This is added for Immutable Approach
      common.publishAMI()
    }

  }
}
