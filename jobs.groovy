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
                    return [
                        "MNTLAB-abutsko-child1-build-job",
                        "MNTLAB-abutsko-child2-build-job",
                        "MNTLAB-abutsko-child3-build-job",
                        "MNTLAB-abutsko-child4-build-job"
                    ]
                ''')
            }
        }
    }
}

job('MNTLAB-abutsko-child1-build-job') {
    description('The simplest child job')
    
    parameters {
        activeChoiceParam('BRANCH_NAME') {
            description('Choose jobs which will be executed')
            choiceType('SINGLE_SELECT')

            groovyScript {
                script('''
                    def branches = "git ls-remote --heads --quiet | awk -F / '{print $NF}'".execute()
                    def listOfBranches = branches.text.split('\n').collect{ it as String }

                    return listOfBranches
                ''')

                fallbackScript('"There is no branches"')
            }
        }
    }
}
