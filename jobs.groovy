4.times {
    job("MNTLAB-mmarkova-child${it+1}-build-job")
}

job("MNTLAB-mmarkova-main-build-job") {
  	parameters {
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


// part 2: child jobs execute
1.step 5, 1, {
    job("MNTLAB-mmarkova-child${it}-build-job") {
        
	  	parameters {
	        activeChoiceParam('BRANCH_NAME') {
	            choiceType('SINGLE_SELECT')
	            groovyScript {
	                script('''
	def project = 'MNT-Lab/d192l-module'
	def branchApi = new URL("https://api.github.com/repos/${project}/branches")
	def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
	def branchesNames = []
	def index = 0
	branches.eachWithIndex { elem, ind ->
    	if ("${elem}" == 'mmarkova') 
      		index = ind
		branchesNames.add(${elem})
	}
	branchesNames.swap(0, index)
	return branchesNames
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
	        shell('sh script.sh > output.txt')

	        shell('tar czf $BRANCH_NAME_output.tar.gz jobs.groovy')
	    }

	    publishers {
	        archiveArtifacts {
	            pattern('output.txt')
	            pattern('$BRANCH_NAME_output.tar.gz')
	            onlyIfSuccessful()
	        }
	    }
	}
}