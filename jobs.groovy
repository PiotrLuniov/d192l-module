def STUDENT_NAME = "adalimayeu"

def main_job = "MNTLAB-${STUDENT_NAME}-main-build-job"
def child_jobs = []
for (i in 1..4){
    child_jobs.add("MNTLAB-${STUDENT_NAME}-child${i}-build-job")
}
// add quotes for jenkins parser
def child_jobs_formatted = child_jobs.collect {it -> return "\'$it\'"}

freeStyleJob("${main_job}"){
    parameters {
//        activeChoiceParam('BRANCH_NAME') {
//            description('Choose branch name')
//            choiceType('SINGLE_SELECT')
//            groovyScript {
//                script('return [\'adalimayeu\', \'master\']')
//                fallbackScript('"fallback choice"')
//            }
//        }
        choiceParam('BRANCH_NAME', ['adalimayeu', 'master'], 'Choose branch name')
        activeChoiceParam('JOBS') {
            description('Choose job which will be executed')
            choiceType('CHECKBOX')
            groovyScript {
                script("return ${child_jobs_formatted}")
                fallbackScript('"fallback choice"')
            }
        }
    }
    blockOnDownstreamProjects()
    publishers {
        downstreamParameterized {
            trigger('$JOBS') {
                parameters {
                    predefinedBuildParameters {
                        properties('BRANCH_NAME=$BRANCH_NAME')
                        textParamValueOnNewLine(true)

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
// add quotes for jenkins parser
def branches_formatted = branches.collect {it -> return "\'$it\'"}

//println(branches)
for (job in child_jobs) {
    freeStyleJob("${job}"){
        parameters {
            activeChoiceParam('BRANCH_NAME') {
                description('Choose branch name')
                choiceType('SINGLE_SELECT')
                groovyScript {
                    script("return ${branches_formatted}")
                    fallbackScript('"fallback choice"')
                }
            }
        }
        scm {
            github('MNT-Lab/d192l-module', '$BRANCH_NAME')
        }
        steps {
            shell('chmod +x script.sh; ' +
                    './script.sh > output.txt; ' +
                    'tar czf ${BRANCH_NAME}_dsl_script.tar.gz jobs.groovy output.txt')
        }
        publishers {
            archiveArtifacts {
                pattern('${BRANCH_NAME}_dsl_script.tar.gz')
                onlyIfSuccessful()
            }
        }
    }
}