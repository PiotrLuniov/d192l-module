job('MNT-lab-hbledai-main-job') {
    parameters {
        activeChoiceParam('BRANCH') {
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
            }
        }
         activeChoiceParam('CHECK_BOXES') {
            choiceType('CHECKBOX')
            groovyScript {
                script('''
def myList = []
for (i in 1..4) {
myList.add("MNT-LAB-hbledai-childe-"+ it)
}
return myList
''')
            }
        }
    }
    steps {
        shell('''
childes=$(echo $CHECK_BOXES | tr "," " ")

for child in $childes
do
    echo 'BRANCH=$BRANCH' > $child
done
''')
        for (i in 1..4) { 
          downstreamParameterized {
            trigger("MNT-LAB-hbledai-childe-"+ i) {

                parameters {
                propertiesFile("MNT-LAB-hbledai-childe-"+ i,  failTriggerOnMissing = true)}
 
            }}
        }
    }
}
for (i in 1..4) {
    job("MNT-LAB-hbledai-childe-"+ i){
        parameters {
          stringParam('BRANCH')
        }
         scm {
        github("MNT-Lab/d192l-module.git", '$BRANCH' )
    }
        steps{
        shell('''
./script.sh > output.txt
tar czvf $BRANCH_dsl_script.tar.gz jobs.groovy
''')}  
    }
}

