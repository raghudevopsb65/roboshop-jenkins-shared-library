def pipelineInit() {
  stage('Initiate Repo') {
    sh 'find . | sed -e "1d" | xargs rm -rf'
    git branch: "main", url: "https://github.com/raghudevopsb65/${COMPONENT}.git"
    sh 'git tag --list'
    sh 'exit 1'
  }
}

def publishArtifacts() {
  env.ENV = "dev"
  stage("Prepare Artifacts"){
    if (env.APP_TYPE == "nodejs") {
      sh """
        zip -r ${ENV}-${COMPONENT}-${TAG_NAME}.zip node_modules server.js
      """
    }
    if (env.APP_TYPE == "maven") {
      sh """
        cp target/${COMPONENT}-1.0.jar ${COMPONENT}.jar
        zip -r ${ENV}-${COMPONENT}-${TAG_NAME}.zip ${COMPONENT}.jar
      """
    }
    if (env.APP_TYPE == "python") {
      sh """
        zip -r ${ENV}-${COMPONENT}-${TAG_NAME}.zip *.py ${COMPONENT}.ini requirements.txt
      """
    }
    if (env.APP_TYPE == "nginx") {
      sh """
        cd static
        zip -r ../${ENV}-${COMPONENT}-${TAG_NAME}.zip *
      """
    }
  }

  stage('Push Artifacts to Nexus') {
    withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'pass', usernameVariable: 'user')]) {
      sh """
        curl -v -u ${user}:${pass} --upload-file ${ENV}-${COMPONENT}-${TAG_NAME}.zip http://172.31.7.89:8081/repository/${COMPONENT}/${ENV}-${COMPONENT}-${TAG_NAME}.zip
      """
    }
  }

  stage('Deploy to Dev Env') {
    build job: 'deploy-to-any-env', parameters: [string(name: 'COMPONENT', value: "${COMPONENT}"), string(name: 'ENV', value: "${ENV}"), string(name: 'APP_VERSION', value: "${TAG_NAME}")]
  }

  stage('Run Smoke Tests on DEV') {
    sh "echo Smoke tests"
  }

  promoteRelease("dev", "qa")

  stage('Deploy to QA Env') {
    //build job: 'deploy-to-any-env', parameters: [string(name: 'COMPONENT', value: "${COMPONENT}"), string(name: 'ENV', value: "qa"), string(name: 'APP_VERSION', value: "${TAG_NAME}")]
    echo 'QA Deploy'
  }

  testRuns()

  stage('Run Smoke Tests on QA') {
    sh "echo Smoke tests"
  }

  promoteRelease("qa", "prod")
}

def testRuns() {
  stage('Quality Checks & Unit Tests') {
    parallel([
        integrationTests: {
          echo 'integrationTests'
        },
        e2eTests: {
          echo 'e2eTests'
        },
        penTests: {
          echo 'penTests'
        }
    ])
  }
}

def promoteRelease(SOURCE_ENV,ENV) {
  stage("Promoting Artifact from ${SOURCE_ENV} to ${ENV}") {
    withCredentials([usernamePassword(credentialsId: 'NEXUS', passwordVariable: 'pass', usernameVariable: 'user')]) {
      sh """
      cp ${SOURCE_ENV}-${COMPONENT}-${TAG_NAME}.zip ${ENV}-${COMPONENT}-${TAG_NAME}.zip
      curl -v -u ${user}:${pass} --upload-file ${ENV}-${COMPONENT}-${TAG_NAME}.zip http://172.31.7.89:8081/repository/${COMPONENT}/${ENV}-${COMPONENT}-${TAG_NAME}.zip
    """
    }
  }
}

def codeChecks() {
  stage('Quality Checks & Unit Tests') {
    parallel([
        qualityChecks: {
          withCredentials([usernamePassword(credentialsId: 'SONAR', passwordVariable: 'pass', usernameVariable: 'user')]) {
            //sh "sonar-scanner -Dsonar.projectKey=${COMPONENT} -Dsonar.host.url=http://172.31.14.79:9000 -Dsonar.login=${user} -Dsonar.password=${pass} ${EXTRA_OPTS}"
            //sh "sonar-quality-gate.sh ${user} ${pass} 172.31.14.79 ${COMPONENT}"
            echo "Code Analysis"
          }
        },
        unitTests: {
          unitTests()
        }
    ])
  }
}

def unitTests() {
    if (env.APP_TYPE == "nodejs") {
      sh """
        # npm run test 
        echo Run test cases
      """
    }
    if (env.APP_TYPE == "maven") {
      sh """
        # mvn test 
        echo Run test cases
      """
    }
    if (env.APP_TYPE == "python") {
      sh """
        # pythom -m unittest
        echo Run test cases
      """
    }
    if (env.APP_TYPE == "nginx") {
      sh """
        # npm run test 
        echo Run test cases
      """
    }
}

def publishAMI() {
  ansiColor('xterm') {
    stage('Publish AMI') {
      sh '''
      terraform init 
      terraform apply -auto-approve -var APP_VERSION=${TAG_NAME}
    '''
    }
  }
}

def publishLocalArtifacts() {
  stage('Publish Local Artifacts') {
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
    if (env.APP_TYPE == "python") {
      sh """
        zip -r ${COMPONENT}-${TAG_NAME}.zip *.py ${COMPONENT}.ini requirements.txt
      """
    }
    if (env.APP_TYPE == "nginx") {
      sh """
        cd static
        zip -r ../${COMPONENT}-${TAG_NAME}.zip *
      """
    }
  }
}