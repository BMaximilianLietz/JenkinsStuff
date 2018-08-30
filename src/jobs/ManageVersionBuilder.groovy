package jobs

class ManageVersionBuilder {
	
	public static class Builder {
		
		Object mavenJob;
		 
		public Builder (job) {
			this.mavenJob = job;	
		}
		
		void setDescription() {
			this.mavenJob.with {
				// was previously hard coded in some cases
				//description(Utilities.RELEASE_UNIT_NAME + 'release build ')
				description(Utilities.RELEASE_UNIT_NAME + 'release build ')
			}
		}
		
		void setParameters(Object[][] parameterse) {
			this.mavenJob.with {
				parameters {
					for (Object[] element : parameterse) {
						booleanParam(element[0], element[1], element[2])
					}
				}
			}
		}
		
		void setWrappersPreBuildCleanUp() {
			this.mavenJob.with {
				wrappers {
					preBuildCleanup()
				}
			}
		}
		
		void setScm() {
			this.mavenJob.with {
				scm {
					rtc {
					  buildWorkspace(Utilities.RELEASE_UNIT_REPO_WS_PREFIX + '_ManageVersion')
					}
				}
			}
		}
		
		void setWrappersBuildName(isPreBuildCleanUpWrapper) {
			this.mavenJob.with {
				wrappers {
					buildName('#${ENV,var="SNAPSHOT_TAG"} (${BUILD_NUMBER})')
				}
			}
		}
		
		void setWrappersCredentialBinding() {
			this.mavenJob.with {
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
			}
		}
		
		void setSteps() {
			this.mavenJob.with {
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
				
				shell {
					// welcher code soll hier rein? Sollen alle die selbe Aufgabe erfüllen oder soll ReferenceApplication weniger machen als die anderen Files
				}
			}
		}
	}
}
