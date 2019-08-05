job('MNTLAB-iyaruk-main-build-job') {
    description('Main job')

    parameters {
        choiceParam('BRANCH_NAME', ['iyaruk', 'master'])
        activeChoiceParam('EXECUTED_JOBS') {
            description('Main job for working with childs jobs')
            choiceType('CHECKBOX')
            groovyScript {
                script('''
                    def list = []
                    (1..4).each {
                        list.add("MNTLAB-iyaruk-child${it}-build-job")
                    }
                    return list
                ''')
            }
        }
    }

    blockOnDownstreamProjects()

    steps {
        downstreamParameterized {
            trigger('$EXECUTED_JOBS') {
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

for ( i in (1..4) ) {
    job("MNTLAB-iyaruk-child${i}-build-job") {
        description('Child job')

        parameters {
            activeChoiceParam('BRANCH_NAME') {
                description('Click for choiceing job')
                choiceType('SINGLE_SELECT')

                groovyScript {
                    script('''
                        def Git = "https://github.com/MNT-Lab/d192l-module.git"
                        def task = "git ls-remote -h $Git"
                        def brot = task.execute()
                        brot.waitFor()
                        def trees = brot.in.text.readLines().collect{
                            it.split('/')[-1]
                        }
                        return trees
                    ''')
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
            shell('''
            bash script.sh > output.txt
            tar czf ${BRANCH_NAME}_dsl_script.tar.gz jobs.groovy
            ''')
        }

        publishers {
            archiveArtifacts {
                pattern('output.txt')
                pattern('${BRANCH_NAME}_dsl_script.tar.gz')
                onlyIfSuccessful()
            }
        }
    }
