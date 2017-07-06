
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class InstallWebTest {
    public static ArrayList<WebApp> AppList;

    public static void listTest(arg1) {
        def Platform;
        def Template;
        def Name;
        def sout = new StringBuilder();
        def serr = new StringBuilder();
        AppList = new ArrayList<WebApp>();
        def platform = arg1;

        def proc = ["${Util.tizen_cmd}", "list", "web-project"].execute();
        proc.waitForProcessOutput(sout, serr);

        if( proc.exitValue() == 0 ){
            Util.log ("Success: list");
        }else{
            Util.log ("Fail   : list");
            Util.log ("$sout"); Util.log ("$serr");
        }

        sout.eachLine { line, count ->
            if ( line.contains("${platform} ") ){
                String[] splited = line.split("\\s+");
                Platform = splited[0];
                Template = splited[1];
                Name = splited[1].replaceAll('_','');
                Name = Name.replaceAll('-','');

                def app = new WebApp(Platform, Template, Name);
                AppList.add(app);
            }
        }
    }
}

class InstallWebTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def platform;
    def serial;

    @TaskAction
        def test() {
            int i = 0;
            int total = 0;

            Util.init(sdk_path, project.gradle.startParameter.taskNames);

            Util.log("=====================================");
            Util.log("${test_name}");
            Util.log("sdk path: ${sdk_path}");
            Util.log("platform: ${platform}");
            Util.log("serial: ${serial}");
            Util.log("-------------------------------------");


            InstallWebTest.listTest(platform);

            i = 0;
            InstallWebTest.AppList.each {
                Util.log(++i + " " + it.Name );
                Util.log ("  TC Path: [${Util.pwd}/out/$it.Platform]");
                it.installTest(serial);
            }
        }
}

