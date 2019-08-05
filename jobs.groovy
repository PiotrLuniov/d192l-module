def student = "ashamchonak"

job("MNTLAB-{{ student }}-main-build-job") {
	description()
	keepDependencies(false)
	parameters {
		stringParam("BRANCH_NAME", "{student}", "")
	}
	scm {
		git {
			remote {
				github("MNT-Lab/d192l-module", "https")
			}
			branch("*/{student}")
		}
	}
	disabled(false)
	concurrentBuild(false)
	steps {
		shell("git ls-remote --heads  https://github.com/MNT-Lab/d192l-module.git")
        shell("chmod +x script.sh")
        shell("./script.sh")
	}
}

job("MNTLAB-{student}-child1-build-job") {
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
