package jobs

import jobs.ReleaseBuildVersuch2_.Utilities

public class ReleaseBuildVersuch2_ {
	
	public static class Utilities {
		
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
		
		String STABLE_STREAMPROMOTION;
		
		public Builder(job) {
			this.mavenJob = job;
		}
		
		
		void setDescription(descriptionArgument) {
			this.mavenJob.with {
				description(descriptionArgument)
				// was previously hard coded in some cases
				//description(Utilities.RELEASE_UNIT_NAME + 'release build ')
				/*
				if (hasUnitIterationBool) {
					description(Utilities.RELEASE_UNIT_NAME + 'release build ' + Utilities.RELEASE_UNIT_ITERATION)
				} else {
					description(Utilities.RELEASE_UNIT_NAME + 'release build ')
				}
				*/
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
		
		void setArchivingDisabled(archivingDisabledBool) {
			this.mavenJob.with {
				archivingDisabled(archivingDisabledBool)
			}
		}
		
		// dabag snapshot deployment feature
		void setDisableDownstreamTrigger(disableDownstreamTriggerBool) {
			this.mavenJob.with {
				disableDownstreamTrigger(disableDownstreamTriggerBool)
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
		
		// insert Utilities.RELEASE_UNIT_NAME as second argument
		void setDeliveryPipelineConfiguration(stringOne, stringTwo){
			mavenJob.with {
				deliveryPipelineConfiguration(stringOne, stringTwo)
			}
		}
		
		void setLogRotator(numToKeepInt, artifactDaysToKeepInt) {
			this.mavenJob.with {
				logRotator {
					numToKeep(numToKeepInt)
					// dabag Deployment feature
					if (artifactDaysToKeepInt != null) {
						artifactDaysToKeep(artifactDaysToKeepInt)
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
		
		// dabag snapshot deployment feature
		void setProperties() {
			this.mavenJob.with {
				properties {
					envInjectJobProperty {
						info {
								// Gives a file path of a properties file.
								propertiesFilePath('')
								// Give a set of key/value (one variable per line): KEY=VALUE.
								propertiesContent('REPO_NAME='+ Utilities.RELEASE_UNIT_REPO_WS_PREFIX +'_Snapshot')
								// Execute a script file aimed at setting an environment such a create folders, copying files, and so on.
								scriptFilePath('')
								// Execute a script file aimed at setting an environment such as creating folders, copying files, and so on.
								scriptContent('')
								// If enabled, load files (properties or scripts) from the master node.
								loadFilesFromMaster(false)
								// Evaluates a Groovy script and injects the results into the environment.
								secureGroovyScript {
									script('')
									// If checked, run this Groovy script in a sandbox with limited abilities.
									sandbox(false)
									// Additional classpath entries accessible from the script.
									//classpath {
									//}
								}
							}
							//contributors {}
							// Inject Jenkins build variables such as EXECUTOR_NUMBER, BUILD_ID, BUILD_TAG, JOB_NAME and so on.
							keepBuildVariables(true)
							// Inject Jenkins system variables such as JENKINS_HOME, JENKINS_URL, NODE_NAME and so on.
							keepJenkinsSystemVariables(true)
							on(true)
							overrideBuildParameters(false)
					}
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
		
		void setScm(isSnapshot) {
			mavenJob.with {
				scm {
					rtc {
						if (isSnapshot) {
							buildWorkspace(Utilities.RELEASE_UNIT_REPO_WS_PREFIX + '_Snapshot')
						} else {
							buildWorkspace(Utilities.RELEASE_UNIT_REPO_WS_PREFIX + '_Release')
						}
					}
				}
			}
		}
		
		// dabag ReleaseBuild feature
		// throws cannot read write-only property: scm at some point
		void setConfigure() {
			this.mavenJob.with {
				configure { project ->
					project.remove(project / scm) // remove the existing 'scm' element
					project / scm(class: 'com.ibm.team.build.internal.hjplugin.RTCScm') {
						overrideGlobal false
						timeout 0
						buildType 'buildStream'
						buildTypeStr 'buildStream'
						buildStream 'justiz-dabag-iteration-4-stable'
						loadDirectory ''
						clearLoadDirectory false
						createFoldersForComponents false
						acceptBeforeLoad true
						generateChangelogWithGoodBuild false
						avoidUsingToolkit false
						processArea 'DE_MCH_SOL_JUSTIZ_DABAG_CCM2/Infrastructure'
						overrideDefaultSnapshotName false
						customizedSnapshotName ''
						loadPolicy 'useComponentLoadConfig'
						componentLoadConfig 'loadAllComponents'
						useDynamicLoadRules false
					}
				}
			}
		}
		
		// dabag snapshot deployment & ManageVersionJob feature
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
		
		// method should replace goals; equivalent to goals('build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.${BUILD_NUMBER}')
		
		void setPreBuildStepsTest(goalsString, goalsStringTwo, isSnapshot) {
			this.mavenJob.with {
				preBuildSteps {
					if (isSnapshot) {
						shell("echo 'run before Maven'")
					}
					maven {
						goals(goalsString)
						rootPOM(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
						mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
						//localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
						//properties(skipTests: true)
						mavenInstallation('maven339')
						//providedSettings('central-mirror')
						providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
					}
					if (goalsStringTwo != null ) {
						maven {
							goals(goalsStringTwo)
							rootPOM(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
							mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
							//localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
							//properties(skipTests: true)
							mavenInstallation('maven339')
							//providedSettings('central-mirror')
							providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
						}
					}
					environmentVariables {
						propertiesFile(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/release.properties')
					}
					
					wrappers {
						buildName('#${ENV,var="RELEASE_TAG"}')
					}
				} 
			}
		}
		
		void PreBuildStepsAddConditionalSteps() {
			this.mavenJob.with {
				preBuildSteps {
					conditionalSteps {
						condition {
							booleanCondition('${PARALLEL}')
						}
						//default:fail
						runner('DontRun')
						steps {
							environmentVariables {
								env('PARALLEL_PARAM', '-T 8')
							}
						}
					}
					
					conditionalSteps {
						condition {
							not {
								booleanCondition('${PARALLEL}')
							}
						}
						//default:fail
						runner('DontRun')
						steps {
							environmentVariables {
								env('PARALLEL_PARAM', '')
							   
							}
						}
					}
				}
			}
		} 		
		
		void setPreBuildStepsBuildNameUpdater(isSnapshot) {
			mavenJob.with {
				if (isSnapshot) {
					preBuildSteps {
						buildNameUpdater {
							fromFile(false)
							  buildName('#${ENV,var="RELEASE_TAG"} (${BUILD_NUMBER})')
							  fromMacro(true)
							  macroTemplate('#${ENV,var="RELEASE_TAG"} (${BUILD_NUMBER})')
							  macroFirst(true)
						}
					}
				} else {
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
		
		/*
		void setGoals(isSnapshot){
			mavenJob.with {
				if (isSnapshot) {
					goals('clean deploy -B -Ddabag.maven.versionmode=snapshot -Dsonar.skip=true -X')
				} else {
					goals('clean deploy -B -Ddabag.maven.versionmode=release -Dsonar.skip=true -X');
				}
			}
		}
		*/
		void setGoals(goalsString){
			mavenJob.with {
				goals(goalsString);
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
						rootPOM(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
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
		
		// dabag releasebuild feature
		void addPostBuildStepsTopShellArgument(topShellArgument) {
			this.mavenJob.with {
				postBuildSteps {
					shell(topShellArgument)
				}
			}
		}
		
		// dabag releasebuild feature
		void addPostBuildStepsTopDownstreamParameterized() {
			this.mavenJob.with {
				postBuildSteps {
					downstreamParameterized {
						trigger(Utilities.RELEASE_UNIT_NAME + '-sonarchecks-' + Utilities.RELEASE_UNIT_ITERATION) {
							parameters {
								predefinedProp('RELEASE_TAG', '${RELEASE_TAG}')
								predefinedProp('SNAPSHOT_NAME', '${JOB_NAME}_#${BUILD_NUMBER}')
							}
						}
					}
				}
			}
		}
		
		void addPostBuildStepsConditionalSteps(booleanConditionArgument, downStreamTrigger, key, value) {
			this.mavenJob.with {
				postBuildSteps {
					conditionalSteps {
						condition {
							booleanCondition(booleanConditionArgument)
						}
						//default:fail
						runner('DontRun')
						downstreamParameterized {
							trigger(downStreamTrigger) {
								parameters {
									predefinedProp(key, value)
								}
							}
						}
					}
				}
			}
		}

		void addPostBuildStepsShellCommandInSteps() {
			this.mavenJob.with {
				postBuildSteps {
					conditionalSteps {
						condition {
							booleanCondition(STABLE_STREAMPROMOTION)
						}
						//default:fail
						runner('DontRun')
						steps {
							shell(this.STABLE_STREAMPROMOTION);
						}
					}
				}
			}
		}
		
		void setSTABLE_STREAMPROMOTION() {
			this.STABLE_STREAMPROMOTION= '''#exit 0
SNAPSHOT_REP="https://rtc5a.gsissc.myatos.net/rtc5a/"

#export SCM_ALLOW_INSECURE=1

if ! lscm list connections | grep "${SNAPSHOT_REP}" | grep "Password stored" 
then lscm login -r ${SNAPSHOT_REP} -n jenkins -u ${RTC_USER} -P ${RTC_PWD} 
fi

lscm show status
# exit 0

# add flow target stable stream, change current / default
lscm list flowtargets "${REPO_NAME}" -r jenkins
lscm add flowtarget "${REPO_NAME}" "justiz-dabag-iteration-4-stable" -r jenkins
lscm set flowtarget "${REPO_NAME}" "justiz-dabag-iteration-4-stable" --default --current
lscm list flowtargets "${REPO_NAME}" -r jenkins

lscm show status

lscm deliver -i -v -r jenkins || echo "in case of error we continue .."
#-t justiz-dabag-iteration-4-stable 

# change current / default, remove flow target stable stream
lscm set flowtarget "${REPO_NAME}" "justiz-dabag-iteration-4" --default --current
lscm remove flowtarget "${REPO_NAME}" "justiz-dabag-iteration-4-stable" -r jenkins
lscm list flowtargets "${REPO_NAME}" -r jenkins

# lscm logout -r jenkins
				''';
		}
		
		void setAddPublisher(buildPipelineTriggerExists, buildPipelineTriggerArgument, isSnapshot) {
			this.mavenJob.with {
				if (isSnapshot) {
					publishers {
						// change to extendedEmailPublisher
						extendedEmail {
							
							recipientList('cc:sebastian.roetus@atos.net, cc:bernhard.hirsch@accenture.com, cc:ninad.joshi@accenture.com, cc:dominik.seichter@accenture.com')
							defaultSubject('Urgent Fix is needed! ' + Utilities.RELEASE_UNIT_NAME + ' Snapshot Build is broken!')
							defaultContent('''Hi,
		
		your last delivery seems to caused the Snapshot Build to fail.
		Please investigate and fix the problem with highest priority!
		
		More information you can find below and at Jenkins.
		
		$DEFAULT_CONTENT
		
		Changes since last Success:
		${CHANGES_SINCE_LAST_SUCCESS}
		''')
							contentType('text/plain')
							  //project_attachments('')
							  preSendScript('''import hudson.model.*
		import javax.mail.Message
		import javax.mail.internet.InternetAddress
		
		
		// static developers list
		//bernhard.hirsch@accenture.com, $DEFAULT_RECIPIENTS, ninad.joshi@accenture.com, mariya.jagan.v.nadar@accenture.com, prashant.hariharan@accenture.com, sharon.eric.vaz@accenture.com, s.arvind.sawant@accenture.com, pranay.suresh.dhoke@accenture.com, sanjeet.pattnaik@accenture.com, amol.redekar@accenture.com, jammula.siva.sagar@accenture.com, sadiq.nabi@accenture.com, devina.swain@accenture.com, a.anand.devadiga@accenture.com, pawel.barowski.external@atos.net, maciej.mrozek.external@atos.net, dominik.wilczynski.external@atos.net, johannes.seib@atos.net, sebastian.gruchot@atos.net, wiktoria.mandok@atos.net, barbara.kozlowska@atos.net, pawel.frackiewicz@atos.net, remigiusz.dworczak.external@atos.net, przemyslaw.przetocki@atos.net, johannes.meier@atos.net, anna.litwin-ratajczak@atos.net, maciej.falkowski@atos.net, tomasz.szypowski@atos.net, piotr.golebiowski@atos.net
		
		
		def emails_all = [maximilian.lietz.ml@gmail.com]
		
		//logger.println("${emails_all}")
		
		def culprits = build.getCulprits()
		
		culprits.each {
                    culpritIterator ->
		    //das_id = culpritIterator
		    //logger.println("culprit das id: ${culpritIterator}")
		    email_address = emails_all[culpritIterator.toString()]
		    //logger.println("culprit email address: ${email_address}")
		    //logger.println("email: " + email_address);
		    //logger.println("culprit email address: " + emails_all[culpritIterator.toString()])
		    if (email_address?.trim()) {
		        logger.println("add email address: " + email_address)
		        msg.addRecipient(Message.RecipientType.TO, new InternetAddress(email_address))
		    } else {
		        logger.println("WARNING! The following DAS ID ist not in the List: " + culpritIterator.toString())
		    }
		}
		
		//if (build.result.toString().equals("FAILURE")) { 
		    msg.addHeader("X-Priority", "1 (Highest)")
		    msg.addHeader("Importance", "High")
		//}''') 
							  // boolean needs to be changed at some point 
							  attachBuildLog(false)
							  //project_replyto('$DEFAULT_REPLYTO')
							  //project_save_output(false)
							  //matrixTriggerMode('ONLY_PARENT')
							  
							  // add project prefix to both
							  disabled(false)
							triggers {
								/*
								notBuiltTrigger {
									  subject('$PROJECT_DEFAULT_SUBJECT')
									  body('$PROJECT_DEFAULT_CONTENT')
									  replyTo('$PROJECT_DEFAULT_REPLYTO')
									  contentType('text/plain')
									  attachmentsPattern('')
									  attachBuildLog(0)
									  recipientList('')
									recipientProviders {
										listRecipientProvider()
									}
								}
								  scriptTrigger {
									  secureTriggerScript {
										script('''build.result.toString().equals('FAILURE') || build.result.toString().equals('UNSTABLE')''')
								// If checked, run this Groovy script in a sandbox with limited abilities.
								sandbox(false)
								// Additional classpath entries accessible from the script.
								//classpath {}
							}
		                    subject('$PROJECT_DEFAULT_SUBJECT')
		                  	body('$PROJECT_DEFAULT_CONTENT')
		                  	replyTo('$PROJECT_DEFAULT_REPLYTO')
		                  	contentType('text/plain')
		                  	attachmentsPattern('')
		                  	attachBuildLog(0)
		                  	recipientList('')
		                  	recipientProviders {
		                        listRecipientProvider()
		                    }
		                }
						*/
		            }
		        }
		    
					}
				} else {
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
}

def test = new Utilities();
test.setPrefixMethod('bc-');

def builder = new ReleaseBuildVersuch2_.Builder(mavenJob("test"));
builder.setDescription(Utilities.RELEASE_UNIT_NAME + 'release build ' + Utilities.RELEASE_UNIT_ITERATION);
Object[][] matrix2
matrix2 = [['TRIGGER_RU1_CI_DEPLOYMENT', true, 'should an RU1-CI Deployment triggered automatically after successful release build?'], ['TRIGGER_RU2_CI_DEPLOYMENT', true, 'should an RU2-CI Deployment triggered automatically after successful release build?']]
builder.setParameters(matrix2);
builder.setConcurrentBuild(false);
builder.setArchivingDisabled(true);
builder.setDisableDownstreamTrigger(true);
builder.setAuthorization(['hudson.model.Item.Discover':'developer', 'hudson.model.Item.Read':'developer']);
builder.setDeliveryPipelineConfiguration("Test", "String");
builder.setLogRotator(20, null);
builder.setWrappersPreBuildCleanUp();
builder.setTriggers(true, '@midnight')
builder.setScm(true);
//builder.setConfigure();

builder.setPreBuildStepsTest('build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.${BUILD_NUMBER}', '-N antrun:run', true)
//builder.setPreBuildStepsBuildNameUpdater(false);
builder.PreBuildStepsAddConditionalSteps();
builder.setGoals('clean deploy -B -Ddabag.maven.versionmode=snapshot -Dsonar.skip=true -X');
builder.setPostBuildStepsMaven('sonar:sonar -B');

builder.addPostBuildStepsTopShellArgument('''
rm -rf ${JENKINS_HOME}/workspace/'''+Utilities.RELEASE_UNIT_NAME+'-sonarchecks-'+Utilities.RELEASE_UNIT_ITERATION+'''/*
cp -r ${JENKINS_HOME}/workspace/'''+Utilities.RELEASE_UNIT_NAME+'-releasebuild-'+Utilities.RELEASE_UNIT_ITERATION+'''/* $JENKINS_HOME/workspace/'''+Utilities.RELEASE_UNIT_NAME+'-sonarchecks-'+Utilities.RELEASE_UNIT_ITERATION+'''
              	''');
				  
builder.addPostBuildStepsTopDownstreamParameterized();
builder.addPostBuildStepsConditionalSteps('${TRIGGER_RU1_CI_DEPLOYMENT}', Utilities.RELEASE_UNIT_NAME + '-releasedeployment-' + Utilities.RELEASE_UNIT_ITERATION + '_RU1-CI05',
	'RELEASE_TAG', '${RELEASE_TAG}');
builder.addPostBuildStepsConditionalSteps('${TRIGGER_RU1_CI_DEPLOYMENT}', Utilities.RELEASE_UNIT_NAME + '-releasedeployment-' + Utilities.RELEASE_UNIT_ITERATION + '_RU1-CI05',
	'EXECUTE_SOAPUI_TEST', 'true');

builder.setSTABLE_STREAMPROMOTION();

builder.addPostBuildStepsShellCommandInSteps();


/* builder.setPostBuildStepsConditionalSteps('${TRIGGER_RU1_CI_DEPLOYMENT}', 
	'${TRIGGER_RU2_CI_DEPLOYMENT}', false, 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' + 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-FIT05', 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05,' + 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-FIT05',
	false, null, false);


builder.setAddPublisher(true, Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' + 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05', //+ ',' +
	true, );
*/

