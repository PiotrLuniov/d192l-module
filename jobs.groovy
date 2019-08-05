def student = "hkanonik"
def master = "master"

job("EPBYMINW9010/MNTLAB-${student}-main-build-job"){
  parameters {
    choiceParam('BRANCH_NAME', ["${student}", "${master}"])
    activeChoiceParam('BRANCH_TRIGGER') {
      description('Allows user choose from multiple choices')
      filterable()
      choiceType('CHECKBOX')
      for (i in 1..3) { 
        groovyScript {
          script('return["MNTLAB-hkanonik-child1-build-job", "MNTLAB-hkanonik-child2-build-job", "MNTLAB-hkanonik-child3-build-job"]')
          // script(for (i in 1..3) {return['MNTLAB-${student}-child$i-build-job']})
          fallbackScript('"fallback choice"')
        }
      }
    }
  }
  for (i in 1..3) {
    publishers {
      downstream("MNTLAB-${student}-child$i-build-job", 'SUCCESS')
    }
  }
  blockOnDownstreamProjects()
}

for (i in 1..3) {
  job("EPBYMINW9010/MNTLAB-${student}-child$i-build-job") {
    scm {
      git{
        remote{
          name('MNTlab')
          url('https://github.com/MNT-Lab/d192l-module.git')
        }
        branch('hkanonik')
      }
    }
    steps {
      shell("chmod +x /var/jenkins_home/workspace/EPBYMINW9010/MNTLAB-hkanonik-child$i-build-job/script.sh \
      /var/jenkins_home/workspace/MNTLAB-hkanonik-child1-build-job/script.sh > output.txt")
      shell('tar -zcvf $BUILD_NUMBER_dsl_script.tar.gz ./output.txt')
    }
  }
}

listView("EPBYMINW9010/${student}") {
  description("MNTLAB Day7: 5-Aug-2019")
  filterBuildQueue()
  filterExecutors()
  jobs {
    name("MNTLAB-${student}-main-build-job")
    for (i in 1..3) {name("MNTLAB-${student}-child$i-build-job")}
  }
  columns{
    status()
    weather()
    name()
    lastSuccess()
    lastFailure()
    lastDuration()
    buildButton()
  }
}
