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
myList.add("MNT-LAB-hbledai-childe-"+ i)
}
return myList
''')
            }
        }
    }
    steps {
        shell('''
rm -rf MNT-LAB*
childes=$(echo $CHECK_BOXES | tr "," " ")

for child in $childes
do
    echo 'BRANCH=$BRANCH' > $child
done
echo 'BRANCH=$BRANCH' > master
''')
        for (i in 1..4) { 
          downstreamParameterized {
            trigger("MNT-LAB-hbledai-childe-"+ i) {

                parameters {
                propertiesFile("MNT-LAB-hbledai-childe-"+ i,  failTriggerOnMissing = true)}
 
            }}
        downstreamParameterized {
            trigger("master") {

                parameters {
                propertiesFile("MNT-LAB-hbledai-childe-"+ i,  failTriggerOnMissing = true)}
 
            }}  
    }
    
    }}
    
    
for (i in 1..4) {
    job("MNT-LAB-hbledai-childe-"+ i){
        parameters {
          stringParam('BRANCH')
        }
         scm {
        github("MNT-Lab/d192l-module", '$BRANCH' )
    }
        steps{
        shell('''

chmod +x script.sh
./script.sh > output.txt
tar czvf ${WORKSPACE}/${BRANCH}_dsl_script.tar.gz jobs.groovy
''')}
         publishers {
             archiveArtifacts('*_dsl_script.tar.gz')}
    }
}
