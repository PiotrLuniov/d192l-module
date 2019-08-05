job("MNTLAB-sbarysevich-master") {
 parameters {
   choiceParam('BRANCH_NAME', ['sbarysevich', 'master',])
   activeChoiceParam('slaves') {
     choiceType('CHECKBOX')
     description('slaves')
     groovyScript {
          script('''def slave = []
                 (1..4).each {
                     slave.add("MNTLAB-sbarysevich-child"+it as String)
                 }
              return slave
           ''')
           fallbackScript("error")
      }
  } 
 }
  blockOnDownstreamProjects()
   steps {
        downstreamParameterized {
            trigger('$slaves') {
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

(1..4).each{
      job("MNTLAB-sbarysevich-child"+it as String) {
      parameters {
        activeChoiceParam('BRANCH_NAME') {
          description('Branches')
          choiceType('SINGLE_SELECT')
          groovyScript {
             script('''
                    def gitURL = "https://github.com/MNT-Lab/d192l-module.git"
                    def command = "git ls-remote -h $gitURL"
                
                    def proc = command.execute()
                    proc.waitFor()
                
                    if ( proc.exitValue() != 0 ) {
                       println "Error, ${proc.err.text}"
                       System.exit(-1)
                    }
                
                    def branches = proc.in.text.readLines().collect {
                        it.replaceAll(/[a-z0-9]*\trefs\\/heads\\//, '')
                    }
                    return branches
                  ''')
             fallbackScript('"any branche not found"')
          }
        }
      }
        scm {
          git {
            remote {
              url('https://github.com/MNT-Lab/d192l-module.git')
            }
            branch('*/$')
          }
        }
        
        triggers {
          scm('*/15 * * * *')
        }
        steps {
          shell('sh $WORKSPACE/script.sh > output.txt && tar -cf $BRANCH_NAME_dsl_script.tar.gz jobs.groovy')
        }
     }
}













