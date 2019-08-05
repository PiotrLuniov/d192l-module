job('MNTLAB-kkaminski-main-build-job') {
   
  blockOnDownstreamProjects()
  
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
  
  scm {
        git {
            remote {
                name('repo-branch')
                url('https://github.com/MNT-Lab/d192l-module.git')
            }
          
            branch("\${BRANCH_NAME}")
       
    }
  }
  
  steps {
        downstreamParameterized {
            trigger('BUILDS_TRIGGER') {
                block {
                    buildStepFailure('FAILURE')
                    failure('FAILURE')
                    unstable('UNSTABLE')
                }
                parameters {
                  predefinedProp('Branch_name', '${BRANCH_NAME}')
  
               }
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
                 it.replaceAll(/[a-z0-9]*\trefs\\/heads\\//,'') 
             }

           return branches
                    
            ''')
              
         }
       }
     }
    
    }
}