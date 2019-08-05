// main job
job('MNTLAB-abutsko-main-build-job') {
    description('The job triggers the child jobs')

    // Branch name parameter
    parameters {
        stringParam('BRANCH_NAME', 'abutsko', 'Choose a branch name')
    }
}
