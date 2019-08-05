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

for(i in 1..4) {
  job("MNTLAB-kkaminski-child${i}-build-job") {
    
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
   }
 }