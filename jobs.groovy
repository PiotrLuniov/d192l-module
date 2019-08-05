job('MNTLAB-iyaruk-main-build-job') {

  parameters {
      choiceParam('BRANCH_NAME', ['iyaruk', 'master'])

      activeChoiceParam('BUILDS_JOBS') {
            description('Job for works')
            choiceType('CHECKBOX')
            
            groovyScript {
                script('''
                 def child=[]
	         (1..4).each {
	         list.add("MNTLAB-iyaruk-child${it}-build-job")
       }
	      return child
            ''')

        }
    }
	scm {
            git {
                remote {
                    name('branch')
                    url('https://github.com/MNT-Lab/d192l-module.git')
                }
                branch("\$BRANCH_NAME")
            }
        }
      triggers {
          scm('H/15 * * * *')
      }
      steps {
      	  shell('bash ./script.sh')
      }
  }
}

