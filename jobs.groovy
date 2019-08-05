// main job
job('MNTLAB-apavarnitsyn-main-build-job') {

   	description('The job triggers four child jobs')

    // Branch name parameter
    parameters {
        choiceParam('BRANCH_NAME', ['apavarnitsyn (default)', 'master'], 'Choose branch name')

        activeChoiceParam('CHOICE-1') {
            description('Allows user choose from multiple choices')
            choiceType('CHECKBOX')
            groovyScript {
                script('''
		        def list=[]
		        (1..4).each {
		        list.add("MNTLAB-apavarnitsyn-child${it}-build-job")
		        }
		        return list
                ''')
                fallbackScript('"fallback choice"')
            }
        }
    }

    publishers {
        buildPipelineTrigger('deploy-cluster-1, deploy-cluster-2') {
            parameters {
                predefinedProp('BRANCH_NAME', '$BRANCH_NAME')
            }
        }
    }

}
(1..4).each {
job("MNTLAB-apavarnitsyn-child${it}-build-job") {

   	description('child build job')

    // Branch name parameter
    parameters {
        activeChoiceParam('BRANCH_NAME') {
            description('Allows user choose from multiple choices')
            filterable()
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('''
        def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
        def command = "git ls-remote -h $gitURL"
        def proc = command.execute()
        def branches = proc.in.text.readLines().collect { 
        it.replaceAll(/[a-z0-9]*\\trefs\\/heads\\//, "") 
        }
        return branches
                ''')
                fallbackScript('"fallback choice"')
            }
        }
    }
}

}