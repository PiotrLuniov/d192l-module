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
  }
}

