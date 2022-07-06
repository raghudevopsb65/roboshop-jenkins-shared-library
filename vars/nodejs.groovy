def call() {
  def TAG_NAME = null
  node() {

    common.pipelineInit()

    stage('Download Dependencies') {
      sh '''
        ls -ltr
        npm install 
      '''
    }

    if( BRANCH_NAME == TAG_NAME )
    {
      sh 'echo Yes TAG'
    }
    //common.publishArtifacts()

  }
}
