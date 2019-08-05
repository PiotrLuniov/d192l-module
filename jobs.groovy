4.times {
    job("MNTLAB-mmarkova-child${it+1}-build-job")
}

job("MNTLAB-mmarkova-main-build-job") {
  	parameters {
        activeChoiceParam('BRANCH_NAME') {
            description('Allows user choose from multiple choices')
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('''
					def project = 'MNT-Lab/d192l-module'
					def branchApi = new URL("https://api.github.com/repos/${project}/branches")
					def branches = new groovy.json.JsonSlurper().parse(branchApi.newReader())
					def branchesNames = []
					branches.each {
						branchesNames.add("${it.name}")
					}
					return branchesNames
          		''')
            }
        }

		activeChoiceParam('JOBS') {
			description('Allows user choose from multiple choices')
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
}
