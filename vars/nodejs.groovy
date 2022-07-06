def call() {
  def optVar = binding.variables.get("TAG_NAME")
  print optVar
  
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
