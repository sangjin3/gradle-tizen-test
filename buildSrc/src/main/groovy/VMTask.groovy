
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class vm {
    private String platform;
    private String name;

    vm(arg1, arg2){
        platform = arg1; name = arg2;
        println ("VM created with name($name): $platform ");
    }
}

class VmTest {

    public static void createVM(arg1, arg2) {
        def platform = arg1;
        def proc;
        def name;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        def vm_platform = arg1;

        if( arg1.contains("wearable")){
            vm_platform += "-circle";
        }
        vm_platform += "-" + arg2;

        name = platform.replaceAll('-','_');
        name = name.replaceAll('\\.','_');

        proc = ["${Util.em_cli_cmd}", "delete", "--name", "${name}"].execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();
        if( proc.exitValue() != 0 ){
            println ("$sout"); println ("$serr");
        }
        sout.delete(0, sout.length()); serr.delete(0, serr.length());

        proc = ["${Util.em_cli_cmd}", "create", "--platform", "${vm_platform}", "--name", "${name}"].execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();

        if( proc.exitValue() != 0 ){
            println ("$sout"); println ("$serr");
        }
        sout.delete(0, sout.length()); serr.delete(0, serr.length());

        proc = ["${Util.em_cli_cmd}", "launch", "--name", "${name}"].execute();
        proc.consumeProcessOutput(sout, serr);
        proc.waitFor();
        if( proc.exitValue() != 0 ){
            println ("$sout"); println ("$serr");
        }
        sout.delete(0, sout.length()); serr.delete(0, serr.length());
    }
}

class VMTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def platform;
    def arch;

    @TaskAction
        def test() {
            println("=====================================");
            println("${test_name}");
            println("sdk path: ${sdk_path}");
            println("platform: ${platform}");
            println("architecture: ${arch}");
            println("-------------------------------------");

            Util.init(sdk_path, project.gradle.startParameter.taskNames);

            VmTest.createVM(platform, arch);
        }
}

