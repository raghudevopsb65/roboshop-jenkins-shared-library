def call() {
  node {
    properties([
        parameters([
            choice(choices: ['dev', 'prod'], description: "Choose Environment", name: "ENV"),
            choice(choices: ['apply', 'destroy'], description: "Choose Action", name: "ACTION"),
        ])
    ])

    ansiColor('xterm') {

      stage('Code Checkout') {
        sh 'find . | sed -e "1d" | xargs rm -rf'
        git branch: 'main', url: "https://github.com/raghudevopsb65/${REPO_NAME}.git"
      }

      stage('Terraform INIT') {
        sh 'terraform init -backend-config=env/${ENV}-backend.tfvars'
      }

      stage('Terraform Plan') {
        sh 'terraform plan -var-file=env/${ENV}.tfvars'
      }

      stage("Terraform ${ACTION}") {
        sh 'terraform ${ACTION} -auto-approve -var-file=env/${ENV}.tfvars'
      }
    }
  }
}
