
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class image {
    private String Platform;
    private String Profile;
    private String Version;

    image(arg1, arg2, arg3){
        Platform = arg1; Profile = arg2; Version = arg3;
    }
}

class VmTest {
    public static ArrayList<image> ImageList;

    public static void listTest(arg1) {
        def Platform;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        ImageList = new ArrayList<image>();
        def profile = arg1;

        def proc = ["${Util.em_cli_cmd}", "list-image"]. execute();
        proc.waitForProcessOutput(sout, serr);

        if( proc.exitValue() == 0 ){
            println ("Success: list");
        }else{
            println ("Fail   : list");
            println ("$sout"); println ("$serr");
        }

        sout.eachLine { line, count ->
            println ("$line");
            //if ( line.contains("${profile}") ){
            //}
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
            int i = 0;
            int total = 0;

            println("=====================================");
            println("${test_name}");
            println("sdk path: ${sdk_path}");
            println("profile: ${profile}");
            println("serial: ${serial}");
            println("-------------------------------------");

            Util.init(sdk_path);

            VmTest.listTest(profile);

            //println("Total template number: " + total);
        }
}

