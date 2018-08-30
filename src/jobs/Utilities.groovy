package jobs

//import javaposse.jobdsl.dsl.Job

public class Utilities {
	
	// should return something like
	// 'acn-hpsapf-'
	// 'ba-'
	// 'bc-'
	static finalPrefix;
	
	def preFixMethod(String prefix) {
		this.finalPrefix = prefix;
	}

	// name of the Release Unit (dabag, functionallog, deadlinemanagement, ...)
	static final RELEASE_UNIT_NAME  = 'functionallog'
	static final ITERATION_NUMBER = '4'
	static final RELEASE_UNIT_ITERATION = 'it' + ITERATION_NUMBER
	static final RELEASE_UNIT_REPO_WS_PREFIX = 'BUILD_DABAG_justiz-' + RELEASE_UNIT_NAME
	static final RELEASE_UNIT_PARENT_ARTIFACT_ID = finalPrefix + RELEASE_UNIT_NAME + '-basecomponent-parent' 

	static void addMavenFeature(def job) {
        job.with {
            // global maven settings
		    mavenInstallation('maven339')
		    mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
		  	//providedSettings('central-mirror')
		  	providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
		  	rootPOM(RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
        }
    }

}