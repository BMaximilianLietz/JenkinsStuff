package jobs

import jobs.Utilities

public class ReleaseBuildJob {

    static void createJob(def dslFactory) {

        def newMavenJob = dslFactory.mavenJob(Utilities.RELEASE_UNIT_NAME + '-releasebuild')

        Utilities.addMavenFeature(newMavenJob)

        addSteps(newMavenJob)

        addPublisher(newMavenJob)

    }

    static void addSteps(def job) {
        job.with {
            description(Utilities.RELEASE_UNIT_NAME + 'release build ')

            parameters {
                booleanParam('TRIGGER_RU1_CI_DEPLOYMENT', true, 'should an RU1-CI Deployment triggered automatically after successful release build?')
                booleanParam('TRIGGER_RU2_CI_DEPLOYMENT', true, 'should an RU2-CI Deployment triggered automatically after successful release build?')
            }


            concurrentBuild(false)
            archivingDisabled(true)

            authorization {
                // Item Discover permissions
                permission('hudson.model.Item.Discover', 'developer')
                permission('hudson.model.Item.Discover', 'tester')
                permission('hudson.model.Item.Discover', 'release-engineer')

                // Item Read permissions
                permission('hudson.model.Item.Read', 'developer')
                permission('hudson.model.Item.Read', 'tester')
                permission('hudson.model.Item.Read', 'release-engineer')

                // Item Build permissions
                permission('hudson.model.Item.Build', 'release-engineer')
            }

            // step, stage
            deliveryPipelineConfiguration('Releasebuild', 'DeadlineManagement')

            logRotator {
                numToKeep(20)
            }

            wrappers {
                preBuildCleanup()
            }

            triggers {
                scm('@midnight')
            }

            scm {
                rtc {
                    buildWorkspace(Utilities.RELEASE_UNIT_REPO_WS_PREFIX + '_Release')
                }
            }
            preBuildSteps {
                //shell("echo 'run before Maven'")
                maven {
                    goals('build-helper:parse-version versions:set -DnewVersion=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion}.${BUILD_NUMBER}')
                    rootPOM(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
                    mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
                    //localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                    //properties(skipTests: true)
                    mavenInstallation('maven339')
                    //providedSettings('central-mirror')
                    providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
                }
                maven {
                    goals('-N antrun:run')
                    rootPOM(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
                    mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
                    //localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                    //properties(skipTests: true)
                    mavenInstallation('maven339')
                    //providedSettings('central-mirror')
                    providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
                }
                environmentVariables {
                    propertiesFile(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/release.properties')
                }
                buildNameUpdater {
                    fromFile(false)
                    buildName('#${ENV,var="RELEASE_TAG"}')
                    fromMacro(true)
                    macroTemplate('#${ENV,var="RELEASE_TAG"}')
                    macroFirst(true)
                }

            }
            goals('clean deploy -B -Ddabag.maven.versionmode=release -Dsonar.skip=true -X')
            postBuildSteps('UNSTABLE') {
                maven {
                    goals('sonar:sonar -B')
                    rootPOM(Utilities.RELEASE_UNIT_PARENT_ARTIFACT_ID + '/pom.xml')
                    mavenOpts('-Dmaven.wagon.http.ssl.insecure=true -Xms256m -Xmx1024m')
                    //localRepository(LocalRepositoryLocation.LOCAL_TO_WORKSPACE)
                    //properties(skipTests: true)
                    mavenInstallation('maven339')
                    //providedSettings('central-mirror')
                    providedGlobalSettings('08542a7b-ab08-4458-a03e-63ea111f60d1')
                }
                conditionalSteps {
                    condition {
                        booleanCondition('${TRIGGER_RU1_CI_DEPLOYMENT}')
                    }
                    //default:fail
                    runner('DontRun')
                    steps {
                        downstreamParameterized {
                            trigger(Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' + 
                            Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-FIT05') {
                                parameters {
                                    predefinedProp('RELEASE_TAG', '${RELEASE_TAG}')
                                    predefinedProp('EXECUTE_SOAPUI_TEST', 'true')
                                }
                            }
                        }
                    }
                }
                conditionalSteps {
                    condition {
                        booleanCondition('${TRIGGER_RU2_CI_DEPLOYMENT}')
                    }
                    //default:fail
                    runner('DontRun')
                    steps {
                        
                        downstreamParameterized {
                            trigger(Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05,' + 
                            Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-FIT05') {
                                parameters {
                                    predefinedProp('RELEASE_TAG', '${RELEASE_TAG}')
                                    predefinedProp('EXECUTE_SOAPUI_TEST', 'true')
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static void addPublisher(def job) {
        job.with {
            publishers {
                buildPipelineTrigger(Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU1-CI05,' +
                        Utilities.RELEASE_UNIT_NAME + '-releasedeployment' + '_RU2-CI05' //+ ',' +
                ) {
                    parameters {
                        predefinedProp('RELEASE_TAG', '${RELEASE_TAG}')
                    }
                }
                extendedEmailPublisher { 
                	project_from('DABAG-Jenkins@atos.net')
                    project_recipient_list('bernhard.hirsch@accenture.com, $DEFAULT_RECIPIENTS')
                    project_default_subject('$DEFAULT_SUBJECT')
                    project_default_content('$DEFAULT_CONTENT')
                    project_content_type('text/plain')
                    project_attachments('')
                    project_presend_script('')
                    project_attach_buildlog(0)
                    project_replyto('$DEFAULT_REPLYTO')
                    project_save_output(false)
                    matrixTriggerMode('ONLY_PARENT')
                    project_disabled(false)
                    project_triggers {
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
                    }
                }
            }
        }
    }

}