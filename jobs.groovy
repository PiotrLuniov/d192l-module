def student = 'ymlechka'
def mainJob = 'MNTLAB-' + student + '-main-build-job'
//def childJob = 'MNTLAB-' + student + '-child1-build-job'

job(mainJob) {
	description()
	keepDependencies(false)
	parameters {
//execution job
		activeChoiceParam('JOBS') {
			description('Choose execute jobs name')
			choiceType('CHECKBOX')
			groovyScript {
				script('''
def jobs = []
(1..4).each {
  jobs.add("MNTLAB-ymlechka-child${it}-build-job")
}
return jobs
                			'''
				)
				fallbackScript('"fallback choice"')
			}
		}
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
}


for (i in (1..4)) {
	job("MNTLAB-" + student + "-child${i}-build-job") {
		description()
		keepDependencies(false)
		disabled(false)
		concurrentBuild(false)
		parameters {
			activeChoiceParam('BRANCH_NAME') {
				choiceType('SINGLE_SELECT')
				groovyScript {
					script('''
def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
def command = "git ls-remote --heads $gitURL"
def proc = command.execute()
proc.waitFor()
def branches = proc.in.text.readLines().collect{
it.split('/')[-1]
}
return branches
					''')
					fallbackScript('"No branches"')
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
			shell("ls -la")
			shell("chmod +x script.sh")
			shell("echo \$(./script.sh) > output.txt")
			shell("tar czvf \${BRANCH_NAME}_dsl_script.tar.gz jobs.groovy")
		}

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
