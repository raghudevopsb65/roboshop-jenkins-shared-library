def pipelineInit() {
  stage('Initiate Repo') {
    sh 'rm -rf *'
    git branch: 'main', url: 'https://github.com/raghudevopsb65/cart.git'
  }
}
