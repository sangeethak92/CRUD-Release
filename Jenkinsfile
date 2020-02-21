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
						sh "mvn clean package -DskipTests"
						//sh "mvn sonar:sonar clean compile package -Dtest=\\!TestRunner* -DfailIfNoTests=false -Dsonar.projectKey=CrudApp -Dsonar.host.url=http://10.62.125.9:8085/ -Dsonar.login=f16fabd2605044f38e79e4c0e4bc5f73c55dd144"				 
				}
		}				
 /*		stage("XLDeploy Package") {
			agent { label 'master' }
            steps {
                script {
		     pom = readMavenPom file: "pom.xml";
		     POM_VERSION = "${pom.version}";
		     
                    if (env.BRANCH_NAME == 'master' | env.BRANCH_NAME == 'PR*' ) {
                       sh "sed -i 's/{{PACKAGE_VERSION}}/$BUILD_NUMBER/g' deployit-manifest.xml"
				sh "sed -i 's/{{Deploy-App}}/$JOB_BASE_NAME/g' deployit-manifest.xml"
				xldCreatePackage artifactsPath: 'target', manifestPath: 'deployit-manifest.xml', darPath: "${BUILD_NUMBER}.0.dar"
                    } else {
                       sh "sed -i 's/{{PACKAGE_VERSION}}/$pom.version.$BUILD_NUMBER/g' deployit-manifest.xml"
				sh "sed -i 's/{{Deploy-App}}/$JOB_BASE_NAME/g' deployit-manifest.xml"
				xldCreatePackage artifactsPath: 'target', manifestPath: 'deployit-manifest.xml', darPath: "${pom.version}.${BUILD_NUMBER}.dar"
                    }
                }
            }
             } 
		
		
		stage('XLDeploy Publish') {  
		agent { label 'master' }

			steps {
			
			script {
			 pom = readMavenPom file: "pom.xml";
			 if (env.BRANCH_NAME == 'master' | env.BRANCH_NAME == 'PR*' ) {
			 
                               xldPublishPackage serverCredentials: 'XLDeployServer', darPath: "${BUILD_NUMBER}.0.dar"
                       } else {
		       
                             xldPublishPackage serverCredentials: 'XLDeployServer', darPath: "${pom.version}.${BUILD_NUMBER}.dar"
                       }
				
	             }			
		 }
              } 
		
	*/	
			
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
				//sh "git pull upstream master && git push origin '${PR_NUMBER}'"
				//sh "git checkout '${PR_NUMBER}'"
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
				sh "curl --user tonysandeep:b0b923b0eb57279f2f598b9d9ffdf189af9763a1 --data '{\"body\":\"This PR build is success from ${BUILD_TAG}\",\"event\":\"APPROVE\"}' --header Content-Type:application/json  --request POST https://api.github.com/repos/sangeethak92/CRUD-Release/pulls/${PR_NUMBER}/reviews"
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

