
job("topic10") {
	description()
	keepDependencies(false)
	scm {
		git {
			remote {
				github("MNT-Lab/d192l-module", "https")
			}
			branch("*/ashamchonak")
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

