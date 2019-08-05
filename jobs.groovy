// main job
job('MNTLAB-apavarnitsyn-main-build-job') {
   	description('The job triggers four child jobs')
    parameters {
        choiceParam('BRANCH_NAME', ['apavarnitsyn (default)', 'master'], 'Choose branch name')
        activeChoiceParam('BUILDS_TRIGGER') {
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
        downstreamParameterized {
            trigger('$BUILDS_TRIGGER') {
                condition('UNSTABLE_OR_BETTER')
                parameters {
                    predefinedBuildParameters {
                        properties('BRANCH_NAME=$BRANCH_NAME')
                        textParamValueOnNewLine(true)

                	}
                }
            }
        }
	}
}
// child job
(1..4).each {
	job("MNTLAB-apavarnitsyn-child${it}-build-job") {
	   	description('child build job')
	    parameters {
	        activeChoiceParam('BRANCH_NAME') {
	            description('Allows user choose from multiple choices')
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


		scm {
		    git('https://github.com/MNT-Lab/d192l-module.git', '$BRANCH_NAME')
		}

		steps {
		    shell {
		    	command('''
chmod +x script.sh  
./script.sh > output.txt 
tar czvf ${BRANCH_NAME}_dsl_script.tar.gz jobs.groovy
				''')
			}
		}
	}
}