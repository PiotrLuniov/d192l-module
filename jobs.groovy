job('MNTLAB-kkaminski-main-build-job') {
    
  parameters {
           
      choiceParam('BRANCH_NAME', ['kkaminski (default)', 'master'])
       
      activeChoiceParam('BUILDS_TRIGGER') {
            description('Available options')
            choiceType('CHECKBOX')
            
        }
    
    }
}