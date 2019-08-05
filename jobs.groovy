// main job
job('MNTLAB-abutsko-main-build-job') {
    description('The job triggers the child jobs')

    // Branch name parameter
    parameters {
        // I don't understand we should use choiceParam or stringParam…
        // But I prefer choiceParam because we should have only 2 predefined choices
        // stringParam('BRANCH_NAME', 'abutsko', 'Choose a branch name')
        choiceParam('BRANCH_NAME', ['abutsko', 'master'], 'Choose a branch name')
        
        activeChoiceParam('EXECUTED_JOBS') {
            description('Choose jobs which will be executed')
            filterable()
            choiceType('CHECKBOX')

            groovyScript {
                script('''
                    def branches = "git ls-remote --heads --quiet | awk -F / '{print $NF}'".execute()
                    listOfBranches = branches.split('\n').collect{ it as String }

                    return [listOfBranches]
                ''')

                fallbackScript('"There is no branches"')
            }
        }
    }
}
