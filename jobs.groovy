def project = 'MNT-Lab/d192l-module'
def privateToken = '775768f59bed5fca4f1c5687b29ba8f8fab72315'
def branchApi = new URL("https://api.github.com/repos/${project}/branches?private_token=${privateToken}")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
def branchesNames = []
branches.each {
	branchesNames.add("${it.name}")
}

4.times {
	job("MNTLAB-mmarkova-child${it+1}-build-job")
}

job("MNTLAB-mmarkova-main-build-job") {
	properties {
		parameters {
		choiceParam('BRANCH_NAME', branchesNames, 'description')
		}
	}
}