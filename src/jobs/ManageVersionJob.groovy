package jobs

//import javaposse.jobdsl.dsl.Job
import jobs.Utilities

public class ManageVersionJob {

	static void createJob(def dslFactory) {
	
		def newJob = dslFactory.job(Utilities.RELEASE_UNIT_NAME + '-manageversion')
		
		//Utilities.addMavenFeature(newMavenJob)
		
		addSteps(newJob)
		
		//addPublisher(newJob)
	
	}
	
	def 
	
	static void addSteps(def job) {
        job.with {
			// removed the dot from reference application 
        	description(Utilities.RELEASE_UNIT_NAME + 'version management job ')
        	
        	parameters {
		        stringParam('SNAPSHOT_TAG', '0.1.13-SNAPSHOT', 'new Snapshot Version like 0.1.12-SNAPSHOT')
	        }
	        
	        wrappers {
    		    preBuildCleanup()
    		}
    		
    		scm {
		      rtc {
		        buildWorkspace(Utilities.RELEASE_UNIT_REPO_WS_PREFIX + '_ManageVersion')
		      }
		    } 
		    
		    wrappers {
		        buildName('#${ENV,var="SNAPSHOT_TAG"} (${BUILD_NUMBER})')
		    }
		    
		    wrappers {
			  	credentialsBinding {
				  usernamePassword {
				    // Name of an environment variable to be set to the username during the build.
				    usernameVariable('RTC_USER')
				    // Name of an environment variable to be set to the password during the build.
				    passwordVariable('RTC_PWD')
				    // Credentials of an appropriate type to be set to the variable. --> BUILD_DABAG2
				    credentialsId('b84f68be-3c25-43ed-8dd2-f3c7a9ec8528')
				  }
			    }
			}
			
			steps {
		    
		    	maven {
		            goals('versions:set -DnewVersion=${SNAPSHOT_TAG} -DgenerateBackupPoms=false')
		          	rootPOM(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
		            mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
		            //localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
		            //properties(skipTests: true)
		            mavenInstallation('maven339')
		            //providedSettings('central-mirror')
		          	providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
		        }
		        
				// referenceapplication was slightly shorter
		        shell('''#exit 0
# rm -rf env.properties

#source /etc/environment
REPO_URL="https://rtc5a.gsissc.myatos.net/rtc5a/"

if ! lscm list connections | grep "${REPO_URL}" | grep "Password stored" 
then lscm login -r ${REPO_URL} -n jenkins -c -u ${RTC_USER} -P ${RTC_PWD} 
fi

lscm show status

#exit 0

lscm checkin --comment "update Version to ${SNAPSHOT_TAG}" ${WORKSPACE}

lscm deliver -i -v -r jenkins    
#lscm logout -r jenkins
				''')
						
		    
		    }
        
        }
        
    }
    
}