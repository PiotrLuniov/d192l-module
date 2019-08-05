// main job
job('MNTLAB-abutsko-main-build-job') {
    description('The job triggers the child jobs')

    // Branch name parameter
    parameters {
        choiceParam('BRANCH_NAME', ['abutsko (default)', 'master'], 'Choose branch name')
    }
}
