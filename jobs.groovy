def STUDENT_NAME = "adalimayeu"
def main_job = "MNTLAB-${STUDENT_NAME}-main-build-job"
def child_jobs = []
for (i in 1..2){
    child_jobs.add("MNTLAB-${STUDENT_NAME}-child${i}-build-job")
}
def child_jobs_for_ACplug = child_jobs.collect {it -> return "\'$it\'"}

freeStyleJob('MNTLAB-adalimayeu-main-build-job'){
    parameters {
        activeChoiceParam('BRANCH_NAME') {
            description('Choose branch name')
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('return [\'adalimayeu\', \'master\']')
                fallbackScript('"fallback choice"')
            }
        }
        activeChoiceParam('JOBS') {
            description('Choose job which will be executed')
            choiceType('CHECKBOX')
            groovyScript {
                script("return ${child_jobs_for_ACplug}")
                fallbackScript('"fallback choice"')
            }
        }
    }
    steps {
        triggerBuilder {
            configs {
                blockableBuildTriggerConfig {
                    projects("\$JOBS")

                    configs {
                        predefinedBuildParameters {
                            properties("\$BRANCH_NAME")
                            textParamValueOnNewLine(false)
                        }
                    }
                    block{
                        buildStepFailureThreshold("FAILURE")
                        unstableThreshold("")
                        failureThreshold("")
                    }
                }
            }

        }
    }
}


def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
def command = "git ls-remote -h ${gitURL}"

def proc = command.execute()
proc.waitFor()
def branches = proc.in.text.readLines().collect {
    it.replaceAll(/[a-z0-9]*\trefs\/heads\//, '')
}
def branches_for_ACplug = branches.collect {it -> return "\'$it\'"}

println(branches)
for (job in child_jobs) {
    freeStyleJob("${job}"){
        parameters {
            activeChoiceParam('BRANCH_NAME') {
                description('Choose branch name')
                choiceType('SINGLE_SELECT')
                groovyScript {
                    script("return ${branches_for_ACplug}")
                    fallbackScript('"fallback choice"')
                }
            }
        }
        gitSCM {
            userRemoteConfigs {
                userRemoteConfig {
                    url(gitURL)
                }
            }
            branches {
                branchSpec {
                    name("\$BRANCH_NAME")
                }
            }
        }
        steps {
            shell('$JENKINS_HOME/workspace/a/script.sh > output.txt')
        }
    }
}