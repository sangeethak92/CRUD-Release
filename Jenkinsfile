pipeline {
    agent { label 'master' }
    //options { skipDefaultCheckout() }
      stages {
		stage('Unit Test') {
				steps {
						echo 'Unit Test'
					
					
					echo "***** ${env.BRANCH_NAME} *******"
						
						 
				}
		}	    
		stage('Build and Package') {
			agent { label 'master' }
				steps {
					script {
						echo 'Clean Build'
						cleanWs()
					        def scmVars = checkout scm
						
					        env.GIT_COMMIT = scmVars.GIT_COMMIT
						sh "mvn clean package -DskipTests"
						//sh "mvn sonar:sonar clean compile package -Dtest=\\!TestRunner* -DfailIfNoTests=false -Dsonar.projectKey=CrudApp -Dsonar.host.url=http://10.62.125.9:8085/ -Dsonar.login=f16fabd2605044f38e79e4c0e4bc5f73c55dd144"				 
						
					}
				}
		}	
	      
	      
		stage("XLDeploy Package") {
			agent { label 'master' }
            steps {
                script {
		     pom = readMavenPom file: "pom.xml";
		     POM_VERSION = "${pom.version}";
		     
		     if (env.BRANCH_NAME == 'Release*' | env.BRANCH_NAME == 'RELEASE*' ) {
                       sh "sed -i 's/{{PACKAGE_VERSION}}/$pom.version.$BUILD_NUMBER/g' deployit-manifest.xml"
				sh "sed -i 's/{{Deploy-App}}/$JOB_BASE_NAME/g' deployit-manifest.xml"
				xldCreatePackage artifactsPath: 'target', manifestPath: 'deployit-manifest.xml', darPath: "${pom.version}.${BUILD_NUMBER}.dar"
                    }
		    else if(env.BRANCH_NAME == 'master') {
		     
                       sh "sed -i 's/{{PACKAGE_VERSION}}/$BUILD_NUMBER/g' deployit-manifest.xml"
				sh "sed -i 's/{{Deploy-App}}/$JOB_BASE_NAME/g' deployit-manifest.xml"
				xldCreatePackage artifactsPath: 'target', manifestPath: 'deployit-manifest.xml', darPath: "${BUILD_NUMBER}.0.dar"
                    } 
		    
		    else
		    {
		    echo "********************* CHECK THE BRANCH NAME  *****************"
		    }
		    
                }
            }
             } 
		
		
	 stage('XLDeploy Publish') {  
		agent { label 'master' }

			steps {
			
			script {
			 pom = readMavenPom file: "pom.xml";
			   if (env.BRANCH_NAME == 'RELEASE*' | env.BRANCH_NAME == 'Release*' ) {
		       
                             xldPublishPackage serverCredentials: 'XLDeployServer', darPath: "${pom.version}.${BUILD_NUMBER}.dar"
                       } 
		           else if (env.BRANCH_NAME == 'master'){
			 
                               xldPublishPackage serverCredentials: 'XLDeployServer', darPath: "${BUILD_NUMBER}.0.dar"
                             }
			     
			     else
			     {
			     echo "********************* CHECK THE BRANCH NAME  *****************"
			     }
				
	             }			
		 }
              } 
		
                           
			
		/* stage('Merge the PR Locally') {
			
			steps {
				
				sh "git checkout -b ${PR_NUMBER}"	
				//sh "git checkout -b pullrequest"
				sh "git fetch origin master:master"					
				sh "git checkout master"
				echo 'checking local merge to master'
				sh "git merge ${PR_NUMBER}"
				//sh "git merge pullrequest"
				
				//sh "git pull upstream master && git push origin '${PR_NUMBER}'"
				//sh "git checkout '${PR_NUMBER}'"
				//sh "git rebase master"
				//sh "git push -f origin master"
			}
		} */
	      
	        /*stage('Pre check MergeBuild') {
				
			when {
			  not{
			   
				   branch comparator: 'REGEXP', pattern: 'origin/Release.*|origin/master|origin/RELEASE.*'
			  }
			}
                   
			steps {
				
				echo 'Clean Build'
					sh "ls"
					sh "git branch"
					sh 'mvn clean compile package -Dtest=\\!TestRunner* -DfailIfNoTests=false test'
				
			}
		}	*/    
		stage('Approve the PR request') {
			when {
			  not{
			   
				   branch comparator: 'REGEXP', pattern: 'origin/Release.*|origin/master|origin/RELEASE.*'
			  }
			}
			steps {
				echo "Approve"
			       sh "curl --user tonysandeep:Qwerty0420 --data '{\"body\":\"This PR build is success from ${BUILD_TAG}\",\"event\":\"APPROVE\"}' --header Content-Type:application/json  --request POST https://api.github.com/repos/sangeethak92/CRUD-Release/pulls/${PR_NUMBER}/reviews" 
				/*sh "curl --user tonysandeep:Qwerty0420 --data '{\"body\":\"This PR build is success from ${BUILD_TAG}\",\"event\":\"APPROVE\"}' --header Content-Type:application/json  --request POST https://api.github.com/repos/sangeethak92/CRUD-Release/pulls/${env.CHANGE_ID}/reviews" */
			}
			post {
				success{
					script { 
						
						
						echo "git commit !!!!!!!!!!!!!!  ${env.GIT_COMMIT}"
						
						sh "curl --user sangeethak92:Jothi1724 --data '{\"state\": \"success\",\"context\": \"continuous-integration/jenkins\", \"description\": \"Jenkins\", \"target_url\": \"http://10.62.125.9:8087/job/$JOB_NAME/$BUILD_NUMBER/console\"}' --header Content-Type:application/json --request POST https://api.GitHub.com/repos/sangeethak92/CRUD-Release/statuses/$env.GIT_COMMIT"
					}
				}
				failure {
					script {		
						
						sh "curl --user sangeethak92:Jothi1724 --data '{\"state\": \"failure\",\"context\": \"continuous-integration/jenkins\", \"description\": \"Jenkins\", \"target_url\": \"http://10.62.125.9:8087/job/$JOB_NAME/$BUILD_NUMBER/console\"}' --header Content-Type:application/json --request POST https://api.GitHub.com/repos/sangeethak92/CRUD-Release/statuses/$env.GIT_COMMIT"
					}
				} 
				
				 /* success{
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
				
				*/
				
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
		 cleanWs()
        }
        success {
            echo 'JENKINS PIPELINE SUCCESSFUL'
        }
        failure {
            echo 'JENKINS PIPELINE FAILED'
	    echo "RES: ${currentBuild.currentResult}"
            
        }
        unstable {
            echo 'JENKINS PIPELINE WAS MARKED AS UNSTABLE'
	    echo "RES: ${currentBuild.currentResult}"
        }
        changed {
            echo 'JENKINS PIPELINE STATUS HAS CHANGED SINCE LAST EXECUTION'
        }
    }     
}

