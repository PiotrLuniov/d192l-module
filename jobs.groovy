// main job
job('MNTLAB-abutsko-main-build-job') {
    description('The job triggers the child jobs')

    parameters {
        // I don't understand we should use choiceParam or stringParam…
        // But I prefer choiceParam because we should have only 2 predefined choices
        // stringParam('BRANCH_NAME', 'abutsko', 'Choose a branch name')
        choiceParam('BRANCH_NAME', ['abutsko', 'master'], 'Choose a branch name')
        activeChoiceParam('EXECUTED_JOBS') {
            description('Choose jobs which will be executed')
            choiceType('CHECKBOX')
            groovyScript {
                script('''
                    def jobs = []
                    (1..4).each {
                        jobs.add("MNTLAB-abutsko-child${it}-build-job")
                    }
                    return jobs
                ''')
            }
        }
    }

    blockOnDownstreamProjects()

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
        downstreamParameterized {
            for ( i in (1..4) ) {
                trigger("MNTLAB-abutsko-child${i}-build-job") {
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
}

for ( i in (1..4) ) {
    job("MNTLAB-abutsko-child${i}-build-job") {
        description('The simplest child job')
        
        parameters {
            activeChoiceParam('BRANCH_NAME') {
                description('Choose jobs which will be executed')
                choiceType('SINGLE_SELECT')

                groovyScript {
                    script('''
                        def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
                        def command = "git ls-remote -h $gitURL"
                        def proc = command.execute()
                        proc.waitFor()

                        def branches = proc.in.text.readLines().collect{
                            it.split('/')[-1]
                        }

                        return branches
                    ''')

                    fallbackScript('"There is no branches"')
                }
            }
        }
    }
}
