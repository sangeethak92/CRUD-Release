pipeline {
    agent { label 'master' }
    options { skipDefaultCheckout() }
      stages {
		stage('Unit Test') {
				steps {
						echo 'Clean Build'
						cleanWs()
						checkout scm
						sh "ls"
						sh "pwd"
						sh 'mvn clean test'
						 
				}
		}	    
		stage('Build and Package') {
			agent { label 'master' }
				steps {
						echo 'Clean Build'
						cleanWs()
						checkout scm
						sh "ls"
						sh "pwd"
						//sh "mvn clean package -DskipTests"
						sh "mvn sonar:sonar clean compile package -Dtest=\\!TestRunner* -DfailIfNoTests=false -Dsonar.projectKey=CrudApp -Dsonar.host.url=http://10.62.125.9:8085/ -Dsonar.login=f16fabd2605044f38e79e4c0e4bc5f73c55dd144"				 
				}
		}				
		stage("XLDeploy Package") {
			agent { label 'master' }
			when {
				not {
					anyOf { 
						branch 'Re*' 
						branch 'master'
					}
				}
			}
			steps {
				sh "sed -i 's/{{PACKAGE_VERSION}}/$BUILD_NUMBER/g' deployit-manifest.xml"
				sh "sed -i 's/{{Deploy-App}}/$JOB_BASE_NAME/g' deployit-manifest.xml"
				xldCreatePackage artifactsPath: 'target', manifestPath: 'deployit-manifest.xml', darPath: "${BUILD_NUMBER}.0.dar"
			
			}
		}
		stage('XLDeploy Publish') {  
		agent { label 'master' }
			when {
				not {
					anyOf { 
						branch 'Re*' 
						branch 'master'
					}
				}
			}
			steps {
				xldPublishPackage serverCredentials: 'XLDeployServer', darPath: "${BUILD_NUMBER}.0.dar"
			}
		}
		/*stage('Request to Pull Changes') {
			when {
					branch 'dev'
			}
			steps {
					sh "hub pull-request -m '${BUILD_TAG}' -b master -h dev"
			}
		}*/
		/*stage('XL Release') {
			when {
					branch 'dev'
			}
			agent { label 'master' }
				steps {
					xlrCreateRelease releaseTitle: 'Release for $BUILD_TAG', serverCredentials: 'XLReleaseServer', startRelease: true, template: 'Demo/ReleaseTemplate-1.0', variables: [[propertyName: 'Version', propertyValue: "$BUILD_NUMBER.0"], [propertyName: 'Environment', propertyValue: 'QATomcatENv']]
				}
		}*/
		stage('Test Automation') {
			when {
				branch 'PR*'
				not {
					anyOf { 
						branch 'Re*' 
						branch 'master'
					}
				}
			}
			steps {
				echo "test phase"
			}
		}					
		stage('Merge the PR Locally') {
			when {
				branch 'PR*'
			}
			steps {
				sh "git checkout -b pullrequest"						
				sh "git fetch origin master:master"					
				sh "git checkout master"
				echo 'checking local merge to master'
				sh "git merge pullrequest"
				//sh "git pull upstream master && git push origin '${CHANGE_ID}'"
				//sh "git checkout '${CHANGE_ID}'"
				//sh "git rebase master"
				//sh "git push -f origin master"
			}
		}
	      
	        stage('Pre check MergeBuild') {
			when {
				branch 'PR*'
			}
			steps {
					echo 'Clean Build'
					sh "ls"
					sh "git branch"
					sh 'mvn clean compile package -Dtest=\\!TestRunner* -DfailIfNoTests=false test'

			}
		}	    
		stage('Approve the PR request') {
			when {
				branch 'PR*'
			}
			steps {
				echo "Approve"
				sh "curl --user sangeethak92:4aecd275521fe519115ce2c9ed51c4a418a977b4 --data '{\"body\":\"This PR build is success from ${BUILD_TAG}\",\"event\":\"APPROVE\"}' --header Content-Type:application/json  --request POST https://api.github.com/repos/tonysandeep/CRUD-Release/pulls/${CHANGE_ID}/reviews"
			}
			post {
				success{
					script { 
						pullRequest.addLabel('BUILD SUCCESS')
						gitHubPRStatus githubPRMessage(" SUCCESS ${JOB_NAME}")
						pullRequest.createStatus(status: 'success',
						 context: 'Jenkins',
						 description: "This PR passed the Jenkins Reg Test ${BUILD_TAG} ${JOB_NAME}",
						 targetUrl: "${env.JOB_URL}${env.BUILD_NUMBER}/testResults")						
					}
				}
				failure {
					script {		
						pullRequest.addLabel("Failed")
						gitHubPRStatus githubPRMessage(" Failure ${BUILD_TAG}")
						pullRequest.createStatus(status: 'failure',
						 context: 'jenkins',
						 description: "oops Build got failed ${BUILD_TAG} ${JOB_NAME}",
						 targetUrl: "${env.JOB_URL}${env.BUILD_NUMBER}/testResults")						
					}
				}
				
			}
		}
		/* stage('Release Approval') {
			when {
				anyOf { branch 'Re*' 
					branch 'master'
				}
			}
			steps {
				echo 'Confirm the deployment'
				echo "waiting for approval"
				timeout( time: 120, unit: "SECONDS" ){
					input 'Do you want to proceed to the Deployment?'
					//milestone 1
				}
			}
		} */
		stage('Staging Release version') {
			when {
				anyOf { branch 'Re*' 
					branch 'master'
				}
			}						
				steps {
					echo 'set version to Release'
					script{
						def releasedVersion = getReleaseVersion()
						print releasedVersion
						sh "mvn versions:set -DnewVersion=${releasedVersion}-RELEASE"
						echo "commit the release version"
						sh "mvn versions:commit"
				
					}
				}
		}
		stage('Release Tag to SCM') {
			when {
				anyOf { branch 'Re*' 
					branch 'master'
				}
			}
			steps {
				echo 'clean test'
				script {
					def releasedVersion = getReleaseVersion()
					sh 'git status'
					//comment done to create the release branch in repo
					/*sh 'git add -- pom.xml'
					sh "git commit -a -m 'Release-${releasedVersion}'"
					sh "git checkout -b Release-${releasedVersion}"
					sh "git push https://tonysandeep:Qwerty0420@github.com/tonysandeep/trunk_release.git Release-${releasedVersion}"
					*/
					sh "git tag -a ${releasedVersion} -m 'CRUD-${releasedVersion}'"
					sh "git push https://tonysandeep:Qwerty0420@github.com/tonysandeep/trunk_release.git ${releasedVersion}"
				}
			}
		}
		stage('Sanity Test') {
			when {
				anyOf { branch 'Re*' 
					branch 'master'
				}
			}
			steps {
				script{
					echo 'clean test'
					def releasedVersion = getReleaseVersion()
					checkout([  
						$class: 'GitSCM', 
						branches: [[name: "refs/tags/$BUILD_NUMBER-${releasedVersion}"]], 
						doGenerateSubmoduleConfigurations: false, 
						extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'release']], 
						submoduleCfg: [], 
						userRemoteConfigs: [[credentialsId: '', url: 'https://github.com/tonysandeep/trunk_release.git']]
					])
					dir('release'){
						sh "mvn sonar:sonar clean compile package -Dtest=\\!TestRunner* -DfailIfNoTests=false -Dsonar.projectKey=CrudApp -Dsonar.host.url=http://10.62.125.9:8085/ -Dsonar.login=f16fabd2605044f38e79e4c0e4bc5f73c55dd144"
					}
				}
			}
		}
		stage("Create RELEASE Package") {
			when {
				anyOf { branch 'Re*' 
					branch 'master'
				}
			}
			steps {
				script {
					dir('release') { 	
						def releasedVersion = getReleaseVersion()
						sh "sed -i 's/{{PACKAGE_VERSION}}/${releasedVersion}.0/g' deployit-manifest.xml"
						sh "sed -i 's/{{Deploy-App}}/CRUD-RELEASE/g' deployit-manifest.xml"
						xldCreatePackage artifactsPath: 'release/target', manifestPath: 'release/deployit-manifest.xml', darPath: "release/${releasedVersion}.0.dar" 
					}
				}
			}
		}
		stage('XLDeploy  Publish Release package') { 
			when {
				anyOf { branch 'Re*' 
					branch 'master'
				}
			}
			steps {
				script {
					dir('release') {
						def releasedVersion = getReleaseVersion()
						xldPublishPackage serverCredentials: 'XLDeployServer', darPath: "${releasedVersion}.0.dar"
					}
				}
			}	
		}
		/*stage('UAT XL Release Deployment') { 
			when {
				 branch 'master'
			}
			steps {
				script {
					dir('release') {
						def releasedVersion = getReleaseVersion()
						xlrCreateRelease releaseTitle: "Release for $BUILD_TAG", serverCredentials: 'XLReleaseServer', startRelease: true, template: 'Demo/ReleaseTemplate-1.0', variables: [[propertyName: 'Version', propertyValue: "${releasedVersion}.0"], [propertyName: 'Environment', propertyValue: 'UATTomcatEnv']]
						//xldDeploy serverCredentials: 'XLDeployServer', environmentId: 'Environments/QATomcatENv', packageId: "Applications/AddressBook-RELEASE/${releasedVersion}.0"
					}
				}
			}
		}*/
	}     
        
    tools {
        maven 'maven3.3.9'
        jdk 'openjdk8'
    }
    post {
         always {
            echo 'JENKINS PIPELINE'
        }
        success {
            echo 'JENKINS PIPELINE SUCCESSFUL'
        }
        failure {
            echo 'JENKINS PIPELINE FAILED'
        }
        unstable {
            echo 'JENKINS PIPELINE WAS MARKED AS UNSTABLE'
        }
        changed {
            echo 'JENKINS PIPELINE STATUS HAS CHANGED SINCE LAST EXECUTION'
        }
    }     
}
def getReleaseVersion() {
	def pom = readMavenPom file: 'pom.xml'
	print pom.version
	def versionNumber;
	versionNumber = env.BUILD_NUMBER;
	print versionNumber
	return pom.version.replace("-SNAPSHOT", ".${versionNumber}")
}
