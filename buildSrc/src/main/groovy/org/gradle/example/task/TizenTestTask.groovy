import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class TizenTestTask extends DefaultTask {
    def test_name;
    def list;

    @TaskAction
        def test() {
            def cmd;
            cmd = "/home/dkyun77/tizen-sdk/tools/ide/bin/tizen ";
            project.exec {

                ignoreExitValue true;

                logger.info("cmd: " + cmd.split().toList());
                logger.info("args: " + list);

                if(list) {
                    commandLine = cmd.split().toList() + list.split().toList()
                } else {
                    commandLine = cmd.split().toList() 
                }
            }
        }
}
