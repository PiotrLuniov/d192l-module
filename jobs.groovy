
job('MNTLAB-kshevchenko-main-build-job') {
    parameters {
        activeChoiceParam('BRANCH_NAME') {
            choiceType('SINGLE_SELECT')
            groovyScript {
                script('return ["kshevchenko", "master"]')
                fallbackScript('return ["error"]')
            }
        }
        activeChoiceReactiveParam('BUILDS_TRIGGER') {
            choiceType('CHECKBOX')
            groovyScript {
                script('''
                def list=[];
                for (i in 1..4){
                list.add("MNTLAB-kshevchenko-child"+i+"-build-job");
                }
                return list;
                ''')
                fallbackScript('"error"')
                referencedParameter('BRANCH_NAME')
            }
        }
    }
    publishers {
        downstreamParameterized {
            trigger('$BUILDS_TRIGGER') {
                condition('UNSTABLE_OR_BETTER')
                parameters {
                    predefinedBuildParameters {
                        properties('BRANCH_NAME=$BRANCH_NAME')
                        textParamValueOnNewLine(true)

                    }
                }
            }
        }
    }

}
for(i in 1..4){
    job('MNTLAB-kshevchenko-child'+i+'-build-job'){
        parameters {
            activeChoiceParam('BRANCH_NAME') {
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
                it.replaceAll(/[a-z0-9]*\\trefs\\/heads\\//, '')
                }
                return branches;
                ''')
                    fallbackScript('"error"')
                }
            }
        }

        scm {
            git('https://github.com/MNT-Lab/d192l-module.git', '$BRANCH_NAME')
        }
        steps {
            shell("chmod +x script.sh")
            shell("echo \$(./script.sh) > output.txt")
            shell("tar -czvf \${BRANCH_NAME}_dsl_script.tar.gz jobs.groovy")
        }

        publishers {
            archiveArtifacts {
                pattern("output.txt")
                pattern("\${BRANCH_NAME}_dsl_script.tar.gz")
                allowEmpty(false)
                onlyIfSuccessful(false)
                fingerprint(false)
                defaultExcludes(true)
            }
        }
    }
}