/*

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
		
		String postBuildStepsArgument = 'UNSTABLE'
		
		public Builder(job) {
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
		
		// insert Utilities.RELEASE_UNIT_NAME as second argument
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
						buildWorkspace(Utilities.RELEASE_UNIT_REPO_WS_PREFIX + '_Release')
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
		
		
		void setPreBuildStepsMavenGoals(goalsString) {
			mavenJob.with {
				preBuildSteps {
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
		
		void setGoals(isSnapshot){
			mavenJob.with {
				if (isSnapshot) {
					goals('clean deploy -B -Ddabag.maven.versionmode=snapshot -Dsonar.skip=true -X')
				} else {
					goals('clean deploy -B -Ddabag.maven.versionmode=release -Dsonar.skip=true -X');
				}
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
		
		
		def emails_all = [A400082: 'christian.abesser@atos.net',
		A643553: 'amol.redekar@accenture.com',
		A641662: 'a.anand.devadiga@accenture.com',
		A641682: 's.arvind.sawant@accenture.com',
		A620314: 'pawel.barowski.external@atos.net',
		A402207: 'denise.bermek@atos.net',
		A627267: 'Nadine.Beste@iuk-fz.justiz.bwl.de',
		A627259: 'Heike.Bochnia@it-stelle.justiz.hessen.de',
		A627265: 'andrea.brenne@jm.mv-regierung.de',
		A627264: 'uwe.daenner@olg-koeln.nrw.de',
		A406728: 'martin.duesterhoeft@atos.net',
		A615661: 'remigiusz.dworczak.external@atos.net',
		A407078: 'sabine.eichentopf@atos.net',
		A627261: 'Jacqueline.Enax@jus-it.bayern.de',
		A600756: 'maciej.falkowski@atos.net',
		A625052: 'tobiasz.filusz@atos.net',
		A544846: 'patrick.fink@atos.net',
		A408103: 'pawel.frackiewicz@atos.net',
		A624902: 'roland.freier@atos.net',
		A627268: 'heike.gibson@jus-it.bayern.de',
		A628730: 'piotr.golebiowski@atos.net',
		A624962: 'r.gopalan@accenture.com',
		A639447: 'sebastian.gruchot@atos.net',
		A624968: 'prashant.hariharan@accenture.com',
		A624967: 'sandra.heise@accenture.com',
		A627279: 'hermann@infora.de',
		A620921: 'bernhard.hirsch@accenture.com',
		A627251: 'peter.hirsch@jus-it.bayern.de',
		A627274: 'peter.hock@justiz.niedersachsen.de',
		A624605: 'michael.jacobs@accenture.com',
		A627280: 'jaehrling@infora.de',
		A620923: 'karl.janssen@accenture.com',
		A636436: 'anzelika.jasinska@accenture.com',
		A620410: 'adam.jaszke@atos.net',
		A574642: 'bartosz.jelinski@atos.net',
		A624963: 'ninad.joshi@accenture.com',
		A413428: 'petra.karstensen@atos.net',
		A627286: 'Isabel.Klante@justiz.hamburg.de',
		A624954: 's.klingseisen@accenture.com',
		A597048: 'krzysztof.kowalski@atos.net',
		A530038: 'barbara.kozlowska@atos.net',
		A618332: 'lukasz.kubicki@atos.net',
		A527150: 'keno.kuhlmann@atos.net', 
		A627284: 'ingrid.lamssies@ag-bielefeld.nrw.de',
		A596913: 'anna.litwin-ratajczak@atos.net', 
		A624955: 'sven.loewke@accenture.com',
		A640484: 'pooja.j.mahadik@accenture.com',
		A625008: 'wiktoria.mandok@atos.net',
		A631189: 'andreas.mann@materna.de',
		A641671: 'charvee.maulin.shah@accenture.com',
		A418210: 'johannes.meier@atos.net',
		A418245: 'thomas.meischner@atos.net',
		A568589: 'lukasz.morawiec@atos.net',
		A615660: 'maciej.mrozek.external@atos.net',
		A627295: 'Lutz.Mueller@justiz.niedersachsen.de',
		A641678: 'sadiq.nabi@accenture.com',
		A633437: 'mariya.jagan.v.nadar@accenture.com',
		A632474: 'Thomas.Nagel@materna.de',
		A632458: 'Oliver.Nehring@jus-it.bayern.de',
		A622594: 'iwona.2.niemczyk@atos.net',
		A620917: 'timo.nink@accenture.com',
		A633354: 'parthasarthi.ojha@accenture.com',
		A627371: 'magdalena.panasiewicz@atos.net',
		A631201: 'alkesh.parikh@accenture.com',
		A641702: 'sanjeet.pattnaik@accenture.com',
		A637017: 'przemyslaw.przetocki@atos.net',
		A640965: 'leon.ramzews@accenture.com',
		A633963: 'johannes.rath@accenture.com',
		A638892: 'swapnil.raut@accenture.com',
		A627296: 'Ulrike.Riedel@smj.justiz.sachsen.de',
		A627298: 'joerg.roesicke@jus-it.bayern.de',
		A423676: 'sebastian.roetus@atos.net',
		A627303: 'irene.roller@ko.jm.rlp.de',
		A423651: 'sven.roeminger@atos.net',
		A627289: 'martin.rother@justiz.niedersachsen.de',
		A627276: 'olaf.schmedt@justiz.niedersachsen.de',
		A627301: 'Juergen.Schodt@bitbw.bwl.de',
		A627277: 'Heidelore.Schrank@ITOLG.Brandenburg.de',
		A425625: 'florian.schwanz@atos.net',
		A425810: 'johannes.seib@atos.net',
		A640473: 'jammula.siva.sagar@accenture.com',
		A565353: 'lukasz.skiba@atos.net', 
		A620924: 'christopher.stephan@accenture.com',
		A627270: 'philipp.streblow@jus-it.bayern.de',
		A641681: 'pranay.suresh.dhoke@accenture.com',
		A641674: 'devina.swain@accenture.com',
		A589274: 'tomasz.szypowski@atos.net',
		A624965: 'girish.tandel@accenture.com',
		A627273: 'Rudolf.Themann@ag-neuss.nrw.de',
		A627299: 'tochtrop@infora.de',
		A627304: 'Detlev.Trense@ag-minden.nrw.de',
		A429100: 'thomas.trettin@atos.net',
		A624952: 'sharon.eric.vaz@accenture.com',
		A430602: 'weber.michael@atos.net',
		A627293: 'august.wegmann@web.de',
		A627297: 'joachim.wiegelmann@ag-marsberg.nrw.de',
		A632192: 'dominik.wilczynski.external@atos.net',
		A627287: 'daniel.winter@jus-it.bayern.de',
		A627272: 'meinhard.woehrmann@olg-duesseldorf.nrw.de',
		A627271: 'Matthias.Wolf@jus-it.bayern.de',
		A620884: 'lars.wuehrl@accenture.com',
		A431868: 'zerbes@atos.net',
		A642243: 'beata.gaweda@atos.net',
		A646199: 'shubhangi.kulkarni@accenture.com',
		A646937: 'marita.jerald.monis@accenture.com',
		A646938: 'sonali.tushar.dalvi@accenture.com',
		A649202: 'v.baumgartner@accenture.com',
		A649562: 'sapna.b.singh@accenture.com',
		A649565: 'chandan.h.kumar@accenture.com',
		A649567: 'sweta.raj@accenture.com',
		A650890: 'tomasz.rykala@atos.net',
		A648088: 'krzysztof.ryba@atos.net',
		A601765: 'jacek.iwasieczko@atos.net',
		A635886: 'lukasz.lamek@atos.net',
		A595151: 'krystian.wojtaczka@atos.net',
		A568589: 'lukasz.morawiec@atos.net',
		A655030: 'rachana.shewade@accenture.com',
		A663094: 'sebastian.misiewicz.external@atos.net',
		A661391: 'artur.sobieski.external@atos.net',
		A652758: 'dominik.seichter@accenture.com']
		
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

builder.setPreBuildStepsTest('build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.${BUILD_NUMBER}', '-N antrun:run', true)
//builder.setPreBuildStepsBuildNameUpdater(false);
builder.setGoals(true);
builder.setPostBuildStepsMaven('sonar:sonar -B');
builder.setPostBuildStepsConditionalSteps('${TRIGGER_RU1_CI_DEPLOYMENT}', 
	'${TRIGGER_RU2_CI_DEPLOYMENT}', false, 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' + 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-FIT05', 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05,' + 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-FIT05');
builder.setAddPublisher(true, Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' + 
	Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05', //+ ',' +
	true);


