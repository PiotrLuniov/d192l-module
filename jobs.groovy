job('MNTLAB-pramanouski-main-build-job') {
  blockOnDownstreamProjects()

  parameters {
      choiceParam('BRANCH_NAME', ['pramanouski (default)', 'master'])

      activeChoiceParam('BUILDS_TRIGGER') {
            description('Available options')
            choiceType('CHECKBOX')
            groovyScript {
                script('''
             def list=[]
	         (1..4).each {
	         list.add("MNTLAB-pramanouski-child${it}-build-job")
       }
	      return list
          ''')
        }

    }
  }

  steps {
        downstreamParameterized {
            trigger('$BUILDS_TRIGGER') {
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

  or(i in 1..4) {
    job("MNTLAB-pramanouski-child${i}-build-job") {
      parameters {
        activeChoiceParam('BRANCH_NAME') {
              description('List of branches')
              choiceType('SINGLE_SELECT')

              groovyScript {
                  script('''
               def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
               def command = "git ls-remote -h $gitURL"
               def proc = command.execute()
               proc.waitFor()
               def branches = proc.in.text.readLines().collect {
                   it.replaceAll(/[a-z0-9]*\trefs\\/heads\\//, '')
               }
               return branches
              ''')
              }
         }
      }

  scm {
    git {
      remote {
        url('https://github.com/MNT-Lab/d192l-module.git')
      }
      branch("\${BRANCH_NAME}")
    }

    }
    steps {
    shell ('chmod +x script.sh  &&  ./script.sh > output.txt && tar czvf ${BRANCH_NAME}_dsl_script.tar.gz output.txt jobs.groovy')
    }

    publishers {
      archiveArtifacts {
      pattern('${BRANCH_NAME}_dsl_script.tar.gz')
      onlyIfSuccessful()
      }
    }
  }
}
