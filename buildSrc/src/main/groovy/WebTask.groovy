
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import static groovy.io.FileType.FILES

import Util

class WebApp {
    private String Platform;
    private String Template;
    private String Name;

    WebApp(arg1, arg2, arg3){
        Platform = arg1; Template = arg2; Name = arg3;
    }

    def createTest(){
        def args = "create web-project ";
        args += "-p ${Platform} ";
        args += "-t ${Template} ";
        args += "-n ${Name} ";
        args += "-- ${Util.pwd}/out/${Platform}";
        Util.tizen_exec("create", args, 0, 0);
    }

    def buildTest(){
        def args = "build-web ";
        args += "-- ${Util.pwd}/out/${Platform}/${Name}";
        Util.tizen_exec("build", args, 0, 0);
    }

    def packageTest(){
        def args = "package ";
        args += "--type wgt ";
        args += "--sign test_alias  ";
        args += "-- ${Util.pwd}/out/${Platform}/${Name}/.buildResult";
        Util.tizen_exec("package", args, 0, 0);
    }

    def checkWgt(){
        int success = 0;

        new File("${Util.pwd}/out/${Platform}/${Name}").eachFileRecurse(FILES) {
            if( it.name.endsWith('.wgt') ){
                Util.log ("    Success: ${Name}.wgt");
                success = 1;
            }
        }
        if (success == 0 ){
            Util.log ("    Fail: ${Name}.wgt");
        }
    }

    def installTest(serial){

        new File("${Util.pwd}/out/${Platform}/${Name}/.buildResult").eachFile(FILES) {

            if( it.name.endsWith('.wgt') ){
                def args;

                XmlParser parser = new XmlParser();
                def widget = parser.parse( new File("${Util.pwd}/out/${Platform}/${Name}/config.xml") );
                def pkgid = widget.'tizen:application'.'@package'.toString();
                pkgid = pkgid.replaceAll('\\[','');
                pkgid = pkgid.replaceAll('\\]','');

                args = "install ";
                args += "--name ${it.name} ";
                args += "--serial ${serial} ";
                args += "-- ${Util.pwd}/out/${Platform}/${Name}/.buildResult";
                Util.tizen_exec("install ${Name}", args, 0, 0);

                args = "run ";
                args += "--pkgid ${pkgid} ";
                args += "--serial ${serial} ";
                Util.tizen_exec("run ${Name}", args, 0, 0);

                sleep(1000);

                args = "uninstall ";
                args += "--pkgid ${pkgid} ";
                args += "--serial ${serial} ";
                Util.tizen_exec("uninstall ${Name}", args, 0, 0);

            	args = "-s ${serial} ";
                args += "install ";
                args += "${Util.pwd}/out/${Platform}/${Name}/.buildResult/${it.name}";
            	Util.sdb_exec("sdb install ${Name}", args, 0, 0);

            	args = "-s ${serial} ";
                args += "uninstall ";
                args += "${pkgid} ";
            	Util.sdb_exec("sdb uninstall ${Name}", args, 0, 0);
            }
        }
    }
}

class WebTest {
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

class WebTask extends DefaultTask {
    def test_name;
    def sdk_path;
    def platform;

    @TaskAction
        def test() {
            int i = 0;
            int total = 0;

            Util.init(sdk_path, project.gradle.startParameter.taskNames);

            Util.log("=====================================");
            Util.log("${test_name}");
            Util.log("platform: ${platform}");
            Util.log("sdk path: ${sdk_path}");
            Util.log("-------------------------------------");


            WebTest.listTest(platform);

            i = 0;
            WebTest.AppList.each {
                Util.log(++i + " " + it.Name );
                Util.log ("  TC Path: [${Util.pwd}/out/$it.Platform]");
                it.createTest();
                it.buildTest();
                it.packageTest();
                it.checkWgt();
            }
            total = i;
            //Util.log("Total template number: " + total);
        }
}

