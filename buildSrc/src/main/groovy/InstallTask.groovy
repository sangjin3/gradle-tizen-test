
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class vm {
    private String profile;
    private String version;
    private String name;

    vm(arg1, arg2, arg3){
        profile = arg1; version = arg2; name = arg3;
        println ("VM created with name($name): $profile $version ");
    }
}

class VmTest {
    public static ArrayList<vm> vmList;

    public static void createVM(arg1) {
        def profile = arg1;
        def version;
        def proc;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        vmList = new ArrayList<vm>();

        ['2_3_1', '2_4', '3_0'].each { name ->

            version = name.replaceAll('_','.');

            proc = ["${Util.em_cli_cmd}", "delete", "--name", "${profile}_${name}"].execute();
            proc.consumeProcessOutput(sout, serr);
            proc.waitFor();
            sout.delete(0, sout.length()); serr.delete(0, serr.length());

            proc = ["${Util.em_cli_cmd}", "create", "--platform", "${profile}-${version}", "--name", "${profile}_${name}"].execute();
            proc.consumeProcessOutput(sout, serr);
            proc.waitFor();

            if( proc.exitValue() == 0 ){
                def tmp  = new vm(profile, version, "${profile}_${name}");
                vmList.add(tmp);
            }else{
                println ("$sout"); println ("$serr");
            }
            sout.delete(0, sout.length()); serr.delete(0, serr.length());
        }
    }
}

class InstallTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def profile;
    def serial;

    @TaskAction
        def test() {
            println("=====================================");
            println("${test_name}");
            println("sdk path: ${sdk_path}");
            println("profile: ${profile}");
            println("serial: ${serial}");
            println("-------------------------------------");

            Util.init(sdk_path);

            VmTest.createVM(profile);
        }
}

