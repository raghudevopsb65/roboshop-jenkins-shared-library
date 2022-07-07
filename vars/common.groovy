def pipelineInit() {
  stage('Initiate Repo') {
    sh 'rm -rf *'
    git branch: 'main', url: "https://github.com/raghudevopsb65/${COMPONENT}.git"
  }
}

def publishArtifacts() {
  stage("Prepare Artifacts"){
    if (env.APP_TYPE == "nodejs") {
      sh """
        zip -r ${COMPONENT}-${TAG_NAME}.zip node_modules server.js
      """
    }
    if (env.APP_TYPE == "maven") {
      sh """
        cp target/${COMPONENT}-1.0.jar ${COMPONENT}.jar
        zip -r ${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar
      """
    }
  }

  stage('Push Artifacts to Nexus') {
    withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'pass', usernameVariable: 'user')]) {
      sh """
        curl -v -u ${user}:${pass} --upload-file ${COMPONENT}-${TAG_NAME}.zip http://172.31.7.89:8081/repository/${COMPONENT}/${COMPONENT}-${TAG_NAME}.zip
      """
    }
  }

}
