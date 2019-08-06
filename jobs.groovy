def project = 'MNT-Lab/d192l-module.git'
def student_name = 'uzubtsou'
def jobs_number = 4

job("MNTLAB-${student_name}-main-build-job") {
    blockOnDownstreamProjects()

    parameters {
    choiceParam('BRANCH_NAME', ["${student_name}", 'master'], 'branch name')

    activeChoiceParam('EXECUTE_JOB') {
        description('Checkbox options')
        choiceType('CHECKBOX')
        groovyScript {
            script(
'''def jobs = []
(1..4).each {
jobs.add("MNTLAB-uzubtsou-child${it}-build-job")
}
return jobs'''
            )
        }
    }

    }

    steps {
        downstreamParameterized {
            trigger('${EXECUTE_JOB}') {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                    predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
                }
            }
        }
    }
}


///jobs_number.times {
for (i in 1..4) {

    job("MNTLAB-${student_name}-child${i}-build-job") {
        description('Simple Child Job')
        
        parameters {
            activeChoiceParam('BRANCH_NAME') {
                description('Choose jobs which will be executed')
                choiceType('SINGLE_SELECT')

                groovyScript {
                    script(
'''def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
def command = "git ls-remote -h $gitURL"

def proc = command.execute()
proc.waitFor()

def branches = proc.in.text.readLines().collect{
    it.split('/')[-1]
}
return branches'''
                )
                }
            }
        }

        scm {
            git {
                remote {
                    name('branch')
                    url('https://github.com/MNT-Lab/d192l-module.git')
                }
                branch('$BRANCH_NAME')
            }
        }

        steps {
            shell('sh script.sh > output.txt')
            shell(' if [[ -f "jobs.groovy"  ]];then tar -czf ${BRANCH_NAME}_dsl_script.tar.gz jobs.groovy output.txt; else tar -czf ${BRANCH_NAME}_dsl_script.tar.gz output.txt;fi')
        }

        publishers {
            archiveArtifacts {
                pattern('output.txt')
                pattern('${BRANCH_NAME}_dsl_script.tar.gz')
                onlyIfSuccessful()
            }
        }
    }
}