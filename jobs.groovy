def student = "ashamchonak"
def mainjob = "MNTLAB-" + student + "-main-build-job"
def childjob = "MNTLAB-" + student + "-child1-build-job"

job(mainjob) {
	description()
	keepDependencies(false)
	parameters {
        activeChoiceParam('CHOICE-1') {
        description('Allows user choose from multiple choices')
        filterable()
        choiceType('SINGLE_SELECT')
        groovyScript {
          	script('["choice1", "choice2"]')
            fallbackScript('"fallback choice"')
            }
		}
		stringParam("BRANCH_NAME", student, "")
		
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
