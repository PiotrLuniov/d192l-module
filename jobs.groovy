job('MNTLAB-kkaminski-main-build-job') {
    
  parameters {
           
      choiceParam('BRANCH_NAME', ['kkaminski (default)', 'master'])
       
      activeChoiceParam('BUILDS_TRIGGER') {
            description('Available options')
            choiceType('CHECKBOX')
            
            groovyScript {
                script('''
                
             def list=[]
	         (1..4).each {
	         list.add("MNTLAB-kkaminski-child${it}-build-job")
       }
	      return list
                    
            ''')
              
        }
    
    }
  }
}