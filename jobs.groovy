def student = "ashamchonak"
def mainjob = "MNTLAB-" + student + "-main-build-job"

job(mainjob) {
	description()
	keepDependencies(false)
	parameters {
        	//jobs execute
		activeChoiceParam('JOBS') {
	        	description('Choose execute jobs name')
	        	choiceType('CHECKBOX')
	        	groovyScript {
        	  		script('''
def jobs = []
(1..4).each {
  jobs.add("MNTLAB-ashamchonak-child${it}-build-job")
}
return jobs
                			'''
				)
	            		fallbackScript('"fallback choice"')
        	    	}
		}
		//branch name
		choiceParam("BRANCH_NAME", [student, "master"])
	}
	
	blockOnDownstreamProjects()
	
	steps {
        	downstreamParameterized {
          		trigger("\$JOBS") {
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
	
	
	
	scm {
		git {
			remote {
				github("MNT-Lab/d192l-module", "https")
			}
			branch("*/" + student)
		}
	}
	disabled(false)
	concurrentBuild(false)
	steps {
		shell("git ls-remote --heads  https://github.com/MNT-Lab/d192l-module.git")
        shell("chmod +x script.sh")
        shell("echo \$(./script.sh)")
	}

	
}


for (i in (1..4)) {
	job("MNTLAB-" + student + "-child${i}-build-job") {
		description()
		keepDependencies(false)
		disabled(false)
		concurrentBuild(false)
		steps {
			shell('''
def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
def command = "git ls-remote --heads $gitURL"
def proc = command.execute()
proc.waitFor()

def branches = proc.in.text.readLines().collect{
it.split('/')[-1]
}
return branches
			'''
			)			
		
		}
		// ./script.sh > output.txt
		publishers {
			archiveArtifacts {
				pattern("output.txt")
				allowEmpty(false)
				onlyIfSuccessful(false)
				fingerprint(false)
				defaultExcludes(true)
			}
		}
	}
}






