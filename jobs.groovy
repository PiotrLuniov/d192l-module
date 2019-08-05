job('MNTLAB-pramanouski-main-build-job') {

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
}
