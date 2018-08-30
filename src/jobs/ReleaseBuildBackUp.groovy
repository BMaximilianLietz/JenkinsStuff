package jobs

//import jobs.ReleaseBuildVersuch2_.UtilitiesBackup

public class ReleaseBuildBackup_ {
	
	public static class UtilitiesBackup {
		
		static String prefix
		// should return something like
		// 'acn-hpsapf-'
		// 'ba-'
		// 'bc-'
		def setPrefixMethod(String prefix) {
			this.prefix = prefix
		}
	
		// name of the Release Unit (dabag, functionallog, deadlinemanagement, ...)
		static final RELEASE_UNIT_NAME  = 'functionallog'
		static final ITERATION_NUMBER = '4'
		static final RELEASE_UNIT_ITERATION = 'it' + ITERATION_NUMBER
		static final RELEASE_UNIT_REPO_WS_PREFIX = 'BUILD_DABAG_justiz-' + RELEASE_UNIT_NAME
		static final RELEASE_UNIT_PARENT_ARTIFACT_ID = prefix + RELEASE_UNIT_NAME + '-basecomponent-parent'
	
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
	
	public static class Builder {
		
		Object mavenJob;
		
		//String postBuildStepsArgument = 'UNSTABLE'
		
		public Builder(job) {
			this.mavenJob = job;
		}
		
		
		void setDescription() {
			this.mavenJob.with {
				// was previously hard coded in some cases
				//description(UtilitiesBackup.RELEASE_UNIT_NAME + 'release build ')
				description(UtilitiesBackup.RELEASE_UNIT_NAME + 'release build ')
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
		
		void setConcurrentBuild(concurrentBuildBool) {
			this.mavenJob.with {
				concurrentBuild(concurrentBuildBool)
			}
		}
		
		// ERROR MESSAGE:
		// Cannot read write-only property: archivingDisabled 
		void setArchivingDisabled(archivingDisabledBool) {
			this.mavenJob.with {
				archivingDisabled(archivingDisabledBool)
			}
		}
		
		// permission1, permission2, etc. should be something like 
		// 'hudson.model.Item.Read', 'developer'
		void setAuthorization(Object permissions) {
			
			this.mavenJob.with {
				authorization {
					permissions.each { key, value ->
						permission(key, value)
					}
				}				
			}
		}
		
		// insert UtilitiesBackup.RELEASE_UNIT_NAME as second argument
		void setDeliveryPipelineConfiguration(){
			mavenJob.with {
				deliveryPipelineConfiguration('Releasebuild', "Test String")
			}
		}
		
		void setLogRotator(numToKeepInt) {
			this.mavenJob.with {
				logRotator {
					numToKeep(numToKeepInt)
				}
			}
		}
		
		void setWrappers() {
			this.mavenJob.with {
				wrappers {
					preBuildCleanup()
				}
			}
		}
		
		// differentiate between cron and scm trigger?
		void setTriggers(isScm, triggerTime) {
			mavenJob.with {
				triggers {
					if (isScm) {
						scm(triggerTime)
					} else {
						cron(triggerTime)
					}
				}
			}
		}
		
		void setScm() {
			mavenJob.with {
				scm {
					rtc {
						buildWorkspace(UtilitiesBackup.RELEASE_UNIT_REPO_WS_PREFIX + '_Release')
					}
				}
			}
		}
		
		// method should replace goals; equivalent to goals('build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.${BUILD_NUMBER}')
		
		void setPreBuildStepsTest(goalsString, goalsStringTwo) {
			this.mavenJob.with {
				preBuildSteps {
					//shell("echo 'run before Maven'")
					maven {
						goals(goalsString)
						rootPOM(UtilitiesBackup.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
						mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
						//localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
						//properties(skipTests: true)
						mavenInstallation('maven339')
						//providedSettings('central-mirror')
						providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
					}
					
					maven {
						goals(goalsStringTwo)
						rootPOM(UtilitiesBackup.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
						mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
						//localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
						//properties(skipTests: true)
						mavenInstallation('maven339')
						//providedSettings('central-mirror')
						providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
					}
					environmentVariables {
						propertiesFile(UtilitiesBackup.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/release.properties')
					}
				}
				
				wrappers {
					buildName('#${ENV,var="RELEASE_TAG"}')
				}
			}
		}
		
		
		void setPreBuildStepsMavenGoals(goalsString) {
			mavenJob.with {
				preBuildSteps {
					maven {
						goals(goalsString)
						rootPOM(UtilitiesBackup.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
						mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
						//localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
						//properties(skipTests: true)
						mavenInstallation('maven339')
						//providedSettings('central-mirror')
						providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
					}
				}
			}
		}
		
		void setPreBuildStepsBuildNameUpdater() {
			mavenJob.with {
				preBuildSteps {
					buildNameUpdater {
						fromFile(false)
						buildName('#${ENV,var="RELEASE_TAG"}')
						fromMacro(true)
						macroTemplate('#${ENV,var="RELEASE_TAG"}')
						macroFirst(true)
					}
				}
			}
		}
		
		/*
		void setPreBuildStepsBuildNameUpdater(fromFileBool, buildNameString, fromMacroBool,
			macroTemplateString, macroFirstBool) {
			mavenJob.with {
				buildNameUpdater {
					fromFile(fromFileBool)
					buildName(buildNameString)
					fromMacro(fromMacroBool)
					macroTemplate(macroTemplateString)
					macroFirst(macroFirstBool)
				}
			}
		}
		*/
		
		// preBuildSteps Code ends here
		
		void setGoals(){
			mavenJob.with {
				goals('clean deploy -B -Ddabag.maven.versionmode=release -Dsonar.skip=true -X');
			}
		}
		
		// postBuildSteps Code starts here
		
		void setPostBuildStepsMaven(postBuildGoals, String threshold = "UNSTABLE") {
			mavenJob.with {
				postBuildSteps(threshold) {
					maven {
						// goals varies in files
						goals(postBuildGoals)
						
						// Other attributes are the same in given files
						rootPOM(UtilitiesBackup.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
						mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
						//localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
						//properties(skipTests: true)
						mavenInstallation('maven339')
						//providedSettings('central-mirror')
						providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
					}
				}
			}
		}
		
		void setPostBuildStepsConditionalSteps(firstBooleanCondition, secondBooleanCondition, 
			execute_SAOPUI_bool, downStreamTriggerOne, downStreamTriggerTwo) {
			mavenJob.with {
				postBuildSteps(postBuildStepsArgument) {
					
					conditionalSteps {
	                    condition {
	                        booleanCondition(firstBooleanCondition)
	                    }
	                    //default:fail
	                    runner('DontRun')
	                    steps {
	                        downstreamParameterized {
	                            trigger(downStreamTriggerOne) {
	                                parameters {
	                                    predefinedProp('RELEASE_TAG', '${RELEASE_TAG}')
										if (execute_SAOPUI_bool) {
											predefinedProp('EXECUTE_SOAPUI_TEST', 'true')
										}
	                                }
	                            }
	                        }
	                    }
					}
					
					conditionalSteps {
						condition {
							booleanCondition(secondBooleanCondition)
						}
						//default:fail
						runner('DontRun')
						steps {
							downstreamParameterized {
								trigger(downStreamTriggerTwo) {
									parameters {
										predefinedProp('RELEASE_TAG', '${RELEASE_TAG}')
										if (execute_SAOPUI_bool) {
											predefinedProp('EXECUTE_SOAPUI_TEST', 'true')
										}
									}
								}
							}
						}
					}
				}
			}
		}		
		
		void setAddPublisher(buildPipelineTriggerExists, buildPipelineTriggerArgument) {
			this.mavenJob.with {
				publishers {
					if (buildPipelineTriggerExists) {
						buildPipelineTrigger(buildPipelineTriggerArgument) {
							parameters {
								predefinedProp('RELEASE_TAG', '${RELEASE_TAG}')
							}
						}
					}
					extendedEmail {
						// Sets the default email content that will be used for each email that is sent.
						defaultContent('$DEFAULT_CONTENT')
						// Sets the default email subject that will be used for each email that is sent.
						defaultSubject('$DEFAULT_SUBJECT')
						recipientList('')
						
					}
				}
			}
		}
	}
}

def test = new UtilitiesBackup();
test.setPrefixMethod('bc-');

def builder = new ReleaseBuildVersuch2_.Builder(mavenJob("test"));
builder.setDescription();
Object[][] matrix2
matrix2 = [['TRIGGER_RU1_CI_DEPLOYMENT', true, 'should an RU1-CI Deployment triggered automatically after successful release build?'], ['TRIGGER_RU2_CI_DEPLOYMENT', true, 'should an RU2-CI Deployment triggered automatically after successful release build?']]
builder.setParameters(matrix2);
builder.setConcurrentBuild(false);
builder.setArchivingDisabled(true);
builder.setAuthorization(['hudson.model.Item.Discover':'developer', 'hudson.model.Item.Read':'developer']);
builder.setDeliveryPipelineConfiguration();
builder.setLogRotator(20);
builder.setWrappers();
builder.setTriggers(true, '@midnight')
builder.setScm();

builder.setPreBuildStepsTest('build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.${BUILD_NUMBER}', '-N antrun:run')
//builder.setPreBuildStepsBuildNameUpdater();
builder.setGoals();
builder.setPostBuildStepsMaven('sonar:sonar -B');
builder.setPostBuildStepsConditionalSteps('${TRIGGER_RU1_CI_DEPLOYMENT}', 
	'${TRIGGER_RU2_CI_DEPLOYMENT}', false, 
	UtilitiesBackup.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' + 
	UtilitiesBackup.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-FIT05', 
	UtilitiesBackup.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05,' + 
	UtilitiesBackup.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-FIT05');
builder.setAddPublisher(true, UtilitiesBackup.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' + 
	UtilitiesBackup.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05' //+ ',' +
	);


