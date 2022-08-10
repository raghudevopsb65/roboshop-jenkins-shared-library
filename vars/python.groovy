def call() {
  env.EXTRA_OPTS=""
  node() {

    common.pipelineInit()
    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      sh 'git checkout ${TAG_NAME}'
    }
    common.codeChecks()

    if( env.BRANCH_NAME == env.TAG_NAME )
    {
      //common.publishArtifacts()
      common.publishLocalArtifacts()
      // This is added for Immutable Approach
      common.publishAMI()
    }

  }
}
