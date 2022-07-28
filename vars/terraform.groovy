def call() {
  node {
    ansiColor('xterm') {
      stage('Terraform INIT') {
        sh 'terraform init'
      }

      stage('Terraform Plan') {
        sh 'terraform plan'
      }

      stage('Terraform Apply') {
        sh 'terraform apply -auto-approve'
      }
    }
  }
}
