4.times {
    job("MNTLAB-mmarkova-child${it+1}-build-job")
}

job("MNTLAB-mmarkova-main-build-job") {
  	parameters {
        activeChoiceParam('BRANCH_NAME') {
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('''
def project = 'MNT-Lab/d192l-module'
def branchApi = new URL("https://api.github.com/repos/${project}/branches")
def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
def branchesNames = []
branches.each {
		branchesNames.add(it.name)
}
def index = 0
branchesNames.findIndexOf {
	index = it.equals('mmarkova')
}
branchesNames.swap(0, index)
return branchesNames
          		''')
            }
        }

		activeChoiceParam('BUILDS_TRIGGER') {
			description('Available options')
			choiceType('CHECKBOX')
			groovyScript {
				script('''
def jobs = []
4.times {
    jobs.add("MNTLAB-mmarkova-child${it+1}-build-job")
}
return jobs
				''')
	      	}
   		}
	}

	blockOnDownstreamProjects()

	steps {
        downstreamParameterized {
            trigger('$BUILDS_TRIGGER') {
                block {
                	// Fails the build step if the triggered build is worse or equal to the threshold. 
                    buildStepFailure('FAILURE')

                    // Marks this build as failure if the triggered build is worse or equal to the threshold. 
                    failure('FAILURE')

                    // Mark this build as unstable if the triggered build is worse or equal to the threshold. 
                    unstable('UNSTABLE')
                }
                parameters {
                    predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
                }
            }
        }
    }
}
