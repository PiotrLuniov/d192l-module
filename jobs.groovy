def student = "ashamchonak"
def mainjob = "MNTLAB-" + student + "-main-build-job"
def childjob = "MNTLAB-" + student + "-child1-build-job"

job(mainjob) {
	description()
	keepDependencies(false)
	parameters {
        	//jobs execute
		activeChoiceParam('JOBS') {
	        	description('Choose execute jobs name')
	        	//filterable()
	        	choiceType('CHECKBOX')
	        	groovyScript {
        	  		script('''
                    			def jobs = []
                   			(1..4).each {
                        		jobs.add("MNTLAB-" + student + "-child${it}-build-job")
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
	
	steps {
        	downstreamParameterized {
          		trigger('$JOBS') {
                		block {
                    			buildStepFailure('FAILURE')
                    			failure('FAILURE')
                    			unstable('UNSTABLE')
                		}
                		parameters {
                    			predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
                		}
            		}
            		trigger('Project2') {
                		parameters {
                    			currentBuild()
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

job(childjob) {
	description()
	keepDependencies(false)
	disabled(false)
	concurrentBuild(false)
	steps {
		shell("""git ls-remote --heads  https://github.com/MNT-Lab/d192l-module.git

          ./script.sh > output.txt""")
	}
	publishers {
		archiveArtifacts {
			pattern("output.txt,")
			allowEmpty(false)
			onlyIfSuccessful(false)
			fingerprint(false)
			defaultExcludes(true)
		}
	}
}
